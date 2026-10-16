package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public abstract class Planta {
    private final String nombre;
    private final String direccion;
    private final double capacidadMaxima;
    private boolean estaActiva;
    private double totalGenerado;
    private final LocalDateTime fechaPuestaEnFuncionamiento;
    // Una entrada por fecha y hora: los minutos y segundos no crean otra hora.
    private final Map<LocalDateTime, Monitoreo> registros;
    private final LinkedList<PeriodoInactividad> periodosInactivos;
    private LocalDateTime ultimoCambioEstado;

    public Planta(String nombre, String direccion, double capacidadMaxima, LocalDateTime fechaPuestaEnFuncionamiento) {
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
        this.nombre = nombre;
        this.direccion = direccion;
        this.capacidadMaxima = capacidadMaxima;
        this.fechaPuestaEnFuncionamiento = fechaPuestaEnFuncionamiento;
        estaActiva = true;
        totalGenerado = 0;
        registros = new LinkedHashMap<>();
        periodosInactivos = new LinkedList<>();
        ultimoCambioEstado = fechaPuestaEnFuncionamiento;
    }

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

    private void validarCambioEstado(LocalDateTime fechaHora) {
        if (fechaHora == null || fechaHora.isBefore(ultimoCambioEstado)) {
            throw new IllegalArgumentException("Los cambios de estado deben registrarse en orden cronologico");
        }
    }

    public void activar(LocalDateTime fechaHora) {
        validarCambioEstado(fechaHora);
        if (!estaActiva) {
            periodosInactivos.getLast().cerrar(fechaHora);
            estaActiva = true;
            ultimoCambioEstado = fechaHora;
        }
    }

    public void desactivar(LocalDateTime fechaHora) {
        validarCambioEstado(fechaHora);
        if (estaActiva) {
            periodosInactivos.addLast(new PeriodoInactividad(fechaHora));
            estaActiva = false;
            ultimoCambioEstado = fechaHora;
        }
    }

    public boolean estabaInactivaEn(LocalDateTime fechaHora) {
        if (fechaHora == null) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }
        for (PeriodoInactividad periodo : periodosInactivos) {
            if (periodo.contiene(fechaHora)) {
                return true;
            }
        }
        return false;
    }

    protected boolean aceptaMonitoreo(Monitoreo monitoreo) {
        return monitoreo.getClass() == Monitoreo.class;
    }

    public void registrarMonitoreo(Monitoreo monitoreo) {
        if (monitoreo == null || !nombre.equals(monitoreo.getNombrePlanta()) || !aceptaMonitoreo(monitoreo)) {
            throw new IllegalArgumentException("El monitoreo no corresponde a esta planta");
        }
        LocalDateTime fecha = monitoreo.getFechaHora();
        if (!estaActiva || fecha.isBefore(fechaPuestaEnFuncionamiento) || estabaInactivaEn(fecha)) {
            throw new IllegalArgumentException("La planta debe estar activa y operativa en la fecha indicada");
        }
        LocalDateTime hora = fecha.truncatedTo(ChronoUnit.HOURS);
        if (registros.containsKey(hora)) {
            throw new IllegalArgumentException("Ya existe un monitoreo para esa hora");
        }
        double nuevoTotal = totalGenerado + monitoreo.getEnergiaGenerada();
        if (!Double.isFinite(nuevoTotal)) {
            throw new IllegalArgumentException("La energia acumulada excede el rango permitido");
        }
        registros.put(hora, monitoreo);
        totalGenerado = nuevoTotal;
    }

    public double obtenerTotalGenerado() {
        return totalGenerado;
    }

    public abstract double capacidadMinimaCuota();

    public Map<LocalDateTime, Monitoreo> obtenerRegistros() {
        return Collections.unmodifiableMap(registros);
    }

    public abstract boolean cumplioCuota(Monitoreo monitoreo);

    public int[] incidenciasPorHora() {
        int[] incidencias = new int[24];
        for (Monitoreo monitoreo : registros.values()) {
            incidencias[monitoreo.getFechaHora().getHour()] += monitoreo.getCantidadIncidencias();
        }
        return incidencias;
    }

    // O(m) tiempo y O(1) espacio adicional: m monitoreos y un arreglo fijo de 24.
    public int[] histogramaCumplimiento(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null || desde.isAfter(hasta)) {
            throw new IllegalArgumentException("El rango de fechas es invalido");
        }
        int[] histograma = new int[24];
        for (Monitoreo monitoreo : registros.values()) {
            LocalDate dia = monitoreo.getFechaHora().toLocalDate();
            if (!dia.isBefore(desde) && !dia.isAfter(hasta) && cumplioCuota(monitoreo)) {
                histograma[monitoreo.getFechaHora().getHour()]++;
            }
        }
        return histograma;
    }

    public boolean cumplioAlgunaCuota() {
        for (Monitoreo monitoreo : registros.values()) {
            if (cumplioCuota(monitoreo)) {
                return true;
            }
        }
        return false;
    }

    public int limpiarMonitoreosSinDatos() {
        int eliminados = 0;
        Iterator<Monitoreo> iterador = registros.values().iterator();
        while (iterador.hasNext()) {
            Monitoreo monitoreo = iterador.next();
            if (monitoreo.getEnergiaGenerada() == 0 && monitoreo.getCantidadIncidencias() == 0) {
                iterador.remove();
                eliminados++;
            }
        }
        // Solo se eliminan registros de energia cero, por lo que el total no cambia.
        return eliminados;
    }

    @Override
    public String toString() {
        return nombre + " (" + getClass().getSimpleName() + ", " + (estaActiva ? "ACTIVA" : "INACTIVA")
                + "), capacidad: " + capacidadMaxima + ", energia total: " + totalGenerado
                + ", monitoreos: " + registros.values() + ", inactividad: " + periodosInactivos;
    }

    // Intervalo [inicio, fin): el instante de reactivacion ya se considera activo.
    private static class PeriodoInactividad {
        private final LocalDateTime inicio;
        private LocalDateTime fin;

        private PeriodoInactividad(LocalDateTime inicio) {
            this.inicio = inicio;
        }

        private void cerrar(LocalDateTime fin) {
            this.fin = fin;
        }

        private boolean contiene(LocalDateTime fecha) {
            return !fecha.isBefore(inicio) && (fin == null || fecha.isBefore(fin));
        }

        @Override
        public String toString() {
            return "[" + inicio + ", " + (fin == null ? "abierto" : fin) + ")";
        }
    }
}
