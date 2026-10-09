package com.mycompany.vehiculo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Guarda y recupera copias binarias del inventario de vehículos.
 */
public class GestorBackup {

    private static final Path RUTA_BACKUP = Path.of("datos", "backup", "flota_backup.ser");

    /**
     * Guarda los vehículos recibidos en un archivo de copia de seguridad.
     *
     * @param vehiculos lista de vehículos que se va a guardar
     * @throws IOException si no se puede crear la carpeta o escribir el archivo
     */
    public void guardarBackup(List<Vehiculo> vehiculos) throws IOException {
        Path directorio = RUTA_BACKUP.getParent();
        Files.createDirectories(directorio);

        try (ObjectOutputStream salida = new ObjectOutputStream(
                new BufferedOutputStream(Files.newOutputStream(RUTA_BACKUP)))) {
            salida.writeObject(new ArrayList<>(vehiculos));
        }
    }

    /**
     * Recupera los vehículos guardados en el archivo de copia de seguridad.
     *
     * @return lista nueva con los vehículos recuperados
     * @throws IOException si el archivo no se puede leer o no contiene una lista de vehículos válida
     */
    public List<Vehiculo> cargarBackup() throws IOException {
        try (ObjectInputStream entrada = new ObjectInputStream(
                new BufferedInputStream(Files.newInputStream(RUTA_BACKUP)))) {
            Object contenido = entrada.readObject();
            if (!(contenido instanceof List<?>)) {
                throw new IOException("Copia sin lista de vehículos.");
            }
            List<?> lista = (List<?>) contenido;

            ArrayList<Vehiculo> vehiculos = new ArrayList<>();
            for (Object elemento : lista) {
                if (!(elemento instanceof Vehiculo)) {
                    throw new IOException("Copia tiene un dato que no es vehículo.");
                }
                vehiculos.add((Vehiculo) elemento);
            }
            return vehiculos;
        } catch (ClassNotFoundException e) {
            throw new IOException("Clase en la copia no reconocida.", e);
        }
    }
}
