package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDateTime;

public abstract class PlantaRenovable extends Planta {
	private String fuentePrimaria;
	private double eficienciaPromedio;



	public PlantaRenovable (String nombre, String direccion, double capacidadMaxima, LocalDateTime fechaPuestaEnFuncionamiento, String fuentePrimaria, double eficienciaPromedio) {
		super(nombre, direccion, capacidadMaxima, fechaPuestaEnFuncionamiento);
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
