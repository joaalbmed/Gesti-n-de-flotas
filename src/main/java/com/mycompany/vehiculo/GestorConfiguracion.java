package com.mycompany.vehiculo;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Lee y guarda la configuración general de la empresa en un archivo binario.
 */
public class GestorConfiguracion {
    private String nombreEmpresa;
    private String versionSoftware;
    private int totalVehActiv;
    private int kilometrajeAcumulado;
    private static final String RUTA = "datos/config/empresa.dat";

    public GestorConfiguracion() {}

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public String getVersionSoftware() {
        return versionSoftware;
    }

    public int getTotalVehActiv() {
        return totalVehActiv;
    }

    public int getKilometrajeAcumulado() {
        return kilometrajeAcumulado;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;

    }

    public void setVersionSoftware(String versionSoftware) {
        this.versionSoftware = versionSoftware;
    }

    public void setTotalVehActiv(int totalVehActiv) {
        this.totalVehActiv = totalVehActiv;
    }

    public void setKilometrajeAcumulado(int kilometrajeAcumulado) {
        this.kilometrajeAcumulado = kilometrajeAcumulado;
    }
        

    /**
     * Guarda el nombre, la versión, el total de vehículos y el kilometraje
     * acumulado en el archivo de configuración.
     *
     * @throws IOException si no se puede crear la carpeta o escribir el archivo
     */
    public void guardarConfiguracion() throws IOException {
        File directorio = new File(RUTA).getParentFile();
        if (!directorio.exists() && !directorio.mkdirs()) {
            throw new IOException("No se pudo crear el directorio: " + directorio);
        }

        try (DataOutputStream salida = new DataOutputStream(new FileOutputStream(RUTA))) {
            salida.writeUTF(nombreEmpresa);
            salida.writeUTF(versionSoftware);
            salida.writeInt(totalVehActiv);
            salida.writeInt(kilometrajeAcumulado);
        }
    }
    
    /**
     * Lee del archivo los datos de configuración y actualiza este objeto.
     *
     * @throws IOException si no se puede leer el archivo o está incompleto
     */
    public void leerConfiguracion() throws IOException {
        try (DataInputStream entrada = new DataInputStream(new FileInputStream(RUTA))) {
            String nombreLeido = entrada.readUTF();
            String versionLeida = entrada.readUTF();
            int totalLeido = entrada.readInt();
            int kilometrajeLeido = entrada.readInt();

            nombreEmpresa = nombreLeido;
            versionSoftware = versionLeida;
            totalVehActiv = totalLeido;
            kilometrajeAcumulado = kilometrajeLeido;
        } catch (EOFException e) {
            throw new IOException("El archivo de configuración está incompleto: " + RUTA, e);
        }
    }
}