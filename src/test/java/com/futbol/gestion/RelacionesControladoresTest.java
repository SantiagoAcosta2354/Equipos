package com.futbol.gestion;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.futbol.gestion.controladores.ClubApiRest;
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
 * Pruebas del controlador API REST del Club con los cinco repositorios
 * simulados (Mockito). Comprueban el CRUD y, sobre todo, los endpoints
 * anidados que representan las CUATRO relaciones del taller, sin necesitar
 * una conexion real a MongoDB.
 */
class RelacionesControladoresTest {

	private ClubRepositorio clubRepositorio;
	private EntrenadorRepositorio entrenadorRepositorio;
	private JugadorRepositorio jugadorRepositorio;
	private AsociacionRepositorio asociacionRepositorio;
	private CompeticionRepositorio competicionRepositorio;
	private MockMvc api;

	@BeforeEach
	void preparar() {
		clubRepositorio = Mockito.mock(ClubRepositorio.class);
		entrenadorRepositorio = Mockito.mock(EntrenadorRepositorio.class);
		jugadorRepositorio = Mockito.mock(JugadorRepositorio.class);
		asociacionRepositorio = Mockito.mock(AsociacionRepositorio.class);
		competicionRepositorio = Mockito.mock(CompeticionRepositorio.class);

		api = MockMvcBuilders.standaloneSetup(new ClubApiRest(
				clubRepositorio, entrenadorRepositorio, jugadorRepositorio,
				asociacionRepositorio, competicionRepositorio)).build();
	}

	/** Crea un club de prueba ya guardado en el repositorio simulado. */
	private Club clubDePrueba() {
		Club club = new Club("Millonarios", "Bogota");
		club.setId(1L);
		return club;
	}

	// ── CRUD basico ─────────────────────────────────────────────

	@Test
	void listarClubesDevuelveJson() throws Exception {
		when(clubRepositorio.findAll()).thenReturn(List.of(clubDePrueba()));

		api.perform(get("/api/clubes"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].nombre").value("Millonarios"))
				.andExpect(jsonPath("$[0].ciudad").value("Bogota"));
	}

	@Test
	void consultarClubInexistenteDevuelve404() throws Exception {
		when(clubRepositorio.findById(99L)).thenReturn(Optional.empty());

		api.perform(get("/api/clubes/99")).andExpect(status().isNotFound());
	}

	@Test
	void crearClubAsignaIdIncrementalYDevuelve201() throws Exception {
		when(clubRepositorio.siguienteId()).thenReturn(5L);
		when(clubRepositorio.save(any(Club.class))).thenAnswer(inv -> inv.getArgument(0));

		api.perform(post("/api/clubes")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"nombre\":\"Santa Fe\",\"ciudad\":\"Bogota\"}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(5))
				.andExpect(jsonPath("$.nombre").value("Santa Fe"));

		ArgumentCaptor<Club> capturado = ArgumentCaptor.forClass(Club.class);
		verify(clubRepositorio).save(capturado.capture());
		// Las listas de relaciones nunca deben quedar en null
		org.junit.jupiter.api.Assertions.assertNotNull(capturado.getValue().getJugadores());
		org.junit.jupiter.api.Assertions.assertNotNull(capturado.getValue().getCompeticiones());
	}

	@Test
	void crearClubSinNombreDevuelve400() throws Exception {
		api.perform(post("/api/clubes")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"ciudad\":\"Bogota\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void eliminarClubInexistenteDevuelve404() throws Exception {
		when(clubRepositorio.existsById(anyLong())).thenReturn(false);

		api.perform(delete("/api/clubes/77")).andExpect(status().isNotFound());
	}

	// ── Relacion 1:1 -> Entrenador ──────────────────────────────

	@Test
	void verEntrenadorDelClubDevuelveElEntrenador() throws Exception {
		Club club = clubDePrueba();
		club.setEntrenador(new Entrenador("Alberto", "Gamero", 55, "Colombiana"));
		when(clubRepositorio.findById(1L)).thenReturn(Optional.of(club));

		api.perform(get("/api/clubes/1/entrenador"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nombre").value("Alberto"))
				.andExpect(jsonPath("$.nacionalidad").value("Colombiana"));
	}

	@Test
	void verEntrenadorSinAsignarDevuelve404() throws Exception {
		when(clubRepositorio.findById(1L)).thenReturn(Optional.of(clubDePrueba()));

		api.perform(get("/api/clubes/1/entrenador")).andExpect(status().isNotFound());
	}

	@Test
	void asignarEntrenadorGuardarLaReferencia() throws Exception {
		Club club = clubDePrueba();
		Entrenador entrenador = new Entrenador("Alberto", "Gamero", 55, "Colombiana");
		entrenador.setId(3L);

		when(clubRepositorio.findById(1L)).thenReturn(Optional.of(club));
		when(entrenadorRepositorio.findById(3L)).thenReturn(Optional.of(entrenador));
		when(clubRepositorio.save(any(Club.class))).thenAnswer(inv -> inv.getArgument(0));

		api.perform(put("/api/clubes/1/entrenador/3"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.entrenador.apellido").value("Gamero"));
	}

	@Test
	void asignarEntrenadorInexistenteDevuelve404() throws Exception {
		when(clubRepositorio.findById(1L)).thenReturn(Optional.of(clubDePrueba()));
		when(entrenadorRepositorio.findById(50L)).thenReturn(Optional.empty());

		api.perform(put("/api/clubes/1/entrenador/50")).andExpect(status().isNotFound());
	}

	// ── Relacion 1:N -> Jugadores ───────────────────────────────

	@Test
	void verPlantelDevuelveLaListaDeJugadores() throws Exception {
		Club club = clubDePrueba();
		Jugador jugador = new Jugador("David", "Macalister", 10, "Mediocampista");
		jugador.setId(2L);
		club.getJugadores().add(jugador);
		when(clubRepositorio.findById(1L)).thenReturn(Optional.of(club));

		api.perform(get("/api/clubes/1/jugadores"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].nombre").value("David"))
				.andExpect(jsonPath("$[0].posicion").value("Mediocampista"));
	}

	@Test
	void agregarJugadorNoDuplicaSiYaEstaEnElPlantel() throws Exception {
		Club club = clubDePrueba();
		Jugador jugador = new Jugador("David", "Macalister", 10, "Mediocampista");
		jugador.setId(2L);
		club.getJugadores().add(jugador);         // ya estaba en el plantel

		when(clubRepositorio.findById(1L)).thenReturn(Optional.of(club));
		when(jugadorRepositorio.findById(2L)).thenReturn(Optional.of(jugador));
		when(clubRepositorio.save(any(Club.class))).thenAnswer(inv -> inv.getArgument(0));

		api.perform(post("/api/clubes/1/jugadores/2"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.jugadores.length()").value(1)); // sigue habiendo uno
	}

	@Test
	void quitarJugadorQueNoEstaDevuelve404() throws Exception {
		when(clubRepositorio.findById(1L)).thenReturn(Optional.of(clubDePrueba()));

		api.perform(delete("/api/clubes/1/jugadores/9")).andExpect(status().isNotFound());
	}

	// ── Relacion N:1 -> Asociacion ──────────────────────────────

	@Test
	void asignarAsociacionDevuelveElClubActualizado() throws Exception {
		Club club = clubDePrueba();
		Asociacion asociacion = new Asociacion("Federacion Colombiana de Futbol", "Colombia", "Ramon Jesurun");
		asociacion.setId(1L);

		when(clubRepositorio.findById(1L)).thenReturn(Optional.of(club));
		when(asociacionRepositorio.findById(1L)).thenReturn(Optional.of(asociacion));
		when(clubRepositorio.save(any(Club.class))).thenAnswer(inv -> inv.getArgument(0));

		api.perform(put("/api/clubes/1/asociacion/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.asociacion.nombre").value("Federacion Colombiana de Futbol"))
				.andExpect(jsonPath("$.asociacion.presidente").value("Ramon Jesurun"));
	}

	// ── Relacion N:M -> Competiciones ───────────────────────────

	@Test
	void inscribirEnCompeticionAgregaLaReferencia() throws Exception {
		Club club = clubDePrueba();
		Competicion copa = new Competicion("Copa Libertadores", 5000000,
				LocalDate.of(2026, 2, 1), LocalDate.of(2026, 11, 30));
		copa.setId(4L);

		when(clubRepositorio.findById(1L)).thenReturn(Optional.of(club));
		when(competicionRepositorio.findById(4L)).thenReturn(Optional.of(copa));
		when(clubRepositorio.save(any(Club.class))).thenAnswer(inv -> inv.getArgument(0));

		api.perform(post("/api/clubes/1/competiciones/4"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.competiciones[0].nombre").value("Copa Libertadores"))
				.andExpect(jsonPath("$.competiciones[0].montoPremio").value(5000000));
	}

	@Test
	void verCompeticionesDeUnClubSinInscripcionesDevuelveListaVacia() throws Exception {
		when(clubRepositorio.findById(1L)).thenReturn(Optional.of(clubDePrueba()));

		api.perform(get("/api/clubes/1/competiciones"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(0));
	}

	@Test
	void retirarDeCompeticionQuitaLaReferencia() throws Exception {
		Club club = clubDePrueba();
		Competicion copa = new Competicion("Copa Postobon", 1000000,
				LocalDate.of(2026, 1, 15), LocalDate.of(2026, 6, 30));
		copa.setId(4L);
		club.setCompeticiones(new ArrayList<>(List.of(copa)));

		when(clubRepositorio.findById(1L)).thenReturn(Optional.of(club));
		when(clubRepositorio.save(any(Club.class))).thenAnswer(inv -> inv.getArgument(0));

		api.perform(delete("/api/clubes/1/competiciones/4"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.competiciones.length()").value(0));
	}
}
