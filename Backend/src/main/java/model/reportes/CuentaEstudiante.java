package model.reportes;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CuentaEstudiante {

    private String carnet;
    private String estudiante;
    private String grado;
    private String seccion;
    private BigDecimal montoInscripcion;
    private BigDecimal montoMensualidad;
    private int diaVencimiento;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean inscripcionPagada;
    private String mesesPagados;

    public CuentaEstudiante(String carnet, String estudiante, String grado, String seccion,
                            BigDecimal montoInscripcion, BigDecimal montoMensualidad, int diaVencimiento,
                            LocalDate fechaInicio, LocalDate fechaFin, boolean inscripcionPagada,
                            String mesesPagados) {
        this.carnet = carnet;
        this.estudiante = estudiante;
        this.grado = grado;
        this.seccion = seccion;
        this.montoInscripcion = montoInscripcion;
        this.montoMensualidad = montoMensualidad;
        this.diaVencimiento = diaVencimiento;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.inscripcionPagada = inscripcionPagada;
        this.mesesPagados = mesesPagados;
    }

    public String getCarnet() {
        return carnet;
    }

    public String getEstudiante() {
        return estudiante;
    }

    public String getGrado() {
        return grado;
    }

    public String getSeccion() {
        return seccion;
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

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public boolean isInscripcionPagada() {
        return inscripcionPagada;
    }

    public String getMesesPagados() {
        return mesesPagados;
    }
}