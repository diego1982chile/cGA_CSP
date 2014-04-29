/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package problems.Combinatorial.Resources;

import java.util.Stack;
import javax.swing.tree.TreeNode;

/**
 *
 * @author diego
 */
public class BNode {
 
    //definicion de la clase NodoArbol
    //miembros de acceso
    int dato;
    BNode izq;	
    BNode der;

    //iniciar dato y hacer de este nodo un nodo hoja
    public BNode(int dato)
    {
            this.dato = dato;
            izq = der = null; //el nodo no tiene hijos
    }
        
    public BNode getIzq()
    {
        return izq;
    }
    
    public void setIzq(int dato)
    {
        izq=new BNode(dato);
    }
        
    public BNode getDer()
    {
        return der;
    }        
    
    public void setDer(int dato)
    {
        der=new BNode(dato);
    }
    
    public int getDato()
    {
        return dato;
    }
        
    public void preOrden(){
        System.out.print(dato+" ");
        if(izq!=null)
            izq.preOrden();
        if(der!=null)
            der.preOrden();
    }

    //buscar punto de insercion  e insertar nodo nuevo
    public void insertar(BNode p,int val, int pos)
    {                     
        int cuociente=pos;
        Stack path = new Stack();
//        TreeNode p = dato;  // helper node to follow path
        cuociente /= 2;  // skip last step
        while (cuociente>1) {    // build path to follow
            path.push(cuociente%2);
            cuociente /= 2;
        }
        while (!path.isEmpty()) {
            Object q = path.pop();
            if (q.equals(0))
                p = p.getIzq();
            else
                p = p.getDer();
        }
        if (pos%2==0) // check last step
            p.setIzq(val);
        else
            p.setDer(val);
        
    } //fin del metodo insertar
} //fin clase nodoarbol

