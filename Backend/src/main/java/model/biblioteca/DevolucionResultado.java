package model.biblioteca;

public class DevolucionResultado {

    private Prestamo prestamo;
    private Multa multa;

    public DevolucionResultado(Prestamo prestamo, Multa multa) {
        this.prestamo = prestamo;
        this.multa = multa;
    }

    public Prestamo getPrestamo() {
        return prestamo;
    }

    public Multa getMulta() {
        return multa;
    }
}