package exception;

public class ConflictoException extends ServicioException {

    public ConflictoException(String mensaje) {
        super(409, mensaje);
    }
}