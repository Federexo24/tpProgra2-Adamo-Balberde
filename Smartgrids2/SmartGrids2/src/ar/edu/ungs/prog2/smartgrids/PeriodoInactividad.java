package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDateTime;

public class PeriodoInactividad {
	private LocalDateTime inicio;
	private LocalDateTime fin;
	
	public PeriodoInactividad (LocalDateTime inicio) {
		this.inicio=inicio;
		this.fin=null; //Va a ser null mientras que la planta siga inactiva
	}
	
	//Métodos
	//Para ponerle fin a un periodo de inactividad de la planta
	public void cerrar (LocalDateTime fin) {
		this.fin=fin;
	}
	
	//Para preguntar si en un momento dado la planta estaba inactiva
	public boolean contiene (LocalDateTime momento) {
		//Si la planta sigue inactiva si el momento es después de inicio entonces es correcto
		if(fin==null) {
			return !momento.isBefore(inicio);
		}
		
		//Si la planta ya está en actividad devuelta para que sea true tiene que ser un momento después del inicio
		//y antes del fin
		return !momento.isBefore(inicio) && !momento.isAfter(fin);		
	}
}
