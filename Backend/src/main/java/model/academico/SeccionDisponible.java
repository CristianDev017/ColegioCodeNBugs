package model.academico;

public class SeccionDisponible {

    private int idSeccion;
    private String grado;
    private String seccion;
    private int cupo;
    private int inscritos;
    private int disponibles;

    public SeccionDisponible(int idSeccion, String grado, String seccion, int cupo, int inscritos) {
        this.idSeccion = idSeccion;
        this.grado = grado;
        this.seccion = seccion;
        this.cupo = cupo;
        this.inscritos = inscritos;
        this.disponibles = Math.max(0, cupo - inscritos);
    }

    public int getIdSeccion() {
        return idSeccion;
    }

    public String getGrado() {
        return grado;
    }

    public String getSeccion() {
        return seccion;
    }

    public int getCupo() {
        return cupo;
    }

    public int getInscritos() {
        return inscritos;
    }

    public int getDisponibles() {
        return disponibles;
    }
}