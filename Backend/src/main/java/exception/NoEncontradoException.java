package exception;

public class NoEncontradoException extends ServicioException {

    public NoEncontradoException(String mensaje) {
        super(404, mensaje);
    }
}