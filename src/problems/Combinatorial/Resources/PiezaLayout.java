/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package problems.Combinatorial.Resources;

import java.awt.Point;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public class PiezaLayout extends Pieza{
        
    public Point posicion;      
    int tipo; // Combinacion vertical u horizontal
//    int padre;
        
    public PiezaLayout(int i,int w, int h, int c, Point p, int t){
        super(i,w,h,p,c);    
        tipo=t;
        posicion=p;                        
    }        
    
    public Point getPosicion(){
        return posicion;
    }
    
    public void setPosicion(Point p){
        posicion=p;
    }          
    
    public int getTipo(){
        return tipo;
    }
    
    public void setTipo(int t){
        tipo=t;
    }                                      
    
    public void mostrar(){
        if(id==0)
            System.out.println(">Perdida=Id:"+id+" Ancho:"+ancho+" Alto:"+alto+" Area:"+getArea()+" Posicion:("+posicion.x+","+posicion.y+")"+"Tipo="+tipo);                
        else
            System.out.println("Ganancia=Id:"+id+" Ancho:"+ancho+" Alto:"+alto+" Area:"+getArea()+" Posicion:("+posicion.x+","+posicion.y+")"+"Tipo="+tipo);        
    }      
    
    int boolToInt(Boolean b) {
        return b.compareTo(false);
    }           
    
    public boolean reducirPerdidaH(PiezaLayout pieza){
        
        int reduccionPerdida;        
        
        if(pieza.getPosicion().getX()>this.getPosicion().getX())// Si la pieza esta a la derecha de la perdida        
            reduccionPerdida=(int)this.getPosicion().getX()+this.getAncho()-(int)pieza.getPosicion().getX();       
        else // Si la pieza esta a la izquierda de la perdida       
            reduccionPerdida=(int)this.getPosicion().getX()+this.getAncho()-((int)pieza.getPosicion().getX()+pieza.getAncho());                           
        if(pieza.getPosicion().getX()==this.getPosicion().getX())        
            reduccionPerdida=this.getAncho();                     
        
        if(reduccionPerdida!=this.getAncho())
        {
            this.setAncho(this.getAncho()-reduccionPerdida);                         
            return true;            
        }
        else
        {
            return false;
        }
    }          
    
    public boolean reducirPerdidaV(PiezaLayout pieza){

        int reduccionPerdida;                        
        
        if(pieza.getPosicion().getY()>this.getPosicion().getY()) // Si la pieza esta arriba de la perdida        
            reduccionPerdida=(int)this.getPosicion().getY()+this.getAlto()-(int)pieza.getPosicion().getY();               
        else // Si la pieza esta abajo de la perdida               
            reduccionPerdida=(int)this.getPosicion().getY()+this.getAlto()-((int)pieza.getPosicion().getY()+pieza.getAlto());                                   
        if(pieza.getPosicion().getY()==this.getPosicion().getY())        
            reduccionPerdida=this.getAlto();
                            
        if(reduccionPerdida!=this.getAlto())
        {
            this.setAlto(this.getAlto()-reduccionPerdida);                       
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public boolean coincidePerdida(PiezaLayout perdida)
    {
        if(this==null || perdida==null)
            return false;            
        
        if(this.getPosicion().getLocation().equals(perdida.getPosicion().getLocation()) && 
           this.getAncho()==perdida.getAncho() && this.getAlto()==perdida.getAlto())
            return true;
        else
            return false;
    }
    
    public boolean reducirPerdida(PiezaLayout pieza)
    { // Reduce la perdida en funcion de su posicion respecto a la pieza insertada
        // Obtener el vector diferencia entre posicion de la perdida y posicion de la pieza        
        double pendiente=(double)(this.getPosicion().getY()-pieza.getPosicion().getY())/(Math.abs(this.getPosicion().getX()-pieza.getPosicion().getX()));
        // Otener la direccion del vector
        double angulo=Math.atan(pendiente);
        
//        System.out.println("----------------------REDUCCION PERDIDA---------------------");
//        this.mostrar();
//        pieza.mostrar();
//        System.out.println("Pendiente:"+pendiente+" Angulo:"+angulo);
//        System.out.println("------------------------------------------------------------");        
        
        double pi=Math.PI;                        
        
        if(angulo==pi/2 || angulo==-pi/2)
        { // Si la direccion del vector es vertical
            if(this.getTipo()==0) // Si la perdida es vertical, se reduce la perdida verticalmente            
                return this.reducirPerdidaV(pieza);
            else // Si la perdida es horizontal se elimina la perdida           
                return false;                                                    
        }
        if(angulo==0.0)    
        { // Si la direccion del vector es horizontal
            if(this.getTipo()==0) // Si la perdida es vertical, se elimina la perdida            
                return false;                                                                    
            else // Si la perdida es horizontal, se reduce la perdida horizontalmente           
                return this.reducirPerdidaH(pieza);                                
        }
        if(0.0<angulo && angulo<pi/2)
        { // Si la direccion del vector apunta a los cuadrantes superiores, se reduce la perdida horizontalmente
            return this.reducirPerdidaH(pieza);
        }        
        if(-pi/2<angulo && angulo<0.0)            
        { // Si la direccion del vector apunta a los cuadrantes inferiores, se reduce la perdida verticalmente
            return this.reducirPerdidaV(pieza);                    
        }                                        
        return true;
    }                              
}
