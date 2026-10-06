package com.futbol.gestion.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador del cliente web que sirve la consola de la API REST.
 *
 * Responde a GET /api con una pagina HTML desde la que se pueden consultar
 * los endpoints de la API y ver la respuesta JSON, sin necesidad de Postman
 * ni de curl.
 *
 * No hay conflicto con los controladores REST: ellos atienden rutas mas
 * especificas (/api/clubes, /api/jugadores, ...) y este solo atiende /api.
 */
@Controller
public class ApiConsolaWeb {

	/** Consola para consultar la API REST desde el navegador. */
	@GetMapping("/api")
	public String consola() {
		return "consola-api";
	}
}
