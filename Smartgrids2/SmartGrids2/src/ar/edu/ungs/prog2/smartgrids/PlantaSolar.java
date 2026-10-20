package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDateTime;

public class PlantaSolar extends PlantaRenovable {
	private	double superficieCubierta;
	private int cantidadPaneles;
	private double horasRadiacionDiaria;

	public PlantaSolar(String nombre, int idPlanta, String direccion, double capacidadMaxima,
			LocalDateTime fechaPuestaEnFuncionamiento, String fuentePrimaria, double eficienciaPromedio, double superficieCubierta, int cantidadPaneles, double horasRadiacionDiaria) {
		super(nombre, idPlanta, direccion, capacidadMaxima, fechaPuestaEnFuncionamiento, fuentePrimaria, eficienciaPromedio);
		
		if (superficieCubierta<=0 || cantidadPaneles<=0 || horasRadiacionDiaria<0 || horasRadiacionDiaria>24) {
			throw new IllegalArgumentException("Datos de la planta solar inválidos");
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
	//Calculo capacidad minima segun lo especificado en el PDF, en este se calcula a partir de la capacidad maxima
	//la eficiencia promedio y las horas de radiación diaria.
	//Como ya heredó de planta renovable cuota minima (capacidad maxima * eficiencia promedio) solo tengo que
	//multiplicar eso por radiacion diaria
	public double capacidadMinimaCuota() {
		return super.capacidadMinimaCuota() * (this.horasRadiacionDiaria /24.0);
	}

	// Una vez calculada la capacidad mínima, calculamos si la energía que generó el monitoreo que ingresamos
	// cumplió con esta capacidad minima calculada
	public boolean cumplioCuota(Monitoreo m) {
		if (m==null) {
			return false;
					}
		return m.getEnergiaGenerada()>= this.capacidadMinimaCuota();
	}

}
