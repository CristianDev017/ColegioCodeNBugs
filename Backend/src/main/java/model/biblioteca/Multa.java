package model.biblioteca;

import java.math.BigDecimal;

public class Multa {

    private int id;
    private int idPrestamo;
    private String titulo;
    private String solicitante;
    private int diasAtraso;
    private BigDecimal monto;
    private String estado;
    private String fechaGeneracion;

    public Multa(int id, int idPrestamo, String titulo, String solicitante, int diasAtraso,
                 BigDecimal monto, String estado, String fechaGeneracion) {
        this.id = id;
        this.idPrestamo = idPrestamo;
        this.titulo = titulo;
        this.solicitante = solicitante;
        this.diasAtraso = diasAtraso;
        this.monto = monto;
        this.estado = estado;
        this.fechaGeneracion = fechaGeneracion;
    }

    public int getId() {
        return id;
    }

    public int getIdPrestamo() {
        return idPrestamo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public int getDiasAtraso() {
        return diasAtraso;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public String getEstado() {
        return estado;
    }

    public String getFechaGeneracion() {
        return fechaGeneracion;
    }
}