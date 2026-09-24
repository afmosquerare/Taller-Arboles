package taller_arboles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Árbol genealógico n-ario implementado ESTRICTAMENTE con listas generalizadas
 * (ver NodoLG para la representación) y con operaciones RECURSIVAS.
 *
 * Patrón de recursividad usado en toda la clase (el natural de una lista generalizada):
 *   - se recorre HACIA ABAJO (down): de una persona a sus hijos, y
 *   - se recorre HACIA ADELANTE (sig): de un hijo al siguiente hermano.
 * Por eso casi cada operación tiene dos métodos que se llaman entre sí:
 *   xxxRec(elemento)    -> trata a UNA persona
 *   xxxEnCadena(nodo)   -> recorre una cadena de HERMANOS
 *
 * Convenciones:
 *  - La raíz está en el NIVEL 1. La altura es la cantidad de niveles (generaciones).
 *  - Los hijos de un mismo padre se mantienen SIEMPRE ordenados por cédula (ascendente).
 *  - Una persona sin hijos es siempre un átomo; con hijos es una sublista
 *    (se "normaliza" automáticamente después de cada modificación).
 *  - Las modificaciones se hacen "en el lugar": un nodo cambia de átomo a sublista
 *    (o viceversa) sin cambiar su identidad, así los punteros del padre siguen válidos.
 *
 * Las colecciones de java.util solo se usan para DEVOLVER resultados de consultas;
 * el árbol en sí está formado únicamente por nodos NodoLG.
 */
public class ArbolGenealogico {

    /** Raíz del árbol: elemento (átomo o sublista) que representa al ancestro principal. */
    private NodoLG raiz;

    /** Resultado de una búsqueda: dónde está un elemento y quién lo rodea. */
    private static class Ubicacion {
        final NodoLG elem;    // elemento que representa a la persona
        final NodoLG padre;   // elemento del padre (null si es la raíz)
        final NodoLG previo;  // nodo cuyo "sig" apunta a elem (null si es la raíz)
        final int nivel;      // nivel (raíz = 1)

        Ubicacion(NodoLG elem, NodoLG padre, NodoLG previo, int nivel) {
            this.elem = elem;
            this.padre = padre;
            this.previo = previo;
            this.nivel = nivel;
        }
    }

    // =====================================================================
    //  PRIMITIVAS RECURSIVAS SOBRE LA LISTA GENERALIZADA
    // =====================================================================

    private static long cedula(NodoLG e) {
        return e.personaDe().getCedula();
    }

    /** Cantidad de hijos directos de un elemento. */
    private static int grado(NodoLG e) {
        return contarCadena(e.primerHijo());
    }

    private static int contarCadena(NodoLG h) {
        return (h == null) ? 0 : 1 + contarCadena(h.getSig());
    }

    /**
     * Inserta "hijo" (un átomo o sublista ya armado) en la lista del padre,
     * manteniendo el orden ascendente por cédula.
     */
    private static void insertarOrdenado(NodoLG padre, NodoLG hijo) {
        padre.convertirEnSublista();
        insertarDespuesDe(padre.getDown(), hijo);
    }

    private static void insertarDespuesDe(NodoLG ant, NodoLG hijo) {
        NodoLG siguiente = ant.getSig();
        if (siguiente == null || cedula(siguiente) >= cedula(hijo)) {
            hijo.setSig(siguiente);
            ant.setSig(hijo);
        } else {
            insertarDespuesDe(siguiente, hijo);
        }
    }

    /** Mezcla dos cadenas de hermanos ya ordenadas por cédula en una sola ordenada. */
    private static NodoLG mezclar(NodoLG a, NodoLG b) {
        if (a == null) return b;
        if (b == null) return a;
        if (cedula(a) <= cedula(b)) {
            a.setSig(mezclar(a.getSig(), b));
            return a;
        }
        b.setSig(mezclar(a, b.getSig()));
        return b;
    }

    /** Agrega a "res" las personas de una cadena de hermanos, salvo "excluir". */
    private static List<Persona> agregarPersonas(NodoLG h, NodoLG excluir, List<Persona> res) {
        if (h == null) return res;
        if (h != excluir) res.add(h.personaDe());
        return agregarPersonas(h.getSig(), excluir, res);
    }

    /** Agrega a "res" los HIJOS de cada elemento de una cadena de hermanos, salvo los de "excluir". */
    private static List<Persona> agregarHijosDeCadena(NodoLG h, NodoLG excluir, List<Persona> res) {
        if (h == null) return res;
        if (h != excluir) agregarPersonas(h.primerHijo(), null, res);
        return agregarHijosDeCadena(h.getSig(), excluir, res);
    }

    // =====================================================================
    //  BÚSQUEDAS INTERNAS (recursivas)
    // =====================================================================

    private Ubicacion buscar(long ced) {
        return (raiz == null) ? null : buscarRec(raiz, null, null, 1, ced);
    }

    private static Ubicacion buscarRec(NodoLG e, NodoLG padre, NodoLG previo, int nivel, long ced) {
        if (cedula(e) == ced) return new Ubicacion(e, padre, previo, nivel);
        if (!e.esSublista()) return null;
        return buscarEnCadena(e.getDown().getSig(), e.getDown(), e, nivel + 1, ced);
    }

    private static Ubicacion buscarEnCadena(NodoLG h, NodoLG previo, NodoLG padre, int nivel, long ced) {
        if (h == null) return null;
        Ubicacion r = buscarRec(h, padre, previo, nivel, ced);
        return (r != null) ? r : buscarEnCadena(h.getSig(), h, padre, nivel, ced);
    }

    private Ubicacion requerir(long ced) {
        Ubicacion u = buscar(ced);
        if (u == null) throw new NoSuchElementException("No existe ninguna persona con cedula " + ced + ".");
        return u;
    }

    /** Camino desde la raíz hasta la persona (ambos incluidos). */
    private List<Persona> camino(long ced) {
        List<Persona> ruta = new ArrayList<>();
        if (raiz == null || !caminoRec(raiz, ced, ruta)) {
            throw new NoSuchElementException("No existe ninguna persona con cedula " + ced + ".");
        }
        return ruta;
    }

    private static boolean caminoRec(NodoLG e, long ced, List<Persona> ruta) {
        ruta.add(e.personaDe());
        if (cedula(e) == ced || caminoEnCadena(e.primerHijo(), ced, ruta)) return true;
        ruta.remove(ruta.size() - 1);
        return false;
    }

    private static boolean caminoEnCadena(NodoLG h, long ced, List<Persona> ruta) {
        if (h == null) return false;
        return caminoRec(h, ced, ruta) || caminoEnCadena(h.getSig(), ced, ruta);
    }

    // =====================================================================
    //  1. GESTIÓN DE PERSONAS
    // =====================================================================

    public boolean estaVacio() {
        return raiz == null;
    }

    /**
     * Registra una persona. Si el árbol está vacío será la raíz (ancestro principal)
     * y "cedulaPadre" se ignora. En caso contrario se inserta como hijo de "cedulaPadre",
     * respetando el orden por cédula entre hermanos.
     */
    public void registrar(String nombre, long cedula, LocalDate fecha, Long cedulaPadre) {
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre no puede estar vacio.");
        if (fecha == null || fecha.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("La fecha de nacimiento no es valida.");
        if (buscar(cedula) != null)
            throw new IllegalArgumentException("Ya existe una persona con la cedula " + cedula + ".");

        NodoLG nuevo = new NodoLG(new Persona(nombre.trim(), cedula, fecha));

        if (raiz == null) {
            raiz = nuevo;
            return;
        }
        if (cedulaPadre == null)
            throw new IllegalArgumentException("Debe indicar la cedula del padre.");
        insertarOrdenado(requerir(cedulaPadre).elem, nuevo);
    }

    /**
     * Elimina a una persona conservando el linaje:
     *  - Hoja: simplemente se desconecta.
     *  - Con hijos: su hijo de mayor edad la reemplaza en la jerarquía, adopta a sus
     *    hermanos y conserva a sus propios hijos.
     */
    public void eliminar(long ced) {
        Ubicacion u = requerir(ced);
        NodoLG n = u.elem;

        // Caso 1: sin hijos
        if (!n.esSublista()) {
            if (u.padre == null) {
                raiz = null;
            } else {
                u.previo.setSig(n.getSig());
                u.padre.normalizar();
            }
            return;
        }

        // Caso 2: con hijos -> localizar (recursivamente) al hijo de mayor edad
        NodoLG cabeza = n.getDown();
        NodoLG prevMayor = previoDelMasViejo(cabeza, cabeza);
        NodoLG mayor = prevMayor.getSig();

        // Desconectar al hijo mayor de la lista de hijos de n
        prevMayor.setSig(mayor.getSig());
        mayor.setSig(null);

        NodoLG hermanos = cabeza.getSig();          // sus hermanos (con su descendencia)
        NodoLG hijosPropios = mayor.primerHijo();   // sus propios hijos

        // El hijo mayor ocupa el lugar de n: mismo nodo, nueva persona en la cabeza
        cabeza.setPersona(mayor.personaDe());
        cabeza.setSig(mezclar(hijosPropios, hermanos));
        n.normalizar();

        // La cédula de n cambió (ahora es la del hijo mayor): reubicar entre sus hermanos
        if (u.padre != null) {
            u.previo.setSig(n.getSig());
            n.setSig(null);
            insertarOrdenado(u.padre, n);
        }
    }

    /**
     * Devuelve el nodo PREVIO al hijo de mayor edad de una cadena de hermanos.
     * previoActual: previo del candidato que se está evaluando.
     * previoMejor : previo del mejor candidato encontrado hasta ahora.
     */
    private static NodoLG previoDelMasViejo(NodoLG previoActual, NodoLG previoMejor) {
        NodoLG actual = previoActual.getSig();
        if (actual == null) return previoMejor;
        NodoLG mejor = previoMejor.getSig();
        return previoDelMasViejo(actual,
                esMasViejo(actual.personaDe(), mejor.personaDe()) ? previoActual : previoMejor);
    }

    /** true si a nació antes que b (a igual fecha, gana la cédula menor). */
    private static boolean esMasViejo(Persona a, Persona b) {
        int c = a.getFechaNacimiento().compareTo(b.getFechaNacimiento());
        return (c != 0) ? c < 0 : a.getCedula() < b.getCedula();
    }

    /**
     * Actualiza los datos de una persona. Un parámetro null (o nombre vacío) significa
     * "no modificar ese campo".
     */
    public void actualizar(long cedulaActual, String nuevoNombre, Long nuevaCedula, LocalDate nuevaFecha) {
        Ubicacion u = requerir(cedulaActual);
        Persona p = u.elem.personaDe();

        boolean cambiaCedula = nuevaCedula != null && nuevaCedula.longValue() != cedulaActual;
        if (cambiaCedula && buscar(nuevaCedula) != null)
            throw new IllegalArgumentException("Ya existe una persona con la cedula " + nuevaCedula + ".");
        if (nuevaFecha != null && nuevaFecha.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("La fecha de nacimiento no es valida.");

        if (nuevoNombre != null && !nuevoNombre.isBlank()) p.setNombre(nuevoNombre.trim());
        if (nuevaFecha != null) p.setFechaNacimiento(nuevaFecha);

        if (cambiaCedula) {
            p.setCedula(nuevaCedula);
            if (u.padre != null) { // reubicar para conservar el orden entre hermanos
                u.previo.setSig(u.elem.getSig());
                u.elem.setSig(null);
                insertarOrdenado(u.padre, u.elem);
            }
        }
    }

    // =====================================================================
    //  2. CONSULTAS DE RELACIONES FAMILIARES
    // =====================================================================

    public Persona consultar(long ced) {
        return requerir(ced).elem.personaDe();
    }

    public Persona padre(long ced) {
        Ubicacion u = requerir(ced);
        return (u.padre == null) ? null : u.padre.personaDe();
    }

    public List<Persona> hijos(long ced) {
        return agregarPersonas(requerir(ced).elem.primerHijo(), null, new ArrayList<>());
    }

    public List<Persona> hermanos(long ced) {
        return hermanosDe(requerir(ced));
    }

    public List<Persona> tios(long ced) {
        Ubicacion up = ubicacionDelPadre(requerir(ced));
        return (up == null) ? new ArrayList<>() : hermanosDe(up);
    }

    public List<Persona> sobrinos(long ced) {
        return sobrinosDe(requerir(ced));
    }

    /** Primos = hijos de los tíos = sobrinos del padre. */
    public List<Persona> primos(long ced) {
        Ubicacion up = ubicacionDelPadre(requerir(ced));
        return (up == null) ? new ArrayList<>() : sobrinosDe(up);
    }

    /** Ancestros en orden ascendente: padre, abuelo, bisabuelo... hasta la raíz. */
    public List<Persona> ancestros(long ced) {
        List<Persona> ruta = camino(ced);
        ruta.remove(ruta.size() - 1); // quitar a la propia persona
        Collections.reverse(ruta);
        return ruta;
    }

    /** Toda la descendencia (hijos, nietos, bisnietos...) en preorden. */
    public List<Persona> descendientes(long ced) {
        List<Persona> res = new ArrayList<>();
        descendientesEnCadena(requerir(ced).elem.primerHijo(), res);
        return res;
    }

    private static void descendientesEnCadena(NodoLG h, List<Persona> res) {
        if (h == null) return;
        res.add(h.personaDe());
        descendientesEnCadena(h.primerHijo(), res);  // hacia abajo: sus descendientes
        descendientesEnCadena(h.getSig(), res);      // hacia adelante: su siguiente hermano
    }

    private List<Persona> hermanosDe(Ubicacion u) {
        List<Persona> res = new ArrayList<>();
        return (u.padre == null) ? res : agregarPersonas(u.padre.primerHijo(), u.elem, res);
    }

    private List<Persona> sobrinosDe(Ubicacion u) {
        List<Persona> res = new ArrayList<>();
        return (u.padre == null) ? res : agregarHijosDeCadena(u.padre.primerHijo(), u.elem, res);
    }

    private Ubicacion ubicacionDelPadre(Ubicacion u) {
        return (u.padre == null) ? null : buscar(cedula(u.padre));
    }

    // =====================================================================
    //  3. CONSULTAS ESTRUCTURALES Y VISUALIZACIÓN
    // =====================================================================

    /** Dibuja el árbol con sangría (una generación por nivel). */
    public String visualizar() {
        if (raiz == null) return "(arbol vacio)";
        StringBuilder sb = new StringBuilder();
        sb.append(raiz.personaDe().resumen()).append(" [raiz]\n");
        dibujar(raiz.primerHijo(), "", sb);
        return sb.toString();
    }

    private static void dibujar(NodoLG h, String prefijo, StringBuilder sb) {
        if (h == null) return;
        boolean ultimo = (h.getSig() == null);
        sb.append(prefijo).append(ultimo ? "`-- " : "|-- ").append(h.personaDe().resumen()).append('\n');
        dibujar(h.primerHijo(), prefijo + (ultimo ? "    " : "|   "), sb);
        dibujar(h.getSig(), prefijo, sb);
    }

    /** Muestra la estructura interna: la lista generalizada, p. ej. (A (B E F) C D). */
    public String comoListaGeneralizada() {
        if (raiz == null) return "()";
        StringBuilder sb = new StringBuilder();
        if (raiz.esSublista()) {
            listaRec(raiz, sb);
        } else {
            sb.append('(');
            listaRec(raiz, sb);
            sb.append(')');
        }
        return sb.toString();
    }

    private static void listaRec(NodoLG e, StringBuilder sb) {
        if (!e.esSublista()) {
            sb.append(e.getPersona().getNombre()).append('[').append(e.getPersona().getCedula()).append(']');
            return;
        }
        sb.append('(');
        listaEnCadena(e.getDown(), sb);
        sb.append(')');
    }

    private static void listaEnCadena(NodoLG x, StringBuilder sb) {
        if (x == null) return;
        listaRec(x, sb);
        if (x.getSig() != null) sb.append(' ');
        listaEnCadena(x.getSig(), sb);
    }

    /** Persona con más hijos directos (si hay empate, la primera en preorden). */
    public Persona mayorGrado() {
        if (raiz == null) throw new IllegalStateException("El arbol esta vacio.");
        return mayorGradoRec(raiz).personaDe();
    }

    private static NodoLG mayorGradoRec(NodoLG e) {
        return mayorGradoEnCadena(e.primerHijo(), e);
    }

    private static NodoLG mayorGradoEnCadena(NodoLG h, NodoLG mejor) {
        if (h == null) return mejor;
        NodoLG c = mayorGradoRec(h);
        return mayorGradoEnCadena(h.getSig(), (grado(c) > grado(mejor)) ? c : mejor);
    }

    public int cantidadHijos(long ced) {
        return grado(requerir(ced).elem);
    }

    /** Persona de menor edad (si hay empate, la primera en preorden). */
    public Persona masJoven() {
        if (raiz == null) throw new IllegalStateException("El arbol esta vacio.");
        return masJovenRec(raiz).personaDe();
    }

    private static NodoLG masJovenRec(NodoLG e) {
        return masJovenEnCadena(e.primerHijo(), e);
    }

    private static NodoLG masJovenEnCadena(NodoLG h, NodoLG mejor) {
        if (h == null) return mejor;
        NodoLG c = masJovenRec(h);
        boolean esMasJoven = c.personaDe().getFechaNacimiento().isAfter(mejor.personaDe().getFechaNacimiento());
        return masJovenEnCadena(h.getSig(), esMasJoven ? c : mejor);
    }

    /** Altura = cantidad de niveles (generaciones). Árbol vacío: 0. */
    public int altura() {
        return (raiz == null) ? 0 : alturaRec(raiz);
    }

    private static int alturaRec(NodoLG e) {
        return 1 + alturaEnCadena(e.primerHijo());
    }

    private static int alturaEnCadena(NodoLG h) {
        return (h == null) ? 0 : Math.max(alturaRec(h), alturaEnCadena(h.getSig()));
    }

    public int nivelDe(long ced) {
        return requerir(ced).nivel;
    }

    public List<Persona> registrosPorNivel(int nivel) {
        if (nivel < 1 || nivel > altura())
            throw new IllegalArgumentException("El nivel debe estar entre 1 y " + altura() + ".");
        List<Persona> res = new ArrayList<>();
        porNivel(raiz, 1, nivel, res);
        return res;
    }

    private static void porNivel(NodoLG h, int actual, int objetivo, List<Persona> res) {
        if (h == null) return;
        if (actual == objetivo) res.add(h.personaDe());
        else porNivel(h.primerHijo(), actual + 1, objetivo, res);   // bajar un nivel
        porNivel(h.getSig(), actual, objetivo, res);                // siguiente hermano (mismo nivel)
    }

    /** Nodos más profundos (los del último nivel). Normalmente uno; puede haber empates. */
    public List<Persona> nodosMasProfundos() {
        if (raiz == null) throw new IllegalStateException("El arbol esta vacio.");
        return registrosPorNivel(altura());
    }

    // =====================================================================
    //  4. OTRAS OPERACIONES
    // =====================================================================

    /**
     * Elimina todos los nodos de un nivel; sus hijos suben un nivel y quedan conectados
     * a sus abuelos (ordenados por cédula entre sus nuevos hermanos).
     * El nivel 1 (raíz) no se puede eliminar porque no tiene abuelo al que conectar.
     */
    public void eliminarNivel(int nivel) {
        if (raiz == null) throw new IllegalStateException("El arbol esta vacio.");
        if (nivel == 1)
            throw new IllegalArgumentException("No se puede eliminar el nivel 1 (la raiz): sus hijos no tendrian a quien conectarse.");
        if (nivel < 1 || nivel > altura())
            throw new IllegalArgumentException("El nivel debe estar entre 2 y " + altura() + ".");
        eliminarNivelRec(raiz, 1, nivel);
    }

    private static void eliminarNivelRec(NodoLG e, int actual, int nivel) {
        if (!e.esSublista()) return;
        if (actual == nivel - 1) {
            // Los hijos de e están en el nivel a eliminar: se desprenden y sus hijos suben
            NodoLG borrados = e.getDown().getSig();
            e.getDown().setSig(null);
            subirNietos(e, borrados);
            e.normalizar();
        } else {
            eliminarNivelEnCadena(e.getDown().getSig(), actual + 1, nivel);
        }
    }

    private static void eliminarNivelEnCadena(NodoLG h, int actual, int nivel) {
        if (h == null) return;
        eliminarNivelRec(h, actual, nivel);
        eliminarNivelEnCadena(h.getSig(), actual, nivel);
    }

    /** Recorre los nodos borrados y, de cada uno, sube sus hijos hasta el abuelo. */
    private static void subirNietos(NodoLG abuelo, NodoLG borrados) {
        if (borrados == null) return;
        NodoLG siguienteBorrado = borrados.getSig();
        borrados.setSig(null);
        subirHijos(abuelo, borrados.primerHijo());
        subirNietos(abuelo, siguienteBorrado);
    }

    private static void subirHijos(NodoLG abuelo, NodoLG nieto) {
        if (nieto == null) return;
        NodoLG siguiente = nieto.getSig();
        nieto.setSig(null);
        insertarOrdenado(abuelo, nieto);
        subirHijos(abuelo, siguiente);
    }

    /**
     * Ancestro común más cercano (ascendente que comparten ambas personas).
     * Se consideran solo ANCESTROS propios: si A es ancestro de B, el resultado es el
     * padre de A. Devuelve null si no comparten ninguno (por ejemplo, si una es la raíz).
     */
    public Persona ancestroComunMasCercano(long cedA, long cedB) {
        if (cedA == cedB) throw new IllegalArgumentException("Las cedulas deben ser distintas.");
        List<Persona> rutaA = camino(cedA);
        List<Persona> rutaB = camino(cedB);
        int limite = Math.min(rutaA.size(), rutaB.size()) - 1; // sin contar a las propias personas
        return comunRec(rutaA, rutaB, 0, limite, null);
    }

    private static Persona comunRec(List<Persona> a, List<Persona> b, int i, int limite, Persona comun) {
        if (i >= limite || a.get(i).getCedula() != b.get(i).getCedula()) return comun;
        return comunRec(a, b, i + 1, limite, a.get(i));
    }

    /** Desconecta a A de su padre y la reubica, con toda su descendencia, como hija de B. */
    public void trasladarRama(long cedA, long cedB) {
        if (cedA == cedB) throw new IllegalArgumentException("Las cedulas deben ser distintas.");
        Ubicacion ua = requerir(cedA);
        Ubicacion ub = requerir(cedB);

        if (ua.padre == null)
            throw new IllegalStateException("La raiz no tiene padre: no se puede trasladar.");
        if (ua.padre == ub.elem)
            throw new IllegalStateException("La persona " + cedA + " ya es hija de " + cedB + ".");
        if (buscarRec(ua.elem, null, null, 1, cedB) != null)
            throw new IllegalStateException("No se puede trasladar: B es descendiente de A (se formaria un ciclo).");

        ua.previo.setSig(ua.elem.getSig());
        ua.elem.setSig(null);
        ua.padre.normalizar();
        insertarOrdenado(ub.elem, ua.elem);
    }

}
