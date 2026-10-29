package dto;

import exception.DatosInvalidosException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class PeticionEmpleado {

    public static final List<String> PUESTOS =
            List.of("Maestro", "Limpieza", "Biblioteca", "Mantenimiento", "Administrativo");

    private String nombres;
    private String apellidos;
    private String dpi;
    private String fechaNacimiento;
    private String telefono;
    private String direccion;
    private String puesto;
    private String fechaContratacion;
    private BigDecimal salario;

    public String getNombres() {
        return nombres == null ? null : nombres.trim();
    }

    public String getApellidos() {
        return apellidos == null ? null : apellidos.trim();
    }

    public String getDpi() {
        return dpi == null ? null : dpi.trim();
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getTelefono() {
        if (telefono == null || telefono.trim().isEmpty()) {
            return null;
        }
        return telefono.trim();
    }

    public String getDireccion() {
        if (direccion == null || direccion.trim().isEmpty()) {
            return null;
        }
        return direccion.trim();
    }

    public String getPuesto() {
        return puesto;
    }

    public String getFechaContratacion() {
        return fechaContratacion;
    }

    public BigDecimal getSalario() {
        return salario;
    }

    public void validar() throws DatosInvalidosException {
        if (getNombres() == null || getNombres().isEmpty()) {
            throw new DatosInvalidosException("Los nombres son obligatorios");
        }
        if (getNombres().length() > 80) {
            throw new DatosInvalidosException("Los nombres no pueden pasar de 80 caracteres");
        }
        if (getApellidos() == null || getApellidos().isEmpty()) {
            throw new DatosInvalidosException("Los apellidos son obligatorios");
        }
        if (getApellidos().length() > 80) {
            throw new DatosInvalidosException("Los apellidos no pueden pasar de 80 caracteres");
        }
        if (getDpi() == null || !getDpi().matches("[0-9]{13}")) {
            throw new DatosInvalidosException("El DPI debe tener exactamente 13 números");
        }

        LocalDate nacimiento = leerFecha(fechaNacimiento, "de nacimiento");
        if (nacimiento.isAfter(LocalDate.now().minusYears(18))) {
            throw new DatosInvalidosException("El empleado debe ser mayor de 18 años");
        }

        if (getTelefono() != null && !getTelefono().matches("[0-9]{8,15}")) {
            throw new DatosInvalidosException("El teléfono debe tener entre 8 y 15 números");
        }
        if (getDireccion() != null && getDireccion().length() > 200) {
            throw new DatosInvalidosException("La dirección no puede pasar de 200 caracteres");
        }
        if (puesto == null || !PUESTOS.contains(puesto)) {
            throw new DatosInvalidosException("Debes elegir un puesto válido");
        }

        LocalDate contratacion = leerFecha(fechaContratacion, "de contratación");
        if (contratacion.isAfter(LocalDate.now())) {
            throw new DatosInvalidosException("La fecha de contratación no puede ser futura");
        }
        if (contratacion.isBefore(nacimiento)) {
            throw new DatosInvalidosException("La contratación no puede ser antes del nacimiento");
        }

        if (salario == null) {
            throw new DatosInvalidosException("El salario es obligatorio");
        }
        if (salario.signum() < 0) {
            throw new DatosInvalidosException("El salario no puede ser negativo");
        }
        if (salario.stripTrailingZeros().scale() > 2) {
            throw new DatosInvalidosException("El salario solo puede tener 2 decimales");
        }
        if (salario.compareTo(new BigDecimal("99999999.99")) > 0) {
            throw new DatosInvalidosException("El salario es demasiado grande");
        }
    }

    private LocalDate leerFecha(String texto, String nombre) throws DatosInvalidosException {
        if (texto == null || texto.isEmpty()) {
            throw new DatosInvalidosException("La fecha " + nombre + " es obligatoria");
        }
        try {
            return LocalDate.parse(texto);
        } catch (DateTimeParseException e) {
            throw new DatosInvalidosException("La fecha " + nombre + " no es válida");
        }
    }
}