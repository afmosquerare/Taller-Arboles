/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package taller_arboles;

import java.time.LocalDate;

/**
 *
 * @author sala313
 */
public class Taller_arboles {

    public static void main(String[] args) {
        ArbolGenealogico arbol = new ArbolGenealogico( new Persona( "Diego", "5", LocalDate.of(1980, 5, 1) ) );
        System.out.println(  arbol.insertar("5", new Persona("Ana", "2", LocalDate.of(2000,12,24 ) ) ) );
        arbol.mostrar( arbol.getRaiz() );
        
        
    }
    
}
