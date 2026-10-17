package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDateTime;

/**
 * Representa una planta renovable hidráulica con turbinas y embalse.
 * Calcula su cuota a partir de la capacidad máxima y la eficiencia.
 */
public class PlantaHidraulica extends PlantaRenovable{
	private int cantidadTurbinas;
	private double capacidadEmbalse;
	/**
	 * Valida los datos heredados y exige cantidad de turbinas y capacidad de embalse positivas.
	 */
	public PlantaHidraulica(int idPlanta, String nombre, String direccion, double capacidadMaxima,
			LocalDateTime fechaPuestaEnFuncionamiento, String fuentePrimaria, double eficienciaPromedio,
			int cantidadTurbinas, double capacidadEmbalse) {
		
		super(idPlanta, nombre, direccion, capacidadMaxima, fechaPuestaEnFuncionamiento, fuentePrimaria, eficienciaPromedio);
		if (cantidadTurbinas <= 0) {
			throw new IllegalArgumentException("La cantidad de turbinas debe ser positiva");
		}
		if (!Double.isFinite(capacidadEmbalse) || capacidadEmbalse <= 0) {
			throw new IllegalArgumentException("La capacidad del embalse debe ser positiva y finita");
		}
		this.capacidadEmbalse= capacidadEmbalse;
		this.cantidadTurbinas= cantidadTurbinas;
	}
	
	//Getters
	/**
	 * Devuelve la cantidad de turbinas instaladas.
	 */
	public int getCantidadTurbinas() {
		return cantidadTurbinas;
	}
	
	/**
	 * Devuelve la capacidad declarada del embalse.
	 */
	public double getCapacidadEmbalse() {
		return capacidadEmbalse;
	}

	//Métodos
	/**
	 * Calcula la energía mínima como capacidad máxima multiplicada por eficiencia.
	 */
	public double capacidadMinimaCuota() {
		
		return getCapacidadMaxima() * getEficienciaPromedio();
	}

	/**
	 * Comprueba que el monitoreo sea básico y que su energía alcance o supere la cuota hidráulica.
	 */
	@Override
	public boolean cumplioCuota(Monitoreo m) {
		
		return m != null && aceptaMonitoreo(m) && m.getEnergiaGenerada() >= capacidadMinimaCuota();
	}

	
	
	
}
