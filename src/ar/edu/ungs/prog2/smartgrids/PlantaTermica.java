package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDateTime;

/**
 * Representa una planta no renovable cuya cuota exige energía suficiente y emisiones bajas.
 * Necesita monitoreos térmicos para consultar el CO2 emitido.
 */
public class PlantaTermica extends PlantaNoRenovable{

	/**
	 * Construye la planta térmica delegando en super las validaciones comunes y del combustible.
	 */
	public PlantaTermica(String nombre, String direccion, double capacidadMaxima,
			LocalDateTime fechaPuestaEnFuncionamiento, String tipoDeCombustible, double objetivoEmisionCo2Hora,
			double capacidadAlmacenamientoCombustible) {
		super(nombre, direccion, capacidadMaxima, fechaPuestaEnFuncionamiento, tipoDeCombustible, objetivoEmisionCo2Hora,
				capacidadAlmacenamientoCombustible);
	}

	/**
	 * Exige un MonitoreoTermico con datos de CO2 y combustible utilizado.
	 */
	@Override
	protected boolean aceptaMonitoreo(Monitoreo monitoreo) {
		return monitoreo instanceof MonitoreoTermico;
	}

	//Métodos
	/**
	 * Devuelve la mitad de la capacidad máxima como mínimo de energía por hora.
	 */
	public double capacidadMinimaCuota() {
		
		return getCapacidadMaxima() / 2.0;
	}

	/**
	 * Exige un monitoreo térmico con energía igual o mayor a la cuota y CO2 estrictamente menor al objetivo.
	 * Emitir exactamente el objetivo de CO2 no cumple.
	 */
	public boolean cumplioCuota(Monitoreo m) {
		
		return m instanceof MonitoreoTermico && m.getEnergiaGenerada() >= capacidadMinimaCuota()
				&& ((MonitoreoTermico) m).getCo2Generado() < getObjetivoEmisionCo2Hora();
	}
	
}
