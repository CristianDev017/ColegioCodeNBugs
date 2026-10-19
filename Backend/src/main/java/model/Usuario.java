package model;

public class Usuario {

    private int id;
    private int idRol;
    private String rol;
    private String username;
    private String correo;
    private String password;
    private String estado;

    public Usuario(int id, int idRol, String rol, String username, String correo, String password, String estado) {
        this.id = id;
        this.idRol = idRol;
        this.rol = rol;
        this.username = username;
        this.correo = correo;
        this.password = password;
        this.estado = estado;
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

    public String getPassword() {
        return password;
    }

    public String getEstado() {
        return estado;
    }
}