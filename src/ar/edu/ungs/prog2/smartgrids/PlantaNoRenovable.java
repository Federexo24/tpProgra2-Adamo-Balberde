package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDateTime;

/**
 * Base abstracta de las plantas que utilizan combustible.
 * Guarda el tipo de combustible, el objetivo horario de CO2 y la capacidad de almacenamiento.
 */
public abstract class PlantaNoRenovable extends Planta {
	private String tipoDeCombustible;
	private double objetivoEmisionCo2Hora;
	private double capacidadAlmacenamientoCombustible;
	
	/**
	 * Valida los datos de Planta y los atributos del combustible.
	 * Exige un objetivo de emisiones no negativo y una capacidad de almacenamiento positiva y finita.
	 */
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
	/**
	 * Devuelve el combustible declarado para la planta.
	 */
	public String getTipoDeCombustible() {
		return tipoDeCombustible;
	}

	/**
	 * Devuelve el umbral de CO2 por hora; la planta térmica debe emitir estrictamente menos para cumplir.
	 */
	public double getObjetivoEmisionCo2Hora() {
		return objetivoEmisionCo2Hora;
	}

	/**
	 * Devuelve la capacidad declarada de almacenamiento, no la cantidad de combustible disponible.
	 */
	public double getCapacidadAlmacenamientoCombustible() {
		return capacidadAlmacenamientoCombustible;
	}

	

}
