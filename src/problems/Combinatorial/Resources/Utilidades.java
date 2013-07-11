/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package problems.Combinatorial.Resources;

import java.util.Arrays;

/**
 *
 * @author diego
 */
public class Utilidades {
    
    public Utilidades(){
        
    }
    
    public void arrayReverse(Pieza[] piezas){
        Pieza[] piezas_= new Pieza[piezas.length];
        int j=0;
        
        for(int i=piezas.length-2;i>=0;--i)
        {            
            piezas_[j]=new Pieza(piezas[i].getId(),piezas[i].getAncho(),piezas[i].getAlto(),piezas[i].getCantidad());
            ++j;
        }        
        System.arraycopy( piezas_, 0, piezas , 0, piezas.length-1 );
//        piezas = Arrays.copyOf(piezas_, piezas_.length);
    }    
    
    public void mostrar(Pieza[] piezas){
                
        for(int i=0;i<piezas.length-1;++i)
        {
            piezas[i].mostrar();
        }            
        System.out.print('\n');
    }
}
