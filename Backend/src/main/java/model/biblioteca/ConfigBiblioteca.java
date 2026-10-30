package model.biblioteca;

import java.math.BigDecimal;

public class ConfigBiblioteca {

    private int diasPrestamo;
    private BigDecimal multaPorDia;

    public ConfigBiblioteca(int diasPrestamo, BigDecimal multaPorDia) {
        this.diasPrestamo = diasPrestamo;
        this.multaPorDia = multaPorDia;
    }

    public int getDiasPrestamo() {
        return diasPrestamo;
    }

    public BigDecimal getMultaPorDia() {
        return multaPorDia;
    }
}
