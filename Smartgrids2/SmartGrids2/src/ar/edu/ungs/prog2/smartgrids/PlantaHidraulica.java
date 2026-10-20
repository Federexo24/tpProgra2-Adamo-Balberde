package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDateTime;

public class PlantaHidraulica extends PlantaRenovable{
	private int cantidadTurbinas;
	private double capacidadEmbalse;
	public PlantaHidraulica(String nombre, int idPlanta, String direccion, double capacidadMaxima,
			LocalDateTime fechaPuestaEnFuncionamiento, String fuentePrimaria, double eficienciaPromedio,
			int cantidadTurbinas, double capacidadEmbalse) {
		
		super(nombre, idPlanta, direccion, capacidadMaxima, fechaPuestaEnFuncionamiento, fuentePrimaria, eficienciaPromedio);
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
	
	//Como ya heredo de Planta Renovable la funcion capacidad minima cuota solo tengo que controlar que la 
	//energia generada sea mayor o igual a esa cuota minima
	
	public boolean cumplioCuota(Monitoreo m) {
		
		return m.getEnergiaGenerada()>= this.capacidadMinimaCuota();
	}

	
	
	
}
