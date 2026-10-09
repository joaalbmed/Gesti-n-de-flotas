package com.mycompany.vehiculo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Gestiona el inventario en memoria y su importación y exportación en JSON.
 */
public class GestorInventarioJSON {

    private static final String RUTA_JSON = "datos/json/flota.json";

    private final ArrayList<Vehiculo> vehiculos = new ArrayList<>();
    private final HashMap<String, Vehiculo> vehiculosPorMatricula = new HashMap<>();


    /**
     * Importa los vehículos del archivo JSON y actualiza la lista y el índice
     * por matrícula después de comprobar que los datos son válidos.
     *
     * @throws IOException si no se puede leer el archivo o sus datos no son válidos
     */
    public void importarLista() throws IOException {
        Gson gson = new Gson();
        Vehiculo[] vehiculosLeidos;

        try (Reader lector = Files.newBufferedReader(
                Path.of(RUTA_JSON), StandardCharsets.UTF_8)) {
            try {
                vehiculosLeidos = gson.fromJson(lector, Vehiculo[].class);
            } catch (JsonParseException e) {
                throw new IOException("JSON Inválido: " + RUTA_JSON, e);
            }
        }

        if (vehiculosLeidos == null) {
            throw new IOException("No es un JSON: " + RUTA_JSON);
        }


        ArrayList<Vehiculo> nuevaLista = new ArrayList<>(Arrays.asList(vehiculosLeidos));
        HashMap<String, Vehiculo> nuevoMapa = new HashMap<>();

        for (Vehiculo vehiculo : nuevaLista) {
            if (vehiculo == null || vehiculo.getMatricula() == null
                    || vehiculo.getMatricula().isBlank()) {
                throw new IOException("Vehículo sin matrícula.");
            }
            Integer anyo = vehiculo.getAnyo();
            Integer kilometraje = vehiculo.getKilometraje();
            if (anyo == null || anyo <= 0 || kilometraje == null || kilometraje < 0) {
                throw new IOException("Año o kilometraje inválido: "
                        + vehiculo.getMatricula());
            }
            if (nuevoMapa.putIfAbsent(vehiculo.getMatricula(), vehiculo) != null) {
                throw new IOException("Matrícula duplicada: " + vehiculo.getMatricula());
            }
        }

        vehiculos.clear();
        vehiculos.addAll(nuevaLista);
        vehiculosPorMatricula.clear();
        vehiculosPorMatricula.putAll(nuevoMapa);
    }

    
    /**
     * Guarda el inventario actual en el archivo JSON con formato legible.
     *
     * @throws IOException si no se puede crear la carpeta o escribir el archivo
     */
    public void exportarLista() throws IOException {
        Path ruta = Path.of(RUTA_JSON);
        Path directorio = ruta.getParent();

        if (directorio != null) {
            Files.createDirectories(directorio);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer escritor = Files.newBufferedWriter(ruta, StandardCharsets.UTF_8)) {
            gson.toJson(vehiculos, escritor);
        }
    }

    
    /**
     * Devuelve los vehículos en el orden en que están registrados, sin permitir
     * que se modifique la lista desde fuera del gestor.
     *
     * @return vista de solo lectura del inventario
     */
    public List<Vehiculo> getVehiculos() {
        return Collections.unmodifiableList(vehiculos);
    }

   
    /**
     * Añade un vehículo a la lista y al índice por matrícula.
     *
     * @param vehiculo vehículo que se va a registrar
     * @throws IllegalArgumentException si el vehículo no tiene matrícula o esta ya existe
     */
    public void agregarVehiculo(Vehiculo vehiculo) {
        if (vehiculo == null || vehiculo.getMatricula() == null
                || vehiculo.getMatricula().isBlank()) {
            throw new IllegalArgumentException("Vehículo sin matrícula.");
        }
        if (vehiculosPorMatricula.containsKey(vehiculo.getMatricula())) {
            throw new IllegalArgumentException(
                    "Matrícula duplicada: " + vehiculo.getMatricula());
        }

        vehiculos.add(vehiculo);
        vehiculosPorMatricula.put(vehiculo.getMatricula(), vehiculo);
    }

    /**
     * Busca un vehículo por matrícula mediante el índice del inventario.
     *
     * @param matricula matrícula que se quiere buscar
     * @return vehículo encontrado o {@code null} si no está registrado
     */
    public Vehiculo buscarPorMatricula(String matricula) {
        return vehiculosPorMatricula.get(matricula);
    }
}
