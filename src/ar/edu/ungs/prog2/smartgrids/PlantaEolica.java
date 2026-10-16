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

	@Override
	protected boolean aceptaMonitoreo(Monitoreo monitoreo) {
		return monitoreo instanceof MonitoreoEolico;
	}

	public String reporteHorasSinVelocidadMinima() {
		boolean[] hayRegistros = new boolean[24];
		double[] maximos = new double[24];
		for (Monitoreo monitoreo : obtenerRegistros().values()) {
			int hora = monitoreo.getFechaHora().getHour();
			double velocidad = ((MonitoreoEolico) monitoreo).getVelocidadVientoPromedio();
			hayRegistros[hora] = true;
			maximos[hora] = Math.max(maximos[hora], velocidad);
		}
		StringBuilder resultado = new StringBuilder("- ").append(getNombre())
				.append(":  ").append(formatearVelocidad(velocidadMinimaViento)).append(" m/s");
		for (int hora = 0; hora < 24; hora++) {
			if (hayRegistros[hora] && maximos[hora] < velocidadMinimaViento) {
				// Se muestra el maximo observado: ni ese valor llego al minimo requerido.
				resultado.append("\n       ");
				if (hora < 10) {
					resultado.append('0');
				}
				resultado.append(hora).append(": ").append(formatearVelocidad(maximos[hora])).append(" m/s");
			}
		}
		return resultado.toString();
	}

	private String formatearVelocidad(double velocidad) {
		return java.math.BigDecimal.valueOf(velocidad).stripTrailingZeros().toPlainString();
	}

	//Metodos
	public double capacidadMinimaCuota() {
		
		return getCapacidadMaxima() * getEficienciaPromedio();
	}

	
	public boolean cumplioCuota(Monitoreo m) {
		
		return m != null && aceptaMonitoreo(m) && m.getEnergiaGenerada() >= capacidadMinimaCuota();
	}



	
	


	

}
