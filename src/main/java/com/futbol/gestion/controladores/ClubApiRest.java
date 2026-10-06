package com.futbol.gestion.controladores;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
 * Controlador API REST del Club (la entidad central del modelo).
 *
 * Ademas del CRUD basico, expone un grupo de endpoints anidados que
 * demuestran las CUATRO relaciones del taller.
 *
 * CRUD basico
 *   GET    /api/clubes                              -> lista todos los clubes
 *   GET    /api/clubes/{id}                         -> consulta un club
 *   POST   /api/clubes                              -> crea un club
 *   PUT    /api/clubes/{id}                         -> actualiza un club
 *   DELETE /api/clubes/{id}                         -> elimina un club
 *
 * Relacion 1:1 con Entrenador
 *   GET    /api/clubes/{id}/entrenador              -> entrenador del club
 *   PUT    /api/clubes/{id}/entrenador/{idEntrenador} -> asigna un entrenador
 *   DELETE /api/clubes/{id}/entrenador              -> quita el entrenador
 *
 * Relacion 1:N con Jugador
 *   GET    /api/clubes/{id}/jugadores               -> plantel del club
 *   POST   /api/clubes/{id}/jugadores/{idJugador}   -> agrega un jugador
 *   DELETE /api/clubes/{id}/jugadores/{idJugador}   -> quita un jugador
 *
 * Relacion N:1 con Asociacion
 *   GET    /api/clubes/{id}/asociacion              -> asociacion del club
 *   PUT    /api/clubes/{id}/asociacion/{idAsociacion} -> asigna una asociacion
 *   DELETE /api/clubes/{id}/asociacion              -> quita la asociacion
 *
 * Relacion N:M con Competicion
 *   GET    /api/clubes/{id}/competiciones           -> competiciones del club
 *   POST   /api/clubes/{id}/competiciones/{idCompeticion} -> inscribe en una competicion
 *   DELETE /api/clubes/{id}/competiciones/{idCompeticion} -> retira de una competicion
 */
@RestController
@RequestMapping("/api/clubes")
public class ClubApiRest {

	private final ClubRepositorio clubRepositorio;
	private final EntrenadorRepositorio entrenadorRepositorio;
	private final JugadorRepositorio jugadorRepositorio;
	private final AsociacionRepositorio asociacionRepositorio;
	private final CompeticionRepositorio competicionRepositorio;

	/** Inyeccion por constructor de los cinco repositorios. */
	public ClubApiRest(ClubRepositorio clubRepositorio,
			EntrenadorRepositorio entrenadorRepositorio,
			JugadorRepositorio jugadorRepositorio,
			AsociacionRepositorio asociacionRepositorio,
			CompeticionRepositorio competicionRepositorio) {
		this.clubRepositorio = clubRepositorio;
		this.entrenadorRepositorio = entrenadorRepositorio;
		this.jugadorRepositorio = jugadorRepositorio;
		this.asociacionRepositorio = asociacionRepositorio;
		this.competicionRepositorio = competicionRepositorio;
	}

	// ── 1. Listar todos los clubes ──────────────────────────────
	@GetMapping
	public List<Club> listar() {
		return clubRepositorio.findAll();
	}

	// ── 2. Consultar un club por id ─────────────────────────────
	@GetMapping("/{id}")
	public ResponseEntity<Club> consultar(@PathVariable Long id) {
		return clubRepositorio.findById(id)
				.map(ResponseEntity::ok)                     // 200 con el club
				.orElse(ResponseEntity.notFound().build());  // 404 si no existe
	}

	// ── 3. Crear un club ────────────────────────────────────────
	@PostMapping
	public ResponseEntity<Club> crear(@RequestBody Club club) {
		if (club.getNombre() == null || club.getNombre().isBlank()) {
			return ResponseEntity.badRequest().build();      // 400 sin nombre
		}
		club.setId(clubRepositorio.siguienteId());           // id incremental
		if (club.getJugadores() == null) {
			club.setJugadores(new ArrayList<>());
		}
		if (club.getCompeticiones() == null) {
			club.setCompeticiones(new ArrayList<>());
		}
		Club guardado = clubRepositorio.save(club);
		return ResponseEntity.status(HttpStatus.CREATED).body(guardado); // 201
	}

	// ── 4. Actualizar un club existente ─────────────────────────
	@PutMapping("/{id}")
	public ResponseEntity<Club> actualizar(@PathVariable Long id, @RequestBody Club club) {
		if (!clubRepositorio.existsById(id)) {
			return ResponseEntity.notFound().build();        // 404 si no existe
		}
		club.setId(id);                                      // se respeta el id de la URL
		if (club.getJugadores() == null) {
			club.setJugadores(new ArrayList<>());
		}
		if (club.getCompeticiones() == null) {
			club.setCompeticiones(new ArrayList<>());
		}
		Club actualizado = clubRepositorio.save(club);
		return ResponseEntity.ok(actualizado);               // 200
	}

	// ── 5. Eliminar un club ─────────────────────────────────────
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		if (!clubRepositorio.existsById(id)) {
			return ResponseEntity.notFound().build();        // 404 si no existe
		}
		clubRepositorio.deleteById(id);
		return ResponseEntity.noContent().build();           // 204
	}

	// ════════════════════════════════════════════════════════════
	//  RELACION 1:1 -> Club / Entrenador
	// ════════════════════════════════════════════════════════════

	/** Devuelve el entrenador del club (404 si el club no existe o no tiene entrenador). */
	@GetMapping("/{id}/entrenador")
	public ResponseEntity<Entrenador> verEntrenador(@PathVariable Long id) {
		return clubRepositorio.findById(id)
				.filter(club -> club.getEntrenador() != null)
				.map(club -> ResponseEntity.ok(club.getEntrenador()))
				.orElse(ResponseEntity.notFound().build());
	}

	/** Asigna un entrenador existente al club (reemplaza el anterior si lo habia). */
	@PutMapping("/{id}/entrenador/{idEntrenador}")
	public ResponseEntity<Club> asignarEntrenador(@PathVariable Long id,
			@PathVariable Long idEntrenador) {
		var clubOpt = clubRepositorio.findById(id);
		var entrenadorOpt = entrenadorRepositorio.findById(idEntrenador);
		if (clubOpt.isEmpty() || entrenadorOpt.isEmpty()) {
			return ResponseEntity.notFound().build();        // 404 si falta alguno
		}
		Club club = clubOpt.get();
		club.setEntrenador(entrenadorOpt.get());
		return ResponseEntity.ok(clubRepositorio.save(club)); // 200
	}

	/** Quita el entrenador del club (el documento del entrenador NO se borra). */
	@DeleteMapping("/{id}/entrenador")
	public ResponseEntity<Club> quitarEntrenador(@PathVariable Long id) {
		var clubOpt = clubRepositorio.findById(id);
		if (clubOpt.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		Club club = clubOpt.get();
		club.setEntrenador(null);
		return ResponseEntity.ok(clubRepositorio.save(club));
	}

	// ════════════════════════════════════════════════════════════
	//  RELACION 1:N -> Club / Jugador
	// ════════════════════════════════════════════════════════════

	/** Devuelve el plantel completo del club. */
	@GetMapping("/{id}/jugadores")
	public ResponseEntity<List<Jugador>> verJugadores(@PathVariable Long id) {
		return clubRepositorio.findById(id)
				.map(club -> ResponseEntity.ok(club.getJugadores()))
				.orElse(ResponseEntity.notFound().build());
	}

	/** Agrega un jugador existente al plantel del club (sin repetirlo). */
	@PostMapping("/{id}/jugadores/{idJugador}")
	public ResponseEntity<Club> agregarJugador(@PathVariable Long id,
			@PathVariable Long idJugador) {
		var clubOpt = clubRepositorio.findById(id);
		var jugadorOpt = jugadorRepositorio.findById(idJugador);
		if (clubOpt.isEmpty() || jugadorOpt.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		Club club = clubOpt.get();
		Jugador jugador = jugadorOpt.get();
		boolean yaEsta = club.getJugadores().stream()
				.anyMatch(j -> j.getId().equals(jugador.getId()));
		if (!yaEsta) {
			club.getJugadores().add(jugador);
		}
		return ResponseEntity.ok(clubRepositorio.save(club)); // 200 con el club actualizado
	}

	/** Quita un jugador del plantel del club (el documento del jugador NO se borra). */
	@DeleteMapping("/{id}/jugadores/{idJugador}")
	public ResponseEntity<Club> quitarJugador(@PathVariable Long id,
			@PathVariable Long idJugador) {
		var clubOpt = clubRepositorio.findById(id);
		if (clubOpt.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		Club club = clubOpt.get();
		boolean quitado = club.getJugadores().removeIf(j -> j.getId().equals(idJugador));
		if (!quitado) {
			return ResponseEntity.notFound().build();        // el jugador no estaba en el club
		}
		return ResponseEntity.ok(clubRepositorio.save(club));
	}

	// ════════════════════════════════════════════════════════════
	//  RELACION N:1 -> Club / Asociacion
	// ════════════════════════════════════════════════════════════

	/** Devuelve la asociacion a la que pertenece el club. */
	@GetMapping("/{id}/asociacion")
	public ResponseEntity<Asociacion> verAsociacion(@PathVariable Long id) {
		return clubRepositorio.findById(id)
				.filter(club -> club.getAsociacion() != null)
				.map(club -> ResponseEntity.ok(club.getAsociacion()))
				.orElse(ResponseEntity.notFound().build());
	}

	/** Asigna una asociacion existente al club. */
	@PutMapping("/{id}/asociacion/{idAsociacion}")
	public ResponseEntity<Club> asignarAsociacion(@PathVariable Long id,
			@PathVariable Long idAsociacion) {
		var clubOpt = clubRepositorio.findById(id);
		var asociacionOpt = asociacionRepositorio.findById(idAsociacion);
		if (clubOpt.isEmpty() || asociacionOpt.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		Club club = clubOpt.get();
		club.setAsociacion(asociacionOpt.get());
		return ResponseEntity.ok(clubRepositorio.save(club));
	}

	/** Quita la asociacion del club. */
	@DeleteMapping("/{id}/asociacion")
	public ResponseEntity<Club> quitarAsociacion(@PathVariable Long id) {
		var clubOpt = clubRepositorio.findById(id);
		if (clubOpt.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		Club club = clubOpt.get();
		club.setAsociacion(null);
		return ResponseEntity.ok(clubRepositorio.save(club));
	}

	// ════════════════════════════════════════════════════════════
	//  RELACION N:M -> Club / Competicion
	// ════════════════════════════════════════════════════════════

	/** Devuelve las competiciones en las que participa el club. */
	@GetMapping("/{id}/competiciones")
	public ResponseEntity<List<Competicion>> verCompeticiones(@PathVariable Long id) {
		return clubRepositorio.findById(id)
				.map(club -> ResponseEntity.ok(club.getCompeticiones()))
				.orElse(ResponseEntity.notFound().build());
	}

	/** Inscribe el club en una competicion existente (sin repetirla). */
	@PostMapping("/{id}/competiciones/{idCompeticion}")
	public ResponseEntity<Club> inscribirEnCompeticion(@PathVariable Long id,
			@PathVariable Long idCompeticion) {
		var clubOpt = clubRepositorio.findById(id);
		var competicionOpt = competicionRepositorio.findById(idCompeticion);
		if (clubOpt.isEmpty() || competicionOpt.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		Club club = clubOpt.get();
		Competicion competicion = competicionOpt.get();
		boolean yaEsta = club.getCompeticiones().stream()
				.anyMatch(c -> c.getId().equals(competicion.getId()));
		if (!yaEsta) {
			club.getCompeticiones().add(competicion);
		}
		return ResponseEntity.ok(clubRepositorio.save(club));
	}

	/** Retira el club de una competicion (el documento de la competicion NO se borra). */
	@DeleteMapping("/{id}/competiciones/{idCompeticion}")
	public ResponseEntity<Club> retirarDeCompeticion(@PathVariable Long id,
			@PathVariable Long idCompeticion) {
		var clubOpt = clubRepositorio.findById(id);
		if (clubOpt.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		Club club = clubOpt.get();
		boolean quitada = club.getCompeticiones().removeIf(c -> c.getId().equals(idCompeticion));
		if (!quitada) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(clubRepositorio.save(club));
	}
}
