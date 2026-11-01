
package model.reportes;

import java.math.BigDecimal;

public class HistorialPago {

    private int idPago;
    private String carnet;
    private String estudiante;
    private int anio;
    private String concepto;
    private String detalle;
    private String fechaPago;
    private BigDecimal monto;
    private String comprobante;

    public HistorialPago(
            int idPago,
            String carnet,
            String estudiante,
            int anio,
            String concepto,
            String detalle,
            String fechaPago,
            BigDecimal monto,
            String comprobante) {

        this.idPago = idPago;
        this.carnet = carnet;
        this.estudiante = estudiante;
        this.anio = anio;
        this.concepto = concepto;
        this.detalle = detalle;
        this.fechaPago = fechaPago;
        this.monto = monto;
        this.comprobante = comprobante;
    }

    public int getIdPago() {
        return idPago;
    }

    public String getCarnet() {
        return carnet;
    }

    public String getEstudiante() {
        return estudiante;
    }

    public int getAnio() {
        return anio;
    }

    public String getConcepto() {
        return concepto;
    }

    public String getDetalle() {
        return detalle;
    }

    public String getFechaPago() {
        return fechaPago;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public String getComprobante() {
        return comprobante;
    }
}
