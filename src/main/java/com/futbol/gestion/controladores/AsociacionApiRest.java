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

import com.futbol.gestion.entidades.Asociacion;
import com.futbol.gestion.repositorios.AsociacionRepositorio;

/**
 * Controlador API REST (devuelve JSON) para la relacion N:1.
 *
 * Base: /api/asociaciones
 *
 *   GET    /api/asociaciones         -> lista todas las asociaciones
 *   GET    /api/asociaciones/{id}    -> consulta una asociacion
 *   POST   /api/asociaciones         -> crea una asociacion
 *   PUT    /api/asociaciones/{id}    -> actualiza una asociacion
 *   DELETE /api/asociaciones/{id}    -> elimina una asociacion
 */
@RestController
@RequestMapping("/api/asociaciones")
public class AsociacionApiRest {

	private final AsociacionRepositorio asociacionRepositorio;

	/** Inyeccion por constructor del repositorio. */
	public AsociacionApiRest(AsociacionRepositorio asociacionRepositorio) {
		this.asociacionRepositorio = asociacionRepositorio;
	}

	// ── 1. Listar todas las asociaciones ────────────────────────
	@GetMapping
	public List<Asociacion> listar() {
		return asociacionRepositorio.findAll();
	}

	// ── 2. Consultar una asociacion por id ──────────────────────
	@GetMapping("/{id}")
	public ResponseEntity<Asociacion> consultar(@PathVariable Long id) {
		return asociacionRepositorio.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	// ── 3. Crear una asociacion ─────────────────────────────────
	@PostMapping
	public ResponseEntity<Asociacion> crear(@RequestBody Asociacion asociacion) {
		if (asociacion.getNombre() == null || asociacion.getNombre().isBlank()) {
			return ResponseEntity.badRequest().build();
		}
		asociacion.setId(asociacionRepositorio.siguienteId()); // id incremental
		Asociacion guardada = asociacionRepositorio.save(asociacion);
		return ResponseEntity.status(HttpStatus.CREATED).body(guardada); // 201
	}

	// ── 4. Actualizar una asociacion existente ──────────────────
	@PutMapping("/{id}")
	public ResponseEntity<Asociacion> actualizar(@PathVariable Long id,
			@RequestBody Asociacion asociacion) {
		if (!asociacionRepositorio.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		asociacion.setId(id);                                  // se respeta el id de la URL
		Asociacion actualizada = asociacionRepositorio.save(asociacion);
		return ResponseEntity.ok(actualizada);                 // 200
	}

	// ── 5. Eliminar una asociacion ──────────────────────────────
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		if (!asociacionRepositorio.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		asociacionRepositorio.deleteById(id);
		return ResponseEntity.noContent().build();             // 204
	}
}
