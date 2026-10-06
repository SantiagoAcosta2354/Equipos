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

import com.futbol.gestion.entidades.Jugador;
import com.futbol.gestion.repositorios.JugadorRepositorio;

/**
 * Controlador API REST (devuelve JSON) para la relacion 1:N.
 *
 * Base: /api/jugadores
 *
 *   GET    /api/jugadores         -> lista todos los jugadores
 *   GET    /api/jugadores/{id}    -> consulta un jugador
 *   POST   /api/jugadores         -> crea un jugador
 *   PUT    /api/jugadores/{id}    -> actualiza un jugador
 *   DELETE /api/jugadores/{id}    -> elimina un jugador
 */
@RestController
@RequestMapping("/api/jugadores")
public class JugadorApiRest {

	private final JugadorRepositorio jugadorRepositorio;

	/** Inyeccion por constructor del repositorio. */
	public JugadorApiRest(JugadorRepositorio jugadorRepositorio) {
		this.jugadorRepositorio = jugadorRepositorio;
	}

	// ── 1. Listar todos los jugadores ───────────────────────────
	@GetMapping
	public List<Jugador> listar() {
		return jugadorRepositorio.findAll();
	}

	// ── 2. Consultar un jugador por id ──────────────────────────
	@GetMapping("/{id}")
	public ResponseEntity<Jugador> consultar(@PathVariable Long id) {
		return jugadorRepositorio.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	// ── 3. Crear un jugador ─────────────────────────────────────
	@PostMapping
	public ResponseEntity<Jugador> crear(@RequestBody Jugador jugador) {
		if (jugador.getNombre() == null || jugador.getNombre().isBlank()) {
			return ResponseEntity.badRequest().build();
		}
		jugador.setId(jugadorRepositorio.siguienteId());      // id incremental
		Jugador guardado = jugadorRepositorio.save(jugador);
		return ResponseEntity.status(HttpStatus.CREATED).body(guardado); // 201
	}

	// ── 4. Actualizar un jugador existente ──────────────────────
	@PutMapping("/{id}")
	public ResponseEntity<Jugador> actualizar(@PathVariable Long id,
			@RequestBody Jugador jugador) {
		if (!jugadorRepositorio.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		jugador.setId(id);                                    // se respeta el id de la URL
		Jugador actualizado = jugadorRepositorio.save(jugador);
		return ResponseEntity.ok(actualizado);                // 200
	}

	// ── 5. Eliminar un jugador ──────────────────────────────────
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		if (!jugadorRepositorio.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		jugadorRepositorio.deleteById(id);
		return ResponseEntity.noContent().build();            // 204
	}
}
