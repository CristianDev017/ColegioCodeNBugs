package exception;

public class DatosInvalidosException extends ServicioException {

    public DatosInvalidosException(String mensaje) {
        super(400, mensaje);
    }
}