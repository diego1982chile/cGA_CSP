/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package problems.Combinatorial.Resources;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public class Pieza extends Rectangle {
    
    public int id;    
    public int ancho;
    public int alto;    
    public int cantidad;
    public int restriccion;
    
    public Pieza(int id){
        super();
        this.id=id;
    }
    
    public Pieza(int i,int w,int h,int c){
        super(w,h);
        id=i;
        ancho=w;
        alto=h;
        cantidad=c;   
        restriccion=c;
    }
    
    public Pieza(int i,int w,int h, Point p,int c){        
        super((int)p.getX(),(int)p.getY(),w,h);        
        id=i;                
        ancho=w;
        alto=h;        
        cantidad=c;
        restriccion=c;
    }    
    
    public int getId(){
        return id;
    }
    
    public void setId(int a){
        id= a;
    }        
        
    public int getAncho(){
        return ancho;
    }
    
    public void setAncho(int a){
        ancho= a;
        width=a;
    }    
    
    public int getAlto(){
        return alto;
    }
    
    public void setAlto(int a){
        alto= a;
        height=a;
    }    
    
    public int getCantidad(){
        return cantidad;
    }
    
    public void setCantidad(int a){
        cantidad= a;
    }            
    
    public int getArea(){
        return (int)ancho*alto;
    }
    
    public void mostrar(){
        System.out.println("Pieza=Id:"+id+" Ancho:"+ancho+" Alto:"+alto+" Cantidad:"+cantidad);
    }    
    
    public boolean fits(PiezaLayout perdida)
    { // Chequea si la pieza cabe en la perdida
        if(perdida!=null)
        {
            if(this.getAncho()<=perdida.getAncho() && this.getAlto()<=perdida.getAlto())
                return true;
            else
                return false;                      
        }        
        return false;       
    }
                
    public int firstFit(Map<Integer,Pair<PiezaLayout,PiezaLayout> > perdidas)
    {
        Iterator it = perdidas.entrySet().iterator();        
        
        // Devolver la primera perdida en la que quepa la pieza
        while(it.hasNext()) 
        {                    
            Map.Entry pairs = (Map.Entry)it.next();                        
            Integer idPadre=(Integer)pairs.getKey();
                        
            PiezaLayout perdidaV=perdidas.get(idPadre).getFirst();            
            PiezaLayout perdidaH=perdidas.get(idPadre).getSecond();                                                                      
            
            if(this.fits(perdidaV))
                return idPadre;
            
            if(this.fits(perdidaH))
                return -idPadre;            
        }
        return 0;        
    }
    
    public int bestFit(Map<Integer,Pair<PiezaLayout,PiezaLayout> > perdidas)
    {
        Iterator it = perdidas.entrySet().iterator();        
        int mejorArea=999999;
        int mejorId=0;
        
        // Devolver la perdida en la que mejor quepa la pieza (con menor perdida)
        while(it.hasNext()) 
        {                    
            Map.Entry pairs = (Map.Entry)it.next();                        
            Integer idPadre=(Integer)pairs.getKey();
                        
            PiezaLayout perdidaV=perdidas.get(idPadre).getFirst();            
            PiezaLayout perdidaH=perdidas.get(idPadre).getSecond();                                                                      
            
            if(this.fits(perdidaV))
            {
                if(perdidaV.getArea()<mejorArea)
                {
                    mejorArea=perdidaV.getArea();
                    mejorId=idPadre;
                }
            }
            if(this.fits(perdidaH))
            {
                if(perdidaH.getArea()<mejorArea)
                {
                    mejorArea=perdidaH.getArea();
                    mejorId=-idPadre;
                }
            }                            
        }        
    return mejorId;        
    }    
    
    public int firstFitExt(Pair<PiezaLayout,PiezaLayout> perdidas)
    {                
        // Devolver la primera perdida en la que quepa la pieza                                
        PiezaLayout perdidaV=perdidas.getFirst();            
        PiezaLayout perdidaH=perdidas.getSecond();                                                                      

        if(this.fits(perdidaV))
            return 1;

        if(this.fits(perdidaH))
            return -1;            
        
        return 0;        
    }
    
    public int bestFitExt(Pair<PiezaLayout,PiezaLayout> perdidas)
    {        
        int mejorArea=999999;
        int mejorId=0;        
        // Devolver la perdida en la que mejor quepa la pieza (con menor perdida)                        
        PiezaLayout perdidaV=perdidas.getFirst();            
        PiezaLayout perdidaH=perdidas.getSecond();                                                                      
            
        if(this.fits(perdidaV))
        {
            if(perdidaV.getArea()<mejorArea)
            {
                mejorArea=perdidaV.getArea();
                mejorId=1;
            }
        }
        if(this.fits(perdidaH))
        {
            if(perdidaH.getArea()<mejorArea)
            {
                mejorArea=perdidaH.getArea();
                mejorId=-1;
            }
        }                                    
        return mejorId;        
    }     
    
    public boolean existePieza(ArrayList<Pieza> fenotipo)
    {
//        ListIterator it = fenotipo.listIterator();
        
//        while(it.hasNext())
//        {
//            Pieza p=(Pieza)it.next();
//            
//            if(p.getId()==this.id)
//                return true;                            
//        }            
        return false;
    }
}
