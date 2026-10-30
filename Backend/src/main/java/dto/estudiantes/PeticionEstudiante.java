package dto.estudiantes;

import dto.PeticionEncargado;
import exception.DatosInvalidosException;
import util.Validaciones;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class PeticionEstudiante {

    private String nombres;
    private String apellidos;
    private String dpi;
    private String fechaNacimiento;
    private String genero;
    private String direccion;
    private String alergias;
    private String condiciones;
    private String contactoEmergencia;
    private String telefonoEmergencia;
    private PeticionEncargado encargado;

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

    public String getAlergias() {
        return alergias;
    }

    public String getCondiciones() {
        return condiciones;
    }

    public String getContactoEmergencia() {
        return contactoEmergencia;
    }

    public String getTelefonoEmergencia() {
        return telefonoEmergencia;
    }

    public PeticionEncargado getEncargado() {
        return encargado;
    }

    public void validar() throws DatosInvalidosException {
        nombres = Validaciones.limpiar(nombres);
        apellidos = Validaciones.limpiar(apellidos);
        dpi = Validaciones.limpiar(dpi);
        fechaNacimiento = Validaciones.limpiar(fechaNacimiento);
        genero = Validaciones.limpiar(genero);
        direccion = Validaciones.limpiar(direccion);
        alergias = Validaciones.limpiar(alergias);
        condiciones = Validaciones.limpiar(condiciones);
        contactoEmergencia = Validaciones.limpiar(contactoEmergencia);
        telefonoEmergencia = Validaciones.limpiar(telefonoEmergencia);

        if (nombres == null || nombres.length() > 80) {
            throw new DatosInvalidosException("Los nombres son obligatorios (máximo 80 caracteres)");
        }
        if (apellidos == null || apellidos.length() > 80) {
            throw new DatosInvalidosException("Los apellidos son obligatorios (máximo 80 caracteres)");
        }
        if (dpi != null && !Validaciones.soloDigitos(dpi, 13, 13)) {
            throw new DatosInvalidosException("El DPI debe tener 13 números");
        }
        if (fechaNacimiento == null) {
            throw new DatosInvalidosException("La fecha de nacimiento es obligatoria");
        }
        try {
            LocalDate fecha = LocalDate.parse(fechaNacimiento);
            if (fecha.isAfter(LocalDate.now())) {
                throw new DatosInvalidosException("La fecha de nacimiento no puede ser futura");
            }
        } catch (DateTimeParseException e) {
            throw new DatosInvalidosException("La fecha de nacimiento no es válida");
        }
        if (genero == null || !(genero.equals("M") || genero.equals("F"))) {
            throw new DatosInvalidosException("El género debe ser M o F");
        }
        if (direccion != null && direccion.length() > 200) {
            throw new DatosInvalidosException("La dirección no puede pasar de 200 caracteres");
        }
        if (alergias != null && alergias.length() > 500) {
            throw new DatosInvalidosException("Las alergias no pueden pasar de 500 caracteres");
        }
        if (condiciones != null && condiciones.length() > 500) {
            throw new DatosInvalidosException("Las condiciones no pueden pasar de 500 caracteres");
        }
        if (contactoEmergencia == null || contactoEmergencia.length() > 100) {
            throw new DatosInvalidosException("El contacto de emergencia es obligatorio (máximo 100 caracteres)");
        }
        if (!Validaciones.soloDigitos(telefonoEmergencia, 8, 15)) {
            throw new DatosInvalidosException("El teléfono de emergencia debe tener entre 8 y 15 números");
        }
        if (encargado == null) {
            throw new DatosInvalidosException("Faltan los datos del encargado");
        }
        encargado.validar();
    }
}