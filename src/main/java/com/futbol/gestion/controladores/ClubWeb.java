package com.futbol.gestion.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.futbol.gestion.entidades.Club;
import com.futbol.gestion.repositorios.AsociacionRepositorio;
import com.futbol.gestion.repositorios.ClubRepositorio;
import com.futbol.gestion.repositorios.CompeticionRepositorio;
import com.futbol.gestion.repositorios.EntrenadorRepositorio;
import com.futbol.gestion.repositorios.JugadorRepositorio;

/**
 * Controlador del cliente web (Thymeleaf) para los clubes.
 *
 * Pantallas:
 *   GET  /clubes                                  -> listado + formulario de creacion
 *   POST /clubes/crear                            -> crea un club
 *   POST /clubes/eliminar/{id}                    -> elimina un club
 *   GET  /clubes/{id}                             -> ficha del club con sus 4 relaciones
 *
 * Acciones sobre las relaciones (desde la ficha del club):
 *   POST /clubes/{id}/entrenador                  -> asigna entrenador (1:1)
 *   POST /clubes/{id}/entrenador/quitar           -> quita entrenador
 *   POST /clubes/{id}/asociacion                  -> asigna asociacion (N:1)
 *   POST /clubes/{id}/asociacion/quitar           -> quita asociacion
 *   POST /clubes/{id}/jugadores                   -> agrega jugador al plantel (1:N)
 *   POST /clubes/{id}/jugadores/quitar            -> quita jugador del plantel
 *   POST /clubes/{id}/competiciones               -> inscribe en competicion (N:M)
 *   POST /clubes/{id}/competiciones/quitar        -> retira de competicion
 *
 * Nota: las rutas del cliente web viven bajo /clubes (HTML) y las de la API
 * REST bajo /api/clubes (JSON), por eso no se pisan entre si.
 */
@Controller
@RequestMapping("/clubes")
public class ClubWeb {

	private final ClubRepositorio clubRepositorio;
	private final EntrenadorRepositorio entrenadorRepositorio;
	private final JugadorRepositorio jugadorRepositorio;
	private final AsociacionRepositorio asociacionRepositorio;
	private final CompeticionRepositorio competicionRepositorio;

	/** Inyeccion por constructor de los cinco repositorios. */
	public ClubWeb(ClubRepositorio clubRepositorio,
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

	// ── Listado de clubes + formulario de creacion ──────────────
	@GetMapping
	public String listar(Model model) {
		model.addAttribute("clubes", clubRepositorio.findAll());
		return "clubes";
	}

	// ── Crear un club ───────────────────────────────────────────
	@PostMapping("/crear")
	public String crear(@RequestParam String nombre, @RequestParam String ciudad) {
		Club club = new Club(nombre, ciudad);
		club.setId(clubRepositorio.siguienteId());
		clubRepositorio.save(club);
		return "redirect:/clubes";
	}

	// ── Eliminar un club ────────────────────────────────────────
	@PostMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Long id) {
		clubRepositorio.deleteById(id);
		return "redirect:/clubes";
	}

	// ── Ficha del club: muestra las cuatro relaciones ───────────
	@GetMapping("/{id}")
	public String detalle(@PathVariable Long id, Model model) {
		Club club = clubRepositorio.findById(id).orElse(null);
		if (club == null) {
			return "redirect:/clubes";                       // si no existe, vuelve al listado
		}
		model.addAttribute("club", club);
		// Catalogos completos para los desplegables de asignacion
		model.addAttribute("entrenadores", entrenadorRepositorio.findAll());
		model.addAttribute("jugadores", jugadorRepositorio.findAll());
		model.addAttribute("asociaciones", asociacionRepositorio.findAll());
		model.addAttribute("competiciones", competicionRepositorio.findAll());
		return "club-detalle";
	}

	// ════════════════════════════════════════════════════════════
	//  RELACION 1:1 -> Entrenador
	// ════════════════════════════════════════════════════════════

	/** Asigna un entrenador al club. */
	@PostMapping("/{id}/entrenador")
	public String asignarEntrenador(@PathVariable Long id, @RequestParam Long idEntrenador) {
		clubRepositorio.findById(id).ifPresent(club -> {
			entrenadorRepositorio.findById(idEntrenador).ifPresent(club::setEntrenador);
			clubRepositorio.save(club);
		});
		return "redirect:/clubes/" + id;
	}

	/** Quita el entrenador del club. */
	@PostMapping("/{id}/entrenador/quitar")
	public String quitarEntrenador(@PathVariable Long id) {
		clubRepositorio.findById(id).ifPresent(club -> {
			club.setEntrenador(null);
			clubRepositorio.save(club);
		});
		return "redirect:/clubes/" + id;
	}

	// ════════════════════════════════════════════════════════════
	//  RELACION N:1 -> Asociacion
	// ════════════════════════════════════════════════════════════

	/** Asigna una asociacion al club. */
	@PostMapping("/{id}/asociacion")
	public String asignarAsociacion(@PathVariable Long id, @RequestParam Long idAsociacion) {
		clubRepositorio.findById(id).ifPresent(club -> {
			asociacionRepositorio.findById(idAsociacion).ifPresent(club::setAsociacion);
			clubRepositorio.save(club);
		});
		return "redirect:/clubes/" + id;
	}

	/** Quita la asociacion del club. */
	@PostMapping("/{id}/asociacion/quitar")
	public String quitarAsociacion(@PathVariable Long id) {
		clubRepositorio.findById(id).ifPresent(club -> {
			club.setAsociacion(null);
			clubRepositorio.save(club);
		});
		return "redirect:/clubes/" + id;
	}

	// ════════════════════════════════════════════════════════════
	//  RELACION 1:N -> Jugador
	// ════════════════════════════════════════════════════════════

	/** Agrega un jugador al plantel del club. */
	@PostMapping("/{id}/jugadores")
	public String agregarJugador(@PathVariable Long id, @RequestParam Long idJugador) {
		clubRepositorio.findById(id).ifPresent(club -> {
			jugadorRepositorio.findById(idJugador).ifPresent(jugador -> {
				boolean yaEsta = club.getJugadores().stream()
						.anyMatch(j -> j.getId().equals(jugador.getId()));
				if (!yaEsta) {
					club.getJugadores().add(jugador);
				}
			});
			clubRepositorio.save(club);
		});
		return "redirect:/clubes/" + id;
	}

	/** Quita un jugador del plantel del club. */
	@PostMapping("/{id}/jugadores/quitar")
	public String quitarJugador(@PathVariable Long id, @RequestParam Long idJugador) {
		clubRepositorio.findById(id).ifPresent(club -> {
			club.getJugadores().removeIf(j -> j.getId().equals(idJugador));
			clubRepositorio.save(club);
		});
		return "redirect:/clubes/" + id;
	}

	// ════════════════════════════════════════════════════════════
	//  RELACION N:M -> Competicion
	// ════════════════════════════════════════════════════════════

	/** Inscribe el club en una competicion. */
	@PostMapping("/{id}/competiciones")
	public String inscribir(@PathVariable Long id, @RequestParam Long idCompeticion) {
		clubRepositorio.findById(id).ifPresent(club -> {
			competicionRepositorio.findById(idCompeticion).ifPresent(competicion -> {
				boolean yaEsta = club.getCompeticiones().stream()
						.anyMatch(c -> c.getId().equals(competicion.getId()));
				if (!yaEsta) {
					club.getCompeticiones().add(competicion);
				}
			});
			clubRepositorio.save(club);
		});
		return "redirect:/clubes/" + id;
	}

	/** Retira el club de una competicion. */
	@PostMapping("/{id}/competiciones/quitar")
	public String retirar(@PathVariable Long id, @RequestParam Long idCompeticion) {
		clubRepositorio.findById(id).ifPresent(club -> {
			club.getCompeticiones().removeIf(c -> c.getId().equals(idCompeticion));
			clubRepositorio.save(club);
		});
		return "redirect:/clubes/" + id;
	}
}
