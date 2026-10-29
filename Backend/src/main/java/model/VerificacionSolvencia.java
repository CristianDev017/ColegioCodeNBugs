package model;

import java.math.BigDecimal;
import java.util.List;

public class VerificacionSolvencia {

    private boolean solvente;
    private List<Pendiente> pendientes;

    public VerificacionSolvencia(List<Pendiente> pendientes) {
        this.pendientes = pendientes;
        this.solvente = pendientes.isEmpty();
    }

    public boolean isSolvente() {
        return solvente;
    }

    public List<Pendiente> getPendientes() {
        return pendientes;
    }

    public static class Pendiente {

        private String concepto;
        private Integer mes;
        private String detalle;
        private BigDecimal monto;
        private String fechaVencimiento;

        public Pendiente(String concepto, Integer mes, String detalle, BigDecimal monto, String fechaVencimiento) {
            this.concepto = concepto;
            this.mes = mes;
            this.detalle = detalle;
            this.monto = monto;
            this.fechaVencimiento = fechaVencimiento;
        }

        public String getConcepto() {
            return concepto;
        }

        public Integer getMes() {
            return mes;
        }

        public String getDetalle() {
            return detalle;
        }

        public BigDecimal getMonto() {
            return monto;
        }

        public String getFechaVencimiento() {
            return fechaVencimiento;
        }
    }
}