/**
 * @author Diego Soto Jara
 *
 * The problem must provide the following parameters:
 *    - variables: the number of variables of the problem
 *    - maxFitness: the optimum (or best known solution) of the problem
 *    - Target.maximize: set whether it is a maximization or minimization problem
 *    - minAllowedValues: Minimum allowed value for each gene  
 *    - maxAllowedValues: Maximum allowed value for each gene
 *
 * The problem: PCPGBR Problema de corte de piezas guillotina bidimensional restricto
 * 
 */

package problems.Combinatorial;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import jcell.*;
import static problems.Combinatorial.CTDC.longitCrom;
import static problems.Combinatorial.CTDC.maxFitness;
//import org.apache.commons.lang3.ArrayUtils;
import problems.Combinatorial.Resources.*;

public class CTDC2 extends Problem implements Utilities {
    
    public static int longitCrom = 0;
    public static double maxFitness = Double.MAX_VALUE;
    
    /* Estructuras de la aplicación */
    
    public Pieza[] piezas; // Arreglo con los tipos de piezas    
    public ArrayList<Pieza> fenotipo; // Secuencia ordenada de piezas
    public Map<Integer,PiezaLayout> layout2; // Layout 
    public Map<Integer,Pair<PiezaLayout,PiezaLayout> > perdidaInterna; // Perdidas internas
    public Pair<PiezaLayout,PiezaLayout> perdidaExterna2= new Pair(null,null); // Perdida externa     
    public int idL=1; // Id de las piezas que se van agregando al layout (Para poder referenciarlas mediante los mapas)       
    public SortedSet<Pieza> tiposPiezasUsados = new TreeSet<>(new ComparaPiezaId()); // Conjunto ordenado de piezas por id             

    public int ERS=0; // EmptyRectangularSpace, se usa para el fitness modificado (el area de la perdida mayor)
    public int MVR=999999; // MinimumValueRectangle, se usa para el fitness modificado (en este caso equivale al area de la pieza mas pequeña
    
    int bitSalto; // 
    int cantPiezasFenotipo; // Largo arreglo fenotipo

    int numPie; /* Número total de piezas */
    int cantidadtipospiezas; /* Variable que almacena la cantidad de tipos de piezas del problema */
    int altoPl, anchoPl; /* Dimensiones de la placa */
    int altoPatron=0, anchoPatron=0; // Limites del patron de corte
    int totalPie; /* Número total de piezas => Piezas Ganancias + Piezas Pérdidas */
    float peso_func_obj; /* Uso en funcion evaluación - Factor de la pérdida */
    float peso_uni; /* Uso en funcin evaluación - Factor unificación de pérdidas */
    float peso_perdida; /* Factor de la componente pérdida */
    float peso_distancia; /* Factor de la componente distancia */
    float peso_digregacion; /* Factor de la componente digregación */   
    int perdidaTotal;
    int gananciaTotal;        

    public CTDC2(String filename)
    {
        super();
        super.maxFitness = maxFitness;
        Target.maximize = true;
        readInst(filename);
    }
   
    public void readInst(String filename)
    {		        
        BufferedReader br = null;
        StringTokenizer st;                                

        //Inicialización de variables globales
        numPie = 0;
        cantidadtipospiezas = 0;        

        try
        {            
            br = new BufferedReader(new FileReader(filename)); 
            st = new StringTokenizer(br.readLine());

            anchoPl= (new Integer(st.nextToken())).intValue();
            altoPl= (new Integer(st.nextToken())).intValue();                                       
            
            st = new StringTokenizer(br.readLine());            
//            maxFitness = (float) (new Integer(st.nextToken())).intValue();
            super.maxFitness = (float) (new Integer(st.nextToken())).intValue();
            
            st = new StringTokenizer(br.readLine());            
            
            int num= (new Integer(st.nextToken())).intValue();  
                        
            //Se crea arreglo temporal para almacenar los tipos de piezas
            Pieza[] piezas_;                           
            piezas_ = new Pieza[num+1];
            
            int ancho;
            int alto;
            int cantidad;
            int id=1;                        
            
            for (int i = 0; i < num; i++) {                                 
                // Lee ancho, alto y restricciones para cada tipo de pieza
                st = new StringTokenizer(br.readLine());                                            
                ancho= (new Integer(st.nextToken())).intValue();            
                alto= (new Integer(st.nextToken())).intValue();                         
                cantidad= (new Integer(st.nextToken())).intValue();                                                         
                // Insertar pieza
                piezas_[i]= new Pieza(id,ancho,alto,cantidad);     
                if(piezas_[i].getArea()<MVR)
                    MVR=piezas_[i].getArea();
                // Insertar pieza rotada
                // piezas[num+i-1]= new Pieza(id+num,alto,ancho,cantidad);                    
                //Incremento id++ si quiero que sólo cada tipo pieza tenga id distinto
                id++;
                numPie = numPie + cantidad;
            }//End for                                                                                                                                                                           
            
            // Se fija el tamaño del arreglo de tipos de piezas (se duplica para incluir las piezas rotadas)
            piezas= new Pieza[numPie+1];               
            int cont=0;                        
                                   
            for (int i=0;i<piezas_.length-1;++i)  
            {                
                for(int j=0;j<piezas_[i].getCantidad();++j)
                {
                    piezas[cont]=new Pieza(piezas_[i].getId(),piezas_[i].getAncho(),piezas_[i].getAlto(),1);   
                    System.out.print(cont+" ");
                    piezas[cont].mostrar();
                    ++cont;
                }                                               
            }                                                                           
            //Establece la cantidad máxima de tipos de piezas distintos del problema
            id--;
            // (se duplica para incluir las piezas rotadas)
            cantidadtipospiezas = numPie;                        
            
            fenotipo=new ArrayList<>(numPie);                                                        
            perdidaInterna=new HashMap<>();
            layout2=new HashMap<>();                    
            
//            fenotipo= new PiezaFenotipo[cantidadtipospiezas];                                            
            
            if (numPie == 0) 
                System.out.println("ERROR: No se registraron piezas en el archivo de entrada");                                    
                        
            longitCrom = numPie; //Define el largo del cromosoma
            variables = longitCrom;            
            System.out.println("longit crom="+longitCrom);                        
//            maxFitness = (float) (altoPl * anchoPl);
//            fitness_inicial = (float) (AltoPl * AnchoPl); //Obtiene el fitness_inicial
            // Establece valor en variables utilizadas en función evaluación
            peso_func_obj = (float) 0.85; // Uso en función evaluación - Factor de la pérdida
            peso_uni = (float) 0.15; // Uso en función evaluación - Factor unificación de pérdidas
            peso_perdida = (float) 1.0; //0.6;	/*Factor de la componente perdida*/
            peso_distancia = (float) 0.2; //0.2;	/*Factor de la componente distancia*/
            peso_digregacion = (float) 1.0; //0.2;	/*Factor de la componente digregacion*/
            //Ordena las piezas y determina arreglo piezasproblema[]
//            app_ordena_piezas_problema_cp();            
        }
        catch (Exception e)
        {
            System.err.println("ERROR: "+e);
        }        
    }
    
    // Función que interpreta cromosoma y lo convierte en una secuencia ordenada de piezas
    public void funcionConstructora(CustomIndividual genotipo)
    {                                      
        fenotipo.clear();                                                                        
        int cont=0;                                                                      
        int largoGenotipo= genotipo.getLength();
        
//        System.out.println(genotipo.toString());
//        int[] genotipo2= {50,51,0,22,23,57,58,36,1,39,31,24,36,40,24,28,16,2,3,4,5,6,7,8,9,10,11,12,13,14,15,17,18,19,20,21,25,26,27,29,30,31,32,33,34,35,37,38,39,41,42,43,44,45,46,47,48,49,52,53};
        
        // Mientras exista alguna pieza en el string de piezas
        while(cont<largoGenotipo)                        
        {// Mientras no se haya completado un ciclo en la ruleta de piezas                                                   
            int pieza=(int)(genotipo.getAllele(cont));
            boolean combinacion=(boolean)genotipo.getAllele2(cont);
//            int pieza=genotipo2[cont];                  
            // Agregar la pieza al fenotipo
            int id=piezas[pieza].getId();
            int ancho=piezas[pieza].getAncho();
            int alto=piezas[pieza].getAlto();
            int cantidad=piezas[pieza].getCantidad();                            
            
            fenotipo.add(new Pieza(id,ancho,alto,cantidad,combinacion));                        
            cont++;                                                                       
        }
//        System.out.println(fenotipo.toString());
        // Fijar el largo del fenotipo
        cantPiezasFenotipo=cont;        
    }                               
    
    public void actualizarPerdidasInternas(CustomIndividual ci,PiezaLayout pieza, PiezaLayout perdida, int idPadre, int caso)
    {                  
        PiezaLayout nuevaPerdidaV=null;
        PiezaLayout nuevaPerdidaH=null;
        PiezaLayout perdidaV=null;
        PiezaLayout perdidaH=null;        
        
        switch(caso)
        {
            case 1:// Insercion en perdida interna: Se crea la perdida vertical y horizontal asociadas a la pieza
                int anchoPerdidaV=perdida.getAncho();
                int altoPerdidaV=perdida.getAlto()-pieza.getAlto();                                
                Point posicionPerdidaV=
                new Point((int)(perdida.getPosicion().getX()),(int)(perdida.getPosicion().getY()+pieza.getAlto()));                                                        
                int anchoPerdidaH=perdida.getAncho()-pieza.getAncho();
                int altoPerdidaH=perdida.getAlto();                                
                Point posicionPerdidaH=
                new Point((int)(perdida.getPosicion().getX()+pieza.getAncho()),(int)(perdida.getPosicion().getY()));                                
                if(altoPerdidaV>0)               
                    nuevaPerdidaV=new PiezaLayout(0,anchoPerdidaV,altoPerdidaV,1,posicionPerdidaV,0);                                
                if(anchoPerdidaH>0)                                                                                                     
                    nuevaPerdidaH=new PiezaLayout(0,anchoPerdidaH,altoPerdidaH,1,posicionPerdidaH,1);                                                                
                                
                int caso2=0;
                
                Iterator it5 = perdidaInterna.entrySet().iterator();                                                                        
                
                while(it5.hasNext()) 
                {                    
                    Map.Entry pairs = (Map.Entry)it5.next();                                                                                                                                   
                    Pair perdidas=(Pair)pairs.getValue();

                    perdidaV=(PiezaLayout)perdidas.getFirst();                                                                                
                    perdidaH=(PiezaLayout)perdidas.getSecond();                    
                    
                    // Comprobar que las nuevas perdidas no coincidan con perdidas existentes                                        
                    
                    int caso3=0;
                    
                    if(perdidaV != null)
                    {
                        caso3=caso3+1;                        
                    }
                    if(perdidaH != null)
                    {
                        caso3=caso3+2;                        
                    }
                    
                    switch(caso3)
                    {
                        case 0:
                            break;
                        case 1:
                            if(perdidaV.coincidePerdida2(nuevaPerdidaV))
                            {
                                caso2=caso2+1;
                                break;
                            }                                                                        
                            break;
                        case 2:
                            if(perdidaH.coincidePerdida2(nuevaPerdidaV))
                            {
                                caso2=caso2+1;
                                break;
                            }                            
                            break;
                        case 3:
                            if(perdidaV.coincidePerdida2(nuevaPerdidaV) || perdidaH.coincidePerdida2(nuevaPerdidaV))
                            {
                                caso2=caso2+1;
                                break;
                            }                            
                            break;                            
                    }                                        
                }                                                            
                
                it5 = perdidaInterna.entrySet().iterator();                                                                        
                
                while(it5.hasNext()) 
                {                    
                    Map.Entry pairs = (Map.Entry)it5.next();                                                                
                    Pair perdidas=(Pair)pairs.getValue();

                    perdidaV=(PiezaLayout)perdidas.getFirst();                                                                                
                    perdidaH=(PiezaLayout)perdidas.getSecond();                                        
                    
                    int caso3=0;
                    
                    if(perdidaV != null)
                        caso3=caso3+1;
                    if(perdidaH != null)
                        caso3=caso3+2;
                    
                    switch(caso3)
                    {
                        case 0:
                            break;
                        case 1:
                            if(perdidaV.coincidePerdida2(nuevaPerdidaH))
                            {
                                caso2=caso2+2;
                                break;
                            }                                                                        
                            break;
                        case 2:
                            if(perdidaH.coincidePerdida2(nuevaPerdidaH))
                            {
                                caso2=caso2+2;
                                break;
                            }                            
                            break;
                        case 3:
                            if(perdidaV.coincidePerdida2(nuevaPerdidaH) || perdidaH.coincidePerdida2(nuevaPerdidaH))
                            {
                                caso2=caso2+2;
                                break;
                            }                            
                            break;                            
                    }                                        
                }                                                    
                // Segun las coincidencias de perdidas se agregan las perdidas correspondientes
                switch(caso2)
                {
                    case 0:// Ninguna coincidencia
                        perdidaInterna.put(idL,new Pair(nuevaPerdidaV,nuevaPerdidaH));                        
                        break;
                    case 1:// Coincidencia nueva perdida vertical
                        perdidaInterna.put(idL,new Pair(null,nuevaPerdidaH));                        
                        break;                        
                    case 2:// Coincidencia nueva perdida horizontal
                        perdidaInterna.put(idL,new Pair(nuevaPerdidaV,null));                        
                        break;                                                
                    case 3:// Coincidencia ambas perdidas
                        perdidaInterna.put(idL,new Pair(null,null));                        
                        break;                                                                        
                }                                
                
                it5 = layout2.entrySet().iterator();                                                                        
                
                while(it5.hasNext()) 
                {                    
                    Map.Entry pairs = (Map.Entry)it5.next();                        
                    PiezaLayout piezaLayout=(PiezaLayout)pairs.getValue();
                    Integer idPieza=(Integer)pairs.getKey();                                                                                           
                    
                    if(idPieza!=idL && pieza.intersection(piezaLayout).getHeight()>0 && 
                       pieza.intersection(piezaLayout).getWidth()>0)                                
                    {
                        System.out.println("----------------------ME EKIVOKE-----------------------");
                        pieza.mostrar();
                        piezaLayout.mostrar();
                        perdida.mostrar();
                        layout2.get(idPadre).mostrar();
                        System.out.println("-------------------------------------------------------"); 
                        // Deshacer la insercion de la nueva pieza                        
                        return;
                        //exportarIndividuo(ci, "resultados/CU9/traza"+Math.random()+".txt");
                    }
                }                
                
                switch(perdida.getTipo())
                {
                    case 0: // Si la inserción se hizo en una perdida vertical (combinacion vertical)                      
                        // Borrar perdida vertical de la pieza padre                        
                        perdidaInterna.get(idPadre).setFirst(null);
                        // Actualizar perdidas del resto de las piezas si hay interseecion
                        
                        Iterator it2 = perdidaInterna.entrySet().iterator();                                                        
                            
                        // Buscar resto de las perdidas
                        while(it2.hasNext()) 
                        {                                                                            
                            Map.Entry pairs = (Map.Entry)it2.next();                        
                            Pair perdidas=(Pair)pairs.getValue();
                            Integer idPieza=(Integer)pairs.getKey();                                

                            perdidaV=(PiezaLayout)perdidas.getFirst();
                            perdidaH=(PiezaLayout)perdidas.getSecond();  
                                                                                    
                            if(perdidaH!=null && idPieza!=idL) // Verificar que no se aplique a la perdida actual
                            { // Si existe interseccion entre la perdida y la pieza insertada
                                if(pieza.intersection(perdidaH).getHeight()>0 &&
                                   pieza.intersection(perdidaH).getWidth()>0)                                
                                { 
                                    if(!perdidaInterna.get(idPieza).getSecond().reducirPerdida(pieza))                                                                                                            
                                        perdidaInterna.get(idPieza).setSecond(null);                                                                            
                                } // Si la reduccion hace 0 alguna de las dimensiones de la perdida, anularla
                            }                            
                            if(perdidaV!=null && idPieza!=idL) // Verificar que no se aplique a la perdida actual
                            { // Si existe interseccion entre la perdida y la pieza insertada                                
                                if(pieza.intersection(perdidaV).getHeight()>0 &&
                                   pieza.intersection(perdidaV).getWidth()>0)                                
                                { 
                                    if(!perdidaInterna.get(idPieza).getFirst().reducirPerdida(pieza))                                                                                                            
                                        perdidaInterna.get(idPieza).setFirst(null);                                                                          
                                } // Si la reduccion hace 0 alguna de las dimensiones de la perdida, anularla
                            }
                        }                                                        
                        break;
                    case 1: // Si la inserción se hizo en una perdida horizontal (combinacion horizontal)                     
                        // Borrar perdida horizontal de la pieza padre
                        perdidaInterna.get(idPadre).setSecond(null);                        
                        // Actualizar el resto de las perdidas si es que se sobrepasa dimension limite 
                        
                        Iterator it3 = perdidaInterna.entrySet().iterator();                                                        
                                             
                        // Buscar resto de las perdidas                        
                        while(it3.hasNext()) 
                        {                    
                            Map.Entry pairs = (Map.Entry)it3.next();                        
                            Pair perdidas=(Pair)pairs.getValue();
                            Integer idPieza=(Integer)pairs.getKey();                                

                            perdidaV=(PiezaLayout)perdidas.getFirst();
                            perdidaH=(PiezaLayout)perdidas.getSecond();                                                                                   

                            if(perdidaH!=null && idPieza!=idL) // Verificar que no se aplique a la perdida actual
                            { // Si existe interseccion entre la perdida y la pieza insertada
                                if(pieza.intersection(perdidaH).getWidth()>0 && 
                                   pieza.intersection(perdidaH).getHeight()>0)
                                { 
                                    if(!perdidaInterna.get(idPieza).getSecond().reducirPerdida(pieza))                                                                                                            
                                        perdidaInterna.get(idPieza).setSecond(null);                                                                                       
                                } // Si la reduccion hace 0 alguna de las dimensiones de la perdida, anularla
                            }
                            if(perdidaV!=null && idPieza!=idL) // Verificar que no se aplique a la perdida actual
                            { // Si existe interseccion entre la perdida y la pieza insertada                                
                                if(pieza.intersection(perdidaV).getWidth()>0 &&
                                   pieza.intersection(perdidaV).getHeight()>0)        
                                { 
                                    if(!perdidaInterna.get(idPieza).getFirst().reducirPerdida(pieza))                                                                                                            
                                        perdidaInterna.get(idPieza).setFirst(null);                                                                          
                                } // Si la reduccion hace 0 alguna de las dimensiones de la perdida, anularla
                            }
                        }                                                
                        break;                        
                }
                break;
            case 2: // Insercion en perdida externa
                switch(perdida.getTipo())                
                {
                    case 0:                        
                        if(pieza.getAncho()<=anchoPatron)
                        {// Si la pieza se inserto en la perdida externa vertical y es de menor o igual ancho que el patron
                            // Crear la perdida generada por la pieza
                            anchoPerdidaH=anchoPatron-pieza.getAncho();                                                                                    
                            altoPerdidaH=pieza.getAlto();
                            posicionPerdidaH= new Point(pieza.getAncho(),altoPatron);                                                                                            
                            
                            perdidaH=new PiezaLayout(0,anchoPerdidaH,altoPerdidaH,1,posicionPerdidaH,1);
                            
                            if(pieza.getAncho()<anchoPatron)
                                perdidaInterna.put(idL,new Pair(null,perdidaH));                                                                                                                                                                    
                            else // Si es de igual ancho que el patron no existen perdidas asociadas
                                perdidaInterna.put(idL,new Pair(null,null));                                
                        }                        
                        else
                        {// Si la pieza se inserto en la perdida externa vertical y es de mayor ancho que el patron                                                                                                                 
                            anchoPerdidaH=pieza.getAncho()-anchoPatron;                                                                                    
                            altoPerdidaH=altoPatron;                         
                            posicionPerdidaH= new Point(anchoPatron,0);                          
                            // Insertar perdida como perdida vertical de la pieza insertada
                            perdidaV=new PiezaLayout(0,anchoPerdidaH,altoPerdidaH,1,posicionPerdidaH,0);                            
                            perdidaInterna.put(idL,new Pair(perdidaV,null));                            
                            
                            // Buscar piezas en el extremo derecho del patron
                            
                            Iterator it1 = layout2.entrySet().iterator();                                                                                                                             

                            Iterator it2 = perdidaInterna.entrySet().iterator();                                                        
                            
                            // Buscar perdidas en el extremo derecho del patrón
                            while(it2.hasNext()) 
                            {                    
                                Map.Entry pairs = (Map.Entry)it2.next();                        
                                Pair perdidas=(Pair)pairs.getValue();
                                Integer idPieza=(Integer)pairs.getKey();                                

                                perdidaV=(PiezaLayout)perdidas.getFirst();
                                perdidaH=(PiezaLayout)perdidas.getSecond();
                                
                                if(perdidaH!=null)
                                {
                                    if(perdidaH.posicion.getX()+perdidaH.getAncho()==anchoPatron)                                                                                                    
                                        perdidaInterna.get(idPieza).getSecond().setAncho(perdidaH.getAncho()+anchoPerdidaH);                                
                                }
                                if(perdidaV!=null)
                                {                                
                                    if(perdidaV.posicion.getX()+perdidaV.getAncho()==anchoPatron)                                
                                        perdidaInterna.get(idPieza).getFirst().setAncho(perdidaV.getAncho()+anchoPerdidaH);                                                                                                                                            
                                }
                            }                            
                            perdidaInterna.put(idL,new Pair(null,null));                            
                        }
                        break;
                    case 1:
                        if(pieza.getAlto()<=altoPatron)
                        {// Si la pieza se inserto en la perdida externa horizontal y es de menor o igual alto que el patron
                            anchoPerdidaV=pieza.getAncho();                                                                                    
                            altoPerdidaV=altoPatron-pieza.getAlto();
                            posicionPerdidaV= new Point(anchoPatron,pieza.getAlto());                                                                                            
                            
                            perdidaV=new PiezaLayout(0,anchoPerdidaV,altoPerdidaV,1,posicionPerdidaV,0);
                            
                            if(pieza.getAlto()<altoPatron)
                                perdidaInterna.put(idL,new Pair(perdidaV,null));    
                            else // Si la pieza es de igual alto que el patron no existen perdidas asociadas
                                perdidaInterna.put(idL,new Pair(null,null));                                                                                            
                        }
                        else
                        {// Si la pieza se inserto en la perdida externa horizontal y es de mayor alto que el patron        
                            anchoPerdidaV=anchoPatron;                                                                                    
                            altoPerdidaV=pieza.getAlto()-altoPatron;                        
                            posicionPerdidaV= new Point(0,altoPatron);                      
                            // Insertar perdida como perdida horizontal de la pieza insertada
                            perdidaH=new PiezaLayout(0,anchoPerdidaV,altoPerdidaV,1,posicionPerdidaV,1);                            
                            perdidaInterna.put(idL,new Pair(null,perdidaH));                                                                                    

                            Iterator it2 = perdidaInterna.entrySet().iterator();                                                        
                            
                            // Buscar perdidas en el extremo inferior del patrón
                            while(it2.hasNext()) 
                            {                    
                                Map.Entry pairs = (Map.Entry)it2.next();                        
                                Pair perdidas=(Pair)pairs.getValue();
                                Integer idPieza=(Integer)pairs.getKey();                                

                                perdidaV=(PiezaLayout)perdidas.getFirst();
                                perdidaH=(PiezaLayout)perdidas.getSecond();
                                
                                if(perdidaH!=null)
                                {   
                                    if(perdidaH.posicion.getY()+perdidaH.getAlto()==altoPatron)                                                                                                    
                                        perdidaInterna.get(idPieza).getSecond().setAlto(perdidaH.getAlto()+altoPerdidaV);                                
                                }
                                if(perdidaV!=null)
                                {                                
                                    if(perdidaV.posicion.getY()+perdidaV.getAlto()==altoPatron)                                
                                        perdidaInterna.get(idPieza).getFirst().setAlto(perdidaV.getAlto()+altoPerdidaV);                                                                                                                                            
                                }
                            }                               
                            perdidaInterna.put(idL,new Pair(null,null));                            
                        }                        
                        break;
                }                
                break;                
        }      
    }
    
    public void actualizarPerdidasExternas()
    {                      
        // Perdida horizontal
        int anchoPerdida=anchoPl-anchoPatron;
        int altoPerdida=altoPl;
        Point posicionPerdida= new Point(anchoPatron,0);        
        perdidaExterna2.setSecond(new PiezaLayout(0,anchoPerdida,altoPerdida,1,posicionPerdida,1));        
        if(anchoPerdida==0)
            perdidaExterna2.setSecond(null);
        // Perdida vertical
        anchoPerdida=anchoPl;
        altoPerdida=altoPl-altoPatron;
        posicionPerdida= new Point(0,altoPatron);        
        perdidaExterna2.setFirst(new PiezaLayout(0,anchoPerdida,altoPerdida,1,posicionPerdida,0));                
        if(altoPerdida==0)
            perdidaExterna2.setFirst(null);        
    }    
    
    public boolean descontarPieza(ArrayList<Pieza> piezas,int id)
    {
        ListIterator it = piezas.listIterator();                                
        int flag=0;
        
        while(it.hasNext())
        {
            Pieza p=(Pieza)it.next();
            
            if(p.getId()==id)
            {
                p.setCantidad(p.getCantidad()-1);
                if(p.getCantidad()==0)
                    flag++;                
            }
        }
        if(flag>0)
            return true;
        else            
            return false;
    }
    
    public void insertarPieza(CustomIndividual ci, Pieza pieza, PiezaLayout perdida,Integer idPadre, int caso)
    {   // Crea la nueva pieza y la inserta en el layout                 
        int id=pieza.getId();
        int ancho=pieza.getAncho();
        int alto=pieza.getAlto();
        Point posicion=perdida.getPosicion();
        int tipo= perdida.getTipo();
        PiezaLayout piezaLayout=new PiezaLayout(id,ancho,alto,1,posicion,tipo);
        layout2.put(idL,piezaLayout);                        
                
        // Dependiendo del caso se reajustan los limites del patrón de corte y las dimensiones de las perdidas
        switch(caso)
        {
            case 1:// Placa vacia
                // Actualizar limites del patron de corte
                anchoPatron=pieza.getAncho();
                altoPatron=pieza.getAlto();                
                // Actualizar perdidas externas
                actualizarPerdidasExternas();                                                                     
                break;
            case 2:// Perdida interna                
                // Actualizar perdidas internas por insercion en perdida interna (caso 1)              
                actualizarPerdidasInternas(ci,piezaLayout,perdida,idPadre,1);                
                break;                
            case 3: // Perdida externa                                                                                                        
                // Actualizar perdidas internas por inserción en perdida externa (caso 2)               
                actualizarPerdidasInternas(ci,piezaLayout,perdida,idPadre,2);                                                  
                // Actualizar limites del patron de corte                                
                if(perdida.getPosicion().getX()+pieza.getAncho()>anchoPatron)                
                    anchoPatron=(int)(perdida.getPosicion().getX()+pieza.getAncho());                                    
                if(perdida.getPosicion().getY()+pieza.getAlto()>altoPatron)                                    
                    altoPatron=(int)(perdida.getPosicion().getY()+pieza.getAlto());                                                             
                // Actualizar perdidas externas
                actualizarPerdidasExternas();                                                                                                              
                break;                                
        }            
    }
    
    public boolean colocarPieza(CustomIndividual ci, Pieza pieza)
    {                
        // Si la placa está vacía colocar la pieza en el origen
        if(layout2.isEmpty())
        {   // Crear perdida equivalente al area de la placa (perdida inicial)
            PiezaLayout perdida= new PiezaLayout(0,anchoPl,altoPl,1,new Point(0,0),0);            
            // Verificar que la pieza quepa en la placa            
            if(pieza.fits(perdida))                       
            { // Insertar la pieza                
                insertarPieza(ci,pieza,perdida,0,1);
                perdidaInterna.put(idL, new Pair(null,null)); // 1a Pieza no genera perdidas internas
                idL++;                
                return true;
            }
            else
            {// Sino cabe, devolver falso
                return false;
            }
        }
        // Sino esta vacia, intentar insertar la pieza en alguna perdida interna (first fit o best fit)
        PiezaLayout perdida=null;
        PiezaLayout perdida2=null;
        int idPiezaLayout=pieza.firstFit(perdidaInterna);  
        
        if(idPiezaLayout>0) // Si es posible insertar en la perdida interna vertical asociada a la pieza
        {
            perdida=perdidaInterna.get(idPiezaLayout).getFirst();                 
        }                          
        if(idPiezaLayout<0) // Si es posible insertar en la perdida interna horizontal asociada a la pieza            
        {
            idPiezaLayout=-1*idPiezaLayout;
            perdida=perdidaInterna.get(idPiezaLayout).getSecond();
        }             
        if(idPiezaLayout!=0) // Si se encontro una perdida, insertar la pieza
        {
            insertarPieza(ci,pieza,perdida,idPiezaLayout,2);                        
            idL++;                                
            return true;
        }                
        // Intentar hacer un BL
        idPiezaLayout=pieza.BL(perdidaInterna, layout2, anchoPatron, altoPatron); 
                
        if(idPiezaLayout!=0) // Si se encontro un BL, insertar la pieza
        {
            // Crear perdida equivalente a la pieza            
            perdida=perdidaInterna.get(idPiezaLayout).getSecond();
            insertarPieza(ci,pieza,perdida,idPiezaLayout,2);                        
            idL++;                                
            return true;
        }
        ///////////////////////
        // Sino se pudo insertar en una perdida interna, intentar insertarla en una perdida externa (first fit o best fit)                       
        if(!pieza.getCombinacion())
        { // Precedencia V->H
            perdida=perdidaExterna2.getFirst();
            perdida2=perdidaExterna2.getSecond();
        }
        else
        { // Precedencia H->V
            perdida2=perdidaExterna2.getFirst();
            perdida=perdidaExterna2.getSecond();
        }
        //        // Sino se pudo insertar en una perdida interna, intentar insertarla en una perdida externa (first fit o best fit)                       
//        idPiezaLayout=pieza.bestFitExt(perdidaExterna2);  
        
        if(pieza.fits(perdida)) // Si se encontro una perdida, insertar la pieza
        {
            insertarPieza(ci,pieza,perdida,0,3);                        
            idL++;                                
            return true;
        }
        if(pieza.fits(perdida2)) // Si se encontro una perdida, insertar la pieza
        {
            insertarPieza(ci,pieza,perdida2,0,3);                        
            idL++;                                
            return true;
        }                    
        // Sino se pudo insertar en una perdida externa, retornar falso
        return false;
    }
    
    public void heuristicaColocacion(CustomIndividual ci, ArrayList<Pieza> fenotipo, boolean verbose)
    {        
        ArrayList<Pieza> fenotipo_temp= (ArrayList<Pieza>)fenotipo.clone();                
        int cont=0;
        
        // Mientras hayan piezas por colocar                
        while(!fenotipo_temp.isEmpty()) 
        {          
            int max=fenotipo_temp.size();
                        
            if(cont>=max) // Si se llego al final de la secuencia de piezas volver al inicio
                cont=0;
                                                                                 
            Pieza pieza = fenotipo_temp.get(cont); // Seleccionar la pieza                                                                                   
//            System.out.println("Tam fenotipo:"+fenotipo_temp.size());            
//            System.out.println("Siguiente Pieza:");
//            pieza.mostrar();                        
            
            if(!colocarPieza(ci,pieza)) // Si no se pudo colocar la pieza en el layout, descartarla             
            {
                fenotipo_temp.remove(cont);            
            }
            else // Si se pudo colocar, decrementar las veces que puede repetirse
            {
                tiposPiezasUsados.add(pieza); // Agregar la pieza al conjunto ordenado por id
                descontarPieza(fenotipo,pieza.getId());
                if(descontarPieza(fenotipo_temp,pieza.getId())) // Si la cantidad de la pieza llega a 0, descartarla
                    fenotipo_temp.remove(cont);
            }            
            cont++;
            
            if(verbose)
                mostrarLayout();
        } 
        idL=1;
    }
    
    public void heuristicaColocacion2(CustomIndividual ci, ArrayList<Pieza> fenotipo, boolean verbose)
    {        
        ArrayList<Pieza> fenotipo_temp= (ArrayList<Pieza>)fenotipo.clone();                
        int cont=0;
        int max=fenotipo_temp.size();         
        
        // Mientras hayan piezas por colocar                
        while(cont<max) 
        {                                                              
            Pieza pieza = fenotipo_temp.get(cont); // Seleccionar la pieza            
            
            colocarPieza(ci, pieza);
            
            cont++;
            
            if(verbose)
                mostrarLayout();
        } 
        // Aqui se hace una post-optimizacion, intentando colocar piezas que no fueron consideradas en el fenotipo, en las pérdidas remanentes        
//        cont=0;
//        max=piezas.length;                
//        
//        while(cont<max-1)
//        {
//            Pieza p=piezas[cont];
//            // Si la pieza actual no esta en el fenotipo intentar insertarla
//            if(!p.existePieza(fenotipo))   
//            {
//                colocarPieza(p);                
//            }
//            cont++;
//        }         
        idL=1;
    }    
    
    public void vaciarLayout()
    {
        layout2.clear();
        perdidaInterna.clear();
        tiposPiezasUsados.clear();
        perdidaExterna2.setFirst(null);
        perdidaExterna2.setSecond(null);              
        ERS=0;
    }        
    
    public double evaluarLayout(boolean verbose)
    {        
        gananciaTotal=0;
        perdidaTotal=0;
        int cantPiezasUsadas=layout2.size();        
        int cont=1;        
        int cantidadPerdidas=0;        
        int perdidasInt=0;
        int perdidasExt=0;
        
        while(cont<=cantPiezasUsadas)        
        {             
                
            PiezaLayout perdidaV=perdidaInterna.get(cont).getFirst();
            PiezaLayout perdidaH=perdidaInterna.get(cont).getSecond(); 
            gananciaTotal=gananciaTotal+layout2.get(cont).getArea();
            
            if(verbose)            
                layout2.get(cont).mostrar();                           
                            
            if(perdidaV!=null)          
            {
                perdidaTotal=perdidaTotal+perdidaV.getArea();
                perdidasInt=perdidasInt+perdidaV.getArea();
                cantidadPerdidas++;
                if(verbose)
                    perdidaV.mostrar();
                if(perdidaV.getArea()>ERS)
                    ERS=perdidaV.getArea();            
            }
            if(perdidaH!=null)     
            {
                perdidaTotal=perdidaTotal+perdidaH.getArea();            
                perdidasInt=perdidasInt+perdidaH.getArea();
                cantidadPerdidas++;                
                if(verbose)
                    perdidaH.mostrar();                
                if(perdidaH.getArea()>ERS)
                    ERS=perdidaH.getArea();
            }
            // Descontar intersecciones entre perdidas       
            if(perdidaV!=null && perdidaH!=null)
            {
                int anchoInterseccionPerdida=(int)perdidaInterna.get(cont).getFirst().intersection(perdidaInterna.get(cont).getSecond()).getWidth();
                int altoInterseccionPerdida=(int)perdidaInterna.get(cont).getFirst().intersection(perdidaInterna.get(cont).getSecond()).getHeight();            

                if(anchoInterseccionPerdida>0 && altoInterseccionPerdida>0)            
                    perdidaTotal=perdidaTotal-anchoInterseccionPerdida*altoInterseccionPerdida;                        
            }
            cont++;
        }                                
        PiezaLayout perdidaV=perdidaExterna2.getFirst();
        PiezaLayout perdidaH=perdidaExterna2.getSecond();         
        
        if(perdidaV!=null)            
        {
            perdidaTotal=perdidaTotal+perdidaV.getArea();
            perdidasExt=perdidasExt+perdidaV.getArea();
            cantidadPerdidas++;
            if(verbose)
                perdidaV.mostrar();            
            if(perdidaV.getArea()>ERS)
                ERS=perdidaV.getArea();            
        }
        if(perdidaH!=null)     
        {               
            perdidaTotal=perdidaTotal+perdidaH.getArea();          
            perdidasExt=perdidasExt+perdidaH.getArea();
            cantidadPerdidas++;                     
            if(verbose)
                perdidaH.mostrar();            
            if(perdidaH.getArea()>ERS)
                ERS=perdidaH.getArea();            
        }
        
        //UnFitness : Componente_Perdida + Componente_Distancia + Componente_Digregacion
        //Componente_Perdida     = Peso_Perdida * (Perdida_Total / Area_Placa)
        //Componente_Distancia   = Peso_Distancia * (SUMA_en_i(Restriccion_pieza_i - Numero_Pieza_i_Presentes) / SUMA_en_i(Restriccion_pieza_i))
        //Componente_Digregacion = Peso_Digregacion * (1 - EXP(- Numero_Perdidas / TAU)) donde TAU = K * Cantidad_de_Piezas_Distintas => La idea es tener las piezas de un mismo tipo, juntas
        //El sistema podra discriminar la digregacion dentro de 2 TAU, K: Hay que determinarlo experimentalmente.

        float areaPlaca=anchoPl*altoPl;
//        float componentePerdida=peso_perdida*(perdidaTotal/areaPlaca);
        
        if(cantPiezasUsadas==0)
            perdidaTotal=(int)areaPlaca;               
        
        ListIterator it= fenotipo.listIterator();

//        return gananciaTotal+0.03*MVR*ERS/areaPlaca;
        return gananciaTotal;
    }
    
@Override
    public void exportarIndividuo(Individual ind, String fileId) {
        CustomIndividual pi = (CustomIndividual)ind;                      
        funcionConstructora(pi);                
        heuristicaColocacion2(pi, fenotipo,false);      
        evaluarLayout(false);        
        ListIterator it = fenotipo.listIterator();  
        
        while(it.hasNext())
        {
            Pieza p=(Pieza)it.next();
            System.out.print(p.getId()+" ");            
        }
        System.out.print("\n");
        System.out.println(pi.toString());
        ExportarLayout eL= new ExportarLayout(anchoPl,altoPl,layout2,perdidaInterna,perdidaExterna2,fileId);        
        try {
            eL.exportar();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CTDC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CTDC.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }     

    public void mostrarLayout()
    {
        Iterator it2 = layout2.entrySet().iterator();        
        System.out.println("LAYOUT");
        while(it2.hasNext())
        {                
            Map.Entry pairs = (Map.Entry)it2.next();                                                        

            Integer idPieza=(Integer)pairs.getKey();                
            PiezaLayout piezaLayout=(PiezaLayout)pairs.getValue();                                

            System.out.print("PiezaLayout["+idPieza+"]=");                                                            
            piezaLayout.mostrar();                                
            System.out.println(">PerdidaV:");                
            if(perdidaInterna.get(idPieza).getFirst()!=null)
                perdidaInterna.get(idPieza).getFirst().mostrar();                                                            
            System.out.println(">PerdidaH:");            
            if(perdidaInterna.get(idPieza).getSecond()!=null)
                perdidaInterna.get(idPieza).getSecond().mostrar();
        }
        System.out.println("PerdidaExtV:");            
        if(perdidaExterna2.getFirst()!=null)            
            perdidaExterna2.getFirst().mostrar();         
        System.out.println("PerdidaExtH:");            
        if(perdidaExterna2.getSecond()!=null)                        
            perdidaExterna2.getSecond().mostrar();                          
    }

    public Object eval(Individual ind)
    {                
//        System.out.println("Cromosoma="+ind.toString());
        CustomIndividual pi = (CustomIndividual)ind;                
//        int fid=(int)(bi.binaryToDecimal(1,20)%100);        
//        System.out.println("Id Layout="+fid);        
        funcionConstructora(pi);        
//        System.out.println("largo fenotipo="+cantPiezasFenotipo);
//        for(int i=0;i<cantPiezasFenotipo;++i)
//        {
//            fenotipo.get(i).mostrar();
//        }                       
        heuristicaColocacion2(pi,fenotipo,false);                        
//        
        // F1 maximization
        double fit = 0.0;        
        // F2 minimization        
        fit=evaluarLayout(false);
        vaciarLayout();                        

        return new Double(fit);
    }   
}
