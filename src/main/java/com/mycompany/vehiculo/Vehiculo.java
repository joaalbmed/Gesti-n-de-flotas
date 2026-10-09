/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.vehiculo;


import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Vehiculo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String matricula;
    private String marca;
    private String modelo;
    private Integer anyo;

    @SerializedName("kms_recorridos")
    private Integer kilometraje;

    private String categoriaMantenimiento;

    public Vehiculo(String matricula, String marca, String modelo, int anyo,
            int kilometraje, String categoriaMantenimiento) {
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.anyo = anyo;
        this.kilometraje = kilometraje;
        this.categoriaMantenimiento = categoriaMantenimiento;
    }

    public Vehiculo() {
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public void setAnyo(int anyo) {
        this.anyo = anyo;
    }

    public void setKilometraje(int kilometraje) {
        this.kilometraje = kilometraje;
    }

    public void setCategoriaMantenimiento(String categoriaMantenimiento) {
        this.categoriaMantenimiento = categoriaMantenimiento;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public Integer getAnyo() {
        return anyo;
    }

    public Integer getKilometraje() {
        return kilometraje;
    }

    public String getCategoriaMantenimiento() {
        return categoriaMantenimiento;
    }

    @Override
    public String toString() {
        return "Vehiculo{" + "matricula=" + matricula + ", marca=" + marca
                + ", modelo=" + modelo + ", anyo=" + anyo + ", kilometraje="
                + kilometraje + ", categoriaMantenimiento=" + categoriaMantenimiento + '}';
    }
}
