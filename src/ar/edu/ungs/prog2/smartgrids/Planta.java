package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

public abstract class Planta {
	private String nombre; 
	private String direccion;
	private double capacidadMaxima;
	private boolean estaActiva;
	private double totalGenerado;
	private LocalDateTime fechaPuestaEnFuncionamiento;
	//Cada planta guarda sus monitoreos
	private Map <LocalDateTime, Monitoreo> registros;
	
	public Planta (String nombre, String direccion, double capacidadMaxima, LocalDateTime fechaPuestaEnFuncionamiento) {
		if (nombre == null || nombre.isBlank()) {
			throw new IllegalArgumentException("El nombre es obligatorio");
		}
		if (direccion == null || direccion.isBlank()) {
			throw new IllegalArgumentException("La direccion es obligatoria");
		}
		if (!Double.isFinite(capacidadMaxima) || capacidadMaxima <= 0) {
			throw new IllegalArgumentException("La capacidad maxima debe ser positiva y finita");
		}
		if (fechaPuestaEnFuncionamiento == null) {
			throw new IllegalArgumentException("La fecha es obligatoria");
		}
		this.nombre= nombre;
		this.direccion= direccion;
		this.capacidadMaxima= capacidadMaxima;
		this.fechaPuestaEnFuncionamiento= fechaPuestaEnFuncionamiento;
		this.estaActiva= true;
		this.totalGenerado=0.0;
		this.registros= new HashMap<>();
	}
	
	
	//Getters
	
	public String getNombre() {
		return nombre;
	}
	
	public String getDireccion() {
		return direccion;
	}
	
	public double getCapacidadMaxima() {
		return capacidadMaxima;
	}
	
	public LocalDateTime getFechaPuestaEnFuncionamiento() {
		return fechaPuestaEnFuncionamiento;
	}
	
	public boolean getEstaActiva() {
		return estaActiva;
	}
	
	public double getTotalGenerado() {
		return totalGenerado;
	}
	
	
	//Métodos
	
	public void activar() {
		this.estaActiva= true;
	}
	
	public void desactivar () {
		this.estaActiva=false;
	}
	
	public void registrarMonitoreo (Monitoreo m) {
		
	}
	
	public double obtenerTotalGenerado () {
		return 0.0;
	}
	
	public abstract double capacidadMinimaCuota ();
	
	public Map <LocalDateTime, Monitoreo> obtenerRegistros (){
		return registros;
	}
	
	public abstract boolean cumplioCuota (Monitoreo m);
	
	
}
