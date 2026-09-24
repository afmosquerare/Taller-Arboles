package taller_arboles;

public class NodoLG {

    private boolean esSublista;
    private Persona persona;   // válido solo si esSublista == false
    private NodoLG down;       // válido solo si esSublista == true
    private NodoLG sig;        // siguiente elemento de la lista

    // ------------------------------------------------------------------
    //  Constructores
    // ------------------------------------------------------------------

    /** Crea un ÁTOMO con una persona y su siguiente elemento. */
    public NodoLG(Persona persona, NodoLG sig) {
        this.esSublista = false;
        this.persona = persona;
        this.down = null;
        this.sig = sig;
    }

    /** Crea un ÁTOMO con una persona (sin siguiente). */
    public NodoLG(Persona persona) {
        this(persona, null);
    }

    /** Crea una SUBLISTA cuya primera posición es "down" y con su siguiente elemento. */
    public NodoLG(NodoLG down, NodoLG sig) {
        this.esSublista = true;
        this.persona = null;
        this.down = down;
        this.sig = sig;
    }

    // ------------------------------------------------------------------
    //  Acceso a los campos
    // ------------------------------------------------------------------

    public boolean esSublista() { return esSublista; }
    public Persona getPersona() { return persona; }
    public NodoLG getDown() { return down; }
    public NodoLG getSig() { return sig; }

    public void setPersona(Persona persona) { this.persona = persona; }
    public void setSig(NodoLG sig) { this.sig = sig; }

    // ------------------------------------------------------------------
    //  Operaciones propias del nodo
    // ------------------------------------------------------------------

    /** Persona que representa este elemento (átomo: su dato; sublista: la de su cabeza). */
    public Persona personaDe() {
        return esSublista ? down.persona : persona;
    }

    /** Primer hijo de la persona que representa este elemento (null si no tiene hijos). */
    public NodoLG primerHijo() {
        return esSublista ? down.sig : null;
    }

    /** Si el nodo es un átomo lo convierte en sublista, con su persona como cabeza. */
    public void convertirEnSublista() {
        if (!esSublista) {
            this.down = new NodoLG(this.persona);
            this.persona = null;
            this.esSublista = true;
        }
    }

    /** Si la sublista quedó solo con su cabeza (sin hijos) la vuelve a convertir en átomo. */
    public void normalizar() {
        if (esSublista && down.sig == null) {
            this.persona = down.persona;
            this.down = null;
            this.esSublista = false;
        }
    }
}
