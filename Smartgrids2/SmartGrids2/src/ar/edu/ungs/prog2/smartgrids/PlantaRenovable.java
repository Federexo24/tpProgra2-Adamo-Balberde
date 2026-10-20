package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDateTime;

public abstract class PlantaRenovable extends Planta {
	private String fuentePrimaria;
	private double eficienciaPromedio;



	public PlantaRenovable (String nombre,  int idPlanta, String direccion, double capacidadMaxima, LocalDateTime fechaPuestaEnFuncionamiento, String fuentePrimaria, double eficienciaPromedio) {
		super(nombre, idPlanta, direccion, capacidadMaxima, fechaPuestaEnFuncionamiento);
		this.fuentePrimaria=fuentePrimaria;
		this.eficienciaPromedio=eficienciaPromedio;
		
								}

	//Getters
	public String getFuentePrimaria () {
		return fuentePrimaria;
	}

	public double getEficienciaPromedio () {
		return eficienciaPromedio;
	}
	
	//Método
	// Planta Hidraulica y Planta Eólica comparten la forma de calcular la cuota mínima, para no repetir
	//hacemos la función acá y lo heredan ambas
	public double capacidadMinimaCuota() {
		return getCapacidadMaxima() * this.eficienciaPromedio;
	}
	
	
}
