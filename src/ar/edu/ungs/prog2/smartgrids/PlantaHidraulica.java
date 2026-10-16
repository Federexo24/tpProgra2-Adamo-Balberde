package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDateTime;

public class PlantaHidraulica extends PlantaRenovable{
	private int cantidadTurbinas;
	private double capacidadEmbalse;
	public PlantaHidraulica(String nombre, String direccion, double capacidadMaxima,
			LocalDateTime fechaPuestaEnFuncionamiento, String fuentePrimaria, double eficienciaPromedio,
			int cantidadTurbinas, double capacidadEmbalse) {
		
		super(nombre, direccion, capacidadMaxima, fechaPuestaEnFuncionamiento, fuentePrimaria, eficienciaPromedio);
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
	public int getCantidadTurbinas() {
		return cantidadTurbinas;
	}
	
	public double getCapacidadEmbalse() {
		return capacidadEmbalse;
	}

	//Métodos
	public double capacidadMinimaCuota() {
		
		return getCapacidadMaxima() * getEficienciaPromedio();
	}

	@Override
	public boolean cumplioCuota(Monitoreo m) {
		
		return m != null && aceptaMonitoreo(m) && m.getEnergiaGenerada() >= capacidadMinimaCuota();
	}

	
	
	
}
