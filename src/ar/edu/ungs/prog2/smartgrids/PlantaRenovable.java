package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDateTime;

/**
 * Base abstracta de las plantas solares, eólicas e hidráulicas.
 * Agrega la fuente primaria y la eficiencia promedio a los datos comunes de Planta.
 */
public abstract class PlantaRenovable extends Planta {
	private String fuentePrimaria;
	private double eficienciaPromedio;



	/**
	 * Valida los datos comunes mediante super y exige fuente no vacía y eficiencia entre 0 y 1.
	 */
	public PlantaRenovable (int idPlanta, String nombre, String direccion, double capacidadMaxima, LocalDateTime fechaPuestaEnFuncionamiento, String fuentePrimaria, double eficienciaPromedio) {
		super(idPlanta, nombre, direccion, capacidadMaxima, fechaPuestaEnFuncionamiento);
		if (fuentePrimaria == null || fuentePrimaria.isBlank()) {
			throw new IllegalArgumentException("La fuente primaria es obligatoria");
		}
		if (!Double.isFinite(eficienciaPromedio) || eficienciaPromedio < 0 || eficienciaPromedio > 1) {
			throw new IllegalArgumentException("La eficiencia debe estar entre 0 y 1");
		}
		this.eficienciaPromedio=eficienciaPromedio;
		this.fuentePrimaria=fuentePrimaria;
								}

	//Getters
	/**
	 * Devuelve la fuente de energía, usada para agrupar las plantas renovables.
	 */
	public String getFuentePrimaria () {
		return fuentePrimaria;
	}

	/**
	 * Devuelve la eficiencia como proporción: por ejemplo, 0.5 representa el 50 %.
	 */
	public double getEficienciaPromedio () {
		return eficienciaPromedio;
	}
}
