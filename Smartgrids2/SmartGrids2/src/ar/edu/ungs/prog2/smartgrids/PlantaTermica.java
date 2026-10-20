package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDateTime;

public class PlantaTermica extends PlantaNoRenovable{

	public PlantaTermica(String nombre,int idPlanta ,String direccion, double capacidadMaxima,
			LocalDateTime fechaPuestaEnFuncionamiento, String tipoDeCombustible, double objetivoEmisionCo2Hora,
			double capacidadAlmacenamientoCombustible) {
		super(nombre, idPlanta ,direccion, capacidadMaxima, fechaPuestaEnFuncionamiento, tipoDeCombustible, objetivoEmisionCo2Hora,
				capacidadAlmacenamientoCombustible);
	}

	//Métodos
	// Se cumple la cuota cuando se consigue mas de la mitad de la capacidad maxima y el co2 que se genera en el
	//monitoreo es menos que el objetivo de emisión co2 por hora
	
	public double capacidadMinimaCuota() {
		return this.getCapacidadMaxima()/2.0;
	}

	public boolean cumplioCuota(Monitoreo m) {
		MonitoreoTermico mt= (MonitoreoTermico) m;
		return mt.getEnergiaGenerada()>= this.capacidadMinimaCuota()
				&& mt.getCo2Generado()< this.getObjetivoEmisionCo2Hora();}
	
}
