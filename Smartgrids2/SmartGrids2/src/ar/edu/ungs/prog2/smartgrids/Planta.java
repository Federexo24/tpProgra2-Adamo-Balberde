package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public abstract class Planta {
	private String nombre; 
	private int idPlanta; 
	private String direccion;
	private double capacidadMaxima;
	private boolean estaActiva;
	private double totalGenerado;
	private LocalDateTime fechaPuestaEnFuncionamiento;
	//Cada planta guarda sus monitoreos
	private Map <LocalDateTime, Monitoreo> registros;
	private List <PeriodoInactividad> periodosInactividad;
	private PeriodoInactividad periodoActual;
	
	public Planta (String nombre,int idPlanta, String direccion, double capacidadMaxima, LocalDateTime fechaPuestaEnFuncionamiento) {
		this.nombre= nombre;
		this.idPlanta= idPlanta;
		this.direccion= direccion;
		this.capacidadMaxima= capacidadMaxima;
		this.fechaPuestaEnFuncionamiento= fechaPuestaEnFuncionamiento;
		this.estaActiva= true;
		this.totalGenerado=0.0;
		this.registros= new HashMap<>();
		this.periodosInactividad= new ArrayList<>(); //Aca se guardan todos los periodos de inactividad
		this.periodoActual= null;//Se activa si se encuentra inactiva en la actualidad
	}
	
	
	//Getters
	
	public String getNombre() {
		return nombre;
	}
	
	public int getIdPlanta() {
		return idPlanta;
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
	
	public void desactivar (LocalDateTime fechaHora){
		this.estaActiva=false; //Desactivamos planta
		this.periodoActual= new PeriodoInactividad(fechaHora); //Creamos nuevo periodo de inactividad
		this.periodosInactividad.add(this.periodoActual); //Agregamos este nuevo periodo a la lista
		
		}
	
	public void activar(LocalDateTime fechaHora) { //Activamos planta y cerramos periodo de inactividad
		this.estaActiva= true;
		if(this.periodoActual!= null) { //Para evitar que tire excepción al querer abrir una planta que ya estaba
			//abierta, cuando la quiera cerrar va a saltar error
			this.periodoActual.cerrar(fechaHora);
			this.periodoActual=null;
		}
	}
	
	//Para preguntar si en un momento dado la planta estaba inactiva, llama a contiene, método de clase PeriodoInactividad
	public boolean estuvoInactiva (LocalDateTime momento) {
		for (PeriodoInactividad periodo: this.periodosInactividad) {
			if (periodo.contiene(momento)) {//Si este momento se encuentra dentro de los periodos que devuelva true
				return true;
			}
		}
		return false;
	}
	
	public void registrarMonitoreo (Monitoreo m) {
		//IREP
		if(m== null) { //Controlamos que el objeto no sea nulo
			throw new IllegalArgumentException("El monitoreo no puede ser nulo");
				}
		
		if (!this.estaActiva) { //Si la planta está inactiva no pueden haber registros
			throw new RuntimeException("No se puede hacer registro de una planta inactiva");
		}
		
		if(m.getFechaHora().isBefore(this.fechaPuestaEnFuncionamiento)) {
			throw new RuntimeException("La fecha del monitoreo no puede ser anterior a la fecha en la que empezó a funcionar la planta");
		}
		//.containsKey es la clave (en este caso la fecha y hora de los registros guardados)
		if (this.registros.containsKey(m.getFechaHora())) {
			throw new RuntimeException("Ya existe un registro en esta fecha y hora");
		}
		
		//Guardamos ese monitoreo en el Map de registros
		this.registros.put(m.getFechaHora(), m);
		//Sumamos la energía generada a la variable totalGenerado
		this.totalGenerado += m.getEnergiaGenerada();
	}
	
	//Este método devuelve un arreglo que indica cuantas incidencias hubo por cada hora
	public int [] incidenciasPorHora () {
		int [] incidencias= new int [24];
		
		for (Monitoreo m: this.registros.values()) {
			int hora= m.getFechaHora().getHour();
			incidencias[hora] += m.getCantidadIncidencias();
		}
		return incidencias;
	}
	
	
	public int limpiarMonitoreoSinDatos() {
		int borrados=0;
		
		Iterator<Monitoreo> it= this.registros.values().iterator(); //Incializamos el iterador
		
		while (it.hasNext()) { //Mientras haya un elemento siguiente
			Monitoreo m= it.next();
			
			if(m.sinDatos()) { //Si ese monitoreo no tiene Energia Generada ni Incidencias
				it.remove(); // Lo borra
				borrados++;} // Suma uno en el contador borrados
						}
		return borrados;
	}
	
	//Creamos este método para simplificar el punto 9 que pide devolver una lista con las plantas que nunca
	//cumplieron la cuota mínima
	public boolean nuncaCumplioCuota() {
		if (this.registros.isEmpty()) { //Si no tiene registros no cuenta como que nunca cumplió
			return false;
				}
		for (Monitoreo m : this.registros.values()){ //Recorremos todos los monitoreos
			if (this.cumplioCuota(m)){ //Si en algún momento cumplió la cuota
				return false;
			}
		}
		return true; //Si recorrió todo y nunca entro al false, entonces nunca cumplió la cuota
	}
	
	public Map <LocalDateTime, Monitoreo> obtenerRegistros (){
		return registros;
	}
	
	public abstract double capacidadMinimaCuota ();
	
	
	public abstract boolean cumplioCuota (Monitoreo m);
	
}
	
	
