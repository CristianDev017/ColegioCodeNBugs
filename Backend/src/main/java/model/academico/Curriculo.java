package model.academico;

import java.util.List;

public class Curriculo {

    private int id;
    private int idCiclo;
    private int idGrado;
    private List<Curso> cursos;

    public Curriculo(int id, int idCiclo, int idGrado, List<Curso> cursos) {
        this.id = id;
        this.idCiclo = idCiclo;
        this.idGrado = idGrado;
        this.cursos = cursos;
    }

    public int getId() {
        return id;
    }

    public int getIdCiclo() {
        return idCiclo;
    }

    public int getIdGrado() {
        return idGrado;
    }

    public List<Curso> getCursos() {
        return cursos;
    }
}