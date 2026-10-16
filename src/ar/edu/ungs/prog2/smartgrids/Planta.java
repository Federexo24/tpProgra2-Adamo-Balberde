package ar.edu.ungs.prog2.smartgrids;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Reúne los datos y comportamientos comunes a todas las plantas.
 * Administra monitoreos, energía acumulada e historial de inactividad.
 * Es abstracta porque cada tipo debe definir cómo calcula y cumple su cuota.
 */
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

    /**
     * Valida los datos comunes y crea una planta activa, sin monitoreos ni períodos de inactividad.
     */
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

    /**
     * Devuelve el nombre que identifica a la planta.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Devuelve la dirección registrada de la planta.
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * Devuelve la capacidad máxima declarada de generación.
     */
    public double getCapacidadMaxima() {
        return capacidadMaxima;
    }

    /**
     * Devuelve el instante a partir del cual la planta está en funcionamiento.
     */
    public LocalDateTime getFechaPuestaEnFuncionamiento() {
        return fechaPuestaEnFuncionamiento;
    }

    /**
     * Indica el estado actual, sin recorrer el historial.
     */
    public boolean getEstaActiva() {
        return estaActiva;
    }

    /**
     * Devuelve el total de energía acumulado al incorporar monitoreos.
     */
    public double getTotalGenerado() {
        return totalGenerado;
    }

    /**
     * Rechaza fechas nulas o anteriores al último cambio efectivo de estado.
     * Inicialmente el límite es la puesta en funcionamiento.
     */
    private void validarCambioEstado(LocalDateTime fechaHora) {
        if (fechaHora == null || fechaHora.isBefore(ultimoCambioEstado)) {
            throw new IllegalArgumentException("Los cambios de estado deben registrarse en orden cronologico");
        }
    }

    /**
     * Cierra el último período abierto y marca la planta como activa en tiempo constante.
     * Si ya está activa, valida la fecha pero no crea un cambio de estado.
     */
    public void activar(LocalDateTime fechaHora) {
        validarCambioEstado(fechaHora);
        if (!estaActiva) {
            periodosInactivos.getLast().cerrar(fechaHora);
            estaActiva = true;
            ultimoCambioEstado = fechaHora;
        }
    }

    /**
     * Abre un período de inactividad y marca la planta como inactiva en tiempo constante.
     * Si ya está inactiva, conserva el inicio del período existente.
     */
    public void desactivar(LocalDateTime fechaHora) {
        validarCambioEstado(fechaHora);
        if (estaActiva) {
            periodosInactivos.addLast(new PeriodoInactividad(fechaHora));
            estaActiva = false;
            ultimoCambioEstado = fechaHora;
        }
    }

    /**
     * Busca si el instante pertenece a alguno de los períodos de inactividad.
     * El inicio está incluido y la reactivación queda excluida; antes de la puesta en marcha no hay períodos.
     */
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

    /**
     * Acepta el registro básico usado por plantas solares e hidráulicas.
     * Las plantas eólicas y térmicas sobrescriben este método para exigir sus registros específicos.
     */
    protected boolean aceptaMonitoreo(Monitoreo monitoreo) {
        return monitoreo.getClass() == Monitoreo.class;
    }

    /**
     * Comprueba que el monitoreo corresponda a esta planta y que esté activa actualmente y en la fecha registrada.
     * Rechaza fechas anteriores a la puesta en marcha y dos registros de la misma fecha y hora.
     * Solo después de validar todo guarda el registro y actualiza el total de energía.
     */
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

    /**
     * Consulta en tiempo constante el total guardado, sin recorrer los monitoreos.
     */
    public double obtenerTotalGenerado() {
        return totalGenerado;
    }

    /**
     * Obliga a cada subclase concreta a definir la energía mínima necesaria para cumplir su cuota.
     */
    public abstract double capacidadMinimaCuota();

    /**
     * Devuelve una vista de los monitoreos que no permite agregar ni eliminar entradas desde afuera.
     * La vista refleja cambios posteriores realizados por la planta; sus claves representan horas completas.
     */
    public Map<LocalDateTime, Monitoreo> obtenerRegistros() {
        return Collections.unmodifiableMap(registros);
    }

    /**
     * Obliga a cada tipo de planta a decidir si un monitoreo cumple sus condiciones de cuota.
     */
    public abstract boolean cumplioCuota(Monitoreo monitoreo);

    /**
     * Recorre los monitoreos y suma sus incidencias en un arreglo de 24 posiciones.
     * Por ejemplo, la posición 12 acumula incidencias de las 12 horas de todos los días.
     */
    public int[] incidenciasPorHora() {
        int[] incidencias = new int[24];
        for (Monitoreo monitoreo : registros.values()) {
            incidencias[monitoreo.getFechaHora().getHour()] += monitoreo.getCantidadIncidencias();
        }
        return incidencias;
    }

    // O(m) tiempo y O(1) espacio adicional: m monitoreos y un arreglo fijo de 24.
    /**
     * Cuenta los días en que se cumplió la cuota para cada hora entre desde y hasta, ambos incluidos.
     * Como solo puede existir un registro por fecha y hora, cada incremento representa un día distinto.
     * Recorre m monitoreos: tiempo O(m) y espacio adicional O(1), por el arreglo fijo de 24 posiciones.
     */
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

    /**
     * Busca un registro conservado que cumpla la cuota y termina al encontrarlo.
     * Devuelve false si no hay registros o ninguno cumple.
     */
    public boolean cumplioAlgunaCuota() {
        for (Monitoreo monitoreo : registros.values()) {
            if (cumplioCuota(monitoreo)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Elimina registros que tienen simultáneamente energía e incidencias en cero.
     * Usa Iterator.remove para borrar de forma segura durante el recorrido y devuelve cuántos borró.
     */
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

    /**
     * Describe el tipo, estado, capacidad, total generado, monitoreos e inactividad de la planta.
     */
    @Override
    public String toString() {
        return nombre + " (" + getClass().getSimpleName() + ", " + (estaActiva ? "ACTIVA" : "INACTIVA")
                + "), capacidad: " + capacidadMaxima + ", energia total: " + totalGenerado
                + ", monitoreos: " + registros.values() + ", inactividad: " + periodosInactivos;
    }

    // Intervalo [inicio, fin): el instante de reactivacion ya se considera activo.
    /**
     * Representa un intervalo de inactividad interno a Planta.
     * El inicio es fijo; fin permanece null mientras la planta sigue inactiva.
     */
    private static class PeriodoInactividad {
        private final LocalDateTime inicio;
        private LocalDateTime fin;

        /**
         * Abre un período en el instante de desactivación, todavía sin fecha de cierre.
         */
        private PeriodoInactividad(LocalDateTime inicio) {
            this.inicio = inicio;
        }

        /**
         * Guarda el instante de reactivación. Planta valida previamente el orden cronológico.
         */
        private void cerrar(LocalDateTime fin) {
            this.fin = fin;
        }

        /**
         * Indica si una fecha está dentro de [inicio, fin).
         * Si fin es null, el intervalo sigue abierto y contiene cualquier fecha desde el inicio.
         */
        private boolean contiene(LocalDateTime fecha) {
            return !fecha.isBefore(inicio) && (fin == null || fecha.isBefore(fin));
        }

        /**
         * Representa el período como intervalo; muestra abierto cuando todavía no tiene fecha de reactivación.
         */
        @Override
        public String toString() {
            return "[" + inicio + ", " + (fin == null ? "abierto" : fin) + ")";
        }
    }
}
