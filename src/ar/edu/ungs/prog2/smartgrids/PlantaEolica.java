package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDateTime;

/**
 * Representa una planta renovable impulsada por viento.
 * Usa monitoreos eólicos para consultar las velocidades observadas en cada hora.
 */
public class PlantaEolica extends PlantaRenovable{
	private int cantidadAerogeneradores;
	private double alturaTorres;
	private double velocidadMinimaViento;
	
	
	
	/**
	 * Valida los datos heredados, la cantidad de aerogeneradores, la altura y la velocidad mínima de viento.
	 */
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
	/**
	 * Devuelve cuántos aerogeneradores tiene la planta.
	 */
	public int getCantidadAerogeneradores() {
		return cantidadAerogeneradores;
	}

	/**
	 * Devuelve la altura declarada de las torres.
	 */
	public double getAlturaTorres() {
		return alturaTorres;
	}

	/**
	 * Devuelve la velocidad mínima de viento usada como referencia en el informe.
	 */
	public double getVelocidadMinimaViento() {
		return velocidadMinimaViento;
	}

	/**
	 * Exige un MonitoreoEolico para que cada registro incluya la velocidad del viento.
	 */
	@Override
	protected boolean aceptaMonitoreo(Monitoreo monitoreo) {
		return monitoreo instanceof MonitoreoEolico;
	}

	/**
	 * Agrupa todos los días por hora y obtiene el máximo viento observado en cada una.
	 * Informa solo horas con registros cuyo máximo es menor al mínimo requerido; así ninguna medición alcanzó el umbral.
	 * Muestra ese máximo como valor representativo de cada hora.
	 */
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

	/**
	 * Convierte la velocidad a texto sin ceros decimales sobrantes ni notación científica.
	 */
	private String formatearVelocidad(double velocidad) {
		return java.math.BigDecimal.valueOf(velocidad).stripTrailingZeros().toPlainString();
	}

	//Metodos
	/**
	 * Calcula la cuota energética como capacidad máxima multiplicada por eficiencia.
	 */
	public double capacidadMinimaCuota() {
		
		return getCapacidadMaxima() * getEficienciaPromedio();
	}

	
	/**
	 * Comprueba el tipo eólico y si la energía alcanza la cuota.
	 * En esta implementación el viento se analiza por separado en el informe de velocidades.
	 */
	public boolean cumplioCuota(Monitoreo m) {
		
		return m != null && aceptaMonitoreo(m) && m.getEnergiaGenerada() >= capacidadMinimaCuota();
	}



	
	


	

}
