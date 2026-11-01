
package model.reportes;

import java.math.BigDecimal;

public class IngresoConcepto {

    private String concepto;
    private int cantidadPagos;
    private BigDecimal total;

    public IngresoConcepto(
            String concepto,
            int cantidadPagos,
            BigDecimal total) {

        this.concepto = concepto;
        this.cantidadPagos = cantidadPagos;
        this.total = total;
    }

    public String getConcepto() {
        return concepto;
    }

    public int getCantidadPagos() {
        return cantidadPagos;
    }

    public BigDecimal getTotal() {
        return total;
    }
}
