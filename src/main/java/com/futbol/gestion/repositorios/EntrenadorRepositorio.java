package com.futbol.gestion.repositorios;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.futbol.gestion.entidades.Entrenador;

/**
 * Repositorio de entrenadores en MongoDB.
 *
 * Al extender MongoRepository hereda el CRUD completo
 * (save, findAll, findById, deleteById, count, ...) sin escribir consultas.
 */
@Repository
public interface EntrenadorRepositorio extends MongoRepository<Entrenador, Long> {

	/** Devuelve el documento con el id mas alto. */
	Optional<Entrenador> findTopByOrderByIdDesc();

	/**
	 * Calcula el siguiente id incremental: ultimo id + 1 (o 1 si esta vacia).
	 * MongoDB no tiene autoincremento nativo, por eso el id se genera aqui.
	 */
	default Long siguienteId() {
		return findTopByOrderByIdDesc()
				.map(entrenador -> entrenador.getId() + 1L)
				.orElse(1L);
	}
}
