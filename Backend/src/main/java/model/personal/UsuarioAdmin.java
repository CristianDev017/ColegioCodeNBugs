package model.personal;

public class UsuarioAdmin {

    private int id;
    private String username;
    private String correo;
    private String rol;
    private String estado;
    private String fechaCreacion;
    private String ultimoAcceso;

    public UsuarioAdmin(int id, String username, String correo, String rol, String estado,
                        String fechaCreacion, String ultimoAcceso) {
        this.id = id;
        this.username = username;
        this.correo = correo;
        this.rol = rol;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.ultimoAcceso = ultimoAcceso;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getCorreo() {
        return correo;
    }

    public String getRol() {
        return rol;
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
}