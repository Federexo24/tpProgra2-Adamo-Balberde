package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDateTime;

public class Monitoreo {
	private String nombrePlanta;
	private LocalDateTime fechaHora;
	private double energiaGenerada;
	private int cantidadIncidencias;
	
	public Monitoreo (String nombrePlanta, LocalDateTime fechaHora, double energiaGenerada, int cantidadIncidencias) {
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
	
	
}