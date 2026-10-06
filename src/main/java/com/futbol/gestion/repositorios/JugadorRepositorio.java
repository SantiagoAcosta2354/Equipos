package com.futbol.gestion.repositorios;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.futbol.gestion.entidades.Jugador;

/**
 * Repositorio de jugadores en MongoDB.
 *
 * Hereda el CRUD completo de MongoRepository. El id incremental se calcula
 * con siguienteId() porque MongoDB no tiene autoincremento nativo.
 */
@Repository
public interface JugadorRepositorio extends MongoRepository<Jugador, Long> {

	/** Devuelve el documento con el id mas alto. */
	Optional<Jugador> findTopByOrderByIdDesc();

	/** Calcula el siguiente id incremental (ultimo id + 1, o 1 si esta vacia). */
	default Long siguienteId() {
		return findTopByOrderByIdDesc()
				.map(jugador -> jugador.getId() + 1L)
				.orElse(1L);
	}
}
