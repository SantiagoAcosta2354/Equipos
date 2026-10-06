package com.futbol.gestion.entidades;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Competicion en la que participan los clubes.
 *
 * Se guarda como documento en la coleccion "competiciones".
 * Relacion: un Club participa en muchas Competiciones y una Competicion tiene
 * muchos Clubes participantes (@ManyToMany en JPA).
 *
 * En JPA esta relacion crearia una tabla intermedia. Aqui el Club guarda una
 * lista de referencias a las competiciones (relacion unidireccional, tal como
 * indica el taller).
 */
@Document(collection = "competiciones")
public class Competicion {

	/** Id incremental del documento (se envia como "_id" a MongoDB). */
	@Id
	private Long id;

	/** Nombre de la competicion (por ejemplo, Copa Libertadores). */
	private String nombre;

	/** Monto del premio en pesos. */
	private int montoPremio;

	/** Fecha en que inicia la competicion. */
	private LocalDate fechaInicio;

	/** Fecha en que termina la competicion. */
	private LocalDate fechaFin;

	/** Constructor vacio: obligatorio para que Spring Data construya el objeto. */
	public Competicion() {
	}

	/** Constructor de conveniencia sin el id. */
	public Competicion(String nombre, int montoPremio, LocalDate fechaInicio, LocalDate fechaFin) {
		this.nombre = nombre;
		this.montoPremio = montoPremio;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
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

	public int getMontoPremio() {
		return montoPremio;
	}

	public void setMontoPremio(int montoPremio) {
		this.montoPremio = montoPremio;
	}

	public LocalDate getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(LocalDate fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public LocalDate getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(LocalDate fechaFin) {
		this.fechaFin = fechaFin;
	}

	@Override
	public String toString() {
		return "Competicion{id=" + id + ", nombre='" + nombre + "', montoPremio=" + montoPremio
				+ ", fechaInicio=" + fechaInicio + ", fechaFin=" + fechaFin + "}";
	}
}
