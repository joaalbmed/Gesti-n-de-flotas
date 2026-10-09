package com.mycompany.vehiculo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import org.xml.sax.SAXException;

/**
 * Punto de entrada que coordina los gestores de la práctica.
 */
public class Main {

    private static final Path RUTA_JSON = Path.of("datos", "json", "flota.json");

    /**
     * Ejecuta la práctica y comunica los errores de lectura, escritura o XML
     * como un fallo de ejecución, sin ocultar su causa.
     *
     * @param args argumentos de la línea de comandos
     */
    public static void main(String[] args) {
        try {
            ejecutarPractica();
        } catch (IOException | SAXException e) {
            throw new IllegalStateException("No se pudo completar la gestión.", e);
        }
    }

    /**
     * Carga o crea el inventario y ejecuta la persistencia binaria, JSON y XML.
     *
     * @throws IOException si falla alguna operación de lectura o escritura
     * @throws SAXException si el informe XML no se puede procesar
     */
    private static void ejecutarPractica() throws IOException, SAXException {
        crearDirectorios();

        GestorInventarioJSON gestorInventario = new GestorInventarioJSON();
        if (Files.exists(RUTA_JSON)) {
            gestorInventario.importarLista();
        } else {
            gestorInventario.agregarVehiculo(new Vehiculo(
                    "1234ABC", "Toyota", "Corolla", 2020, 45000, "Revisión anual"));
            gestorInventario.agregarVehiculo(new Vehiculo(
                    "5678DEF", "Seat", "León", 2019, 62000, "Cambio de aceite"));
            gestorInventario.agregarVehiculo(new Vehiculo(
                    "9012GHI", "Renault", "Clio", 2022, 18000, "Revisión anual"));
            gestorInventario.exportarLista();
        }

        List<Vehiculo> vehiculos = gestorInventario.getVehiculos();

        GestorConfiguracion configuracion = new GestorConfiguracion();
        configuracion.setNombreEmpresa("JoaAlbMed, S.L.");
        configuracion.setVersionSoftware("1.0");
        configuracion.setTotalVehActiv(vehiculos.size());
        configuracion.setKilometrajeAcumulado(calcularKilometraje(vehiculos));
        configuracion.guardarConfiguracion();
        configuracion.leerConfiguracion();

        GestorBackup gestorBackup = new GestorBackup();
        gestorBackup.guardarBackup(vehiculos);
        List<Vehiculo> vehiculosRecuperados = gestorBackup.cargarBackup();

        GestorReportesXML gestorReportes = new GestorReportesXML();
        gestorReportes.generarInforme(vehiculos);
        Set<String> categorias = gestorReportes.leerCategorias();

        System.out.println("Vehículos en el inventario: " + vehiculos.size());
        System.out.println("Vehículos recuperados del backup: " + vehiculosRecuperados.size());
        System.out.println("Kilometraje acumulado: " + configuracion.getKilometrajeAcumulado());
        System.out.println("Categorías de mantenimiento: " + categorias);
        System.out.println("Archivos guardados en la carpeta datos.");
    }

    private static int calcularKilometraje(List<Vehiculo> vehiculos) {
        int total = 0;
        for (Vehiculo vehiculo : vehiculos) {
            total += vehiculo.getKilometraje();
        }
        return total;
    }

    /**
     * Crea las carpetas usadas por los distintos archivos de la práctica.
     *
     * @throws IOException si no se puede crear alguna de las carpetas
     */
    private static void crearDirectorios() throws IOException {
        Files.createDirectories(Path.of("datos", "config"));
        Files.createDirectories(Path.of("datos", "json"));
        Files.createDirectories(Path.of("datos", "backup"));
        Files.createDirectories(Path.of("datos", "reports"));
    }
}
