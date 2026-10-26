package model;

public class Encargado {

    private int id;
    private String nombres;
    private String apellidos;
    private String dpi;
    private String telefono;
    private String correo;
    private String direccion;
    private String parentesco;

    public Encargado(int id, String nombres, String apellidos, String dpi, String telefono,
                     String correo, String direccion, String parentesco) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.dpi = dpi;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
        this.parentesco = parentesco;
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

    public String getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getParentesco() {
        return parentesco;
    }
}