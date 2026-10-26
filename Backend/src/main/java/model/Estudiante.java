package model;

public class Estudiante {

    private int id;
    private String carnet;
    private String nombres;
    private String apellidos;
    private String dpi;
    private String fechaNacimiento;
    private String genero;
    private String direccion;
    private String estado;
    private FichaMedica ficha;
    private Encargado encargado;

    public Estudiante(int id, String carnet, String nombres, String apellidos, String dpi,
                      String fechaNacimiento, String genero, String direccion, String estado,
                      FichaMedica ficha, Encargado encargado) {
        this.id = id;
        this.carnet = carnet;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.dpi = dpi;
        this.fechaNacimiento = fechaNacimiento;
        this.genero = genero;
        this.direccion = direccion;
        this.estado = estado;
        this.ficha = ficha;
        this.encargado = encargado;
    }

    public int getId() {
        return id;
    }

    public String getCarnet() {
        return carnet;
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

    public String getGenero() {
        return genero;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getEstado() {
        return estado;
    }

    public FichaMedica getFicha() {
        return ficha;
    }

    public Encargado getEncargado() {
        return encargado;
    }
}