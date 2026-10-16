package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDateTime;

/**
 * Extiende Monitoreo con el CO2 emitido y el combustible utilizado durante la hora.
 */
public class MonitoreoTermico extends Monitoreo{
	private double co2Generado;
	private double combustibleUtilizado;

	
	/**
	 * Valida los datos comunes y exige CO2 y combustible finitos y no negativos.
	 */
	public MonitoreoTermico (String nombrePlanta, LocalDateTime fechaHora, double energiaGenerada,
			int cantidadIncidencias, double co2Generado, double combustibleUtilizado) {
			
			super (nombrePlanta, fechaHora, energiaGenerada, cantidadIncidencias);
			if (!Double.isFinite(co2Generado) || co2Generado < 0
					|| !Double.isFinite(combustibleUtilizado) || combustibleUtilizado < 0) {
				throw new IllegalArgumentException("El CO2 y el combustible deben ser finitos y no negativos");
			}
			this.co2Generado=co2Generado;
			this.combustibleUtilizado= combustibleUtilizado;
			
	}
	
	
	//Getters
	
	/**
	 * Devuelve el CO2 emitido que se compara con el objetivo horario de la planta.
	 */
	public double getCo2Generado() {
		return co2Generado;
	}
	
	/**
	 * Devuelve el combustible consumido según el registro.
	 */
	public double getCombustibleUtilizado() {
		return combustibleUtilizado;
	}

	/**
	 * Agrega CO2 y combustible a la descripción heredada del monitoreo.
	 */
	@Override
	public String toString() {
		return super.toString() + ", CO2=" + co2Generado + ", combustible=" + combustibleUtilizado;
	}
}
