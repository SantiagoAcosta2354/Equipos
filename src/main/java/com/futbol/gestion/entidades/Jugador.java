package com.futbol.gestion.entidades;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Jugador perteneciente al plantel de un club.
 *
 * Se guarda como documento en la coleccion "jugadores".
 * Relacion: un Club tiene muchos Jugadores (@OneToMany en JPA), y cada
 * Jugador pertenece a un solo club.
 *
 * En JPA, para evitar la tabla intermedia "clubes_jugadores" habria que usar
 * @JoinColumn(name = "id_club"). Aqui el Club guarda la lista de referencias.
 */
@Document(collection = "jugadores")
public class Jugador {

	/** Id incremental del documento (se envia como "_id" a MongoDB). */
	@Id
	private Long id;

	/** Nombre del jugador. */
	private String nombre;

	/** Apellido del jugador. */
	private String apellido;

	/** Numero de camiseta. */
	private int numero;

	/** Posicion en la cancha (portero, defensa, mediocampista, delantero). */
	private String posicion;

	/** Constructor vacio: obligatorio para que Spring Data construya el objeto. */
	public Jugador() {
	}

	/** Constructor de conveniencia sin el id. */
	public Jugador(String nombre, String apellido, int numero, String posicion) {
		this.nombre = nombre;
		this.apellido = apellido;
		this.numero = numero;
		this.posicion = posicion;
	}

	// ── Getters y Setters ───────────────────────────────────────

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public String getPosicion() {
		return posicion;
	}

	public void setPosicion(String posicion) {
		this.posicion = posicion;
	}

	@Override
	public String toString() {
		return "Jugador{id=" + id + ", nombre='" + nombre + "', apellido='" + apellido
				+ "', numero=" + numero + ", posicion='" + posicion + "'}";
	}
}
