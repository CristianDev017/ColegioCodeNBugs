package model.biblioteca;

public class Prestamo {

    private int id;
    private int idLibro;
    private String titulo;
    private String tipoSolicitante;
    private String solicitante;
    private String fechaPrestamo;
    private String fechaEsperada;
    private String fechaDevolucion;
    private String estado;
    private boolean atrasado;
    private int diasAtraso;
    private String observacion;

    public Prestamo(int id, int idLibro, String titulo, String tipoSolicitante, String solicitante,
                    String fechaPrestamo, String fechaEsperada, String fechaDevolucion, String estado,
                    boolean atrasado, int diasAtraso, String observacion) {
        this.id = id;
        this.idLibro = idLibro;
        this.titulo = titulo;
        this.tipoSolicitante = tipoSolicitante;
        this.solicitante = solicitante;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaEsperada = fechaEsperada;
        this.fechaDevolucion = fechaDevolucion;
        this.estado = estado;
        this.atrasado = atrasado;
        this.diasAtraso = diasAtraso;
        this.observacion = observacion;
    }

    public int getId() {
        return id;
    }

    public int getIdLibro() {
        return idLibro;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getTipoSolicitante() {
        return tipoSolicitante;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public String getFechaPrestamo() {
        return fechaPrestamo;
    }

    public String getFechaEsperada() {
        return fechaEsperada;
    }

    public String getFechaDevolucion() {
        return fechaDevolucion;
    }

    public String getEstado() {
        return estado;
    }

    public boolean isAtrasado() {
        return atrasado;
    }

    public int getDiasAtraso() {
        return diasAtraso;
    }

    public String getObservacion() {
        return observacion;
    }
}
