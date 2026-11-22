package colegio.dto;

import colegio.exception.DatosInvalidosException;

public class PeticionEditarUsuario {

    private String username;
    private String correo;
    private int idRol;

    public String getUsername() {
        return username;
    }

    public String getCorreo() {
        return correo;
    }

    public int getIdRol() {
        return idRol;
    }

    public void validar() throws DatosInvalidosException {
        if (username == null || username.trim().isEmpty()) {
            throw new DatosInvalidosException("Falta el usuario");
        }

        username = username.trim();
        if (username.length() > 50) {
            throw new DatosInvalidosException("El usuario es muy largo");
        }

        if (correo == null || correo.trim().isEmpty()) {
            throw new DatosInvalidosException("Falta el correo");
        }

        correo = correo.trim();
        int arroba = correo.indexOf("@");
        int punto = correo.lastIndexOf(".");

        if (correo.length() > 100 || arroba <= 0 || punto <= arroba + 1
                || punto == correo.length() - 1 || correo.contains(" ")) {
            throw new DatosInvalidosException("Correo invalido");
        }

        if (idRol <= 0) {
            throw new DatosInvalidosException("Selecciona un rol");
        }
    }
}
