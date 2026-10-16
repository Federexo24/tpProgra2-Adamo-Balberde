package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDateTime;

public class PlantaSolar extends PlantaRenovable {
	private	double superficieCubierta;
	private int cantidadPaneles;
	private double horasRadiacionDiaria;

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
	public double getSuperficieCubierta() {
		return superficieCubierta;
	}

	public int getCantidadPaneles() {
		return cantidadPaneles;
	}

	public double getHorasRadiacionDiaria() {
		return horasRadiacionDiaria;
	}

	//Metodos
	public double capacidadMinimaCuota() {
		
		return 0;
	}

	
	public boolean cumplioCuota(Monitoreo m) {
		
		return false;
	}

}
