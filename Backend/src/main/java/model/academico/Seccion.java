package model.academico;

public class Seccion {

    private int id;
    private int idGrado;
    private String grado;
    private String nivel;
    private String nombre;
    private int cupo;
    private boolean activo;

    public Seccion(int id, int idGrado, String grado, String nivel, String nombre, int cupo, boolean activo) {
        this.id = id;
        this.idGrado = idGrado;
        this.grado = grado;
        this.nivel = nivel;
        this.nombre = nombre;
        this.cupo = cupo;
        this.activo = activo;
    }

    public int getId() {
        return id;
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

    public String getNombre() {
        return nombre;
    }

    public int getCupo() {
        return cupo;
    }

    public boolean isActivo() {
        return activo;
    }
}