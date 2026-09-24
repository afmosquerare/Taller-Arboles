package taller_arboles;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

public class Persona {

    /** Formato de fecha usado para leer y mostrar (día/mes/año), en modo estricto. */
    public static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private String nombre;
    private long cedula;
    private LocalDate fechaNacimiento;

    public Persona(String nombre, long cedula, LocalDate fechaNacimiento) {
        this.nombre = nombre;
        this.cedula = cedula;
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getNombre() { return nombre; }
    public long getCedula() { return cedula; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCedula(long cedula) { this.cedula = cedula; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    /** Edad en años cumplidos a la fecha actual. */
    public int getEdad() {
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    /** Texto corto: "Nombre (cedula)". */
    public String resumen() {
        return nombre + " (" + cedula + ")";
    }

    @Override
    public String toString() {
        return String.format("%s | C.C. %d | Nac.: %s | %d anos",
                nombre, cedula, fechaNacimiento.format(FORMATO), getEdad());
    }
}
