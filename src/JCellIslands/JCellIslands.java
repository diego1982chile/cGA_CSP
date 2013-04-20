/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package JCellIslands;

import HcGA.Hierarchy;
import HcGA.Swap;
import adaptiveCGA.AF;
import adaptiveCGA.AdaptivePop;
import genEA.GenGA;
import gui.CGADisplay;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Random;
import java.util.StringTokenizer;
import jcell.*;
import jcell.neighborhoods.Linear5;
import operators.mutation.BinaryMutation;
import operators.recombination.Dpx;
import operators.replacement.ReplaceIfBetter;
import operators.replacement.ReplaceIfNonWorse;
import operators.selection.TournamentSelection;
import problems.Combinatorial.CSP;
import ssEA.SSGA;

/**
 *
 * @author Administrator
 */
public class JCellIslands implements GenerationListener {

    /**
     * @param args the command line arguments
     */
    static int islands = 5;
    static int islandSize = 80;
    static int longitCrom;
    static int numberOfFuncts;
    // Default maximum number of function evaluations
    static int evaluationsLimit = 10000;
    //private static boolean showDisplay = false;
    static boolean verbose = true;
    static Integer displaySteps = 1;            
    
    protected static boolean showDisplay = false;   
    protected double lastRatio = -1.0;    
    DecimalFormat df = new DecimalFormat("0.000000000000000000");      

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // TODO code application logic here        
//        Random r = new Random(); // seed for the random number generator        
        Random r = new Random(3816L); // seed for the random number generator        

        long start, end; // starting and ending time

        JCellIslands sel = new JCellIslands();
        
        int idExperimento=3; 
        
//        Identificador del experimento: 
//        0: AG-Generacional
//        1: AG-SteadyState
//        2: AG-Distribuido
//        3: AG-Celular                
        
        // TIPO DE ALGORITMO EVOLUTIVO
        
        EvolutionaryAlg ea= new DistributedGA(r); ;        
        Population pop= new PopIsland(islands, islandSize);   ;                                                            
        
         // Lee archivo en infp con parámetros (todos ya chequeados)                        
        BufferedReader br = null;
        StringTokenizer st;                                            
        
        // Input PARAMILS                
        String instanceName="";     
        int cutOffTime=0;
        int cutOffLength=0;
        Random seed=new Random(3816L);
        String params="";
        
        // Input parametros AG
        // Parametros AG generacional/Steady-State
        int popSize=100; // Tamaño poblacion       
        Double mutac= new Double(1.0); // Probabilidad de mutacion
        Double cross= new Double(1.0); // Probabilidad de cruzamiento
        
        // Parametros AG distribuido
        int islands=0; // Cantidad de islas
        int islandSize=0; // Tamaño de islas
        Integer migrationFreq=1000; // Frecuencia de migracion
        
        // Parametros AG Celular
        int dX=20; // Ancho de grilla
        int dY=20; // Alto de grilla
        String neigh="";  //Tipo de vecindad
        String updatePolicy=""; // Política de actualiacion   
        Neighborhood neighborhood = new Linear5(dX,dY); //              
        
        // Operadores geneticos
        String mutacOp="";
        String crossOp="";
        String selecOp="";
        
        // Parametros AG Celular adicionales (no considerados)
        Class c;
        CellUpdate cu = null;        
        boolean synchronousUpdate=true;
        Boolean alleleMutation = false;	
        AdaptivePop adaptivePolicy = null;        
        Hierarchy hierarchy = null;        
                
        // Capturar parametros de PARAMILS
        instanceName= args[0].toString(); // Capturar nombre del archivo con la instancia        
        cutOffTime= Integer.parseInt(args[1]); // Capturar cutoff time
        cutOffLength= Integer.parseInt(args[2]); // Capturar cutoff length (Ej: Limite de evaluaciones)
        seed= new Random(Long.parseLong(args[3])); // Capturar semilla
        params= args[4].toString(); // Capturar nomre de archivo con parametros                        
        
        // Capturar parametros comunes a todos los AG
        try
        {
            br = new BufferedReader(new FileReader(params));                                         
            st = new StringTokenizer(br.readLine());            
            
            popSize= (new Integer(st.nextToken())).intValue();            
            mutac= (new Double(st.nextToken())).doubleValue();                        
            cross= (new Double(st.nextToken())).doubleValue();                                                                                                   
            System.out.println("Pop size="+popSize+" mutac="+mutac+" cross="+cross);                    
        }
        catch (Exception e)
        {
            System.err.println("ERROR: "+e);
        }
                
        switch(idExperimento)
        {
            case 0:
            case 1:
                if(idExperimento==0)                                  
                    ea = new GenGA(seed);                                        
                else                
                    ea = new SSGA(seed);                                  
                for (int i = 0; i < args.length; i++) 
                {                    
                    if (args[i].compareTo("-po") == 0) popSize = Integer.parseInt(args[++i]);
                    if (args[i].compareTo("-el") == 0) evaluationsLimit = Integer.parseInt(args[++i]);
                    if (args[i].compareTo("-m") == 0) mutac = Double.parseDouble(args[++i]);
                    if (args[i].compareTo("-c") == 0) cross = Double.parseDouble(args[++i]);
                    if (args[i].compareTo("-f") == 0) instanceName= args[++i].toString();
                }                
                pop = new Population(popSize);                                     
                break;
            case 2:
                ea = new DistributedGA(seed);       
             
                for (int i = 0; i < args.length; i++) 
                {                    
                    if (args[i].compareTo("-i") == 0) islands = Integer.parseInt(args[++i]);                    
                    if (args[i].compareTo("-ti") == 0) islandSize = Integer.parseInt(args[++i]);
                    if (args[i].compareTo("-mf") == 0) migrationFreq = Integer.parseInt(args[++i]);                    
                    if (args[i].compareTo("-el") == 0) evaluationsLimit = Integer.parseInt(args[++i]);
                    if (args[i].compareTo("-m") == 0) mutac = Double.parseDouble(args[++i]);
                    if (args[i].compareTo("-c") == 0) cross = Double.parseDouble(args[++i]);
                    if (args[i].compareTo("-f") == 0) instanceName= args[++i].toString();
                }                
                pop = new PopIsland(islands, islandSize);   
                break;
            case 3:
                ea = new CellularGA(seed);                
                               
                for (int i = 0; i < args.length; i++) 
                {                    
                    if (args[i].compareTo("-x") == 0) dX = Integer.parseInt(args[++i]);                    
                    if (args[i].compareTo("-y") == 0) dY = Integer.parseInt(args[++i]);                                        
                    if (args[i].compareTo("-nh") == 0) neigh = args[++i].toString();                    
                    if (args[i].compareTo("-up") == 0) updatePolicy = args[++i].toString();     
                    if (args[i].compareTo("-el") == 0) evaluationsLimit = Integer.parseInt(args[++i]);                    
                    if (args[i].compareTo("-m") == 0) mutac = Double.parseDouble(args[++i]);
                    if (args[i].compareTo("-c") == 0) cross = Double.parseDouble(args[++i]);
                    if (args[i].compareTo("-f") == 0) instanceName= args[++i].toString();
                }                
                pop = new PopGrid(dX, dY);                
                neighborhood = new Linear5(dX,dY);                                       
			
                if ((neigh!=null) && (!neigh.toLowerCase().contains("linear5")))
                {
                        c = Class.forName(neigh);
                        neighborhood = (Neighborhood) c.newInstance(); // Constructor called
                }                         		
                if (!updatePolicy.contains("synchronous"))
                {
                    synchronousUpdate = false;                
                
                    if (updatePolicy.contains("ls"))
                        cu = new LineSweep((PopGrid)pop);				
                    else if (updatePolicy.contains("frs"))
                        cu = new FixedRandomSweep(r,(PopGrid)pop);
                    else if (updatePolicy.contains("nrs"))
                        cu = new NewRandomSweep(r,(PopGrid)pop);
                    else if (updatePolicy.contains("uc"))
                        cu = new UniformChoice(r,(PopGrid)pop);
                    else if (updatePolicy.contains("ss"))
                        cu = new SpiralSweep((PopGrid)pop);			                        
                    break;
                }                
        }                                                                                 
        
        //Problem prob = new MMDP();
//        Problem prob = new MTTP();
        Problem prob = new CSP(instanceName);                
        ea.setParam(CellularGA.PARAM_PROBLEM, prob);
        longitCrom = prob.numberOfVariables();
        numberOfFuncts = prob.numberOfObjectives();        
                
        Individual ind = new BinaryIndividual(longitCrom);
                
        ind.setMinMaxAlleleValue(true, prob.getMinAllowedValues());
        ind.setMinMaxAlleleValue(false, prob.getMaxAllowedValues());
        ind.setLength(prob.numberOfVariables());
        ind.setNumberOfFuncts(prob.numberOfObjectives());
        ind.setRandomValues(seed);        

        pop.setRandomPop(seed, ind);        

//        mutac = new Double(1.0); // probability of individual mutation
//        cross = new Double(1.0); // crossover probability

        // Set parameters of CEA
        ea.setParam(CellularGA.PARAM_EVALUATION_LIMIT, new Integer(cutOffLength));        
        ea.setParam(CellularGA.PARAM_SYNCHR_UPDATE, new Boolean(synchronousUpdate));        
        ea.setParam(EvolutionaryAlg.PARAM_MIGRATION_FREQUENCY, new Integer(10000));
        ea.setParam(CellularGA.PARAM_POPULATION, pop);
        ea.setParam(CellularGA.PARAM_STATISTIC, new ComplexStats());
        ea.setParam(CellularGA.PARAM_LISTENER, sel);
        ea.setParam(CellularGA.PARAM_MUTATION_PROB, mutac);
        ea.setParam(CellularGA.PARAM_CROSSOVER_PROB, cross);                        
        ea.setParam(CellularGA.PARAM_VERBOSE, verbose);
        ea.setParam(CellularGA.PARAM_NEIGHBOURHOOD, neighborhood);                        
        ea.setParam(CellularGA.PARAM_CELL_UPDATE, cu);        
        ea.setParam(CellularGA.PARAM_PROBLEM, prob);
        ea.setParam(CellularGA.PARAM_DISPLAY_STEPS, displaySteps);        
        if (prob.numberOfVariables() > 1)                    
            ea.setParam(CellularGA.PARAM_ALLELE_MUTATION_PROB, new Double(1.0/(double)prob.numberOfVariables())); // probability of allele mutation        
        else        
            ea.setParam(CellularGA.PARAM_ALLELE_MUTATION_PROB, new Double(0.5)); // probability of allele mutation        
        
        ea.setParam(CellularGA.PARAM_TARGET_FITNESS, (Double) new Double(prob.getMaxFitness()));        
        // For the hierarchical algorithm
        ea.setParam(CellularGA.PARAM_ASYNC_SWAP, new Boolean("true"));
        ea.setParam(CellularGA.PARAM_SWAP_FREQ, new Integer("1"));
        ea.setParam(CellularGA.PARAM_SWAP_PROB, new Double("1.0"));
        ea.setParam(CellularGA.PARAM_SWAP_IF_STATIC, new Boolean("false"));
        ea.setParam(CellularGA.PARAM_MOVES_SWAP, new Integer("10"));        
        ea.setParam(CellularGA.PARAM_ANISOTROPIC_ADAPTATION, null);        
        ea.setParam(CellularGA.PARAM_HIERARCHY, hierarchy);        
        ea.setParam(CellularGA.PARAM_POP_ADAPTATION, adaptivePolicy);        
        ea.setParam(EvolutionaryAlg.PARAM_LOCAL_SEARCH_STEPS, 100);        
        ea.setParam(EvolutionaryAlg.PARAM_LOCAL_SEARCH_PROB, 0.0);                
        
        ea.setParam("selection1", new TournamentSelection(r)); // selection of one parent
        ea.setParam("selection2", new TournamentSelection(r)); // selection of one parent
        ea.setParam("crossover", new Dpx(r));
        ea.setParam("mutation", new BinaryMutation(r, ea));
//        ea.setParam("replacement", new ReplaceIfBetter());
        ea.setParam("replacement", new ReplaceIfNonWorse());                        
	ea.setParam("swap", new Swap());                	               
        
        start = (new Date()).getTime();
        // generation cycles        
        ea.experiment();
        end = (new Date()).getTime();

        Double best = (Double) ((Statistic) ea.getParam(CellularGA.PARAM_STATISTIC)).getStat(0);
        int evals = ((Problem) (ea.getParam(CellularGA.PARAM_PROBLEM))).getNEvals();
        // Writes: best found solution, number of generations, elapsed time (mseconds)
        System.out.println("Solution: Best  Generations  Evaluations  Time (ms)  Problem");
        System.out.println(best + " " + (Integer) ea.getParam(CellularGA.PARAM_GENERATION_NUMBER) + " " + evals + " " + (end - start) + " "
                + ((Problem) ea.getParam(CellularGA.PARAM_PROBLEM)));
    }

    private static void writeLine(String line) {
        if (verbose) {
            System.out.println(line);
        }
    }

//    @Override
//    public void generation(EvolutionaryAlg ea) {
//        //CellularGA cea = (CellularGA) ea;
//        verbose = ((Boolean) ea.getParam(CellularGA.PARAM_VERBOSE)).booleanValue();
//
//        //writeLine("Generation: " + (Integer) ea.getParam(CellularGA.PARAM_GENERATION_NUMBER));
//
//        // Migration occurs here:
//        int evals = ((Problem) (ea.getParam(CellularGA.PARAM_PROBLEM))).getNEvals();
//
//        PopIsland pop = (PopIsland) (ea.getParam(CellularGA.PARAM_POPULATION));
//
//        //if (evals >= ea.getParam(ea.PARAM_MIGRATION_FREQUENCY))
//        Individual ind0, ind1;
//        int j;
//        int migrFreq = ((Integer) ea.getParam(EvolutionaryAlg.PARAM_MIGRATION_FREQUENCY)).intValue();
//        if (((double) evals / migrFreq >= 1) && (evals % (migrFreq * islands) == 0)) {
//            System.out.println("Evaluaciones: " + evals);
//            for (int i = 0; i < islands; i++) {
//                ind0 = pop.getIndividual(i, ((DistributedGA) ea).bestInds[i]);
//                if (i == islands - 1) {
//                    j = 0;
//                } else {
//                    j = i + 1;
//                }
//
//                ind1 = pop.getIndividual(j, ((DistributedGA) ea).worstInds[j]);
//                if (Target.isBetter(ind0, ind1)) {
//                    pop.setIndividual(j, ((DistributedGA) ea).worstInds[j], ind0);
//
//                    if (Target.isBetter(ind0, pop.getIndividual(j, ((DistributedGA) ea).bestInds[j]))) {
//                        ((DistributedGA) ea).bestInds[j] = ((DistributedGA) ea).worstInds[j];
//                    }
//
//                    for (int k = 0; k < islandSize; k++) {
//                        if (Target.isWorse(pop.getIndividual(j, k), pop.getIndividual(j, ((DistributedGA) ea).worstInds[j]))) {
//                            ((DistributedGA) ea).worstInds[j] = k;
//                        }
//                    }
//                }
//            }
//        }
//    }
    
    @Override
    public void generation(EvolutionaryAlg ea)
    {   
    	//CellularGA cea = (CellularGA) ea;
    	verbose = ((Boolean) ea.getParam(CellularGA.PARAM_VERBOSE)).booleanValue();                

    	if ((!ea.getParam(EvolutionaryAlg.PARAM_POPULATION).getClass().getName().equalsIgnoreCase("distributedGA")) &&
    			(((Population)ea.getParam(EvolutionaryAlg.PARAM_POPULATION)).getPopSize() != 1))
    	{
			// Get the best Individual
			int pos = ((Integer)((Statistic)ea.getParam(EvolutionaryAlg.PARAM_STATISTIC)).getStat(SimpleStats.MAX_FIT_POS)).intValue();
			Individual bestInd = ((Population) ea.getParam(EvolutionaryAlg.PARAM_POPULATION)).getIndividual(pos);
			Problem prob = (Problem)ea.getParam(EvolutionaryAlg.PARAM_PROBLEM);                        
			if (prob.numberOfObjectives() == 1)
                        {                                
				writeLine("Generation: "+(Integer)ea.getParam(CellularGA.PARAM_GENERATION_NUMBER)+"; Best individual: "+df.format(((Double)bestInd.getFitness()).doubleValue()));                                
                        }
			else
                        {
				writeLine("Generation: " + (Integer) ea.getParam(CellularGA.PARAM_GENERATION_NUMBER));
                        }
    	}
/*		Population population = (Population) cea.getParam(CellularGA.PARAM_POPULATION);
		for (int i=0; i<25; i++)
			writeLine(((Double[])population.getIndividual(i).getFitness())[0].doubleValue() + ", " + ((Double[])population.getIndividual(i).getFitness())[1].doubleValue());
		writeLine();*/
		
		// La realimentaci�n aqu�
		
		int displaySteps = ((Integer)ea.getParam(CellularGA.PARAM_DISPLAY_STEPS)).intValue();
		if ((showDisplay)&& (((Integer)ea.getParam(CellularGA.PARAM_GENERATION_NUMBER)).intValue()%displaySteps==0)) {
			  CGADisplay display = (CGADisplay) ea.getParam(CellularGA.PARAM_DISPLAY);
			  CGADisplay display2 = (CGADisplay) ea.getParam(CellularGA.PARAM_DISPLAY2);
			  if (display != null)
				  display.step();
			  if (display2 != null)
				  display2.step();
		   }
		   
		   /*xfig SNAPSHOT
		   if (((Integer)cea.getParam(CellularGA.PARAM_GENERATION_NUMBER)).intValue()%displaySteps==0) {
			   PopGrid tmppop = (PopGrid) cea.getPopulation();
			    for (int i = 0; i < x*y; i++) {
					Individual tmpind = tmppop.getIndividual(i);
					int fitVal = (int) (256*((tmpind.getFitness()-(maxFitness/4))/(3*maxFitness/4)));
					if (fitVal < 0) fitVal=0;
					String hex = Integer.toHexString(fitVal);
					if (fitVal < 16) hex = "0"+hex;
					System.out.println( "0 " + (i+32) + " #" + hex + hex + hex + " " + tmpind.getFitness());
				    }	  		   
		   }
		   */
		
		// For the adaptive population case
		int n = ((Integer)ea.getParam(CellularGA.PARAM_GENERATION_NUMBER)).intValue();
		//Problem problem = (Problem) ea.getParam(CellularGA.PARAM_PROBLEM);
		//Population pop = (Population) ea.getParam(CellularGA.PARAM_POPULATION);
		
		// Adaptive population
		if (((ea.getParam(CellularGA.PARAM_POP_ADAPTATION))!=null) && 
		   	 // each delta steps
		   	 ((n%AdaptivePop.delta) == 0))
		{
			double ratio = 0.0;
		   	// change the pop shape (if needed)
		   	if ((ratio = ((AdaptivePop)ea.getParam(CellularGA.PARAM_POP_ADAPTATION)).evalChange()) != -1.0)
		   	{
		   		if (ratio != lastRatio)
		   		{
		   			lastRatio = ratio;
		   			if (showDisplay) 
		   			{
		  			  CGADisplay display = (CGADisplay) ea.getParam(CellularGA.PARAM_DISPLAY);
		  			  CGADisplay display2 = (CGADisplay) ea.getParam(CellularGA.PARAM_DISPLAY2);
		  			  if (display != null)
		  			  {
		  				//display.resize(((PopGrid)pop).getDimX(), ((PopGrid)pop).getDimY());
		  			  	display.resize();
		  			    
		  			  display.step();
		  			  	//display = new CGADisplay(cea, problem.getMaxFitness(),CGADisplay.NO_TEXT + CGADisplay.DISPLAY_VALUE);
		  				//cea.setParam(CellularGA.PARAM_DISPLAY, display);
		  			  }
		  			  if (display2 != null)
		  			  {
		  			  	display2.resize();
		  			    
		  			  display2.step();
		  			  	//display2.resize(((PopGrid)pop).getDimX(), ((PopGrid)pop).getDimY());
		  			  	//display2 = new CGADisplay(cea, problem.numberOfVariables(),CGADisplay.NO_TEXT + CGADisplay.DISPLAY_BESTDISTANCE);
		  				//cea.setParam(CellularGA.PARAM_DISPLAY, display2);
		  			  }
		  		   }
		   			writeLine("New ratio: " + ratio);
		   		}
		   	}
		}
	    // For the adaptive population case
		
		// Adaptive anisotropic selection
		//if (((ea.getParam(CellularGA.PARAM_POP_ADAPTATION))!=null) && 
		//   	 // each delta steps
		//   	 ((n%AdaptivePop.delta) == 0))
		double alpha;
		
		// For the island distributed population
		// Migration occurs here:
		if (ea.getParam(EvolutionaryAlg.PARAM_POPULATION).getClass().getName().equalsIgnoreCase("distributedGA"))
		{
			int evals = ((Problem) (ea.getParam(CellularGA.PARAM_PROBLEM))).getNEvals();
			
			PopIsland pop = (PopIsland)(ea.getParam(CellularGA.PARAM_POPULATION));
			
			//if (evals >= ea.getParam(ea.PARAM_MIGRATION_FREQUENCY))
			Individual ind0, ind1;
			int j;
			int freq = ((Integer)ea.getParam(EvolutionaryAlg.PARAM_MIGRATION_FREQUENCY)).intValue();
			int islands = ((DistributedGA)ea).getPopulation().getNumberIslands();
			int islandSize = ((DistributedGA)ea).getPopulation().getSizeIslands();
			
			if (((double)evals/(double)freq >= 1) && (evals%(freq*islands)==0))
			{
				writeLine("Evaluaciones: " + evals);
				for (int i=0; i<islands; i++)
				{
					ind0 = pop.getIndividual(i,((DistributedGA)ea).bestInds[i]);
					if (i==islands-1)
						j = 0;
					else
						j = i+1;
					
					ind1 = pop.getIndividual(j,((DistributedGA)ea).worstInds[j]);
					if (Target.isBetter(ind0, ind1))
					{
						pop.setIndividual(j,((DistributedGA)ea).worstInds[j],ind0);
						
						if (Target.isBetter(ind0, pop.getIndividual(j, ((DistributedGA)ea).bestInds[j])))
							((DistributedGA)ea).bestInds[j] = ((DistributedGA)ea).worstInds[j];
						
						for(int k=0; k<islandSize; k++)
		          		  if (Target.isWorse(pop.getIndividual(j,k), pop.getIndividual(j, ((DistributedGA)ea).worstInds[j])))
			          			((DistributedGA)ea).worstInds[j] = k;
					}
				}
			}
		}
    	// For the island distributed population		
        Population pop = (Population)(ea.getParam(CellularGA.PARAM_POPULATION));
    }    
}
