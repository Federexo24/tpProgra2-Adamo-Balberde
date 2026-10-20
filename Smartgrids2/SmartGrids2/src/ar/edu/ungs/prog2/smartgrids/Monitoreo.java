package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDateTime;
import java.util.Objects;

public class Monitoreo {
	private int idPlanta;
	private LocalDateTime fechaHora;
	private double energiaGenerada;
	private int cantidadIncidencias;
	
	public Monitoreo (int idPlanta, LocalDateTime fechaHora, double energiaGenerada, int cantidadIncidencias) {
		this.idPlanta= idPlanta;
		this.fechaHora= fechaHora;
		this.energiaGenerada= energiaGenerada;
		this.cantidadIncidencias= cantidadIncidencias;
										}
	
	//Getters 
	
	public int getIdPlanta() {
		return idPlanta;
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

	//Método
	//Agregamos método sinRegistro para luego cuando tengamos que eliminar los monitoreos que tienen energiaGenerada en 0
	//y incidencias en 0 sea más fácil identificarlos
	
	public boolean sinDatos () {
		return this.cantidadIncidencias==0 && this.energiaGenerada==0.0;
	}
	
	
	
	//HashCode y Equals
	//Hacemos hashCode y Equals solo de fechaHora e idPlanta ya que no pueden existir dos monitoreos de una planta
	// a la misma hora con valores distintos, solo puede existir un monitoreo por hora de una misma planta
	@Override
	public int hashCode() {
		return Objects.hash(fechaHora, idPlanta);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Monitoreo other = (Monitoreo) obj;
		return Objects.equals(fechaHora, other.fechaHora) && idPlanta == other.idPlanta;
	}
	
	
}