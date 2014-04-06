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
    public boolean combinacion;
    
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
    
    public Pieza(int i,int w,int h,int c, boolean cb){
        super(w,h);
        id=i;
        ancho=w;
        alto=h;
        cantidad=c;   
        restriccion=c;
        combinacion=cb;
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
    
    public boolean getCombinacion(){
        return combinacion;
    }
    
    public void setCombinacion(boolean cb){
        combinacion= cb;
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
    
    public int firstFitExt2(Pair<PiezaLayout,PiezaLayout> perdidas)
    {                
        // Devolver la primera perdida en la que quepa la pieza                                
        PiezaLayout perdidaV=perdidas.getFirst();            
        PiezaLayout perdidaH=perdidas.getSecond();                                                                      

        if(this.fits(perdidaH))
            return 1;

        if(this.fits(perdidaV))
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
    
    public int BL(Map<Integer,Pair<PiezaLayout,PiezaLayout> > perdidas, Map<Integer,PiezaLayout > layout, int anchoPatron, int altoPatron)
    {
        Iterator it = layout.entrySet().iterator();        
        double y=0;
        double xAux=0;
        Integer idPadre=0;
        Integer idL=0;
        
        // Buscar la pieza superior del extremo derecho del layout
        while(it.hasNext()) 
        {                    
            Map.Entry pairs = (Map.Entry)it.next();                        
            idPadre=(Integer)pairs.getKey();
                        
            PiezaLayout pieza=layout.get(idPadre);                        
            
            if(pieza.getX()+pieza.getAncho()==anchoPatron)   
            {
                if(pieza.getY()+pieza.getAlto()>y)
                {
                    y=pieza.getY()+pieza.getAlto();
                    xAux=pieza.getX();
                }
            }
        }
        it = layout.entrySet().iterator();        
        double x=0;
        // Buscar la pieza mas a la derecha del extremo izquierdo del patron
        while(it.hasNext()) 
        {                    
            Map.Entry pairs = (Map.Entry)it.next();                        
            idPadre=(Integer)pairs.getKey();
                        
            PiezaLayout pieza=layout.get(idPadre);                        
            
            if(pieza.getY()<y && pieza.getY()+pieza.getAlto()>y)   
            {
                if(pieza.getX()<xAux && pieza.getX()+pieza.getAncho()>x)              
                {
                    x=pieza.getX()+pieza.getAncho();
                    idL=idPadre;
                }
            }
        }
        if(y==0 || x==0)
               return 0;
        
        // La pieza encontrada es la pieza padre
        // Antes de devolver su id, se crea la perdida y se verifica que la pieza no viole las siguientes restricciones
        // que no intersecte con ninguna pieza y que no sobre pase los limites del patron
        PiezaLayout perdida= new PiezaLayout(0,this.ancho,this.alto,1,new Point((int)x,(int)y),1);
        if(perdida.getX()+perdida.getAncho()>anchoPatron || perdida.getY()+perdida.getAlto()>altoPatron)
            return 0;
        
        it = layout.entrySet().iterator();        
        
        // Buscar intersecciones entre la perdida y las piezas
        while(it.hasNext()) 
        {                    
            Map.Entry pairs = (Map.Entry)it.next();                        
            idPadre=(Integer)pairs.getKey();
                        
            PiezaLayout pieza=layout.get(idPadre);                        
            
            if(pieza.intersection(perdida).getHeight()>0 && pieza.intersection(perdida).getWidth()>0)  
                return 0;
        }
        
        it = perdidas.entrySet().iterator();        
        
        // Buscar intersecciones entre la perdida y las perdidas internas
        while(it.hasNext()) 
        {                    
            Map.Entry pairs = (Map.Entry)it.next();                        
            idPadre=(Integer)pairs.getKey();
                        
            PiezaLayout perdidaV=perdidas.get(idPadre).getFirst();            
            PiezaLayout perdidaH=perdidas.get(idPadre).getSecond();                                       
            
            if(perdidaV!=null)
            {
                if(perdidaV.intersection(perdida).getHeight()>0 && perdidaV.intersection(perdida).getWidth()>0)  
                {                    
                    if(!perdidaV.reducirPerdida(perdida))                                                                                                            
                        perdidaV=null;
//                    return 0;
                }
            }
            if(perdidaH!=null)
            {
                if(perdidaH.intersection(perdida).getHeight()>0 && perdidaH.intersection(perdida).getWidth()>0)  
                {
                    if(!perdidaH.reducirPerdida(perdida))                                                                                                            
                        perdidaH=null;
//                    return 0;
                }
            }                        
        }
        // Si paso todas las pruebas, se asigna la perdida creada al id de la pieza padre y se devuelve
//        System.out.println(idL);
        if(idL!=0)
        {            
            perdidas.get(idL).setSecond(perdida);
        }
        return idL;
    }
}
