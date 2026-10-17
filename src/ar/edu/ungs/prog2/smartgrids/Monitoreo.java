package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDateTime;

/**
 * Guarda los datos de una medición horaria: id de planta, fecha y hora, energía e incidencias.
 * Sirve directamente para solares e hidráulicas y como base de los registros especializados.
 */
public class Monitoreo {
	private final int idPlanta;
	private LocalDateTime fechaHora;
	private double energiaGenerada;
	private int cantidadIncidencias;
	
	/**
	 * Crea una medición con id de planta positivo y fecha obligatoria, energía finita no negativa e incidencias no negativas.
	 */
	public Monitoreo (int idPlanta, LocalDateTime fechaHora, double energiaGenerada, int cantidadIncidencias) {
		if (idPlanta <= 0 || fechaHora == null) {
			throw new IllegalArgumentException("El id debe ser positivo y la fecha es obligatoria");
		}
		if (!Double.isFinite(energiaGenerada) || energiaGenerada < 0 || cantidadIncidencias < 0) {
			throw new IllegalArgumentException("La energia debe ser finita y los valores no negativos");
		}
		this.idPlanta= idPlanta;
		this.fechaHora= fechaHora;
		this.energiaGenerada= energiaGenerada;
		this.cantidadIncidencias= cantidadIncidencias;
										}
	
	//Getters 
	
	/**
	 * Devuelve el identificador de la planta a la que pertenece la medición.
	 */
	public int getIdPlanta() {
		return idPlanta;
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
