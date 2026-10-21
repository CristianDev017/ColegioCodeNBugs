package exception;

public class ServicioException extends Exception {

    private final int estado;

    public ServicioException(int estado, String mensaje) {
        super(mensaje);
        this.estado = estado;
    }

    public int getEstado() {
        return estado;
    }
}