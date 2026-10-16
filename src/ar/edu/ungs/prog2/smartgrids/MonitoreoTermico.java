package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDateTime;

public class MonitoreoTermico extends Monitoreo{
	private double co2Generado;
	private double combustibleUtilizado;

	
	public MonitoreoTermico (String nombrePlanta, LocalDateTime fechaHora, double energiaGenerada,
			int cantidadIncidencias, double co2Generado, double combustibleUtilizado) {
			
			super (nombrePlanta, fechaHora, energiaGenerada, cantidadIncidencias);
			this.co2Generado=co2Generado;
			this.combustibleUtilizado= combustibleUtilizado;
			
	}
	
	
	//Getters
	
	public double getCo2Generado() {
		return co2Generado;
	}
	
	public double getCombustibleUtilizado() {
		return combustibleUtilizado;
	}
}
