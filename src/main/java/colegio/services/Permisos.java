package colegio.services;

public class Permisos {

    public static boolean tienePermiso(String rolUsuario, String... rolesPermitidos) {

        if (rolUsuario == null) {
            return false;
        }

        for (String rol : rolesPermitidos) {
            if (rol.equals(rolUsuario)) {
                return true;
            }
        }

        return false;
    }
}