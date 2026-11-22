package colegio.dto;

import colegio.model.Usuario;

public class RespuestaUsuario {

    private int id;
    private int idRol;
    private String rol;
    private String username;
    private String correo;
    private String estado;

    public RespuestaUsuario(Usuario usuario) {
        this.id = usuario.getId();
        this.idRol = usuario.getIdRol();
        this.rol = usuario.getRol();
        this.username = usuario.getUsername();
        this.correo = usuario.getCorreo();
        this.estado = usuario.getEstado();
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
}
