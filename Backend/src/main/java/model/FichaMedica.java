package model;

public class FichaMedica {

    private String alergias;
    private String condiciones;
    private String contactoEmergencia;
    private String telefonoEmergencia;

    public FichaMedica(String alergias, String condiciones, String contactoEmergencia, String telefonoEmergencia) {
        this.alergias = alergias;
        this.condiciones = condiciones;
        this.contactoEmergencia = contactoEmergencia;
        this.telefonoEmergencia = telefonoEmergencia;
    }

    public String getAlergias() {
        return alergias;
    }

    public String getCondiciones() {
        return condiciones;
    }

    public String getContactoEmergencia() {
        return contactoEmergencia;
    }

    public String getTelefonoEmergencia() {
        return telefonoEmergencia;
    }
}