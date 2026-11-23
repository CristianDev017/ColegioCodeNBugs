
package colegio.model;

public class Seccion {

    private int idSeccion;
    private int idCicloGrado;
    private int idCiclo;
    private int anio;
    private int idGrado;
    private String grado;
    private String nivel;
    private String carrera;
    private String nombre;
    private int cupo;
    private boolean activo;
    private String estadoCiclo;

    public Seccion(int idSeccion, int idCicloGrado, int idCiclo,
                   int anio, int idGrado, String grado, String nivel,
                   String carrera, String nombre, int cupo,
                   boolean activo, String estadoCiclo) {

        this.idSeccion = idSeccion;
        this.idCicloGrado = idCicloGrado;
        this.idCiclo = idCiclo;
        this.anio = anio;
        this.idGrado = idGrado;
        this.grado = grado;
        this.nivel = nivel;
        this.carrera = carrera;
        this.nombre = nombre;
        this.cupo = cupo;
        this.activo = activo;
        this.estadoCiclo = estadoCiclo;
    }

    public int getIdSeccion() {
        return idSeccion;
    }

    public int getIdCicloGrado() {
        return idCicloGrado;
    }

    public int getIdCiclo() {
        return idCiclo;
    }

    public int getAnio() {
        return anio;
    }

    public int getIdGrado() {
        return idGrado;
    }

    public String getGrado() {
        return grado;
    }

    public String getNivel() {
        return nivel;
    }

    public String getCarrera() {
        return carrera;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCupo() {
        return cupo;
    }

    public boolean isActivo() {
        return activo;
    }

    public String getEstadoCiclo() {
        return estadoCiclo;
    }
}
