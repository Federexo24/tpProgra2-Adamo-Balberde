package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Almacena plantas por id y mantiene un índice de nombres para ofrecer las operaciones de ISmartGrids.
 * Delega las reglas de cada tipo en las subclases de Planta: así se aplica el polimorfismo.
 */
public class SmartGrids implements ISmartGrids {
    private final Map<Integer, Planta> plantas;
    // Índice auxiliar: permite buscar por nombre en O(1) esperado sin recorrer las plantas.
    private final Map<String, Integer> idsPorNombre;
    // long permite detectar el agotamiento de identificadores sin desbordar un int.
    private long proximoId = 1;

    /**
     * Crea un sistema vacío. LinkedHashMap conserva el orden de alta y el índice resuelve búsquedas por nombre.
     */
    public SmartGrids() {
        plantas = new LinkedHashMap<>();
        idsPorNombre = new HashMap<>();
    }

    /**
     * Rechaza nombres vacíos o repetidos y convierte la fecha de puesta en funcionamiento.
     * Devuelve la fecha validada; todavía no agrega la planta al sistema.
     */
    private LocalDateTime validarAlta(String nombre, String fechaHora) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (idsPorNombre.containsKey(nombre)) {
            throw new IllegalArgumentException("Ya existe una planta con ese nombre");
        }
        return parsearFechaHora(fechaHora);
    }

    /**
     * Reserva un id positivo para las altas realizadas mediante ISmartGrids.
     * Los ids son únicos en este sistema; una construcción fallida puede dejar un salto.
     */
    private int generarId() {
        if (proximoId > Integer.MAX_VALUE) {
            throw new IllegalStateException("Se agotaron los identificadores de plantas");
        }
        return (int) proximoId++;
    }

    /**
     * Operación del diagrama: incorpora una planta ya construida.
     * Rechaza ids o nombres repetidos antes de actualizar ambos mapas.
     */
    public void registrarPlanta(Planta planta) {
        if (planta == null) {
            throw new IllegalArgumentException("La planta es obligatoria");
        }
        if (plantas.containsKey(planta.getIdPlanta()) || idsPorNombre.containsKey(planta.getNombre())) {
            throw new IllegalArgumentException("Ya existe una planta con ese id o nombre");
        }
        plantas.put(planta.getIdPlanta(), planta);
        idsPorNombre.put(planta.getNombre(), planta.getIdPlanta());
        proximoId = Math.max(proximoId, (long) planta.getIdPlanta() + 1);
    }

    /**
     * Devuelve el objeto Planta registrado, como propone el diagrama.
     * Es el objeto administrado por el sistema, no una copia.
     */
    public Planta consultarPlanta(String nombre) {
        return buscarPlanta(nombre);
    }

    /**
     * Devuelve una lista nueva de los objetos registrados, en orden de alta.
     * Se usa otro nombre porque consultarPlantas() debe devolver List<String> por ISmartGrids.
     */
    public List<Planta> consultarObjetosPlanta() {
        return new ArrayList<>(plantas.values());
    }

    /**
     * Conserva el nombre de la consulta de cuota del diagrama y reutiliza la regla de la planta.
     */
    public double consultarCapacidadMinimaDeCuota(String nombre) {
        return capacidadMinimaCuota(nombre);
    }

    /**
     * Operación del diagrama: actualiza el estado usando la fecha y hora actuales.
     * Para fechas históricas se usan activarPlanta o desactivarPlanta con fecha explícita.
     */
    public void actualizarEstado(String nombre, boolean estado) {
        Planta planta = buscarPlanta(nombre);
        if (estado) {
            planta.activar();
        } else {
            planta.desactivar();
        }
    }

    /**
     * Recibe un monitoreo ya construido, como indica el diagrama.
     * Planta verifica que su id coincida con el del registro y aplica las demás validaciones.
     */
    public void registrarMonitoreo(String nombre, Monitoreo monitoreo) {
        buscarPlanta(nombre).registrarMonitoreo(monitoreo);
    }

    /**
     * Ofrece el nombre del diagrama para consultar la energía acumulada.
     */
    public double obtenerTotalGenerado(String nombre) {
        return totalGenerado(nombre);
    }

    /**
     * Devuelve las incidencias acumuladas para la hora del día indicada, entre todos los registros.
     * Se toma solo getHour(): el día del parámetro no filtra la consulta del punto 6.
     */
    public int consultarIncidenciasPorHora(String nombre, LocalDateTime hora) {
        if (hora == null) {
            throw new IllegalArgumentException("La hora es obligatoria");
        }
        return incidenciasPorHora(nombre)[hora.getHour()];
    }

    /**
     * Adapta el histograma al mapa de hora a cantidad de días del diagrama.
     * Incluye las 24 horas, incluso las que tienen cero cumplimientos.
     */
    public Map<Integer, Integer> calcularHistogramaCumplimiento(String nombre, LocalDate desde, LocalDate hasta) {
        int[] histograma = buscarPlanta(nombre).histogramaCumplimiento(desde, hasta);
        Map<Integer, Integer> resultado = new LinkedHashMap<>();
        for (int hora = 0; hora < histograma.length; hora++) {
            resultado.put(hora, histograma[hora]);
        }
        return resultado;
    }

    /**
     * Conserva el nombre del diagrama para el conteo de renovables por fuente.
     */
    public Map<String, Integer> contarRenovablesPorFuente() {
        return plantasRenovablesPorFuente();
    }

    /**
     * Devuelve los objetos Planta que no tienen registros conservados de cumplimiento.
     * Incluye las plantas sin monitoreos y mantiene el orden de registro.
     */
    public List<Planta> obtenerPlantasQueNuncaCumplieronCuota() {
        List<Planta> resultado = new ArrayList<>();
        for (Planta planta : plantas.values()) {
            if (!planta.cumplioAlgunaCuota()) {
                resultado.add(planta);
            }
        }
        return resultado;
    }

    /**
     * Devuelve el mapa del diagrama: cada planta eólica y sus horas con viento siempre insuficiente.
     * LocalTime representa cada hora con minutos y segundos en cero; los conjuntos son nuevos.
     */
    public Map<PlantaEolica, Set<LocalTime>> obtenerHorasSinVelocidadMinima() {
        Map<PlantaEolica, Set<LocalTime>> resultado = new LinkedHashMap<>();
        for (Planta planta : plantas.values()) {
            if (planta instanceof PlantaEolica) {
                PlantaEolica eolica = (PlantaEolica) planta;
                resultado.put(eolica, eolica.horasSinVelocidadMinima());
            }
        }
        return resultado;
    }

    /**
     * Consulta si el monitoreo de una fecha y hora cumplió la cuota; devuelve false si no hay registro.
     * Minutos y segundos se descartan para buscar la hora completa usada como clave.
     */
    public boolean consultarCumplioCuota(String nombre, LocalDateTime hora) {
        Planta planta = buscarPlanta(nombre);
        if (hora == null) {
            throw new IllegalArgumentException("La hora es obligatoria");
        }
        Monitoreo monitoreo = planta.obtenerRegistros().get(hora.truncatedTo(ChronoUnit.HOURS));
        return monitoreo != null && planta.cumplioCuota(monitoreo);
    }

    /**
     * Convierte el texto de fecha y hora a LocalDateTime.
     * Rechaza null y deja que parse informe fechas inexistentes o formatos inválidos.
     */
    private LocalDateTime parsearFechaHora(String fechaHora) {
        if (fechaHora == null) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }
        return LocalDateTime.parse(fechaHora);
    }

    /**
     * Busca la planta por su nombre en tiempo constante esperado.
     * Lanza una excepción si no existe, para evitar que las consultas devuelvan datos engañosos.
     */
    private Planta buscarPlanta(String nombre) {
        Integer id = idsPorNombre.get(nombre);
        Planta planta = plantas.get(id);
        if (planta == null) {
            throw new IllegalArgumentException("No existe la planta: " + nombre);
        }
        return planta;
    }

    /**
     * Valida el alta, construye una planta solar y la registra por id con su índice de nombre.
     * Los constructores verifican los datos antes de que se modifique el mapa.
     */
    @Override
    public void registrarPlantaSolar(String nombre, String direccion, double capacidadMaxima,
            String fechaHoraPuestaEnFuncionamiento, String fuentePrimaria, double eficienciaPromedio,
            double superficieCubierta, int cantidadPaneles, double horasRadiacionDiaria) {
        LocalDateTime fecha = validarAlta(nombre, fechaHoraPuestaEnFuncionamiento);
        Planta planta = new PlantaSolar(generarId(), nombre, direccion, capacidadMaxima, fecha,
                fuentePrimaria, eficienciaPromedio, superficieCubierta, cantidadPaneles, horasRadiacionDiaria);
        registrarPlanta(planta);
    }

    /**
     * Valida el alta y registra una planta eólica con sus aerogeneradores y condiciones de viento.
     */
    @Override
    public void registrarPlantaEolica(String nombre, String direccion, double capacidadMaxima,
            String fechaHoraPuestaEnFuncionamiento, String fuentePrimaria, double eficienciaPromedio,
            int cantidadAerogeneradores, double alturaTorres, double velocidadMinimaViento) {
        LocalDateTime fecha = validarAlta(nombre, fechaHoraPuestaEnFuncionamiento);
        Planta planta = new PlantaEolica(generarId(), nombre, direccion, capacidadMaxima, fecha,
                fuentePrimaria, eficienciaPromedio, cantidadAerogeneradores, alturaTorres, velocidadMinimaViento);
        registrarPlanta(planta);
    }

    /**
     * Valida el alta y registra una planta hidráulica con sus turbinas y capacidad de embalse.
     */
    @Override
    public void registrarPlantaHidraulica(String nombre, String direccion, double capacidadMaxima,
            String fechaHoraPuestaEnFuncionamiento, String fuentePrimaria, double eficienciaPromedio,
            int cantidadTurbinas, double capacidadEmbalse) {
        LocalDateTime fecha = validarAlta(nombre, fechaHoraPuestaEnFuncionamiento);
        Planta planta = new PlantaHidraulica(generarId(), nombre, direccion, capacidadMaxima, fecha,
                fuentePrimaria, eficienciaPromedio, cantidadTurbinas, capacidadEmbalse);
        registrarPlanta(planta);
    }

    /**
     * Valida el alta y registra una planta térmica con combustible y objetivo de emisiones.
     */
    @Override
    public void registrarPlantaTermica(String nombre, String direccion, double capacidadMaxima,
            String fechaHoraPuestaEnFuncionamiento, String tipoCombustible, double objetivoEmisionesCo2PorHora,
            double capacidadAlmacenamientoCombustible) {
        LocalDateTime fecha = validarAlta(nombre, fechaHoraPuestaEnFuncionamiento);
        Planta planta = new PlantaTermica(generarId(), nombre, direccion, capacidadMaxima, fecha,
                tipoCombustible, objetivoEmisionesCo2PorHora, capacidadAlmacenamientoCombustible);
        registrarPlanta(planta);
    }

    /**
     * Devuelve una lista nueva con nombre, estado actual y fecha de puesta en marcha.
     * Respeta el orden de registro y muestra los segundos incluso cuando son cero.
     */
    @Override
    public List<String> consultarPlantas() {
        List<String> resultado = new ArrayList<>();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        for (Planta planta : plantas.values()) {
            String estado = planta.getEstaActiva() ? "ACTIVA" : "INACTIVA";
            resultado.add(planta.getNombre() + " (" + estado + "): "
                    + planta.getFechaPuestaEnFuncionamiento().format(formato));
        }
        return resultado;
    }

    /**
     * Consulta el mínimo de energía exigido a la planta.
     * La fórmula se elige por polimorfismo según el tipo concreto de Planta.
     */
    @Override
    public double capacidadMinimaCuota(String nombre) {
        return buscarPlanta(nombre).capacidadMinimaCuota();
    }

    /**
     * Busca la planta y la activa en la fecha indicada, cerrando su período de inactividad si corresponde.
     */
    @Override
    public void activarPlanta(String nombre, String fechaHora) {
        buscarPlanta(nombre).activar(parsearFechaHora(fechaHora));
    }

    /**
     * Busca la planta y la desactiva en la fecha indicada, abriendo un período de inactividad si corresponde.
     */
    @Override
    public void desactivarPlanta(String nombre, String fechaHora) {
        buscarPlanta(nombre).desactivar(parsearFechaHora(fechaHora));
    }

    /**
     * Devuelve el estado actual de una planta existente, en tiempo constante esperado.
     */
    @Override
    public boolean estaActiva(String nombre) {
        return buscarPlanta(nombre).getEstaActiva();
    }

    /**
     * Registra energía e incidencias para una planta solar o hidráulica.
     * La planta controla estado, fecha, tipo de registro y duplicados.
     */
    @Override
    public void registrarMonitoreo(String nombre, String fechaHora, double energiaGenerada, int incidencias) {
        Planta planta = buscarPlanta(nombre);
        planta.registrarMonitoreo(
                new Monitoreo(planta.getIdPlanta(), parsearFechaHora(fechaHora), energiaGenerada, incidencias));
    }

    /**
     * Registra un monitoreo eólico, que también incluye la velocidad del viento.
     * La sobrecarga distingue esta operación por su parámetro adicional.
     */
    @Override
    public void registrarMonitoreo(String nombre, String fechaHora, double energiaGenerada, int incidencias,
            double velocidadViento) {
        Planta planta = buscarPlanta(nombre);
        planta.registrarMonitoreo(
                new MonitoreoEolico(planta.getIdPlanta(), parsearFechaHora(fechaHora), energiaGenerada, incidencias, velocidadViento));
    }

    /**
     * Registra un monitoreo térmico con emisiones de CO2 y combustible utilizado.
     * La planta rechaza este tipo de registro si no es térmica.
     */
    @Override
    public void registrarMonitoreo(String nombre, String fechaHora, double energiaGenerada, int incidencias,
            double co2Emitido, double combustibleUtilizado) {
        Planta planta = buscarPlanta(nombre);
        planta.registrarMonitoreo(new MonitoreoTermico(planta.getIdPlanta(), parsearFechaHora(fechaHora),
                energiaGenerada, incidencias, co2Emitido, combustibleUtilizado));
    }

    /**
     * Devuelve la energía acumulada de la planta; es cero si no tiene registros.
     * El total se actualiza al registrar monitoreos, por lo que no hace falta sumarlos otra vez.
     */
    @Override
    public double totalGenerado(String nombre) {
        return buscarPlanta(nombre).obtenerTotalGenerado();
    }

    /**
     * Obtiene 24 contadores: cada posición acumula las incidencias de esa hora en todos los días registrados.
     */
    @Override
    public int[] incidenciasPorHora(String nombre) {
        return buscarPlanta(nombre).incidenciasPorHora();
    }

    /**
     * Convierte las fechas y delega el cálculo del histograma en la planta.
     * Cada posición cuenta días de cumplimiento para una hora, incluyendo ambas fechas del rango.
     */
    @Override
    public int[] histogramaCumplimiento(String nombre, String fechaDesde, String fechaHasta) {
        Planta planta = buscarPlanta(nombre);
        if (fechaDesde == null || fechaHasta == null) {
            throw new IllegalArgumentException("Las dos fechas son obligatorias");
        }
        return planta.histogramaCumplimiento(LocalDate.parse(fechaDesde), LocalDate.parse(fechaHasta));
    }

    /**
     * Cuenta únicamente plantas renovables, agrupadas por su fuente primaria.
     * TreeMap devuelve las fuentes ordenadas alfabéticamente.
     */
    @Override
    public Map<String, Integer> plantasRenovablesPorFuente() {
        Map<String, Integer> resultado = new TreeMap<>();
        for (Planta planta : plantas.values()) {
            if (planta instanceof PlantaRenovable) {
                String fuente = ((PlantaRenovable) planta).getFuentePrimaria();
                resultado.put(fuente, resultado.getOrDefault(fuente, 0) + 1);
            }
        }
        return resultado;
    }

    /**
     * Devuelve los nombres de plantas sin ningún monitoreo conservado que cumpla la cuota.
     * Incluye las plantas sin registros y mantiene el orden de alta.
     */
    @Override
    public List<String> plantasQueNuncaCumplieronCuota() {
        List<String> resultado = new ArrayList<>();
        for (Planta planta : plantas.values()) {
            if (!planta.cumplioAlgunaCuota()) {
                resultado.add(planta.getNombre());
            }
        }
        return resultado;
    }

    /**
     * Reúne los informes de todas las plantas eólicas bajo un encabezado común.
     * StringBuilder permite construir el texto agregando cada informe.
     */
    @Override
    public String horasSinVelocidadMinimaPorPlantaEolica() {
        StringBuilder resultado = new StringBuilder("Plantas Eolicas: Horas con poco viento");
        for (Planta planta : plantas.values()) {
            if (planta instanceof PlantaEolica) {
                resultado.append("\n").append(((PlantaEolica) planta).reporteHorasSinVelocidadMinima());
            }
        }
        return resultado.toString();
    }

    /**
     * Pide a cada planta que borre registros con energía e incidencias en cero.
     * Devuelve la cantidad total eliminada entre todas las plantas.
     */
    @Override
    public int limpiarMonitoreosSinDatos() {
        int eliminados = 0;
        for (Planta planta : plantas.values()) {
            eliminados += planta.limpiarMonitoreosSinDatos();
        }
        return eliminados;
    }

    /**
     * Ordena una copia de las plantas de mayor a menor energía, sin alterar el mapa original.
     * En un empate mantiene el orden de registro; devuelve nombre y total como texto.
     */
    @Override
    public List<String> plantasOrdenadasPorEnergiaGenerada() {
        List<Planta> ordenadas = new ArrayList<>(plantas.values());
        // El ordenamiento estable conserva el orden de registro ante empates.
        ordenadas.sort(Comparator.comparingDouble(Planta::obtenerTotalGenerado).reversed());
        List<String> resultado = new ArrayList<>();
        for (Planta planta : ordenadas) {
            resultado.add(planta.getNombre() + ": " + planta.obtenerTotalGenerado());
        }
        return resultado;
    }

    /**
     * Devuelve las plantas cuyo historial contiene la fecha y hora consultadas.
     * Consulta períodos anteriores y abiertos, no solamente el estado actual.
     */
    @Override
    public List<String> plantasInactivasEn(String fechaHora) {
        LocalDateTime fecha = parsearFechaHora(fechaHora);
        List<String> resultado = new ArrayList<>();
        for (Planta planta : plantas.values()) {
            if (planta.estabaInactivaEn(fecha)) {
                resultado.add(planta.getNombre());
            }
        }
        return resultado;
    }

    /**
     * Construye una representación legible del sistema y del estado de cada planta.
     */
    @Override
    public String toString() {
        StringBuilder resultado = new StringBuilder("SmartGrids: ").append(plantas.size()).append(" plantas");
        for (Planta planta : plantas.values()) {
            resultado.append("\n").append(planta);
        }
        return resultado.toString();
    }
}
