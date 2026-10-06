package com.futbol.gestion.controladores;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.futbol.gestion.entidades.Competicion;
import com.futbol.gestion.repositorios.CompeticionRepositorio;

/**
 * Controlador del cliente web (Thymeleaf) para las competiciones.
 *
 *   GET  /competiciones               -> listado + formulario de creacion
 *   POST /competiciones/crear         -> crea una competicion
 *   POST /competiciones/eliminar/{id} -> elimina una competicion
 *
 * Las fechas llegan del input type="date" en formato ISO (aaaa-MM-dd),
 * que es justo el que entiende LocalDate.parse().
 */
@Controller
@RequestMapping("/competiciones")
public class CompeticionWeb {

	private final CompeticionRepositorio competicionRepositorio;

	/** Inyeccion por constructor del repositorio. */
	public CompeticionWeb(CompeticionRepositorio competicionRepositorio) {
		this.competicionRepositorio = competicionRepositorio;
	}

	/** Listado de competiciones + formulario de creacion. */
	@GetMapping
	public String listar(Model model) {
		model.addAttribute("competiciones", competicionRepositorio.findAll());
		return "competiciones";
	}

	/** Crea una competicion con los datos del formulario. */
	@PostMapping("/crear")
	public String crear(@RequestParam String nombre,
			@RequestParam int montoPremio,
			@RequestParam String fechaInicio,
			@RequestParam String fechaFin) {
		Competicion competicion = new Competicion(nombre, montoPremio,
				LocalDate.parse(fechaInicio), LocalDate.parse(fechaFin));
		competicion.setId(competicionRepositorio.siguienteId());
		competicionRepositorio.save(competicion);
		return "redirect:/competiciones";
	}

	/** Elimina una competicion por su id. */
	@PostMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Long id) {
		competicionRepositorio.deleteById(id);
		return "redirect:/competiciones";
	}
}
