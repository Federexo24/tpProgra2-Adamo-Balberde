package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDateTime;

public class PlantaEolica extends PlantaRenovable{
	private int cantidadAerogeneradores;
	private double alturaTorres;
	private double velocidadMinimaViento;
	
	
	
	public PlantaEolica(String nombre,int idPlanta, String direccion, double capacidadMaxima,
			LocalDateTime fechaPuestaEnFuncionamiento, String fuentePrimaria, double eficienciaPromedio, int cantidadAerogeneradores, double alturaTorres, double velocidadMinimaViento) {
		
		super(nombre, idPlanta, direccion, capacidadMaxima, fechaPuestaEnFuncionamiento, fuentePrimaria, eficienciaPromedio);
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

	//Como ya heredo de Planta Renovable la funcion capacidad minima cuota solo tengo que controlar que la 
	//energia generada sea mayor o igual a esa cuota minima y que la velocdad del viento del monitoreo sea mayor
	// que el viento promedio
	
	public boolean cumplioCuota(Monitoreo m) {
		MonitoreoEolico me= (MonitoreoEolico) m;
		return me.getEnergiaGenerada()>= this.capacidadMinimaCuota() && 
				me.getVelocidadVientoPromedio()>= this.velocidadMinimaViento;
	}

	//
	public  String reporteHorasSinVelocidadMinima() {
		//Va contando del 0 al 23 las horas	
		int [] cantidadPorHora= new int [24]; 
		// Cuando el viento minimo se alcanzó se activa true dejando constancia de esto
		boolean[] alcanzoMinima= new boolean [24]; 
		//Guarda suma de la velocidad del viento para poder después sacar un promedio
		double [] sumaViento= new double [24]; 
		
		//Hacemos un for each para recorrer los monitores quedandonos solo con los objetos (.values()) y no la key
		for (Monitoreo m: this.obtenerRegistros().values()){
			//Permite tratar este monitoreo no como un monitoreo genérico sino como un monitoreo eólico que le
			//agrega la variable viento promedio
			MonitoreoEolico me= (MonitoreoEolico) m;
			
			//Obtenemos la hora de este monitoreo con el método propio de Date .getHour() 
			int hora= me.getFechaHora().getHour();
			
			double viento= me.getVelocidadVientoPromedio();
			
			//Esto sirve para llevar la cuenta de cuantas veces se midió el tiempo en una hora determinada
			cantidadPorHora[hora]++;
			
			//En esta variable acumulamos la velocidad del viento para poder hacer luego un promedio
			sumaViento[hora] += viento;
			
			//Detecta que el viento a este hora superó el mínimo
			if (viento>= this.velocidadMinimaViento) {
				alcanzoMinima[hora] = true; 
			}
		}
		
		
		StringBuilder st= new StringBuilder ();
		st.append("- ").append(this.getNombre()).append(":   ").append((int)this.velocidadMinimaViento)
		.append("m/s\n"); //Usamos el (int) para que quedé como en el PDF y no quedé como double (5 en vez de 5.0)
		
		for (int hora=0; hora < 24; hora++) {
			//(si hubo registro de viento y este nunca alcanzó el minimo){calcula promedio del viento}
			if(cantidadPorHora[hora]>0 && !alcanzoMinima[hora]) {
				int promedioHora= (int) (sumaViento[hora]/ cantidadPorHora[hora]);
				st.append("      ");
				if(hora<10) { 
					st.append("0"); //Si la hora es de un dígito agrega un 0 adelante
				}
				st.append(hora).append(": ").append(promedioHora).append(" m/s\n");
			}
		}
		return st.toString(); 
	
	}

	
	


	

}
 