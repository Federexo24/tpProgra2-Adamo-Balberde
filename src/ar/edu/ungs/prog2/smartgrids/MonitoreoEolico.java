package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDateTime;

public class MonitoreoEolico extends Monitoreo{
	private double velocidadVientoPromedio;

	
	
	public MonitoreoEolico (String nombrePlanta, LocalDateTime fechaHora, double energiaGenerada, int cantidadIncidencias, double velocidadVientoPromedio) {
		super(nombrePlanta, fechaHora, energiaGenerada, cantidadIncidencias);
		if (!Double.isFinite(velocidadVientoPromedio) || velocidadVientoPromedio < 0) {
			throw new IllegalArgumentException("La velocidad debe ser finita y no negativa");
		}
		this.velocidadVientoPromedio= velocidadVientoPromedio;
	}
	
	
	//Getters
	public double getVelocidadVientoPromedio() {
		return velocidadVientoPromedio;
	}

	@Override
	public String toString() {
		return super.toString() + ", viento=" + velocidadVientoPromedio + " m/s";
	}
}
