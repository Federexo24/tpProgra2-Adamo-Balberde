package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDateTime;

public class PlantaEolica extends PlantaRenovable{
	private int cantidadAerogeneradores;
	private double alturaTorres;
	private double velocidadMinimaViento;
	
	
	
	public PlantaEolica(String nombre, String direccion, double capacidadMaxima,
			LocalDateTime fechaPuestaEnFuncionamiento, String fuentePrimaria, double eficienciaPromedio, int cantidadAerogeneradores, double alturaTorres, double velocidadMinimaViento) {
		
		super(nombre, direccion, capacidadMaxima, fechaPuestaEnFuncionamiento, fuentePrimaria, eficienciaPromedio);
		if (cantidadAerogeneradores <= 0) {
			throw new IllegalArgumentException("La cantidad de aerogeneradores debe ser positiva");
		}
		if (!Double.isFinite(alturaTorres) || alturaTorres <= 0) {
			throw new IllegalArgumentException("La altura de las torres debe ser positiva y finita");
		}
		if (!Double.isFinite(velocidadMinimaViento) || velocidadMinimaViento < 0) {
			throw new IllegalArgumentException("La velocidad minima debe ser no negativa y finita");
		}
		this.alturaTorres= alturaTorres;
		this.cantidadAerogeneradores= cantidadAerogeneradores;
		this.velocidadMinimaViento= velocidadMinimaViento;
	}

	//Getters
	public int getCantidadAerogeneradores() {
		return cantidadAerogeneradores;
	}

	public double getAlturaTorres() {
		return alturaTorres;
	}

	public double getVelocidadMinimaViento() {
		return velocidadMinimaViento;
	}

	//Metodos
	public double capacidadMinimaCuota() {
		
		return 0;
	}

	
	public boolean cumplioCuota(Monitoreo m) {
		
		return false;
	}



	
	


	

}
