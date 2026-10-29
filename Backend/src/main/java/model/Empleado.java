package model;

import java.math.BigDecimal;

public class Empleado {

    private int id;
    private String nombres;
    private String apellidos;
    private String dpi;
    private String fechaNacimiento;
    private String telefono;
    private String direccion;
    private String puesto;
    private String fechaContratacion;
    private BigDecimal salario;
    private String estado;

    public Empleado(int id, String nombres, String apellidos, String dpi, String fechaNacimiento,
                    String telefono, String direccion, String puesto, String fechaContratacion,
                    BigDecimal salario, String estado) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.dpi = dpi;
        this.fechaNacimiento = fechaNacimiento;
        this.telefono = telefono;
        this.direccion = direccion;
        this.puesto = puesto;
        this.fechaContratacion = fechaContratacion;
        this.salario = salario;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getDpi() {
        return dpi;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getPuesto() {
        return puesto;
    }

    public String getFechaContratacion() {
        return fechaContratacion;
    }

    public BigDecimal getSalario() {
        return salario;
    }

    public String getEstado() {
        return estado;
    }
}