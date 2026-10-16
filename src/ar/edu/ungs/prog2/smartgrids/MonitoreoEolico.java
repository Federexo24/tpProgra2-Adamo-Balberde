package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDateTime;

/**
 * Extiende Monitoreo con la velocidad promedio del viento de la hora registrada.
 */
public class MonitoreoEolico extends Monitoreo{
	private double velocidadVientoPromedio;

	
	
	/**
	 * Valida los datos comunes con super y exige una velocidad finita y no negativa.
	 */
	public MonitoreoEolico (String nombrePlanta, LocalDateTime fechaHora, double energiaGenerada, int cantidadIncidencias, double velocidadVientoPromedio) {
		super(nombrePlanta, fechaHora, energiaGenerada, cantidadIncidencias);
		if (!Double.isFinite(velocidadVientoPromedio) || velocidadVientoPromedio < 0) {
			throw new IllegalArgumentException("La velocidad debe ser finita y no negativa");
		}
		this.velocidadVientoPromedio= velocidadVientoPromedio;
	}
	
	
	//Getters
	/**
	 * Devuelve la velocidad promedio del viento de este registro.
	 */
	public double getVelocidadVientoPromedio() {
		return velocidadVientoPromedio;
	}

	/**
	 * Agrega la velocidad del viento en m/s a la descripción heredada de Monitoreo.
	 */
	@Override
	public String toString() {
		return super.toString() + ", viento=" + velocidadVientoPromedio + " m/s";
	}
}
