package colegio.model;

public class Curriculo {

    private int idCurriculo;
    private int idGrado;
    private int idCiclo;
    private String grado;
    private String nivel;
    private String carrera;
    private int anio;
    private String estadoCiclo;

    public Curriculo(int idCurriculo, int idGrado, int idCiclo,
                     String grado, String nivel, String carrera,
                     int anio, String estadoCiclo) {

        this.idCurriculo = idCurriculo;
        this.idGrado = idGrado;
        this.idCiclo = idCiclo;
        this.grado = grado;
        this.nivel = nivel;
        this.carrera = carrera;
        this.anio = anio;
        this.estadoCiclo = estadoCiclo;
    }

    public int getIdCurriculo() {
        return idCurriculo;
    }

    public int getIdGrado() {
        return idGrado;
    }

    public int getIdCiclo() {
        return idCiclo;
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

    public int getAnio() {
        return anio;
    }

    public String getEstadoCiclo() {
        return estadoCiclo;
    }
}
