package com.futbol.gestion.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.futbol.gestion.entidades.Asociacion;
import com.futbol.gestion.repositorios.AsociacionRepositorio;

/**
 * Controlador del cliente web (Thymeleaf) para las asociaciones.
 *
 *   GET  /asociaciones               -> listado + formulario de creacion
 *   POST /asociaciones/crear         -> crea una asociacion
 *   POST /asociaciones/eliminar/{id} -> elimina una asociacion
 */
@Controller
@RequestMapping("/asociaciones")
public class AsociacionWeb {

	private final AsociacionRepositorio asociacionRepositorio;

	/** Inyeccion por constructor del repositorio. */
	public AsociacionWeb(AsociacionRepositorio asociacionRepositorio) {
		this.asociacionRepositorio = asociacionRepositorio;
	}

	/** Listado de asociaciones + formulario de creacion. */
	@GetMapping
	public String listar(Model model) {
		model.addAttribute("asociaciones", asociacionRepositorio.findAll());
		return "asociaciones";
	}

	/** Crea una asociacion con los datos del formulario. */
	@PostMapping("/crear")
	public String crear(@RequestParam String nombre,
			@RequestParam String pais,
			@RequestParam String presidente) {
		Asociacion asociacion = new Asociacion(nombre, pais, presidente);
		asociacion.setId(asociacionRepositorio.siguienteId());
		asociacionRepositorio.save(asociacion);
		return "redirect:/asociaciones";
	}

	/** Elimina una asociacion por su id. */
	@PostMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Long id) {
		asociacionRepositorio.deleteById(id);
		return "redirect:/asociaciones";
	}
}
