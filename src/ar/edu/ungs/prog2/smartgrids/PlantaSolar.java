package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDateTime;

/**
 * Representa una planta renovable que genera energía mediante paneles solares.
 * Su cuota depende de la capacidad, la eficiencia y la proporción diaria de horas de radiación.
 */
public class PlantaSolar extends PlantaRenovable {
	private	double superficieCubierta;
	private int cantidadPaneles;
	private double horasRadiacionDiaria;

	/**
	 * Valida los datos heredados y exige superficie y paneles positivos, y radiación entre 0 y 24 horas.
	 */
	public PlantaSolar(String nombre, String direccion, double capacidadMaxima,
			LocalDateTime fechaPuestaEnFuncionamiento, String fuentePrimaria, double eficienciaPromedio, double superficieCubierta, int cantidadPaneles, double horasRadiacionDiaria) {
		super(nombre, direccion, capacidadMaxima, fechaPuestaEnFuncionamiento, fuentePrimaria, eficienciaPromedio);
		if (!Double.isFinite(superficieCubierta) || superficieCubierta <= 0) {
			throw new IllegalArgumentException("La superficie debe ser positiva y finita");
		}
		if (cantidadPaneles <= 0) {
			throw new IllegalArgumentException("La cantidad de paneles debe ser positiva");
		}
		if (!Double.isFinite(horasRadiacionDiaria) || horasRadiacionDiaria < 0 || horasRadiacionDiaria > 24) {
			throw new IllegalArgumentException("Las horas de radiacion deben estar entre 0 y 24");
		}
		this.cantidadPaneles= cantidadPaneles;
		this.horasRadiacionDiaria= horasRadiacionDiaria;
		this.superficieCubierta= superficieCubierta;
	}

	
	//Getters
	/**
	 * Devuelve la superficie cubierta por los paneles.
	 */
	public double getSuperficieCubierta() {
		return superficieCubierta;
	}

	/**
	 * Devuelve la cantidad de paneles instalados.
	 */
	public int getCantidadPaneles() {
		return cantidadPaneles;
	}

	/**
	 * Devuelve las horas diarias de radiación usadas para calcular la cuota.
	 */
	public double getHorasRadiacionDiaria() {
		return horasRadiacionDiaria;
	}

	//Metodos
	/**
	 * Calcula capacidad máxima por eficiencia por horas de radiación divididas por 24.
	 * La división usa decimales para conservar la proporción de horas.
	 */
	public double capacidadMinimaCuota() {
		
		return getCapacidadMaxima() * getEficienciaPromedio() * (horasRadiacionDiaria / 24.0);
	}

	
	/**
	 * Comprueba que sea un monitoreo básico no nulo y que la energía alcance o supere la cuota solar.
	 */
	public boolean cumplioCuota(Monitoreo m) {
		
		return m != null && aceptaMonitoreo(m) && m.getEnergiaGenerada() >= capacidadMinimaCuota();
	}

}
