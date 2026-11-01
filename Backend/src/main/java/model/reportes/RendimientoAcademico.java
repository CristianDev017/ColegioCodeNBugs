
package model.reportes;

import java.math.BigDecimal;

public class RendimientoAcademico {

    private String curso;
    private String grado;
    private String seccion;
    private int alumnosEvaluados;
    private BigDecimal promedio;
    private String rendimiento;

    public RendimientoAcademico(
            String curso,
            String grado,
            String seccion,
            int alumnosEvaluados,
            BigDecimal promedio) {

        this.curso = curso;
        this.grado = grado;
        this.seccion = seccion;
        this.alumnosEvaluados = alumnosEvaluados;
        this.promedio = promedio;
        this.rendimiento = "";
    }

    public String getCurso() {
        return curso;
    }

    public String getGrado() {
        return grado;
    }

    public String getSeccion() {
        return seccion;
    }

    public int getAlumnosEvaluados() {
        return alumnosEvaluados;
    }

    public BigDecimal getPromedio() {
        return promedio;
    }

    public String getRendimiento() {
        return rendimiento;
    }

    public void setRendimiento(String rendimiento) {
        this.rendimiento = rendimiento;
    }
}
