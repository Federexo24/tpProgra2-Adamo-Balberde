package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDateTime;

public abstract class PlantaRenovable extends Planta {
	private String fuentePrimaria;
	private double eficienciaPromedio;



	public PlantaRenovable (String nombre, String direccion, double capacidadMaxima, LocalDateTime fechaPuestaEnFuncionamiento, String fuentePrimaria, double eficienciaPromedio) {
		super(nombre, direccion, capacidadMaxima, fechaPuestaEnFuncionamiento);
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
	public String getFuentePrimaria () {
		return fuentePrimaria;
	}

	public double getEficienciaPromedio () {
		return eficienciaPromedio;
	}
}
