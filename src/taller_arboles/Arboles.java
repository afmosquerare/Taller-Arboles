package taller_arboles;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Arboles {

    private static final Scanner SC = new Scanner(System.in);
    private static final ArbolGenealogico ARBOL = new ArbolGenealogico();

    public static void main(String[] args) {
        System.out.println("=== GESTOR GENEALOGICO (arbol n-ario con listas generalizadas) ===");
        System.out.println("\nPrimero registra a la persona raiz (ancestro principal) del arbol.");
        registrar();
        menuPrincipal();
    }

    private static void menuPrincipal() {
        System.out.println();
        System.out.println("------------- MENU PRINCIPAL -------------");
        System.out.println(" 1. Gestion de personas");
        System.out.println(" 2. Consultas de relaciones familiares");
        System.out.println(" 3. Consultas estructurales y visualizacion");
        System.out.println(" 4. Otras operaciones");
        System.out.println(" 0. Salir");
        int op = leerEntero("Opcion: ");
        switch (op) {
            case 1: menuGestion(); break;
            case 2: menuRelaciones(); break;
            case 3: menuEstructura(); break;
            case 4: menuOtras(); break;
            case 0: System.out.println("Hasta pronto."); break;
            default: System.out.println("Opcion no valida.");
        }
        if (op != 0) menuPrincipal();
    }

    private static void menuGestion() {
        System.out.println("\n--- 1. GESTION DE PERSONAS ---");
        System.out.println(" 1. Registrar persona");
        System.out.println(" 2. Eliminar persona (conservando linaje)");
        System.out.println(" 3. Actualizar persona");
        System.out.println(" 0. Volver");
        int op = leerEntero("Opcion: ");
        try {
            switch (op) {
                case 1: registrar(); break;
                case 2: {
                    long c = leerLong("Cedula a eliminar: ");
                    ARBOL.eliminar(c);
                    System.out.println("Persona eliminada.");
                    break;
                }
                case 3: actualizar(); break;
                case 0: break;
                default: System.out.println("Opcion no valida.");
            }
        } catch (RuntimeException e) {
            System.out.println("[!] " + e.getMessage());
        }
        if (op != 0) menuGestion();
    }

    private static void menuRelaciones() {
        System.out.println("\n--- 2. RELACIONES FAMILIARES ---");
        System.out.println(" 1. Padre");
        System.out.println(" 2. Hijos");
        System.out.println(" 3. Hermanos");
        System.out.println(" 4. Tios");
        System.out.println(" 5. Sobrinos");
        System.out.println(" 6. Primos");
        System.out.println(" 7. Ancestros");
        System.out.println(" 8. Descendientes");
        System.out.println(" 0. Volver");
        int op = leerEntero("Opcion: ");
        try {
            if (op >= 1 && op <= 8) {
                long c = leerLong("Cedula de la persona: ");
                System.out.println("Persona consultada: " + ARBOL.consultar(c));
                switch (op) {
                    case 1: {
                        Persona p = ARBOL.padre(c);
                        System.out.println(p == null ? "Es la raiz: no tiene padre." : "Padre: " + p);
                        break;
                    }
                    case 2: mostrar("Hijos", ARBOL.hijos(c)); break;
                    case 3: mostrar("Hermanos", ARBOL.hermanos(c)); break;
                    case 4: mostrar("Tios", ARBOL.tios(c)); break;
                    case 5: mostrar("Sobrinos", ARBOL.sobrinos(c)); break;
                    case 6: mostrar("Primos", ARBOL.primos(c)); break;
                    case 7: mostrar("Ancestros (del mas cercano a la raiz)", ARBOL.ancestros(c)); break;
                    case 8: mostrar("Descendientes", ARBOL.descendientes(c)); break;
                }
            } else if (op != 0) {
                System.out.println("Opcion no valida.");
            }
        } catch (RuntimeException e) {
            System.out.println("[!] " + e.getMessage());
        }
        if (op != 0) menuRelaciones();
    }

    private static void menuEstructura() {
        System.out.println("\n--- 3. CONSULTAS ESTRUCTURALES ---");
        System.out.println(" 1. Visualizar arbol");
        System.out.println(" 2. Ver representacion como lista generalizada");
        System.out.println(" 3. Nodo con mayor grado");
        System.out.println(" 4. Familiar mas joven");
        System.out.println(" 5. Altura del arbol");
        System.out.println(" 6. Nivel de un registro");
        System.out.println(" 7. Registros por nivel");
        System.out.println(" 8. Nodo con mayor nivel (mas profundo)");
        System.out.println(" 0. Volver");
        int op = leerEntero("Opcion: ");
        try {
            switch (op) {
                case 1: System.out.println(ARBOL.visualizar()); break;
                case 2: System.out.println(ARBOL.comoListaGeneralizada()); break;
                case 3: {
                    Persona p = ARBOL.mayorGrado();
                    System.out.println(p);
                    System.out.println("Hijos directos: " + ARBOL.cantidadHijos(p.getCedula()));
                    break;
                }
                case 4: System.out.println(ARBOL.masJoven()); break;
                case 5: System.out.println("Altura del arbol: " + ARBOL.altura() + " nivel(es)/generacion(es)."); break;
                case 6: {
                    long c = leerLong("Cedula: ");
                    System.out.println(ARBOL.consultar(c));
                    System.out.println("Nivel: " + ARBOL.nivelDe(c) + "  (la raiz es el nivel 1)");
                    break;
                }
                case 7: {
                    int n = leerEntero("Nivel: ");
                    mostrar("Registros del nivel " + n, ARBOL.registrosPorNivel(n));
                    break;
                }
                case 8:
                    mostrar("Nodo(s) mas profundo(s) (nivel " + ARBOL.altura() + ")", ARBOL.nodosMasProfundos());
                    break;
                case 0: break;
                default: System.out.println("Opcion no valida.");
            }
        } catch (RuntimeException e) {
            System.out.println("[!] " + e.getMessage());
        }
        if (op != 0) menuEstructura();
    }

    private static void menuOtras() {
        System.out.println("\n--- 4. OTRAS OPERACIONES ---");
        System.out.println(" 1. Eliminar nivel");
        System.out.println(" 2. Ancestro comun mas cercano");
        System.out.println(" 3. Trasladar rama (adopcion)");
        System.out.println(" 0. Volver");
        int op = leerEntero("Opcion: ");
        try {
            switch (op) {
                case 1: {
                    int n = leerEntero("Nivel a eliminar: ");
                    ARBOL.eliminarNivel(n);
                    System.out.println("Nivel eliminado. Arbol resultante:");
                    System.out.println(ARBOL.visualizar());
                    break;
                }
                case 2: {
                    long a = leerLong("Cedula de la persona 1: ");
                    long b = leerLong("Cedula de la persona 2: ");
                    Persona p = ARBOL.ancestroComunMasCercano(a, b);
                    System.out.println(p == null ? "No comparten ningun ancestro." : "Ancestro comun mas cercano: " + p);
                    break;
                }
                case 3: {
                    long a = leerLong("Cedula de la persona A (rama a trasladar): ");
                    long b = leerLong("Cedula de la persona B (nuevo padre): ");
                    ARBOL.trasladarRama(a, b);
                    System.out.println("Rama trasladada. Arbol resultante:");
                    System.out.println(ARBOL.visualizar());
                    break;
                }
                case 0: break;
                default: System.out.println("Opcion no valida.");
            }
        } catch (RuntimeException e) {
            System.out.println("[!] " + e.getMessage());
        }
        if (op != 0) menuOtras();
    }

    // ------------------------------------------------------------------
    //  Acciones con varios datos de entrada
    // ------------------------------------------------------------------

    private static void registrar() {
        Long cedulaPadre = null;
        if (ARBOL.estaVacio()) {
            System.out.println("El arbol esta vacio: esta persona sera la RAIZ (ancestro principal).");
        }
        String nombre = leerTexto("Nombre: ", false);
        long cedula = leerLong("Cedula: ");
        LocalDate fecha = leerFecha("Fecha de nacimiento (dd/MM/yyyy): ");
        if (!ARBOL.estaVacio()) {
            cedulaPadre = leerLong("Cedula del padre: ");
        }
        ARBOL.registrar(nombre, cedula, fecha, cedulaPadre);
        System.out.println("Persona registrada.");
    }

    private static void actualizar() {
        long actual = leerLong("Cedula de la persona a actualizar: ");
        System.out.println("Registro actual: " + ARBOL.consultar(actual));
        System.out.println("(Deje vacio un campo para no modificarlo)");

        String nombre = leerTexto("Nuevo nombre: ", true);
        Long nuevaCedula = leerLongOpcional("Nueva cedula: ");
        LocalDate fecha = leerFechaOpcional("Nueva fecha de nacimiento (dd/MM/yyyy): ");

        ARBOL.actualizar(actual, nombre, nuevaCedula, fecha);
        System.out.println("Registro actualizado.");
    }

    // ------------------------------------------------------------------
    //  Utilidades de salida
    // ------------------------------------------------------------------

    private static void mostrar(String titulo, List<Persona> lista) {
        System.out.println(titulo + ":");
        if (lista.isEmpty()) {
            System.out.println("  (sin registros)");
        } else {
            mostrarDesde(lista, 0);
        }
    }

    private static void mostrarDesde(List<Persona> lista, int i) {
        if (i >= lista.size()) return;
        System.out.println("  - " + lista.get(i));
        mostrarDesde(lista, i + 1);
    }

    // ------------------------------------------------------------------
    //  Utilidades de entrada (validación recursiva: si el dato es
    //  inválido, el método se vuelve a llamar hasta obtener uno válido)
    // ------------------------------------------------------------------

    private static String leerLinea() {
        if (!SC.hasNextLine()) {          // fin de la entrada (Ctrl+D / Ctrl+Z)
            System.out.println("\nFin de la entrada. Saliendo.");
            System.exit(0);
        }
        return SC.nextLine().trim();
    }

    private static String leerTexto(String msg, boolean permitirVacio) {
        System.out.print(msg);
        String s = leerLinea();
        if (!s.isEmpty() || permitirVacio) return s;
        System.out.println("El valor no puede estar vacio.");
        return leerTexto(msg, permitirVacio);
    }

    private static int leerEntero(String msg) {
        System.out.print(msg);
        try {
            return Integer.parseInt(leerLinea());
        } catch (NumberFormatException e) {
            System.out.println("Ingrese un numero entero valido.");
            return leerEntero(msg);
        }
    }

    private static long leerLong(String msg) {
        System.out.print(msg);
        try {
            return Long.parseLong(leerLinea());
        } catch (NumberFormatException e) {
            System.out.println("Ingrese un numero valido.");
            return leerLong(msg);
        }
    }

    private static LocalDate leerFecha(String msg) {
        System.out.print(msg);
        try {
            return LocalDate.parse(leerLinea(), Persona.FORMATO);
        } catch (DateTimeParseException e) {
            System.out.println("Fecha no valida. Use el formato dd/MM/yyyy (ej. 25/12/1990).");
            return leerFecha(msg);
        }
    }

    /** Igual que leerLong, pero una línea vacía devuelve null ("no modificar"). */
    private static Long leerLongOpcional(String msg) {
        System.out.print(msg);
        String s = leerLinea();
        if (s.isEmpty()) return null;
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            System.out.println("Ingrese un numero valido (o deje vacio).");
            return leerLongOpcional(msg);
        }
    }

    /** Igual que leerFecha, pero una línea vacía devuelve null ("no modificar"). */
    private static LocalDate leerFechaOpcional(String msg) {
        System.out.print(msg);
        String s = leerLinea();
        if (s.isEmpty()) return null;
        try {
            return LocalDate.parse(s, Persona.FORMATO);
        } catch (DateTimeParseException e) {
            System.out.println("Fecha no valida. Use dd/MM/yyyy (o deje vacio).");
            return leerFechaOpcional(msg);
        }
    }
}
