package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
	public PlantaEolica(int idPlanta, String nombre, String direccion, double capacidadMaxima,
			LocalDateTime fechaPuestaEnFuncionamiento, String fuentePrimaria, double eficienciaPromedio, int cantidadAerogeneradores, double alturaTorres, double velocidadMinimaViento) {
		
		super(idPlanta, nombre, direccion, capacidadMaxima, fechaPuestaEnFuncionamiento, fuentePrimaria, eficienciaPromedio);
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
		StringBuilder resultado = new StringBuilder("- ").append(getNombre())
				.append(":  ").append(formatearVelocidad(velocidadMinimaViento)).append(" m/s");
		for (Map.Entry<LocalTime, Double> entrada : velocidadesInsuficientesPorHora().entrySet()) {
			int hora = entrada.getKey().getHour();
			resultado.append("\n       ");
			if (hora < 10) {
				resultado.append('0');
			}
			resultado.append(hora).append(": ").append(formatearVelocidad(entrada.getValue())).append(" m/s");
		}
		return resultado.toString();
	}

	/**
	 * Devuelve las horas que nunca alcanzaron el viento mínimo, ordenadas de 00 a 23.
	 * Comparte el mismo cálculo con el informe de texto para que ambos resultados coincidan.
	 */
	public Set<LocalTime> horasSinVelocidadMinima() {
		return new LinkedHashSet<>(velocidadesInsuficientesPorHora().keySet());
	}

	/**
	 * Agrupa por hora los máximos de viento y conserva solo los menores al mínimo requerido.
	 * Las horas sin registros no se incluyen en el resultado.
	 */
	private Map<LocalTime, Double> velocidadesInsuficientesPorHora() {
		Map<LocalTime, Double> maximos = new TreeMap<>();
		for (Monitoreo monitoreo : obtenerRegistros().values()) {
			LocalTime hora = LocalTime.of(monitoreo.getFechaHora().getHour(), 0);
			double velocidad = ((MonitoreoEolico) monitoreo).getVelocidadVientoPromedio();
			maximos.put(hora, Math.max(maximos.getOrDefault(hora, 0.0), velocidad));
		}
		Map<LocalTime, Double> resultado = new TreeMap<>();
		for (Map.Entry<LocalTime, Double> entrada : maximos.entrySet()) {
			if (entrada.getValue() < velocidadMinimaViento) {
				resultado.put(entrada.getKey(), entrada.getValue());
			}
		}
		return resultado;
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
