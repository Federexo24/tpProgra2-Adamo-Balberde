package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SmartGrids implements ISmartGrids {
	private Map<String,Planta> plantas;
	private int proximoId;
	//Tuvimos que agregar esto porque no pasaba el test, las plantas tenían que estar ordenadas por como 
	//registraron
	private ArrayList <String> ordenNombrePlantas;
	
	public SmartGrids () {
		this.plantas= new HashMap<>();
		// proximoId= 1 se inicializa en 1 y por cada Planta que se inicializa en el futuro 
		//lo vamos aumentando en 1 para que no se repitan ids
		this.proximoId=1;
		this.ordenNombrePlantas= new ArrayList<>();
		
	}
	
	//Métodos
	
	//Tuvimos que crear método para pasar de String a Date porque así aclaraba la interfaz que se recibía asi
	//que creamos este método para poder después usarlo en las clases
	//Investigue sobre esto en internet para poder pasar de un tipo a otro de dato porque me saltaba error
	private LocalDateTime pasarStringADate (String fechaHora) {
		if(fechaHora == null) {
			throw new RuntimeException("La fecha y hora no pueden ser nulas");
																	}
		try {
			return LocalDateTime.parse(fechaHora);
										}
		catch(DateTimeParseException e) {
			throw new RuntimeException("La fecha y la hora tienen un formato inválido: " + fechaHora);
		}
	}
	
	
	
	public void registrarPlantaSolar(String nombre, String direccion, double capacidadMaxima,
			String fechaHoraPuestaEnFuncionamiento, String fuentePrimaria, double eficienciaPromedio,
			double superficieCubierta, int cantidadPaneles, double horasRadiacionDiaria) {
		//Controlamos que no exista ya una planta con este nombre
		if (this.plantas.containsKey(nombre)) {
			throw new RuntimeException("Ya existe una planta con este nombre");
					}
		//Pasamos el String fechaHora a tipo Date para poder registrarlo
		LocalDateTime fecha= pasarStringADate(fechaHoraPuestaEnFuncionamiento);
		//Creo nuevo objeto PlantaSolar
		PlantaSolar solar= new PlantaSolar (nombre, this.proximoId++, direccion,capacidadMaxima, fecha, fuentePrimaria,
				eficienciaPromedio, superficieCubierta, cantidadPaneles, horasRadiacionDiaria);
		//Guardo esta nueva planta en el mapa y la lista por orden
		this.plantas.put(nombre, solar);
		this.ordenNombrePlantas.add(nombre);
		
	}

	public void registrarPlantaEolica(String nombre, String direccion, double capacidadMaxima,
			String fechaHoraPuestaEnFuncionamiento, String fuentePrimaria, double eficienciaPromedio,
			int cantidadAerogeneradores, double alturaTorres, double velocidadMinimaViento) {
		
		if(this.plantas.containsKey(nombre)) {
			throw new RuntimeException("Ya existe una planta con este nombre");
															}
		LocalDateTime fecha= pasarStringADate(fechaHoraPuestaEnFuncionamiento);
		
		PlantaEolica eolica= new PlantaEolica(nombre, this.proximoId++, direccion, capacidadMaxima, fecha, fuentePrimaria,
				eficienciaPromedio,cantidadAerogeneradores,alturaTorres,velocidadMinimaViento);
		
		this.plantas.put(nombre, eolica);
		this.ordenNombrePlantas.add(nombre);
		
	}

	public void registrarPlantaHidraulica(String nombre, String direccion, double capacidadMaxima,
			String fechaHoraPuestaEnFuncionamiento, String fuentePrimaria, double eficienciaPromedio,
			int cantidadTurbinas, double capacidadEmbalse) {
		if (this.plantas.containsKey(nombre)) {
			throw new RuntimeException("Ya existe una planta con este nombre");
																	}
		LocalDateTime fecha= pasarStringADate(fechaHoraPuestaEnFuncionamiento);
		
		PlantaHidraulica hidraulica= new PlantaHidraulica(nombre, this.proximoId++, direccion, capacidadMaxima, fecha, fuentePrimaria,
				eficienciaPromedio, cantidadTurbinas, capacidadEmbalse);
		
		this.plantas.put(nombre, hidraulica);
		this.ordenNombrePlantas.add(nombre);
								}

	public void registrarPlantaTermica(String nombre, String direccion, double capacidadMaxima,
			String fechaHoraPuestaEnFuncionamiento, String tipoCombustible, double objetivoEmisionesCo2PorHora,
			double capacidadAlmacenamientoCombustible) {
		if (this.plantas.containsKey(nombre)) {
			throw new RuntimeException("Ya existe una planta con este nombre");
		}
		LocalDateTime fecha= pasarStringADate(fechaHoraPuestaEnFuncionamiento);
		
		PlantaTermica termica = new PlantaTermica(nombre, this.proximoId++, direccion, capacidadMaxima, 
				fecha,tipoCombustible, objetivoEmisionesCo2PorHora, capacidadAlmacenamientoCombustible);
		
		this.plantas.put(nombre, termica);
		this.ordenNombrePlantas.add(nombre);

	}
	

	public List<String> consultarPlantas() {
		List<String> resultado = new ArrayList <>();
		
		for (String nombre:this.ordenNombrePlantas) {
			Planta p = this.plantas.get(nombre);
			String estado = p.getEstaActiva() ? "ACTIVA" : "INACTIVA";// devuelve en string "Activa" "Inactiva" 
			
			resultado.add(p.getNombre()+ " (" + estado + "): " + p.getFechaPuestaEnFuncionamiento());
		}
		return resultado;
	}

	//Creamos este método para facilitar los controles de que el nombre de planta que ingresamos no sea null o
	// no exista una planta con ese nombre 
	private Planta validarPlanta (String nombre) {
		if (nombre == null || !this.plantas.containsKey(nombre)) {
			throw new RuntimeException("No existe ninguna planta con este nombre");
		}
		return this.plantas.get(nombre);
	}
	
	public double capacidadMinimaCuota(String nombre) {
		Planta p = validarPlanta(nombre);
		return p.capacidadMinimaCuota();
	}

	public void activarPlanta(String nombre, String fechaHora) {
		Planta p = validarPlanta(nombre);
		LocalDateTime fecha = pasarStringADate(fechaHora);
		p.activar(fecha); // Pasa fecha para cerrar período de inactividad
	}

	public void desactivarPlanta(String nombre, String fechaHora) {
		Planta p = validarPlanta(nombre);
		LocalDateTime fecha = pasarStringADate(fechaHora);
		p.desactivar(fecha); // Con esta fecha comienza un nuevo periodo de inactividad
	}

	public boolean estaActiva(String nombre) {
		Planta p = validarPlanta(nombre);
		return p.getEstaActiva();
	}

	public void registrarMonitoreo(String nombre, String fechaHora, double energiaGenerada, int incidencias) {
		Planta p = validarPlanta(nombre);
		
		if (p instanceof PlantaEolica || p instanceof PlantaTermica) {
			throw new RuntimeException("Este registro de monitoreo es para plantas hidráulicas o solares");
		}
		LocalDateTime fecha = pasarStringADate(fechaHora);
		Monitoreo m = new Monitoreo (p.getIdPlanta(), fecha, energiaGenerada, incidencias);
		
		p.registrarMonitoreo(m);
	}


	public void registrarMonitoreo(String nombre, String fechaHora, double energiaGenerada, int incidencias,
			double velocidadViento) {
		Planta p = validarPlanta(nombre);
		if (!(p instanceof PlantaEolica)) {
			throw new RuntimeException("Este registro de monitoreo es para plantas eólicas");
		}
		LocalDateTime fecha = pasarStringADate(fechaHora);
		MonitoreoEolico me  = new MonitoreoEolico (p.getIdPlanta(), fecha, energiaGenerada, incidencias, velocidadViento);

		p.registrarMonitoreo(me);
	}


	public void registrarMonitoreo(String nombre, String fechaHora, double energiaGenerada, int incidencias,
			double co2Emitido, double combustibleUtilizado) {
		Planta p = validarPlanta(nombre);
		if (!(p instanceof PlantaTermica)) {
			throw new RuntimeException("Este registro de monitoreo es para plantas térmicas");
		}
		LocalDateTime fecha = pasarStringADate(fechaHora);
		MonitoreoTermico mt  = new MonitoreoTermico (p.getIdPlanta(), fecha, energiaGenerada, incidencias, co2Emitido, combustibleUtilizado);

		p.registrarMonitoreo(mt);
	}

	public double totalGenerado(String nombre) {
		Planta p = validarPlanta(nombre);
		return p.getTotalGenerado();
	}

	public int[] incidenciasPorHora(String nombre) {
		Planta p = validarPlanta(nombre);
		return p.incidenciasPorHora();
	}

	
	//Se pide que devuelva un arreglo de 2 enteros, en el índice 0 devuelve la cantidad de monitoreos dentro del rango de fechas que nos 
	//en los que no se cumplió la cuota mínima, y en el índice 1 la cantidad de monitoreos dentro del rango de fecha que si se cumplió
	public int[] histogramaCumplimiento(String nombre, String fechaDesde, String fechaHasta) {
		Planta p = validarPlanta(nombre);
		LocalDateTime desde = pasarStringADate(fechaDesde);
		LocalDateTime hasta = pasarStringADate(fechaHasta);
		
		//Controlamos que desde no sea después de fin
		if (desde.isAfter(hasta)) {
			throw new RuntimeException("La fecha de inicio no puede ser posterior a la de fin");
		}
		int[] histograma = new int[2]; 
		
		for (Monitoreo m : p.obtenerRegistros().values()) {
			LocalDateTime fechaMonitoreo = m.getFechaHora();
			//Primero controlamos si el registro está dentro del rango que nos ingresaron
			boolean estaEnRango= !fechaMonitoreo.isBefore(desde) && !fechaMonitoreo.isAfter(hasta);
			
			if (estaEnRango) {
				if (p.cumplioCuota(m)) { //Si está en el rango y cumplió la cuota aumenta en uno el indice 1 del arreglo
					histograma[1]++;	
				}
				else {
					histograma[0]++; //Si no cumplió la cuota aumenta en uno el índice 0 del arreglo
				}
			}
		}
		return histograma;
	}

	//Este método devuelve un Map con la cantidad de plantas renovables que hay por cada tipo de fuente renovable
	public Map<String, Integer> plantasRenovablesPorFuente() {
		Map<String, Integer > plantasPorFuente= new HashMap<>(); //Incializamos mapa donde se guarda la cantidad y el tipo
		
		for(Planta p: this.plantas.values()) {
			if(!(p instanceof PlantaTermica)) {//Descartamos plantas térmicas ya que no tienen fuente primaria
				String fuente = "";
				//Guardamos la fuente de la planta que ingresa
				if (p instanceof PlantaSolar) {
					fuente=((PlantaSolar) p).getFuentePrimaria();
				}
				else if (p instanceof PlantaEolica) {
					fuente=((PlantaEolica) p).getFuentePrimaria();
				}
				else if (p instanceof PlantaHidraulica) {
					fuente=((PlantaHidraulica) p).getFuentePrimaria();
				}
				
				//Si ya se encuentra esa fuennte guardada en el map simplemente el sumamos uno al "contador" int
				if (plantasPorFuente.containsKey(fuente)) {
					plantasPorFuente.put (fuente, plantasPorFuente.get (fuente) + 1);
				}//Sino lo creamos e inicializamos el "contador" en 1
				else {
					plantasPorFuente.put(fuente, 1);
				}
			}
		}
		return plantasPorFuente;
	}

	//Creamos en Planta función nuncaCumplioCuota para delegarle el trabajo y que no tenga que hacerlo SmartGrid
	//lo que simplifica la implementación de este método
	public List<String> plantasQueNuncaCumplieronCuota() {
		List<String> listaPlantas = new ArrayList<>();
		
		for (Planta p : this.plantas.values()) { //Recorremos todas las plantas si alguna cumple con la funcion
			if (p.nuncaCumplioCuota()) { //que agregamos en Planta entonces se agrega a la lista
				listaPlantas.add(p.getNombre());
			}
		}
		return listaPlantas;
	}

	//Ya habiamos armado la base de este método en Planta Eólica así que solo queda crear un String Builder
	//que nos devuelva los resultados de todas las plantas
	public String horasSinVelocidadMinimaPorPlantaEolica() {
		StringBuilder st = new StringBuilder ();
		for (Planta p: this.plantas.values()) {
			if (p instanceof PlantaEolica) {
				PlantaEolica pe = (PlantaEolica) p;
				st.append(pe.reporteHorasSinVelocidadMinima());
			}
		}
		return st.toString();
	}
	
	

	//Ya tenemos en Planta la variable limpiarMonitoresSinDatos asi que solo queda implementarla y guardar la
	//cantidad de monitoreos que se borraron en una variable
	public int limpiarMonitoreosSinDatos() {
		int cantidadMonitoreosEliminados=0;
		for (Planta p: this.plantas.values()) {
			cantidadMonitoreosEliminados+= p.limpiarMonitoreoSinDatos();
		}
		return cantidadMonitoreosEliminados;
	}

	//Este método devuelve una lista con el nombre de las plantas ordenadas por EnergiaGenerada de mayor a menor
	public List<String> plantasOrdenadasPorEnergiaGenerada() {
		List<Planta> lista = new ArrayList<>(this.plantas.values()); // Para modificar orden sin cambiar this.plantas
		
		for (int i= 0; i < lista.size()-1; i++) { //Este for es para indicar el indice por el que vamos ordenando
			int indiceMayor=i; // asumimos que empieza por el primero
			//Vamos recorriendo el resto de la lista a ver si encontramos uno mayor y guardamos el indice en indiceMayor
			for (int j= i+1; j < lista.size()-1; j++) {
				if (lista.get(j).getTotalGenerado()> lista.get(indiceMayor).getTotalGenerado()) {
					indiceMayor=j;
				}
			}
		//Ahora hago el cambio de posición con el índice guardandolo en un objeto planta auxiliar para no perder
		//el objeto
		Planta aux= lista.get(i);
		lista.set(i, lista.get(indiceMayor));
		lista.set(indiceMayor, aux);
		}
		
		List<String> listaOrdenada = new ArrayList<>();
		for (Planta p: lista) {
			listaOrdenada.add(p.getNombre());
		}
		return listaOrdenada;
	}

	// Ya tenemos la clase inactiva donde guarda la ficha con los periodos de inactividad de todas las plantas
	//asi que con la función .estuvoInactiva() podemos consultar si una planta estuvo inactiva en un momento
	//determinado
	public List<String> plantasInactivasEn(String fechaHora) {
		//Recibimos la fecha como string asi que lo pasamos a Date
		LocalDateTime momento= pasarStringADate(fechaHora);
		//Inicializamos una lista para poner todas las plantas que estuvieron inactivas en ese momento
		List<String> inactivas= new ArrayList<>();
		
		for (Planta p: this.plantas.values()) {
			if (p.estuvoInactiva(momento)) {
				inactivas.add(p.getNombre());
			}
		}
		return inactivas;
	}

}
