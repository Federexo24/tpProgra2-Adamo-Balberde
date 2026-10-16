package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDateTime;

public abstract class PlantaNoRenovable extends Planta {
	private String tipoDeCombustible;
	private double objetivoEmisionCo2Hora;
	private double capacidadAlmacenamientoCombustible;
	
	public PlantaNoRenovable(String nombre, String direccion, double capacidadMaxima,
			LocalDateTime fechaPuestaEnFuncionamiento, String tipoDeCombustible, double objetivoEmisionCo2Hora, double capacidadAlmacenamientoCombustible) {
		
		super(nombre, direccion, capacidadMaxima, fechaPuestaEnFuncionamiento);
		if (tipoDeCombustible == null || tipoDeCombustible.isBlank()) {
			throw new IllegalArgumentException("El tipo de combustible es obligatorio");
		}
		if (!Double.isFinite(objetivoEmisionCo2Hora) || objetivoEmisionCo2Hora < 0) {
			throw new IllegalArgumentException("El objetivo de emisiones debe ser no negativo y finito");
		}
		if (!Double.isFinite(capacidadAlmacenamientoCombustible) || capacidadAlmacenamientoCombustible <= 0) {
			throw new IllegalArgumentException("La capacidad de combustible debe ser positiva y finita");
		}
		this.capacidadAlmacenamientoCombustible= capacidadAlmacenamientoCombustible;
		this.objetivoEmisionCo2Hora= objetivoEmisionCo2Hora;
		this.tipoDeCombustible= tipoDeCombustible;
	}

	//Getters
	public String getTipoDeCombustible() {
		return tipoDeCombustible;
	}

	public double getObjetivoEmisionCo2Hora() {
		return objetivoEmisionCo2Hora;
	}

	public double getCapacidadAlmacenamientoCombustible() {
		return capacidadAlmacenamientoCombustible;
	}

	

}
