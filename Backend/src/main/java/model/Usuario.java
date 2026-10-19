package model;

public record Usuario(int id, int idRol, String rol, String username, String correo, String password, String estado) {
}