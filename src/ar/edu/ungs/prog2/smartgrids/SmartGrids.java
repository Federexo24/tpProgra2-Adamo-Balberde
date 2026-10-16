package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SmartGrids implements ISmartGrids {
    private final Map<String, Planta> plantas;

    public SmartGrids() {
        plantas = new LinkedHashMap<>();
    }

    private LocalDateTime validarAlta(String nombre, String fechaHora) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (plantas.containsKey(nombre)) {
            throw new IllegalArgumentException("Ya existe una planta con ese nombre");
        }
        return parsearFechaHora(fechaHora);
    }

    private LocalDateTime parsearFechaHora(String fechaHora) {
        if (fechaHora == null) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }
        return LocalDateTime.parse(fechaHora);
    }

    private Planta buscarPlanta(String nombre) {
        Planta planta = plantas.get(nombre);
        if (planta == null) {
            throw new IllegalArgumentException("No existe la planta: " + nombre);
        }
        return planta;
    }

    @Override
    public void registrarPlantaSolar(String nombre, String direccion, double capacidadMaxima,
            String fechaHoraPuestaEnFuncionamiento, String fuentePrimaria, double eficienciaPromedio,
            double superficieCubierta, int cantidadPaneles, double horasRadiacionDiaria) {
        LocalDateTime fecha = validarAlta(nombre, fechaHoraPuestaEnFuncionamiento);
        Planta planta = new PlantaSolar(nombre, direccion, capacidadMaxima, fecha,
                fuentePrimaria, eficienciaPromedio, superficieCubierta, cantidadPaneles, horasRadiacionDiaria);
        plantas.put(nombre, planta);
    }

    @Override
    public void registrarPlantaEolica(String nombre, String direccion, double capacidadMaxima,
            String fechaHoraPuestaEnFuncionamiento, String fuentePrimaria, double eficienciaPromedio,
            int cantidadAerogeneradores, double alturaTorres, double velocidadMinimaViento) {
        LocalDateTime fecha = validarAlta(nombre, fechaHoraPuestaEnFuncionamiento);
        Planta planta = new PlantaEolica(nombre, direccion, capacidadMaxima, fecha,
                fuentePrimaria, eficienciaPromedio, cantidadAerogeneradores, alturaTorres, velocidadMinimaViento);
        plantas.put(nombre, planta);
    }

    @Override
    public void registrarPlantaHidraulica(String nombre, String direccion, double capacidadMaxima,
            String fechaHoraPuestaEnFuncionamiento, String fuentePrimaria, double eficienciaPromedio,
            int cantidadTurbinas, double capacidadEmbalse) {
        LocalDateTime fecha = validarAlta(nombre, fechaHoraPuestaEnFuncionamiento);
        Planta planta = new PlantaHidraulica(nombre, direccion, capacidadMaxima, fecha,
                fuentePrimaria, eficienciaPromedio, cantidadTurbinas, capacidadEmbalse);
        plantas.put(nombre, planta);
    }

    @Override
    public void registrarPlantaTermica(String nombre, String direccion, double capacidadMaxima,
            String fechaHoraPuestaEnFuncionamiento, String tipoCombustible, double objetivoEmisionesCo2PorHora,
            double capacidadAlmacenamientoCombustible) {
        LocalDateTime fecha = validarAlta(nombre, fechaHoraPuestaEnFuncionamiento);
        Planta planta = new PlantaTermica(nombre, direccion, capacidadMaxima, fecha,
                tipoCombustible, objetivoEmisionesCo2PorHora, capacidadAlmacenamientoCombustible);
        plantas.put(nombre, planta);
    }

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

    @Override
    public double capacidadMinimaCuota(String nombre) {
        return buscarPlanta(nombre).capacidadMinimaCuota();
    }

    @Override
    public void activarPlanta(String nombre, String fechaHora) {
        buscarPlanta(nombre).activar(parsearFechaHora(fechaHora));
    }

    @Override
    public void desactivarPlanta(String nombre, String fechaHora) {
        buscarPlanta(nombre).desactivar(parsearFechaHora(fechaHora));
    }

    @Override
    public boolean estaActiva(String nombre) {
        return buscarPlanta(nombre).getEstaActiva();
    }

    @Override
    public void registrarMonitoreo(String nombre, String fechaHora, double energiaGenerada, int incidencias) {
        buscarPlanta(nombre).registrarMonitoreo(
                new Monitoreo(nombre, parsearFechaHora(fechaHora), energiaGenerada, incidencias));
    }

    @Override
    public void registrarMonitoreo(String nombre, String fechaHora, double energiaGenerada, int incidencias,
            double velocidadViento) {
        buscarPlanta(nombre).registrarMonitoreo(
                new MonitoreoEolico(nombre, parsearFechaHora(fechaHora), energiaGenerada, incidencias, velocidadViento));
    }

    @Override
    public void registrarMonitoreo(String nombre, String fechaHora, double energiaGenerada, int incidencias,
            double co2Emitido, double combustibleUtilizado) {
        buscarPlanta(nombre).registrarMonitoreo(new MonitoreoTermico(nombre, parsearFechaHora(fechaHora),
                energiaGenerada, incidencias, co2Emitido, combustibleUtilizado));
    }

    @Override
    public double totalGenerado(String nombre) {
        return buscarPlanta(nombre).obtenerTotalGenerado();
    }

    @Override
    public int[] incidenciasPorHora(String nombre) {
        return buscarPlanta(nombre).incidenciasPorHora();
    }

    @Override
    public int[] histogramaCumplimiento(String nombre, String fechaDesde, String fechaHasta) {
        Planta planta = buscarPlanta(nombre);
        if (fechaDesde == null || fechaHasta == null) {
            throw new IllegalArgumentException("Las dos fechas son obligatorias");
        }
        return planta.histogramaCumplimiento(LocalDate.parse(fechaDesde), LocalDate.parse(fechaHasta));
    }

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

    @Override
    public int limpiarMonitoreosSinDatos() {
        int eliminados = 0;
        for (Planta planta : plantas.values()) {
            eliminados += planta.limpiarMonitoreosSinDatos();
        }
        return eliminados;
    }

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

    @Override
    public String toString() {
        StringBuilder resultado = new StringBuilder("SmartGrids: ").append(plantas.size()).append(" plantas");
        for (Planta planta : plantas.values()) {
            resultado.append("\n").append(planta);
        }
        return resultado.toString();
    }
}
