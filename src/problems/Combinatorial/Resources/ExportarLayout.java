/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package problems.Combinatorial.Resources;

import java.io.*;
import java.util.Map;
import jcell.CellularGA;
// Imports para escribir archivo

/**
 *
 * @author Administrator
 */
public class ExportarLayout {
 
    int ancho;
    int alto;
    public Map<Integer,PiezaLayout> layout; // Layout
    public Map<Integer,Pair<PiezaLayout,PiezaLayout> > perdidaInterna; // Perdida interna
    public Pair<PiezaLayout,PiezaLayout> perdidaExterna; // Perdida externa     
    public File output;                    
    
    public ExportarLayout(int w,int h,Map<Integer,PiezaLayout> l,Map<Integer,Pair<PiezaLayout,PiezaLayout> > pi, Pair<PiezaLayout,PiezaLayout> pe, String f)
    {    
        ancho=w;
        alto=h;
        layout=l;
        perdidaInterna=pi;
        perdidaExterna=pe;
        output= new File(f);                            
    }
    
    public void exportar() throws FileNotFoundException, IOException
    {        
        if (output.exists()) 
        {            
            FileOutputStream fos = new FileOutputStream(output, false);        
            fos.close();
        }        
        
        BufferedWriter bw = null; 
        
        int cantPiezas=layout.size();
        int cont=1;
        
        while(cont<=cantPiezas)
        {
            PiezaLayout pieza=layout.get(cont);
            PiezaLayout perdidaV=perdidaInterna.get(cont).getFirst();
            PiezaLayout perdidaH=perdidaInterna.get(cont).getSecond();            
            
            try {
                bw = new BufferedWriter(new FileWriter(output, true));
                bw.write("G,"+(int)pieza.getPosicion().getX()+","+(int)pieza.getPosicion().getY()+","+pieza.getAncho()+","+pieza.getAlto()+";");
                if(perdidaV!=null)
                    bw.write("P,"+(int)perdidaV.getPosicion().getX()+","+(int)perdidaV.getPosicion().getY()+","+perdidaV.getAncho()+","+perdidaV.getAlto()+";");                    
                if(perdidaH!=null)
                    bw.write("P,"+(int)perdidaH.getPosicion().getX()+","+(int)perdidaH.getPosicion().getY()+","+perdidaH.getAncho()+","+perdidaH.getAlto()+";");                                    
//                bw.newLine();
                bw.flush();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {                       // always close the file
                if (bw != null) try {
                    bw.close();
                } catch (IOException ioe2) {
                // just ignore it
                }
            } // end try/catch/finally 
            cont++;
        }
        PiezaLayout perdidaExtV=perdidaExterna.getFirst();
        PiezaLayout perdidaExtH=perdidaExterna.getSecond();                    
        
        try {
            bw = new BufferedWriter(new FileWriter(output, true));            
            if(perdidaExtV!=null)            
                bw.write("P,"+(int)perdidaExtV.getPosicion().getX()+","+(int)perdidaExtV.getPosicion().getY()+","+perdidaExtV.getAncho()+","+perdidaExtV.getAlto()+";");                                    
            if(perdidaExtH!=null)
                bw.write("P,"+(int)perdidaExtH.getPosicion().getX()+","+(int)perdidaExtH.getPosicion().getY()+","+perdidaExtH.getAncho()+","+perdidaExtH.getAlto()+";");                                    
//            bw.newLine();
            bw.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {                       // always close the file
            if (bw != null) try {
                bw.close();
            } catch (IOException ioe2) {
            // just ignore it
            }
        } // end try/catch/finally                        
    }
}
