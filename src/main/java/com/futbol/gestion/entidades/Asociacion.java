package com.futbol.gestion.entidades;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Asociacion o federacion a la que esta afiliado un club.
 *
 * Se guarda como documento en la coleccion "asociaciones".
 * Relacion: muchos Club pertenecen a una Asociacion (@ManyToOne en JPA),
 * y una Asociacion agrupa muchos clubes.
 *
 * En JPA esta relacion crea el campo "asociacion_id" en la tabla "clubes"
 * haciendo referencia a "asociaciones". Aqui es una referencia de documento.
 */
@Document(collection = "asociaciones")
public class Asociacion {

	/** Id incremental del documento (se envia como "_id" a MongoDB). */
	@Id
	private Long id;

	/** Nombre de la asociacion (por ejemplo, Federacion Colombiana de Futbol). */
	private String nombre;

	/** Pais donde opera la asociacion. */
	private String pais;

	/** Nombre del presidente de la asociacion. */
	private String presidente;

	/** Constructor vacio: obligatorio para que Spring Data construya el objeto. */
	public Asociacion() {
	}

	/** Constructor de conveniencia sin el id. */
	public Asociacion(String nombre, String pais, String presidente) {
		this.nombre = nombre;
		this.pais = pais;
		this.presidente = presidente;
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

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	public String getPresidente() {
		return presidente;
	}

	public void setPresidente(String presidente) {
		this.presidente = presidente;
	}

	@Override
	public String toString() {
		return "Asociacion{id=" + id + ", nombre='" + nombre + "', pais='" + pais
				+ "', presidente='" + presidente + "'}";
	}
}
