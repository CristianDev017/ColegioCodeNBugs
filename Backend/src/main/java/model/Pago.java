package model;

import java.math.BigDecimal;

public class Pago {

    private int id;
    private int idEstudiante;
    private String carnet;
    private String estudiante;
    private int idCiclo;
    private int anio;
    private Integer idActividad;
    private String actividad;
    private String concepto;
    private Integer mes;
    private BigDecimal monto;
    private String fechaPago;
    private String usuario;
    private String numeroComprobante;
    private String fechaEmision;

    public Pago(int id, int idEstudiante, String carnet, String estudiante, int idCiclo, int anio,
                Integer idActividad, String actividad, String concepto, Integer mes, BigDecimal monto,
                String fechaPago, String usuario, String numeroComprobante, String fechaEmision) {
        this.id = id;
        this.idEstudiante = idEstudiante;
        this.carnet = carnet;
        this.estudiante = estudiante;
        this.idCiclo = idCiclo;
        this.anio = anio;
        this.idActividad = idActividad;
        this.actividad = actividad;
        this.concepto = concepto;
        this.mes = mes;
        this.monto = monto;
        this.fechaPago = fechaPago;
        this.usuario = usuario;
        this.numeroComprobante = numeroComprobante;
        this.fechaEmision = fechaEmision;
    }

    public int getId() {
        return id;
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

    public Integer getIdActividad() {
        return idActividad;
    }

    public String getActividad() {
        return actividad;
    }

    public String getConcepto() {
        return concepto;
    }

    public Integer getMes() {
        return mes;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public String getFechaPago() {
        return fechaPago;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getNumeroComprobante() {
        return numeroComprobante;
    }

    public String getFechaEmision() {
        return fechaEmision;
    }
}