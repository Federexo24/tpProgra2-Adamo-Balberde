package ar.edu.ungs.prog2.smartgrids;

import static org.junit.Assert.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;

public class ConsultasAdicionalesTest {
    private SmartGrids sistema;
    private static final String INICIO = "2026-01-01T00:00:00";

    @Before
    public void preparar() {
        sistema = new SmartGrids();
        sistema.registrarPlantaSolar("Solar", "Ruta 1", 1000, INICIO, "Sol", 0.5, 500, 20, 12);
        sistema.registrarPlantaEolica("Eolica", "Ruta 2", 400, INICIO, "Viento", 0.5, 10, 80, 5);
        sistema.registrarPlantaHidraulica("Hidro", "Ruta 3", 800, INICIO, "Agua", 0.25, 4, 1000);
        sistema.registrarPlantaTermica("Termica", "Ruta 4", 600, INICIO, "Gas", 50, 900);
    }

    @Test
    public void consultasSinRegistros() {
        assertArrayEquals(new int[24], sistema.incidenciasPorHora("Solar"));
        assertArrayEquals(new int[24], sistema.histogramaCumplimiento("Solar", "2026-01-01", "2026-01-03"));
        assertEquals(Arrays.asList("Solar", "Eolica", "Hidro", "Termica"), sistema.plantasQueNuncaCumplieronCuota());
        assertEquals(Arrays.asList("Solar: 0.0", "Eolica: 0.0", "Hidro: 0.0", "Termica: 0.0"),
                sistema.plantasOrdenadasPorEnergiaGenerada());
        assertEquals(0, sistema.limpiarMonitoreosSinDatos());
    }

    @Test
    public void histogramaIncluyeExtremosYCuentaDiasEnCadaHora() {
        sistema.registrarMonitoreo("Solar", "2026-02-28T23:00:00", 300, 0);
        sistema.registrarMonitoreo("Solar", "2026-03-01T00:00:00", 250, 0);
        sistema.registrarMonitoreo("Solar", "2026-03-01T12:15:00", 300, 0);
        sistema.registrarMonitoreo("Solar", "2026-03-02T12:59:59", 250, 0);
        sistema.registrarMonitoreo("Solar", "2026-03-02T23:59:59", 249, 0);
        sistema.registrarMonitoreo("Solar", "2026-03-03T12:00:00", 300, 0);
        int[] esperado = new int[24];
        esperado[0] = 1;
        esperado[12] = 2;
        assertArrayEquals(esperado, sistema.histogramaCumplimiento("Solar", "2026-03-01", "2026-03-02"));
        int[] diaUnico = new int[24];
        diaUnico[12] = 1;
        assertArrayEquals(diaUnico, sistema.histogramaCumplimiento("Solar", "2026-03-02", "2026-03-02"));
    }

    @Test
    public void consultasRechazanFechasInvalidasYRangosInvertidos() {
        assertThrows(RuntimeException.class, () -> sistema.histogramaCumplimiento("Solar", "2026-02-30", "2026-03-02"));
        assertThrows(RuntimeException.class, () -> sistema.histogramaCumplimiento("Solar", "2026-03-02", "2026-03-01"));
        assertThrows(RuntimeException.class, () -> sistema.histogramaCumplimiento("Solar", null, "2026-03-01"));
        assertThrows(RuntimeException.class, () -> sistema.histogramaCumplimiento("No existe", "2026-03-01", "2026-03-02"));
        assertThrows(RuntimeException.class, () -> sistema.incidenciasPorHora("No existe"));
        assertThrows(RuntimeException.class, () -> sistema.plantasInactivasEn("fecha incorrecta"));
    }

    @Test
    public void rechazaDosRegistrosDeLaMismaHoraSinAlterarTotal() {
        sistema.registrarMonitoreo("Solar", "2026-03-01T12:10:00", 250, 2);
        assertThrows(RuntimeException.class,
                () -> sistema.registrarMonitoreo("Solar", "2026-03-01T12:50:00", 500, 5));
        assertEquals(250, sistema.totalGenerado("Solar"), 0);
        assertEquals(2, sistema.incidenciasPorHora("Solar")[12]);
        assertEquals(1, sistema.histogramaCumplimiento("Solar", "2026-03-01", "2026-03-01")[12]);
    }

    @Test
    public void cuotasUsanReglasDeCadaTipo() {
        sistema.registrarMonitoreo("Eolica", "2026-03-01T01:00:00", 200, 0, 6);
        sistema.registrarMonitoreo("Hidro", "2026-03-01T01:00:00", 200, 0);
        sistema.registrarMonitoreo("Termica", "2026-03-01T01:00:00", 300, 0, 49, 10);
        sistema.registrarMonitoreo("Termica", "2026-03-01T02:00:00", 300, 0, 50, 10);
        sistema.registrarMonitoreo("Termica", "2026-03-01T03:00:00", 299, 0, 0, 10);
        assertEquals(1, sistema.histogramaCumplimiento("Eolica", "2026-03-01", "2026-03-01")[1]);
        assertEquals(1, sistema.histogramaCumplimiento("Hidro", "2026-03-01", "2026-03-01")[1]);
        int[] esperado = new int[24];
        esperado[1] = 1;
        assertArrayEquals(esperado, sistema.histogramaCumplimiento("Termica", "2026-03-01", "2026-03-01"));
        assertEquals(Collections.singletonList("Solar"), sistema.plantasQueNuncaCumplieronCuota());
    }

    @Test
    public void fuentesOrdenadasSinContarTermicas() {
        sistema.registrarPlantaSolar("Otra solar", "Ruta", 100, INICIO, "Sol", 1, 10, 2, 12);
        assertEquals(Arrays.asList("Agua", "Sol", "Viento"),
                new java.util.ArrayList<>(sistema.plantasRenovablesPorFuente().keySet()));
        assertEquals(Integer.valueOf(2), sistema.plantasRenovablesPorFuente().get("Sol"));
    }

    @Test
    public void vientoAgrupaDiasYExcluyeHoraQueAlgunaVezAlcanzoElMinimo() {
        sistema.registrarMonitoreo("Eolica", "2026-03-01T01:00:00", 10, 0, 3);
        sistema.registrarMonitoreo("Eolica", "2026-03-02T01:00:00", 10, 0, 5);
        sistema.registrarMonitoreo("Eolica", "2026-03-01T02:00:00", 10, 0, 4);
        sistema.registrarMonitoreo("Eolica", "2026-03-02T02:00:00", 10, 0, 2);
        sistema.registrarMonitoreo("Eolica", "2026-03-01T05:00:00", 10, 0, 0);
        assertEquals("Plantas Eolicas: Horas con poco viento\n- Eolica:  5 m/s\n       02: 4 m/s\n       05: 0 m/s",
                sistema.horasSinVelocidadMinimaPorPlantaEolica());
    }

    @Test
    public void limpiezaBorraSoloEnergiaEIncidenciasCeroEnTodosLosTipos() {
        sistema.registrarMonitoreo("Solar", "2026-03-01T01:00:00", 0, 0);
        sistema.registrarMonitoreo("Solar", "2026-03-01T02:00:00", 100, 0);
        sistema.registrarMonitoreo("Solar", "2026-03-01T03:00:00", 0, 2);
        sistema.registrarMonitoreo("Eolica", "2026-03-01T04:00:00", 0, 0, 8);
        sistema.registrarMonitoreo("Eolica", "2026-03-02T04:00:00", 10, 0, 3);
        sistema.registrarMonitoreo("Termica", "2026-03-01T01:00:00", 0, 0, 20, 10);
        assertFalse(sistema.horasSinVelocidadMinimaPorPlantaEolica().contains("04:"));
        assertEquals(3, sistema.limpiarMonitoreosSinDatos());
        assertEquals(0, sistema.limpiarMonitoreosSinDatos());
        assertEquals(100, sistema.totalGenerado("Solar"), 0);
        assertEquals(2, sistema.incidenciasPorHora("Solar")[3]);
        assertTrue(sistema.horasSinVelocidadMinimaPorPlantaEolica().contains("04: 3 m/s"));
        sistema.registrarMonitoreo("Solar", "2026-03-01T01:00:00", 250, 0);
        assertEquals(350, sistema.totalGenerado("Solar"), 0);
    }

    @Test
    public void ordenamientoEstableNoCambiaElOrdenDeRegistro() {
        sistema.registrarMonitoreo("Hidro", "2026-03-01T01:00:00", 100, 0);
        sistema.registrarMonitoreo("Solar", "2026-03-01T01:00:00", 100, 0);
        sistema.registrarMonitoreo("Termica", "2026-03-01T01:00:00", 300, 0, 20, 10);
        assertEquals(Arrays.asList("Termica: 300.0", "Solar: 100.0", "Hidro: 100.0", "Eolica: 0.0"),
                sistema.plantasOrdenadasPorEnergiaGenerada());
        assertTrue(sistema.consultarPlantas().get(0).startsWith("Solar "));
    }

    @Test
    public void historialConservaVariosPeriodosYRespetaLimites() {
        sistema.desactivarPlanta("Solar", "2026-03-01T09:00:00");
        sistema.desactivarPlanta("Solar", "2026-03-01T10:00:00");
        sistema.activarPlanta("Solar", "2026-03-01T11:00:00");
        sistema.activarPlanta("Solar", "2026-03-01T12:00:00");
        sistema.desactivarPlanta("Solar", "2026-03-02T09:00:00");
        assertTrue(sistema.plantasInactivasEn("2025-12-01T00:00:00").isEmpty());
        assertTrue(sistema.plantasInactivasEn("2026-03-01T08:59:59").isEmpty());
        assertEquals(Collections.singletonList("Solar"), sistema.plantasInactivasEn("2026-03-01T09:00:00"));
        assertEquals(Collections.singletonList("Solar"), sistema.plantasInactivasEn("2026-03-01T10:59:59"));
        assertTrue(sistema.plantasInactivasEn("2026-03-01T11:00:00").isEmpty());
        assertEquals(Collections.singletonList("Solar"), sistema.plantasInactivasEn("2026-03-03T00:00:00"));
        sistema.activarPlanta("Solar", "2026-03-03T10:00:00");
        assertTrue(sistema.estaActiva("Solar"));
        assertEquals(Collections.singletonList("Solar"), sistema.plantasInactivasEn("2026-03-02T10:00:00"));
        assertThrows(RuntimeException.class,
                () -> sistema.registrarMonitoreo("Solar", "2026-03-02T10:00:00", 100, 0));
        assertThrows(RuntimeException.class, () -> sistema.desactivarPlanta("Solar", "2026-02-01T00:00:00"));
        assertTrue(sistema.estaActiva("Solar"));
    }

    @Test
    public void datosInvalidosNoModificanTotalesNiRegistros() {
        assertThrows(RuntimeException.class,
                () -> sistema.registrarMonitoreo("Solar", "2026-03-01T12:00:00", 10, 0, 3));
        assertThrows(RuntimeException.class,
                () -> sistema.registrarMonitoreo("Eolica", "2026-03-01T12:00:00", 10, 0));
        assertThrows(RuntimeException.class,
                () -> sistema.registrarMonitoreo("Hidro", "2026-03-01T12:00:00", Double.NaN, 0));
        assertThrows(RuntimeException.class,
                () -> sistema.registrarMonitoreo("Termica", "2026-03-01T12:00:00", 10, 0, -1, 3));
        assertThrows(RuntimeException.class,
                () -> sistema.registrarMonitoreo("Solar", "2025-12-31T23:00:00", 10, 0));
        sistema.registrarMonitoreo("Solar", "2026-03-01T12:00:00", 250, 0);
        assertEquals(250, sistema.totalGenerado("Solar"), 0);
        assertEquals(0, sistema.totalGenerado("Eolica"), 0);
        assertEquals(0, sistema.totalGenerado("Hidro"), 0);
        assertEquals(0, sistema.totalGenerado("Termica"), 0);
    }

    @Test
    public void mapaDeMonitoreosNoPermiteEvitarLasValidaciones() {
        PlantaSolar planta = new PlantaSolar("S", "Ruta", 100, LocalDateTime.parse(INICIO), "Sol", 0.5, 20, 1, 12);
        planta.registrarMonitoreo(new Monitoreo("S", LocalDateTime.parse("2026-03-01T12:00:00"), 100, 1));
        assertThrows(UnsupportedOperationException.class, () -> planta.obtenerRegistros().clear());
        assertEquals(100, planta.obtenerTotalGenerado(), 0);
    }
}
