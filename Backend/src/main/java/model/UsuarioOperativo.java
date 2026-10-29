package model;

public class UsuarioOperativo {

    private int id;
    private int idRol;
    private String rol;
    private String username;
    private String correo;
    private String estado;
    private String fechaCreacion;
    private String ultimoAcceso;
    private Integer idEmpleado;
    private String empleado;

    public UsuarioOperativo(int id, int idRol, String rol, String username, String correo, String estado,
                            String fechaCreacion, String ultimoAcceso, Integer idEmpleado, String empleado) {
        this.id = id;
        this.idRol = idRol;
        this.rol = rol;
        this.username = username;
        this.correo = correo;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.ultimoAcceso = ultimoAcceso;
        this.idEmpleado = idEmpleado;
        this.empleado = empleado;
    }

    public int getId() {
        return id;
    }

    public int getIdRol() {
        return idRol;
    }

    public String getRol() {
        return rol;
    }

    public String getUsername() {
        return username;
    }

    public String getCorreo() {
        return correo;
    }

    public String getEstado() {
        return estado;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public String getUltimoAcceso() {
        return ultimoAcceso;
    }

    public Integer getIdEmpleado() {
        return idEmpleado;
    }

    public String getEmpleado() {
        return empleado;
    }
}