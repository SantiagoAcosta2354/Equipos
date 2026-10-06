package com.futbol.gestion.repositorios;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.futbol.gestion.entidades.Club;

/**
 * Repositorio de clubes en MongoDB.
 *
 * Hereda el CRUD completo de MongoRepository. El id incremental se calcula
 * con siguienteId() porque MongoDB no tiene autoincremento nativo.
 *
 * Las relaciones del Club (@DocumentReference a Entrenador, Jugador,
 * Asociacion y Competicion) las resuelve Spring Data automaticamente: en el
 * documento solo se guardan los ids, pero al leer un club se devuelven los
 * objetos completos.
 */
@Repository
public interface ClubRepositorio extends MongoRepository<Club, Long> {

	/** Devuelve el documento con el id mas alto. */
	Optional<Club> findTopByOrderByIdDesc();

	/** Calcula el siguiente id incremental (ultimo id + 1, o 1 si esta vacia). */
	default Long siguienteId() {
		return findTopByOrderByIdDesc()
				.map(club -> club.getId() + 1L)
				.orElse(1L);
	}
}
