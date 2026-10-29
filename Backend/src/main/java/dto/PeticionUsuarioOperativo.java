package dto;

import exception.DatosInvalidosException;

import java.util.List;

public class PeticionUsuarioOperativo {

    public static final List<String> ROLES = List.of("SECRETARIA", "MAESTRO", "BIBLIOTECARIO", "MANTENIMIENTO");

    private String username;
    private String correo;
    private String password;
    private String rol;
    private int idEmpleado;

    public String getUsername() {
        return username == null ? null : username.trim();
    }

    public String getCorreo() {
        return correo == null ? null : correo.trim();
    }

    public String getPassword() {
        return password;
    }

    public String getRol() {
        return rol;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public boolean traePassword() {
        return password != null && !password.isEmpty();
    }

    public void validar() throws DatosInvalidosException {
        if (getUsername() == null || !getUsername().matches("[A-Za-z0-9._-]{4,50}")) {
            throw new DatosInvalidosException(
                    "El usuario debe tener de 4 a 50 caracteres: letras, números, punto, guion o guion bajo");
        }
        if (rol == null || !ROLES.contains(rol)) {
            throw new DatosInvalidosException("Debes elegir un rol válido");
        }
        if (idEmpleado < 1) {
            throw new DatosInvalidosException("Debes elegir el empleado de esta cuenta");
        }
        validarCorreo();
        if (!traePassword()) {
            throw new DatosInvalidosException("La contraseña es obligatoria");
        }
        validarPassword();
    }

    public void validarEdicion() throws DatosInvalidosException {
        validarCorreo();
        if (traePassword()) {
            validarPassword();
        }
    }

    private void validarCorreo() throws DatosInvalidosException {
        if (getCorreo() == null || getCorreo().isEmpty()) {
            throw new DatosInvalidosException("El correo es obligatorio");
        }
        if (getCorreo().length() > 100) {
            throw new DatosInvalidosException("El correo no puede pasar de 100 caracteres");
        }
        if (!getCorreo().matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")) {
            throw new DatosInvalidosException("El correo no tiene un formato válido");
        }
    }

    private void validarPassword() throws DatosInvalidosException {
        if (password.length() < 6 || password.length() > 50) {
            throw new DatosInvalidosException("La contraseña debe tener de 6 a 50 caracteres");
        }
    }
}