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

import com.futbol.gestion.entidades.Entrenador;
import com.futbol.gestion.repositorios.EntrenadorRepositorio;

/**
 * Controlador API REST (devuelve JSON) para la relacion 1:1.
 *
 * Base: /api/entrenadores
 *
 *   GET    /api/entrenadores         -> lista todos los entrenadores
 *   GET    /api/entrenadores/{id}    -> consulta un entrenador
 *   POST   /api/entrenadores         -> crea un entrenador
 *   PUT    /api/entrenadores/{id}    -> actualiza un entrenador
 *   DELETE /api/entrenadores/{id}    -> elimina un entrenador
 */
@RestController
@RequestMapping("/api/entrenadores")
public class EntrenadorApiRest {

	private final EntrenadorRepositorio entrenadorRepositorio;

	/** Inyeccion por constructor del repositorio. */
	public EntrenadorApiRest(EntrenadorRepositorio entrenadorRepositorio) {
		this.entrenadorRepositorio = entrenadorRepositorio;
	}

	// ── 1. Listar todos los entrenadores ────────────────────────
	@GetMapping
	public List<Entrenador> listar() {
		return entrenadorRepositorio.findAll();
	}

	// ── 2. Consultar un entrenador por id ───────────────────────
	@GetMapping("/{id}")
	public ResponseEntity<Entrenador> consultar(@PathVariable Long id) {
		return entrenadorRepositorio.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	// ── 3. Crear un entrenador ──────────────────────────────────
	@PostMapping
	public ResponseEntity<Entrenador> crear(@RequestBody Entrenador entrenador) {
		if (entrenador.getNombre() == null || entrenador.getNombre().isBlank()) {
			return ResponseEntity.badRequest().build();
		}
		entrenador.setId(entrenadorRepositorio.siguienteId()); // id incremental
		Entrenador guardado = entrenadorRepositorio.save(entrenador);
		return ResponseEntity.status(HttpStatus.CREATED).body(guardado); // 201
	}

	// ── 4. Actualizar un entrenador existente ───────────────────
	@PutMapping("/{id}")
	public ResponseEntity<Entrenador> actualizar(@PathVariable Long id,
			@RequestBody Entrenador entrenador) {
		if (!entrenadorRepositorio.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		entrenador.setId(id);                                  // se respeta el id de la URL
		Entrenador actualizado = entrenadorRepositorio.save(entrenador);
		return ResponseEntity.ok(actualizado);                 // 200
	}

	// ── 5. Eliminar un entrenador ───────────────────────────────
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		if (!entrenadorRepositorio.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		entrenadorRepositorio.deleteById(id);
		return ResponseEntity.noContent().build();              // 204
	}
}
