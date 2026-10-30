package model.finanzas;

import java.math.BigDecimal;
import java.util.List;

public class EstadoCuenta {

    private int idEstudiante;
    private String carnet;
    private String estudiante;
    private int idCiclo;
    private int anio;
    private String grado;
    private BigDecimal montoInscripcion;
    private BigDecimal montoMensualidad;
    private int diaVencimiento;
    private boolean inscripcionPagada;
    private List<Integer> mesesPagados;

    public EstadoCuenta(int idEstudiante, String carnet, String estudiante, int idCiclo, int anio, String grado,
                        BigDecimal montoInscripcion, BigDecimal montoMensualidad, int diaVencimiento,
                        boolean inscripcionPagada, List<Integer> mesesPagados) {
        this.idEstudiante = idEstudiante;
        this.carnet = carnet;
        this.estudiante = estudiante;
        this.idCiclo = idCiclo;
        this.anio = anio;
        this.grado = grado;
        this.montoInscripcion = montoInscripcion;
        this.montoMensualidad = montoMensualidad;
        this.diaVencimiento = diaVencimiento;
        this.inscripcionPagada = inscripcionPagada;
        this.mesesPagados = mesesPagados;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public String getCarnet() {
        return carnet;
    }

    public String getEstudiante() {
        return estudiante;
    }

    public int getIdCiclo() {
        return idCiclo;
    }

    public int getAnio() {
        return anio;
    }

    public String getGrado() {
        return grado;
    }

    public BigDecimal getMontoInscripcion() {
        return montoInscripcion;
    }

    public BigDecimal getMontoMensualidad() {
        return montoMensualidad;
    }

    public int getDiaVencimiento() {
        return diaVencimiento;
    }

    public boolean isInscripcionPagada() {
        return inscripcionPagada;
    }

    public List<Integer> getMesesPagados() {
        return mesesPagados;
    }
}