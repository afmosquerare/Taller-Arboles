package taller_arboles;

import java.time.LocalDate;
import java.time.Period;
import java.util.Date;

public class Persona {

    private String nombre;
    private String cedula;
    private LocalDate fechaNacimiento;

    public Persona(String nombre, String cedula, LocalDate fechaNacimiento) {
        this.nombre = nombre;
        this.cedula = cedula;
        this.fechaNacimiento = fechaNacimiento;
    }

    public Persona(Persona persona) {
        this.nombre = persona.nombre;
        this.cedula = persona.cedula;
        this.fechaNacimiento = persona.fechaNacimiento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
    
    public int getEdad(){
        return Period.between(fechaNacimiento, LocalDate.now() ).getYears();
    }
    
 
}
