package com.futbol.gestion.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.futbol.gestion.entidades.Entrenador;
import com.futbol.gestion.repositorios.EntrenadorRepositorio;

/**
 * Controlador del cliente web (Thymeleaf) para los entrenadores.
 *
 *   GET  /entrenadores               -> listado + formulario de creacion
 *   POST /entrenadores/crear         -> crea un entrenador
 *   POST /entrenadores/eliminar/{id} -> elimina un entrenador
 */
@Controller
@RequestMapping("/entrenadores")
public class EntrenadorWeb {

	private final EntrenadorRepositorio entrenadorRepositorio;

	/** Inyeccion por constructor del repositorio. */
	public EntrenadorWeb(EntrenadorRepositorio entrenadorRepositorio) {
		this.entrenadorRepositorio = entrenadorRepositorio;
	}

	/** Listado de entrenadores + formulario de creacion. */
	@GetMapping
	public String listar(Model model) {
		model.addAttribute("entrenadores", entrenadorRepositorio.findAll());
		return "entrenadores";
	}

	/** Crea un entrenador con los datos del formulario. */
	@PostMapping("/crear")
	public String crear(@RequestParam String nombre,
			@RequestParam String apellido,
			@RequestParam int edad,
			@RequestParam String nacionalidad) {
		Entrenador entrenador = new Entrenador(nombre, apellido, edad, nacionalidad);
		entrenador.setId(entrenadorRepositorio.siguienteId());
		entrenadorRepositorio.save(entrenador);
		return "redirect:/entrenadores";
	}

	/** Elimina un entrenador por su id. */
	@PostMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Long id) {
		entrenadorRepositorio.deleteById(id);
		return "redirect:/entrenadores";
	}
}
