package com.futbol.gestion.entidades;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Entrenador de un club de futbol.
 *
 * Se guarda como documento en la coleccion "entrenadores".
 * Relacion: un Club tiene exactamente un Entrenador (@OneToOne en JPA).
 *
 * En el modelo relacional del taller el propietario de la relacion es el Club,
 * porque el Entrenador no tiene razon de existir por si solo. Aqui el Club
 * guarda la referencia hacia el Entrenador.
 */
@Document(collection = "entrenadores")
public class Entrenador {

	/** Id incremental del documento (se envia como "_id" a MongoDB). */
	@Id
	private Long id;

	/** Nombre del entrenador. */
	private String nombre;

	/** Apellido del entrenador. */
	private String apellido;

	/** Edad en anos. */
	private int edad;

	/** Nacionalidad del entrenador. */
	private String nacionalidad;

	/** Constructor vacio: obligatorio para que Spring Data construya el objeto. */
	public Entrenador() {
	}

	/** Constructor de conveniencia sin el id. */
	public Entrenador(String nombre, String apellido, int edad, String nacionalidad) {
		this.nombre = nombre;
		this.apellido = apellido;
		this.edad = edad;
		this.nacionalidad = nacionalidad;
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

	public int getEdad() {
		return edad;
	}

	public void setEdad(int edad) {
		this.edad = edad;
	}

	public String getNacionalidad() {
		return nacionalidad;
	}

	public void setNacionalidad(String nacionalidad) {
		this.nacionalidad = nacionalidad;
	}

	@Override
	public String toString() {
		return "Entrenador{id=" + id + ", nombre='" + nombre + "', apellido='" + apellido
				+ "', edad=" + edad + ", nacionalidad='" + nacionalidad + "'}";
	}
}
