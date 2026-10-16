package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDateTime;

public class PlantaTermica extends PlantaNoRenovable{

	public PlantaTermica(String nombre, String direccion, double capacidadMaxima,
			LocalDateTime fechaPuestaEnFuncionamiento, String tipoDeCombustible, double objetivoEmisionCo2Hora,
			double capacidadAlmacenamientoCombustible) {
		super(nombre, direccion, capacidadMaxima, fechaPuestaEnFuncionamiento, tipoDeCombustible, objetivoEmisionCo2Hora,
				capacidadAlmacenamientoCombustible);
	}

	//Métodos
	public double capacidadMinimaCuota() {
		
		return 0;
	}

	public boolean cumplioCuota(Monitoreo m) {
		
		return false;
	}
	
}
