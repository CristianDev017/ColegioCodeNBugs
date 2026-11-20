package colegio.model;

public class Grado {

    private int idGrado;
    private int idNivel;
    private String nivel;
    private Integer idCarrera;
    private String carrera;
    private String nombre;
    private int cupoDefault;
    private boolean activo;

    public Grado(int idGrado, int idNivel, String nivel,
                 Integer idCarrera, String carrera, String nombre,
                 int cupoDefault, boolean activo) {
        this.idGrado = idGrado;
        this.idNivel = idNivel;
        this.nivel = nivel;
        this.idCarrera = idCarrera;
        this.carrera = carrera;
        this.nombre = nombre;
        this.cupoDefault = cupoDefault;
        this.activo = activo;
    }

    public int getIdGrado() {
        return idGrado;
    }

    public int getIdNivel() {
        return idNivel;
    }

    public String getNivel() {
        return nivel;
    }

    public Integer getIdCarrera() {
        return idCarrera;
    }

    public String getCarrera() {
        return carrera;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCupoDefault() {
        return cupoDefault;
    }

    public boolean isActivo() {
        return activo;
    }
}
