package com.futbol.gestion.controladores;

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

import com.futbol.gestion.entidades.Competicion;
import com.futbol.gestion.repositorios.CompeticionRepositorio;

/**
 * Controlador API REST (devuelve JSON) para la relacion N:M.
 *
 * Base: /api/competiciones
 *
 *   GET    /api/competiciones         -> lista todas las competiciones
 *   GET    /api/competiciones/{id}    -> consulta una competicion
 *   POST   /api/competiciones         -> crea una competicion
 *   PUT    /api/competiciones/{id}    -> actualiza una competicion
 *   DELETE /api/competiciones/{id}    -> elimina una competicion
 *
 * Las fechas se envian en formato ISO (aaaa-MM-dd), por ejemplo:
 *   { "nombre": "Copa Libertadores", "montoPremio": 5000000,
 *     "fechaInicio": "2026-02-01", "fechaFin": "2026-11-30" }
 */
@RestController
@RequestMapping("/api/competiciones")
public class CompeticionApiRest {

	private final CompeticionRepositorio competicionRepositorio;

	/** Inyeccion por constructor del repositorio. */
	public CompeticionApiRest(CompeticionRepositorio competicionRepositorio) {
		this.competicionRepositorio = competicionRepositorio;
	}

	// ── 1. Listar todas las competiciones ───────────────────────
	@GetMapping
	public List<Competicion> listar() {
		return competicionRepositorio.findAll();
	}

	// ── 2. Consultar una competicion por id ─────────────────────
	@GetMapping("/{id}")
	public ResponseEntity<Competicion> consultar(@PathVariable Long id) {
		return competicionRepositorio.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	// ── 3. Crear una competicion ────────────────────────────────
	@PostMapping
	public ResponseEntity<Competicion> crear(@RequestBody Competicion competicion) {
		if (competicion.getNombre() == null || competicion.getNombre().isBlank()) {
			return ResponseEntity.badRequest().build();
		}
		competicion.setId(competicionRepositorio.siguienteId()); // id incremental
		Competicion guardada = competicionRepositorio.save(competicion);
		return ResponseEntity.status(HttpStatus.CREATED).body(guardada); // 201
	}

	// ── 4. Actualizar una competicion existente ─────────────────
	@PutMapping("/{id}")
	public ResponseEntity<Competicion> actualizar(@PathVariable Long id,
			@RequestBody Competicion competicion) {
		if (!competicionRepositorio.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		competicion.setId(id);                                 // se respeta el id de la URL
		Competicion actualizada = competicionRepositorio.save(competicion);
		return ResponseEntity.ok(actualizada);                 // 200
	}

	// ── 5. Eliminar una competicion ─────────────────────────────
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		if (!competicionRepositorio.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		competicionRepositorio.deleteById(id);
		return ResponseEntity.noContent().build();             // 204
	}
}
