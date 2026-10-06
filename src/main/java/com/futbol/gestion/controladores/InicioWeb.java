package com.futbol.gestion.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.futbol.gestion.repositorios.AsociacionRepositorio;
import com.futbol.gestion.repositorios.ClubRepositorio;
import com.futbol.gestion.repositorios.CompeticionRepositorio;
import com.futbol.gestion.repositorios.EntrenadorRepositorio;
import com.futbol.gestion.repositorios.JugadorRepositorio;

/**
 * Controlador del cliente web (Thymeleaf) para la pagina de inicio.
 *
 * Muestra un panel con el numero de documentos de cada coleccion y los
 * enlaces a las pantallas de cada entidad. Es el punto de entrada del
 * "cliente" en la arquitectura cliente-servidor: el navegador pide HTML
 * al servidor, y el servidor consulta MongoDB.
 */
@Controller
public class InicioWeb {

	private final ClubRepositorio clubRepositorio;
	private final EntrenadorRepositorio entrenadorRepositorio;
	private final JugadorRepositorio jugadorRepositorio;
	private final AsociacionRepositorio asociacionRepositorio;
	private final CompeticionRepositorio competicionRepositorio;

	/** Inyeccion por constructor de los cinco repositorios. */
	public InicioWeb(ClubRepositorio clubRepositorio,
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

	/** Panel principal con los contadores de cada coleccion. */
	@GetMapping("/")
	public String inicio(Model model) {
		model.addAttribute("totalClubes", clubRepositorio.count());
		model.addAttribute("totalEntrenadores", entrenadorRepositorio.count());
		model.addAttribute("totalJugadores", jugadorRepositorio.count());
		model.addAttribute("totalAsociaciones", asociacionRepositorio.count());
		model.addAttribute("totalCompeticiones", competicionRepositorio.count());
		return "index";
	}
}
