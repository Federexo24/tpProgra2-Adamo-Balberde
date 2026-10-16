package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDateTime;

public class MonitoreoEolico extends Monitoreo{
	private double velocidadVientoPromedio;

	
	
	public MonitoreoEolico (String nombrePlanta, LocalDateTime fechaHora, double energiaGenerada, int cantidadIncidencias, double velocidadVientoPromedio) {
		//REPASAR ESTO
		super(nombrePlanta, fechaHora, energiaGenerada, cantidadIncidencias);
		this.velocidadVientoPromedio= velocidadVientoPromedio;
	}
	
	
	//Getters
	public double getVelocidadVientoPromedio() {
		return velocidadVientoPromedio;
	}
}
