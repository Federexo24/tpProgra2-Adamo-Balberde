package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDateTime;

public class MonitoreoEolico extends Monitoreo{
	private double velocidadVientoPromedio;

	
	
	public MonitoreoEolico (int idPlanta, LocalDateTime fechaHora, double energiaGenerada, int cantidadIncidencias, double velocidadVientoPromedio) {
		
		super(idPlanta, fechaHora, energiaGenerada, cantidadIncidencias);
		this.velocidadVientoPromedio= velocidadVientoPromedio;
	}
	
	
	//Getters
	public double getVelocidadVientoPromedio() {
		return velocidadVientoPromedio;
	}
}
