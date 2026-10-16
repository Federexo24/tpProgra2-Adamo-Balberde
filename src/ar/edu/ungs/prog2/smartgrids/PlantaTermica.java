package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDateTime;

public class PlantaTermica extends PlantaNoRenovable{

	public PlantaTermica(String nombre, String direccion, double capacidadMaxima,
			LocalDateTime fechaPuestaEnFuncionamiento, String tipoDeCombustible, double objetivoEmisionCo2Hora,
			double capacidadAlmacenamientoCombustible) {
		super(nombre, direccion, capacidadMaxima, fechaPuestaEnFuncionamiento, tipoDeCombustible, objetivoEmisionCo2Hora,
				capacidadAlmacenamientoCombustible);
	}

	@Override
	protected boolean aceptaMonitoreo(Monitoreo monitoreo) {
		return monitoreo instanceof MonitoreoTermico;
	}

	//Métodos
	public double capacidadMinimaCuota() {
		
		return getCapacidadMaxima() / 2.0;
	}

	public boolean cumplioCuota(Monitoreo m) {
		
		return m instanceof MonitoreoTermico && m.getEnergiaGenerada() >= capacidadMinimaCuota()
				&& ((MonitoreoTermico) m).getCo2Generado() < getObjetivoEmisionCo2Hora();
	}
	
}
