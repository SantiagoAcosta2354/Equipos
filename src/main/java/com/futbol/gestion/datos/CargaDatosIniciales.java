package com.futbol.gestion.datos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.futbol.gestion.entidades.Asociacion;
import com.futbol.gestion.entidades.Club;
import com.futbol.gestion.entidades.Competicion;
import com.futbol.gestion.entidades.Entrenador;
import com.futbol.gestion.entidades.Jugador;
import com.futbol.gestion.repositorios.AsociacionRepositorio;
import com.futbol.gestion.repositorios.ClubRepositorio;
import com.futbol.gestion.repositorios.CompeticionRepositorio;
import com.futbol.gestion.repositorios.EntrenadorRepositorio;
import com.futbol.gestion.repositorios.JugadorRepositorio;

/**
 * Carga los datos de ejemplo del Mundial de Clubes.
 *
 * Se ejecuta solo cuando esta activo el perfil "datos-iniciales":
 *
 *     mvn spring-boot:run -Dspring-boot.run.profiles=datos-iniciales
 *
 * Es IDEMPOTENTE: antes de crear un registro busca uno con el mismo nombre y,
 * si ya existe, lo reutiliza. Por eso se puede ejecutar varias veces sin
 * duplicar la informacion.
 */
@Component
@Profile("datos-iniciales")
public class CargaDatosIniciales implements CommandLineRunner {

	private final AsociacionRepositorio asociacionRepositorio;
	private final EntrenadorRepositorio entrenadorRepositorio;
	private final JugadorRepositorio jugadorRepositorio;
	private final CompeticionRepositorio competicionRepositorio;
	private final ClubRepositorio clubRepositorio;

	public CargaDatosIniciales(AsociacionRepositorio asociacionRepositorio,
			EntrenadorRepositorio entrenadorRepositorio,
			JugadorRepositorio jugadorRepositorio,
			CompeticionRepositorio competicionRepositorio,
			ClubRepositorio clubRepositorio) {
		this.asociacionRepositorio = asociacionRepositorio;
		this.entrenadorRepositorio = entrenadorRepositorio;
		this.jugadorRepositorio = jugadorRepositorio;
		this.competicionRepositorio = competicionRepositorio;
		this.clubRepositorio = clubRepositorio;
	}

	@Override
	public void run(String... args) {

		// ── Asociaciones (una federacion por pais) ──────────────
		Long fcf = asociacion("Federacion Colombiana de Futbol", "Colombia", "Ramon Jesurun");
		Long rfef = asociacion("Real Federacion Espanola de Futbol", "Espana", "Alvaro Rivas");
		Long thefa = asociacion("The Football Association", "Inglaterra", "Daniel Whitfield");
		Long cbf = asociacion("Confederacao Brasileira de Futebol", "Brasil", "Marcos Tavares");
		Long afa = asociacion("Asociacion del Futbol Argentino", "Argentina", "Hector Villalba");
		Long dfb = asociacion("Deutscher Fussball-Bund", "Alemania", "Klaus Brenner");
		Long figc = asociacion("Federazione Italiana Giuoco Calcio", "Italia", "Paolo Ricci");
		Long saff = asociacion("Saudi Arabian Football Federation", "Arabia Saudita", "Faisal Al Mansour");
		Long efa = asociacion("Egyptian Football Association", "Egipto", "Omar Halabi");

		// ── Competiciones ──────────────────────────────────────
		Long mundial = competicion("Mundial de Clubes FIFA 2026", 12000000,
				LocalDate.of(2026, 6, 15), LocalDate.of(2026, 7, 13));
		Long libertadores = competicion("Copa Libertadores", 5000000,
				LocalDate.of(2026, 2, 1), LocalDate.of(2026, 11, 30));
		Long sudamericana = competicion("Copa CONMEBOL Sudamericana 2026", 3000000,
				LocalDate.of(2026, 3, 3), LocalDate.of(2026, 10, 24));
		Long recopa = competicion("Recopa Sudamericana 2026", 1500000,
				LocalDate.of(2026, 2, 18), LocalDate.of(2026, 2, 25));
		Long champions = competicion("UEFA Champions League 2025-26", 20000000,
				LocalDate.of(2025, 9, 16), LocalDate.of(2026, 5, 30));
		Long supercopa = competicion("Supercopa de Espana 2026", 2000000,
				LocalDate.of(2026, 1, 7), LocalDate.of(2026, 1, 11));
		Long premier = competicion("Premier League 2026-27", 25000000,
				LocalDate.of(2026, 8, 15), LocalDate.of(2027, 5, 24));
		Long seriea = competicion("Serie A 2026-27", 18000000,
				LocalDate.of(2026, 8, 22), LocalDate.of(2027, 5, 30));
		Long bundesliga = competicion("Bundesliga 2026-27", 16000000,
				LocalDate.of(2026, 8, 21), LocalDate.of(2027, 5, 22));
		Long saudipro = competicion("Saudi Pro League 2026-27", 4000000,
				LocalDate.of(2026, 8, 20), LocalDate.of(2027, 5, 28));
		Long caf = competicion("CAF Champions League 2026", 2500000,
				LocalDate.of(2026, 3, 1), LocalDate.of(2026, 11, 15));

		// ── Clubes ─────────────────────────────────────────────
		club("Millonarios", "Bogota", fcf, entrenador("Alberto", "Gamero", 55, "Colombiana"),
				new Long[] { mundial, libertadores }, new String[][] {
						{ "David", "Macalister", "10", "Mediocampista" },
						{ "Radamel", "Falcao", "9", "Delantero" },
						{ "Andres", "Pedraza", "5", "Defensa" },
						{ "Juan", "Estrada", "1", "Portero" },
						{ "Felipe", "Rojas", "8", "Mediocampista" } });

		club("Atletico Bucaramanga", "Bucaramanga", fcf, entrenador("Diego", "Rueda", 48, "Colombiana"),
				new Long[] { libertadores, sudamericana }, new String[][] {
						{ "Carlos", "Mendoza", "1", "Portero" },
						{ "Ivan", "Torres", "4", "Defensa" },
						{ "Oscar", "Pineda", "6", "Mediocampista" },
						{ "Fabian", "Quintero", "7", "Extremo" },
						{ "Luis", "Herrera", "11", "Delantero" } });

		club("Real Madrid", "Madrid", rfef, entrenador("Martin", "Delgado", 52, "Espanola"),
				new Long[] { mundial, champions, supercopa }, new String[][] {
						{ "Andres", "Villalba", "1", "Portero" },
						{ "Nicolas", "Ferrer", "4", "Defensa" },
						{ "Mateo", "Salinas", "8", "Mediocampista" },
						{ "Gonzalo", "Ibarra", "10", "Enganche" },
						{ "Bruno", "Cardozo", "9", "Delantero" } });

		club("Manchester City", "Manchester", thefa, entrenador("Graham", "Whitlock", 50, "Inglesa"),
				new Long[] { mundial, premier }, new String[][] {
						{ "Peter", "Hallam", "1", "Portero" },
						{ "Owen", "Radcliffe", "3", "Lateral" },
						{ "Callum", "Doyle", "6", "Mediocampista" },
						{ "Nathan", "Beck", "10", "Enganche" },
						{ "Jordan", "Sutcliffe", "9", "Delantero" } });

		club("Palmeiras", "Sao Paulo", cbf, entrenador("Paulo", "Andrade", 49, "Brasilena"),
				new Long[] { mundial, libertadores }, new String[][] {
						{ "Rogerio", "Bianchi", "1", "Portero" },
						{ "Tadeu", "Moura", "3", "Defensa" },
						{ "Vinicius", "Prado", "7", "Extremo" },
						{ "Caio", "Bertoldo", "8", "Mediocampista" },
						{ "Everton", "Sales", "9", "Delantero" } });

		club("Flamengo", "Rio de Janeiro", cbf, entrenador("Ricardo", "Salgado", 51, "Brasilena"),
				new Long[] { mundial, libertadores, sudamericana }, new String[][] {
						{ "Wagner", "Duarte", "1", "Portero" },
						{ "Helio", "Barbosa", "4", "Defensa" },
						{ "Igor", "Lacerda", "5", "Defensa" },
						{ "Renan", "Coutinho", "10", "Enganche" },
						{ "Douglas", "Peixoto", "9", "Delantero" } });

		club("Boca Juniors", "Buenos Aires", afa, entrenador("Daniel", "Ocampo", 54, "Argentina"),
				new Long[] { mundial, libertadores }, new String[][] {
						{ "Emiliano", "Sosa", "1", "Portero" },
						{ "Ramiro", "Velez", "2", "Lateral" },
						{ "Tomas", "Aguirre", "5", "Defensa" },
						{ "Julian", "Barrios", "8", "Mediocampista" },
						{ "Franco", "Cabrera", "9", "Delantero" } });

		club("River Plate", "Buenos Aires", afa, entrenador("Lucas", "Ferrari", 47, "Argentina"),
				new Long[] { mundial, libertadores, recopa }, new String[][] {
						{ "Matias", "Ferreyra", "1", "Portero" },
						{ "Bruno", "Luduena", "3", "Defensa" },
						{ "Agustin", "Peralta", "6", "Volante" },
						{ "Nicolas", "Ojeda", "10", "Enganche" },
						{ "Santiago", "Godoy", "11", "Extremo" } });

		club("Inter de Milan", "Milan", figc, entrenador("Marco", "Bellini", 53, "Italiana"),
				new Long[] { mundial, seriea }, new String[][] {
						{ "Lorenzo", "Bassi", "1", "Portero" },
						{ "Andrea", "Conti", "4", "Defensa" },
						{ "Matteo", "Greco", "5", "Defensa" },
						{ "Davide", "Marchetti", "8", "Mediocampista" },
						{ "Simone", "Rinaldi", "9", "Delantero" } });

		club("Bayern de Munich", "Munich", dfb, entrenador("Stefan", "Keller", 56, "Alemana"),
				new Long[] { mundial, bundesliga }, new String[][] {
						{ "Jonas", "Brandt", "1", "Portero" },
						{ "Tobias", "Neumann", "4", "Defensa" },
						{ "Leon", "Hartmann", "6", "Volante" },
						{ "Felix", "Wagner", "10", "Enganche" },
						{ "Niklas", "Krug", "9", "Delantero" } });

		club("Al Hilal", "Riad", saff, entrenador("Nawaf", "Al Harbi", 48, "Saudita"),
				new Long[] { mundial, saudipro }, new String[][] {
						{ "Yasser", "Al Otaibi", "1", "Portero" },
						{ "Sultan", "Al Qahtani", "4", "Defensa" },
						{ "Majed", "Al Dossari", "8", "Mediocampista" },
						{ "Fahad", "Al Shehri", "10", "Enganche" },
						{ "Talal", "Al Zahrani", "9", "Delantero" } });

		club("Al Ahly", "El Cairo", efa, entrenador("Ahmed", "Zaki", 52, "Egipcia"),
				new Long[] { mundial, caf }, new String[][] {
						{ "Karim", "Fathi", "1", "Portero" },
						{ "Hossam", "Adel", "4", "Defensa" },
						{ "Mostafa", "Nabil", "6", "Mediocampista" },
						{ "Youssef", "Samir", "10", "Enganche" },
						{ "Amr", "Salah", "9", "Delantero" } });

		System.out.println();
		System.out.println("========== DATOS INICIALES DEL MUNDIAL DE CLUBES ==========");
		System.out.println("  Asociaciones : " + asociacionRepositorio.count());
		System.out.println("  Entrenadores : " + entrenadorRepositorio.count());
		System.out.println("  Jugadores    : " + jugadorRepositorio.count());
		System.out.println("  Competiciones: " + competicionRepositorio.count());
		System.out.println("  Clubes       : " + clubRepositorio.count());
		System.out.println("===========================================================");
	}

	// ── Ayudantes: buscan por nombre y crean solo si no existe ──

	private Long asociacion(String nombre, String pais, String presidente) {
		return asociacionRepositorio.findAll().stream()
				.filter(a -> a.getNombre().trim().equalsIgnoreCase(nombre))
				.map(Asociacion::getId)
				.findFirst()
				.orElseGet(() -> {
					Asociacion nueva = new Asociacion();
					nueva.setId(asociacionRepositorio.siguienteId());
					nueva.setNombre(nombre);
					nueva.setPais(pais);
					nueva.setPresidente(presidente);
					return asociacionRepositorio.save(nueva).getId();
				});
	}

	private Long entrenador(String nombre, String apellido, int edad, String nacionalidad) {
		return entrenadorRepositorio.findAll().stream()
				.filter(e -> e.getNombre().trim().equalsIgnoreCase(nombre)
						&& e.getApellido().trim().equalsIgnoreCase(apellido))
				.map(Entrenador::getId)
				.findFirst()
				.orElseGet(() -> {
					Entrenador nuevo = new Entrenador();
					nuevo.setId(entrenadorRepositorio.siguienteId());
					nuevo.setNombre(nombre);
					nuevo.setApellido(apellido);
					nuevo.setEdad(edad);
					nuevo.setNacionalidad(nacionalidad);
					return entrenadorRepositorio.save(nuevo).getId();
				});
	}

	private Long jugador(String nombre, String apellido, int numero, String posicion) {
		return jugadorRepositorio.findAll().stream()
				.filter(j -> j.getNombre().trim().equalsIgnoreCase(nombre)
						&& j.getApellido().trim().equalsIgnoreCase(apellido))
				.map(Jugador::getId)
				.findFirst()
				.orElseGet(() -> {
					Jugador nuevo = new Jugador();
					nuevo.setId(jugadorRepositorio.siguienteId());
					nuevo.setNombre(nombre);
					nuevo.setApellido(apellido);
					nuevo.setNumero(numero);
					nuevo.setPosicion(posicion);
					return jugadorRepositorio.save(nuevo).getId();
				});
	}

	private Long competicion(String nombre, int montoPremio, LocalDate inicio, LocalDate fin) {
		return competicionRepositorio.findAll().stream()
				.filter(c -> c.getNombre().trim().equalsIgnoreCase(nombre))
				.map(Competicion::getId)
				.findFirst()
				.orElseGet(() -> {
					Competicion nueva = new Competicion();
					nueva.setId(competicionRepositorio.siguienteId());
					nueva.setNombre(nombre);
					nueva.setMontoPremio(montoPremio);
					nueva.setFechaInicio(inicio);
					nueva.setFechaFin(fin);
					return competicionRepositorio.save(nueva).getId();
				});
	}

	/**
	 * Crea el club si no existe y, en cualquier caso, le deja asignadas sus
	 * cuatro relaciones: entrenador (1:1), jugadores (1:N), asociacion (N:1)
	 * y competiciones (N:M).
	 */
	private void club(String nombre, String ciudad, Long idAsociacion, Long idEntrenador,
			Long[] idsCompeticiones, String[][] plantel) {

		Club club = clubRepositorio.findAll().stream()
				.filter(c -> c.getNombre().trim().equalsIgnoreCase(nombre))
				.findFirst()
				.orElseGet(() -> {
					Club nuevo = new Club();
					nuevo.setId(clubRepositorio.siguienteId());
					nuevo.setNombre(nombre);
					return nuevo;
				});

		club.setCiudad(ciudad);
		club.setAsociacion(asociacionRepositorio.findById(idAsociacion).orElse(null));
		club.setEntrenador(entrenadorRepositorio.findById(idEntrenador).orElse(null));

		List<Competicion> competiciones = new ArrayList<>();
		for (Long id : idsCompeticiones) {
			competicionRepositorio.findById(id).ifPresent(competiciones::add);
		}
		club.setCompeticiones(competiciones);

		List<Jugador> jugadores = new ArrayList<>();
		for (String[] j : plantel) {
			Long id = jugador(j[0], j[1], Integer.parseInt(j[2]), j[3]);
			jugadorRepositorio.findById(id).ifPresent(jugadores::add);
		}
		club.setJugadores(jugadores);

		clubRepositorio.save(club);
	}
}
