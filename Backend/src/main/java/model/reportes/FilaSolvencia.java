package model.reportes;

import java.math.BigDecimal;

public class FilaSolvencia {

    private String carnet;
    private String estudiante;
    private String grado;
    private String seccion;
    private String estado;
    private String detalle;
    private BigDecimal montoAdeudado;

    public FilaSolvencia(String carnet, String estudiante, String grado, String seccion, String estado,
                         String detalle, BigDecimal montoAdeudado) {
        this.carnet = carnet;
        this.estudiante = estudiante;
        this.grado = grado;
        this.seccion = seccion;
        this.estado = estado;
        this.detalle = detalle;
        this.montoAdeudado = montoAdeudado;
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

    public String getEstado() {
        return estado;
    }

    public String getDetalle() {
        return detalle;
    }

    public BigDecimal getMontoAdeudado() {
        return montoAdeudado;
    }
}
