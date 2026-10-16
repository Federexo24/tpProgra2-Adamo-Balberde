package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDateTime;

/**
 * Guarda los datos de una medición horaria: planta, fecha y hora, energía e incidencias.
 * Sirve directamente para solares e hidráulicas y como base de los registros especializados.
 */
public class Monitoreo {
	private String nombrePlanta;
	private LocalDateTime fechaHora;
	private double energiaGenerada;
	private int cantidadIncidencias;
	
	/**
	 * Crea una medición con nombre y fecha obligatorios, energía finita no negativa e incidencias no negativas.
	 */
	public Monitoreo (String nombrePlanta, LocalDateTime fechaHora, double energiaGenerada, int cantidadIncidencias) {
		if (nombrePlanta == null || nombrePlanta.isBlank() || fechaHora == null) {
			throw new IllegalArgumentException("El nombre y la fecha son obligatorios");
		}
		if (!Double.isFinite(energiaGenerada) || energiaGenerada < 0 || cantidadIncidencias < 0) {
			throw new IllegalArgumentException("La energia debe ser finita y los valores no negativos");
		}
		this.nombrePlanta= nombrePlanta;
		this.fechaHora= fechaHora;
		this.energiaGenerada= energiaGenerada;
		this.cantidadIncidencias= cantidadIncidencias;
										}
	
	//Getters 
	
	/**
	 * Devuelve el nombre de la planta a la que pertenece la medición.
	 */
	public String getNombrePlanta() {
		return nombrePlanta;
	}
	
	/**
	 * Devuelve la fecha y hora originales de la medición, incluidos sus minutos y segundos.
	 */
	public LocalDateTime getFechaHora () {
		return fechaHora;
	}
	
	/**
	 * Devuelve la energía informada en este monitoreo.
	 */
	public double getEnergiaGenerada () {
		return energiaGenerada;
	}
	
	/**
	 * Devuelve el número de incidencias informadas en este monitoreo.
	 */
	public int getCantidadIncidencias () {
		return cantidadIncidencias;
	}

	/**
	 * Muestra fecha, energía e incidencias para facilitar la lectura del estado del sistema.
	 */
	@Override
	public String toString() {
		return fechaHora + ": energia=" + energiaGenerada + ", incidencias=" + cantidadIncidencias;
	}
	
	
}
