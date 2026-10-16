package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDateTime;

public class PlantaSolar extends PlantaRenovable {
	private	double superficieCubierta;
	private int cantidadPaneles;
	private double horasRadiacionDiaria;

	public PlantaSolar(String nombre, String direccion, double capacidadMaxima,
			LocalDateTime fechaPuestaEnFuncionamiento, String fuentePrimaria, double eficienciaPromedio, double superficieCubierta, int cantidadPaneles, double horasRadiacionDiaria) {
		super(nombre, direccion, capacidadMaxima, fechaPuestaEnFuncionamiento, fuentePrimaria, eficienciaPromedio);
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
