package model.reportes;

import java.math.BigDecimal;

public class FilaMora {

    private String libro;
    private String solicitante;
    private String tipoSolicitante;
    private String fechaPrestamo;
    private String fechaEsperada;
    private Integer diasAtraso;
    private BigDecimal multaEstimada;

    public FilaMora(String libro, String solicitante, String tipoSolicitante, String fechaPrestamo,
                    String fechaEsperada, Integer diasAtraso, BigDecimal multaEstimada) {
        this.libro = libro;
        this.solicitante = solicitante;
        this.tipoSolicitante = tipoSolicitante;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaEsperada = fechaEsperada;
        this.diasAtraso = diasAtraso;
        this.multaEstimada = multaEstimada;
    }

    public String getLibro() {
        return libro;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public String getTipoSolicitante() {
        return tipoSolicitante;
    }

    public String getFechaPrestamo() {
        return fechaPrestamo;
    }

    public String getFechaEsperada() {
        return fechaEsperada;
    }

    public Integer getDiasAtraso() {
        return diasAtraso;
    }

    public BigDecimal getMultaEstimada() {
        return multaEstimada;
    }
}