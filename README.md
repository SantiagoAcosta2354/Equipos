# Gestión de Equipos — API REST + Cliente Servidor

Taller **"Relaciones Spring FrameWork / Mongo"**: CRUD API REST y repaso Cliente-Servidor.

Aplicación Spring Boot sobre **MongoDB Atlas** que modela las cuatro relaciones del taller
(1:1, 1:N, N:1 y N:M) aplicadas al dominio de los clubes de fútbol.

---

## 1. Modelo de dominio

Cinco documentos en MongoDB:

| Documento | Colección | Atributos |
|---|---|---|
| `Club` | `clubes` | `id`, `nombre`, `ciudad`, + las 4 relaciones |
| `Entrenador` | `entrenadores` | `id`, `nombre`, `apellido`, `edad`, `nacionalidad` |
| `Jugador` | `jugadores` | `id`, `nombre`, `apellido`, `numero`, `posicion` |
| `Asociacion` | `asociaciones` | `id`, `nombre`, `pais`, `presidente` |
| `Competicion` | `competiciones` | `id`, `nombre`, `montoPremio`, `fechaInicio`, `fechaFin` |

### Las cuatro relaciones

| # | Relación | Anotación JPA (taller) | Implementación en MongoDB |
|---|---|---|---|
| 1 | `Club` **1:1** `Entrenador` | `@OneToOne` | `@DocumentReference Entrenador` |
| 2 | `Club` **1:N** `Jugador` | `@OneToMany` | `List<@DocumentReference Jugador>` |
| 3 | `Club` **N:1** `Asociacion` | `@ManyToOne` | `@DocumentReference Asociacion` |
| 4 | `Club` **N:M** `Competicion` | `@ManyToMany` | `List<@DocumentReference Competicion>` |

**Todas las relaciones son unidireccionales**: el `Club` es el propietario, tal como pide el
taller (evitar relaciones bidireccionales).

### Equivalencias JPA → MongoDB

| JPA | MongoDB (Spring Data) |
|---|---|
| `@Entity` + `@Table(name = "clubes")` | `@Document(collection = "clubes")` |
| `@Id private Long id;` | `@Id private Long id;` (id incremental con `siguienteId()`) |
| `@OneToOne` / `@ManyToOne` | `@DocumentReference` |
| `@OneToMany` / `@ManyToMany` | `List<@DocumentReference ...>` |
| `@JoinColumn(name = "id_club")` | **No existe**: la relación *es* el campo o el array |
| `@OnDelete(CASCADE)` | **No existe**: el borrado en cascada se maneja en el controlador |
| `FetchType.LAZY / EAGER` | **No existe**: Spring Data resuelve las referencias al leer |

> En MongoDB **no hay tablas, foreign keys ni tabla intermedia**. La tabla intermedia
> `clubes_jugadores` del taller y el campo `asociacion_id` se convierten aquí en referencias
> dentro del documento `club`.

---

## 2. Arquitectura cliente-servidor

```
  NAVEGADOR  ──HTTP──►  Controladores Web (@Controller)  ──┐
  (cliente)              Thymeleaf + Bootstrap 5           │
                                                           ├──► Repositorios ──► MONGODB ATLAS
  POSTMAN /  ──HTTP──►  Controladores REST (@RestController)│      (Spring Data)   (colecciones)
  curl                   JSON                              ─┘
  (cliente)
```

Hay **dos clientes** sobre el mismo conjunto de repositorios:

1. **Cliente web** (`/clubes`, `/entrenadores`, ...) — devuelve HTML con Thymeleaf.
2. **Cliente REST** (`/api/clubes`, ...) — devuelve JSON, para Postman o `curl`.

---

## 3. Ejecución

Requisitos: **JDK 21**, **Maven 3.9+** y acceso a internet (MongoDB Atlas).

```bash
# 1. Compilar y ejecutar las pruebas
mvn test

# 2. Arrancar la aplicación
mvn spring-boot:run
```

Luego abrir en el navegador:

- Cliente web: <http://localhost:8085/>
- API REST: <http://localhost:8085/api/clubes>

### Configuración de la base de datos

En `src/main/resources/application.properties`:

```properties
spring.mongodb.uri=mongodb+srv://usuario:contraseña@cluster.mongodb.net/
spring.mongodb.database=equiposdb
```

> **Importante (Spring Boot 4):** la propiedad correcta es `spring.mongodb.uri`. La antigua
> `spring.data.mongodb.uri` quedó obsoleta. Alternativa sin escribir la contraseña en el
> archivo: definir la variable de entorno `SPRING_MONGODB_URI` con la cadena completa.
>
> Recuerda que la dirección IP desde la que ejecutas debe estar en la lista de accesos
> permitidos (*Network Access*) del clúster de Atlas.

---

## 4. API REST — endpoints

### CRUD por recurso

| Recurso | Endpoints |
|---|---|
| Clubes | `GET/POST /api/clubes`, `GET/PUT/DELETE /api/clubes/{id}` |
| Entrenadores | `GET/POST /api/entrenadores`, `GET/PUT/DELETE /api/entrenadores/{id}` |
| Jugadores | `GET/POST /api/jugadores`, `GET/PUT/DELETE /api/jugadores/{id}` |
| Asociaciones | `GET/POST /api/asociaciones`, `GET/PUT/DELETE /api/asociaciones/{id}` |
| Competiciones | `GET/POST /api/competiciones`, `GET/PUT/DELETE /api/competiciones/{id}` |

Códigos HTTP: `200` OK · `201` creado · `204` eliminado · `400` datos inválidos · `404` no existe.

### Endpoints que exponen cada relación

| Relación | Método y ruta | Qué hace |
|---|---|---|
| **1:1** | `GET /api/clubes/{id}/entrenador` | Entrenador del club |
| | `PUT /api/clubes/{id}/entrenador/{idEntrenador}` | Asigna entrenador |
| | `DELETE /api/clubes/{id}/entrenador` | Quita el entrenador |
| **1:N** | `GET /api/clubes/{id}/jugadores` | Plantel del club |
| | `POST /api/clubes/{id}/jugadores/{idJugador}` | Agrega jugador al plantel |
| | `DELETE /api/clubes/{id}/jugadores/{idJugador}` | Quita jugador del plantel |
| **N:1** | `GET /api/clubes/{id}/asociacion` | Asociación del club |
| | `PUT /api/clubes/{id}/asociacion/{idAsociacion}` | Asigna asociación |
| | `DELETE /api/clubes/{id}/asociacion` | Quita la asociación |
| **N:M** | `GET /api/clubes/{id}/competiciones` | Competiciones del club |
| | `POST /api/clubes/{id}/competiciones/{idCompeticion}` | Inscribe en competición |
| | `DELETE /api/clubes/{id}/competiciones/{idCompeticion}` | Retira de competición |

### Ejemplo de uso con `curl`

```bash
# 1. Crear el catálogo
curl -X POST http://localhost:8085/api/entrenadores \
     -H "Content-Type: application/json" \
     -d '{"nombre":"Alberto","apellido":"Gamero","edad":55,"nacionalidad":"Colombiana"}'

curl -X POST http://localhost:8085/api/asociaciones \
     -H "Content-Type: application/json" \
     -d '{"nombre":"Federacion Colombiana de Futbol","pais":"Colombia","presidente":"Ramon Jesurun"}'

curl -X POST http://localhost:8085/api/competiciones \
     -H "Content-Type: application/json" \
     -d '{"nombre":"Copa Libertadores","montoPremio":5000000,"fechaInicio":"2026-02-01","fechaFin":"2026-11-30"}'

curl -X POST http://localhost:8085/api/jugadores \
     -H "Content-Type: application/json" \
     -d '{"nombre":"David","apellido":"Macalister","numero":10,"posicion":"Mediocampista"}'

# 2. Crear el club
curl -X POST http://localhost:8085/api/clubes \
     -H "Content-Type: application/json" \
     -d '{"nombre":"Millonarios","ciudad":"Bogota"}'

# 3. Enlazar las relaciones (1:1, N:1, 1:N, N:M)
curl -X PUT  http://localhost:8085/api/clubes/1/entrenador/1
curl -X PUT  http://localhost:8085/api/clubes/1/asociacion/1
curl -X POST http://localhost:8085/api/clubes/1/jugadores/1
curl -X POST http://localhost:8085/api/clubes/1/competiciones/1

# 4. Ver el club con todas sus relaciones resueltas
curl http://localhost:8085/api/clubes/1
```

Respuesta de `GET /api/clubes/1` (las referencias se devuelven resueltas):

```json
{
  "id": 1,
  "nombre": "Millonarios",
  "ciudad": "Bogota",
  "entrenador": { "id": 1, "nombre": "Alberto", "apellido": "Gamero", "edad": 55, "nacionalidad": "Colombiana" },
  "jugadores": [ { "id": 1, "nombre": "David", "apellido": "Macalister", "numero": 10, "posicion": "Mediocampista" } ],
  "asociacion": { "id": 1, "nombre": "Federacion Colombiana de Futbol", "pais": "Colombia", "presidente": "Ramon Jesurun" },
  "competiciones": [ { "id": 1, "nombre": "Copa Libertadores", "montoPremio": 5000000, "fechaInicio": "2026-02-01", "fechaFin": "2026-11-30" } ]
}
```

---

## 5. Estructura del proyecto

```
gestion-equipos/
├── pom.xml
├── README.md
└── src
    ├── main
    │   ├── java/com/futbol/gestion
    │   │   ├── GestionEquiposApplication.java     ← clase main
    │   │   ├── entidades/                            ← @Document y @DocumentReference
    │   │   │   ├── Club.java                         ← entidad central (4 relaciones)
    │   │   │   ├── Entrenador.java
    │   │   │   ├── Jugador.java
    │   │   │   ├── Asociacion.java
    │   │   │   └── Competicion.java
    │   │   ├── repositorios/                         ← MongoRepository + id incremental
    │   │   │   ├── ClubRepositorio.java
    │   │   │   ├── EntrenadorRepositorio.java
    │   │   │   ├── JugadorRepositorio.java
    │   │   │   ├── AsociacionRepositorio.java
    │   │   │   └── CompeticionRepositorio.java
    │   │   └── controladores/
    │   │       ├── *ApiRest.java                     ← API REST (JSON)
    │   │       ├── *Web.java                         ← cliente web (Thymeleaf)
    │   │       └── InicioWeb.java                    ← panel de inicio
    │   └── resources
    │       ├── application.properties                ← cadena de conexión a Atlas
    │       ├── static/css/estilos.css
    │       └── templates/                            ← vistas del cliente web
    │           ├── fragmentos.html
    │           ├── index.html
    │           ├── clubes.html
    │           ├── club-detalle.html                 ← muestra las 4 relaciones
    │           ├── entrenadores.html
    │           ├── jugadores.html
    │           ├── asociaciones.html
    │           └── competiciones.html
    └── test/java/com/futbol/gestion
        ├── GestionEquiposApplicationTests.java    ← arranque del contexto
        └── RelacionesControladoresTest.java          ← CRUD + relaciones (Mockito)
```

---

## 6. Cómo verificar las relaciones en MongoDB Compass

1. Abrir Compass y conectarse al clúster con la misma cadena de `application.properties`.
2. Entrar a la base de datos `equiposdb`.
3. Abrir la colección `clubes`: en el documento del club se ven los campos
   `entrenador`, `jugadores`, `asociacion` y `competiciones` guardados **solo como ids**
   (por ejemplo `"entrenador": 1` y `"jugadores": [1, 2]`).
4. Abrir `entrenadores`, `jugadores`, `asociaciones` y `competiciones`: cada documento vive
   en su propia colección.
5. Cambiar los ids en `clubes` y comprobar que la API devuelve los objetos completos:
   eso es la resolución de `@DocumentReference`.
