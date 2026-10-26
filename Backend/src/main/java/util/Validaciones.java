package util;

public class Validaciones {

    public static boolean soloDigitos(String texto, int minimo, int maximo) {
        return texto != null && texto.matches("[0-9]{" + minimo + "," + maximo + "}");
    }

    public static boolean correoValido(String texto) {
        return texto != null && texto.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    }

    public static String limpiar(String texto) {
        if (texto == null) {
            return null;
        }
        String limpio = texto.trim();
        return limpio.isEmpty() ? null : limpio;
    }
}