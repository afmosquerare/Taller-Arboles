package taller_arboles;

public class Nodo {

    private Persona persona;
    private boolean esPadre;
    private Nodo liga;

    public Nodo(Persona persona) {
        this.persona = persona;
        this.esPadre = false;
        this.liga = null;
        this.ligaPadre = null;
    }

    public Nodo(Persona persona, boolean esPadre) {
        this.persona = persona;
        this.esPadre = esPadre;
        this.liga = null;
        this.ligaPadre = null;
    }

    private Nodo ligaPadre;

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public boolean EsPadre() {
        return esPadre;
    }

    public void setEsPadre(boolean esPadre) {
        this.esPadre = esPadre;
    }

    public Nodo getLiga() {
        return liga;
    }

    public void setLiga(Nodo liga) {
        this.liga = liga;
    }

    public Nodo getLigaPadre() {
        return ligaPadre;
    }

    public void setLigaPadre(Nodo ligaPadre) {
        this.ligaPadre = ligaPadre;
    }

}
