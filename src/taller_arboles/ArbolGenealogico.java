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

    public ArbolGenealogico(Nodo raiz) {
        this.raiz = null;
    }
    
    public void mostrar(Nodo r){
        if(this.raiz == null ) return;
        Nodo aux = this.raiz;
        while( aux != null){
            if( !aux.EsPadre()){
                System.out.println(aux.getPersona().getNombre());
            }
            else{
                mostrar(aux.getLigaPadre());
            }
            aux = aux.getLiga();
        }
    }
    
    public String insertar(String cedulaPadre, Persona persona ){
        if( this.raiz == null ) return "El arbol esta vacio";
        Nodo padre = buscarNodo(this.raiz, cedulaPadre);
        if( padre == null ) return "La cedula ingresada no existe";
        
        
        Nodo nuevoHijo = new Nodo( new Persona( persona ) );
        Nodo anterior;
        if( padre.getLiga() == null ){
            padre.setLiga( nuevoHijo );
            return "Persona insertada correctamente";
        }
        Nodo siguiente = padre.getLiga();
        while( siguiente != null){
            if( siguiente.getPersona().getCedula().compareTo(nuevoHijo.getPersona().getCedula() ) > 0   ){
            
            }
        
            siguiente = siguiente.getLiga();
        }
        
    }
    
    
    public Nodo buscarNodo(Nodo r, String cedula){
        if(r == null ) return null;
        if( r.getPersona().getCedula().equals(cedula) ) return r;
        Nodo aux = buscarNodo(r.getLigaPadre(), cedula);
        if(aux != null ) return aux;
        
        return buscarNodo( r.getLiga(), cedula );
    }
    
    
}
