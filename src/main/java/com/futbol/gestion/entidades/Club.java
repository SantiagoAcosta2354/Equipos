package com.futbol.gestion.entidades;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

/**
 * Club de futbol: documento central del modelo.
 *
 * Se guarda como documento en la coleccion "clubes" y reune las CUATRO
 * relaciones del taller:
 *
 *   1. Club 1:1 Entrenador     -> @OneToOne       -> @DocumentReference
 *   2. Club 1:N Jugador        -> @OneToMany      -> List&lt;@DocumentReference&gt;
 *   3. Club N:1 Asociacion     -> @ManyToOne      -> @DocumentReference
 *   4. Club N:M Competicion    -> @ManyToMany     -> List&lt;@DocumentReference&gt;
 *
 * Equivalencias con el modelo relacional del taller:
 *
 *   @Entity + @Table(name = "clubes")  ->  @Document(collection = "clubes")
 *   @OneToOne  Entrenador              ->  @DocumentReference Entrenador
 *   @OneToMany List&lt;Jugador&gt;           ->  List&lt;@DocumentReference Jugador&gt;
 *   @ManyToOne Asociacion              ->  @DocumentReference Asociacion
 *   @ManyToMany List&lt;Competicion&gt;      ->  List&lt;@DocumentReference Competicion&gt;
 *   @JoinColumn(name = "id_club")      ->  no existe: la relacion es el array
 *
 * Todas las relaciones son UNIDIRECCIONALES (el Club es el propietario, igual
 * que en el taller, que evita relaciones bidireccionales).
 *
 * Nota: se agrega el campo "nombre" porque los documentos del taller no lo
 * incluyen, pero un club necesita identificarse en la API y en el cliente web.
 */
@Document(collection = "clubes")
public class Club {

	/** Id incremental del documento (se envia como "_id" a MongoDB). */
	@Id
	private Long id;

	/** Nombre del club (por ejemplo, Millonarios). */
	private String nombre;

	/** Ciudad donde esta radicado el club. */
	private String ciudad;

	// ── Relacion 1:1 con Entrenador ─────────────────────────────
	/**
	 * Un club tiene un solo entrenador.
	 * En MongoDB solo se guarda el id del entrenador; Spring Data lo resuelve
	 * automaticamente al leer el club.
	 */
	@DocumentReference
	private Entrenador entrenador;

	// ── Relacion 1:N con Jugador ────────────────────────────────
	/**
	 * Plantel de jugadores del club.
	 * En MongoDB se guarda un array con los ids de los jugadores.
	 */
	@DocumentReference
	private List<Jugador> jugadores = new ArrayList<>();

	// ── Relacion N:1 con Asociacion ─────────────────────────────
	/**
	 * Asociacion a la que pertenece el club.
	 * Equivale al campo "asociacion_id" (FK) que crearia JPA en la tabla clubes.
	 */
	@DocumentReference
	private Asociacion asociacion;

	// ── Relacion N:M con Competicion ────────────────────────────
	/**
	 * Competiciones en las que participa el club.
	 * En JPA generaria una tabla intermedia; aqui es un array de referencias.
	 */
	@DocumentReference
	private List<Competicion> competiciones = new ArrayList<>();

	/** Constructor vacio: obligatorio para que Spring Data construya el objeto. */
	public Club() {
	}

	/** Constructor de conveniencia sin el id. */
	public Club(String nombre, String ciudad) {
		this.nombre = nombre;
		this.ciudad = ciudad;
	}

	// ── Getters y Setters ───────────────────────────────────────

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCiudad() {
		return ciudad;
	}

	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	public Entrenador getEntrenador() {
		return entrenador;
	}

	public void setEntrenador(Entrenador entrenador) {
		this.entrenador = entrenador;
	}

	public List<Jugador> getJugadores() {
		return jugadores;
	}

	public void setJugadores(List<Jugador> jugadores) {
		this.jugadores = jugadores;
	}

	public Asociacion getAsociacion() {
		return asociacion;
	}

	public void setAsociacion(Asociacion asociacion) {
		this.asociacion = asociacion;
	}

	public List<Competicion> getCompeticiones() {
		return competiciones;
	}

	public void setCompeticiones(List<Competicion> competiciones) {
		this.competiciones = competiciones;
	}

	@Override
	public String toString() {
		return "Club{id=" + id + ", nombre='" + nombre + "', ciudad='" + ciudad
				+ "', entrenador=" + (entrenador != null ? entrenador.getId() : null)
				+ ", jugadores=" + jugadores.size()
				+ ", asociacion=" + (asociacion != null ? asociacion.getId() : null)
				+ ", competiciones=" + competiciones.size() + "}";
	}
}
