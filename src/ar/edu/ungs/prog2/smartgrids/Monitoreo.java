package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDateTime;

public class Monitoreo {
	private String nombrePlanta;
	private LocalDateTime fechaHora;
	private double energiaGenerada;
	private int cantidadIncidencias;
	
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
	
	public String getNombrePlanta() {
		return nombrePlanta;
	}
	
	public LocalDateTime getFechaHora () {
		return fechaHora;
	}
	
	public double getEnergiaGenerada () {
		return energiaGenerada;
	}
	
	public int getCantidadIncidencias () {
		return cantidadIncidencias;
	}

	@Override
	public String toString() {
		return fechaHora + ": energia=" + energiaGenerada + ", incidencias=" + cantidadIncidencias;
	}
	
	
}
