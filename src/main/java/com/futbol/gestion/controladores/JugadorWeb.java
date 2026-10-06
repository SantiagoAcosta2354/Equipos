package com.futbol.gestion.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.futbol.gestion.entidades.Jugador;
import com.futbol.gestion.repositorios.JugadorRepositorio;

/**
 * Controlador del cliente web (Thymeleaf) para los jugadores.
 *
 *   GET  /jugadores               -> listado + formulario de creacion
 *   POST /jugadores/crear         -> crea un jugador
 *   POST /jugadores/eliminar/{id} -> elimina un jugador
 */
@Controller
@RequestMapping("/jugadores")
public class JugadorWeb {

	private final JugadorRepositorio jugadorRepositorio;

	/** Inyeccion por constructor del repositorio. */
	public JugadorWeb(JugadorRepositorio jugadorRepositorio) {
		this.jugadorRepositorio = jugadorRepositorio;
	}

	/** Listado de jugadores + formulario de creacion. */
	@GetMapping
	public String listar(Model model) {
		model.addAttribute("jugadores", jugadorRepositorio.findAll());
		return "jugadores";
	}

	/** Crea un jugador con los datos del formulario. */
	@PostMapping("/crear")
	public String crear(@RequestParam String nombre,
			@RequestParam String apellido,
			@RequestParam int numero,
			@RequestParam String posicion) {
		Jugador jugador = new Jugador(nombre, apellido, numero, posicion);
		jugador.setId(jugadorRepositorio.siguienteId());
		jugadorRepositorio.save(jugador);
		return "redirect:/jugadores";
	}

	/** Elimina un jugador por su id. */
	@PostMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Long id) {
		jugadorRepositorio.deleteById(id);
		return "redirect:/jugadores";
	}
}
