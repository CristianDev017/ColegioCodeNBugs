package dto;

import exception.DatosInvalidosException;
import util.Validaciones;

public class PeticionEncargado {
    private String nombres;
    private String apellidos;
    private String dpi;
    private String telefono;
    private String correo;
    private String direccion;
    private String parentesco;

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

    public void validar() throws DatosInvalidosException {
        nombres = Validaciones.limpiar(nombres);
        apellidos = Validaciones.limpiar(apellidos);
        dpi = Validaciones.limpiar(dpi);
        telefono = Validaciones.limpiar(telefono);
        correo = Validaciones.limpiar(correo);
        direccion = Validaciones.limpiar(direccion);
        parentesco = Validaciones.limpiar(parentesco);
        if (nombres == null || nombres.length() > 80) {
            throw new DatosInvalidosException("Los nombres del encargado son obligatorios (máximo 80 caracteres)");
        }
        if (apellidos == null || apellidos.length() > 80) {
            throw new DatosInvalidosException("Los apellidos del encargado son obligatorios (máximo 80 caracteres)");
        }
        if (!Validaciones.soloDigitos(dpi, 13, 13)) {
            throw new DatosInvalidosException("El DPI del encargado debe tener 13 números");
        }
        if (!Validaciones.soloDigitos(telefono, 8, 15)) {
            throw new DatosInvalidosException("El teléfono del encargado debe tener entre 8 y 15 números");
        }
        if (correo != null && (!Validaciones.correoValido(correo) || correo.length() > 100)) {
            throw new DatosInvalidosException("El correo del encargado no es válido");
        }
        if (direccion != null && direccion.length() > 200) {
            throw new DatosInvalidosException("La dirección del encargado no puede pasar de 200 caracteres");
        }
        if (parentesco == null || parentesco.length() > 30) {
            throw new DatosInvalidosException("El parentesco es obligatorio (máximo 30 caracteres)");
        }
    }
}
