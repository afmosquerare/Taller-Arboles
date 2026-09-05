/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package taller_arboles;

/**
 *
 * @author sala313
 */
public class ArbolGenealogico {
    private Nodo raiz;

    public ArbolGenealogico( Persona persona ) {
        this.raiz = new Nodo( persona );
    }
    
    public void mostrar(Nodo r){
        if(this.raiz == null ) return;
        Nodo aux = this.raiz;
        while( aux != null){
            if( !aux.EsPadre()){
                System.out.println(aux.getPersona().getNombre( ) + ", Edad: " + aux.getPersona().getEdad());
            }
            else{
                mostrar(aux.getLigaPadre());
            }
            aux = aux.getLiga();
        }
    }

    public Nodo getRaiz() {
        return raiz;
    }

    
    public String insertar(String cedulaPadre, Persona persona ){
        /*if( this.raiz == null ){
            this.raiz = new Nodo( persona );
            return "Esta persona se ha convertido en la raiz del arbol";
        }*/
           
        Nodo padre = buscarNodo(this.raiz, cedulaPadre);
        if( padre == null ) return "La cedula ingresada no existe";
        
        
        Nodo nuevoHijo = new Nodo( new Persona( persona ) );
        Nodo anterior;
        if( padre.getLiga() == null ){
            padre.setLiga( nuevoHijo );
            return "Persona insertada correctamente";
        }
        anterior = padre;
        Nodo siguiente = padre.getLiga();
        while( siguiente != null){
            if( siguiente.getPersona().getCedula().compareTo(nuevoHijo.getPersona().getCedula() ) > 0   ){
                anterior.setLiga( nuevoHijo );
                nuevoHijo.setLiga( siguiente );
                return "Persona insertada correctamente";
            }
            if( siguiente.getLiga() == null ){
                siguiente.setLiga( nuevoHijo );
                return "Persona insertada correctamente";
            }
            anterior = siguiente;
            siguiente = siguiente.getLiga();

        }
        return "Hubo un error al insertar";
    }
    
    
    public Nodo buscarNodo(Nodo r, String cedula){
        if(r == null ) return null;
        if( r.getPersona().getCedula().equals(cedula) ) return r;
        Nodo aux = buscarNodo(r.getLigaPadre(), cedula);
        if(aux != null ) return aux;
        
        return buscarNodo( r.getLiga(), cedula );
    }
    
    
}
