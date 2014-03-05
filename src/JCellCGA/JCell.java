package JCellCGA;

/**
 * @author Bernabe Dorronsoro 
 * 
 * Description
 * This file contains the main process for running JCell
 * It requires a configuration file. Some examples are
 * given in cfg directory.
 * 
 * For more information:
 * https://gforge.uni.lu
 * http://neo.lcc.uma.es/Software/JCell/index.htm
 * 
 */

import gui.CGADisplay;
import jcell.*;
import MO.*;

import adaptiveCGA.*;
import java.io.*;
import java.util.Random;
import java.util.Date;

// Imports para escribir archivo
import java.text.DecimalFormat;
import java.util.*;
import problems.Combinatorial.Resources.Pieza;

public class JCell implements GenerationListener
{
    // Default population shape
    static int x = 10;
    static int y = 10;
    
    static int longitCrom      ;
    static int numberOfFuncts  ;
    
    // Default maximum number of function evaluations
    static int evaluationsLimit = 30000;
    
    protected static boolean showDisplay = false;
   
    protected double lastRatio = -1.0;
    protected boolean verbose = true;
    private static EvolutionaryAlg ea;      
    
    static String prefix="resultados/";
    
    DecimalFormat df = new DecimalFormat("0.000000000000000000");  
    
    static String algoritmo="";
    static String instancia="";    
    static int ejecucion=0;
    
    static File curvas_conv;
    static File resultados;
    
    static boolean experimento=true;
    
    public static void main (String args[]) throws Exception
    {
//        System.out.println(args[0]);        
//        System.out.println(System.getProperty("java.class.path"));                                   

    	if (args.length > 2)
        {
           System.out.println("Error. Try java JCell <ConfigFile>");
           System.exit(-1);
        }                 
    	
//		Random r = new Random(3816L); // seed for the random number generator
                Random r = new Random(); // seed for the random number generator
                System.out.println(r.toString());
                
                long inicio, fin; // starting and ending time
	
		JCell sel = new JCell();
		
		// Read the configuration file
                               
		ReadConf conf = new ReadConf(args[0], r);
                
                System.out.println(args.length);
                
                if(args.length>1)
                    ejecucion= Integer.parseInt(args[1]);                
		
		// Create and initialice cea with the parameters of the configuration
//		EvolutionaryAlg ea = conf.getParameters();
		ea = conf.getParameters();
                
                System.out.println(ea.getClass().getName());
                String nameSpace= ea.getClass().getName();                
                if(nameSpace.contains("jcell"))
                    algoritmo=nameSpace.split("jcell.")[1];
                else if(nameSpace.contains("genEA"))
                    algoritmo=nameSpace.split("genEA.")[1];
                else
                    algoritmo=nameSpace.split("ssEA.")[1];
                
                instancia=conf.getProperties().getProperty("InstanceFile").split("/")[4].split(".txt")[0];                                 
                // Código agregado para obtener curvas de convergencia   
                curvas_conv= new File(prefix+instancia+"/"+"CurvaConvergencia_"+algoritmo+"_"+instancia+"_"+ejecucion+".csv");                             

                // Código agregado para obtener estadísticas del algoritmo
                resultados= new File(prefix+instancia+"/"+"Resultados_"+algoritmo+"_"+instancia+".csv");         
                
               // Codigo agregado para escribir en archivo de salida
               BufferedWriter bw = null;                                
               if(ejecucion==1)
               {
                    try {
                        bw = new BufferedWriter(new FileWriter(resultados, true));                   
                        bw.write("Ejecucion;Optimo;Best;Hit;Generaciones;Evaluaciones;Tiempo");
                        bw.newLine();
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
                    //////////////////////
               }
//                if (curvas_conv.exists()) {            
//                    FileOutputStream fos = new FileOutputStream(curvas_conv, false);        
//                    fos.close();
//                }        
//
//                if (output2.exists()) {            
//                    FileOutputStream fos = new FileOutputStream(output2, false);        
//                    fos.close();
//                }     
				
		Problem prob = (Problem)ea.getParam(CellularGA.PARAM_PROBLEM);
		longitCrom = prob.numberOfVariables();
		numberOfFuncts = prob.numberOfObjectives();
		
		ea.setParam(CellularGA.PARAM_LISTENER,sel);
		ea.setParam(CellularGA.PARAM_FEEDBACK, new Integer(4));
		
		int displaySteps = ((Integer)ea.getParam(CellularGA.PARAM_DISPLAY_STEPS)).intValue();
		
		showDisplay = (ea.getParam(CellularGA.PARAM_DISPLAY) != null);
		
		inicio = (new Date()).getTime();
		// generation cycles
		ea.experiment();
		fin = (new Date()).getTime();
				
		// output best solution
	   if (showDisplay && (((Integer)ea.getParam(CellularGA.PARAM_GENERATION_NUMBER)).intValue()%displaySteps==0)) {
			  CGADisplay display1 = (CGADisplay) ea.getParam(CellularGA.PARAM_DISPLAY);
			  CGADisplay display2 = (CGADisplay) ea.getParam(CellularGA.PARAM_DISPLAY2);
			  if (ea.getParam(CellularGA.PARAM_DISPLAY) != null)
				  display1.step();
			  if (ea.getParam(CellularGA.PARAM_DISPLAY2) != null)
				  display2.step();
		   }

	   //last step 
	   /*xfig SNAPSHOT
	   PopGrid tmppop = (PopGrid) cea.getPopulation();
	    for (int i = 0; i < x*y; i++) {
			Individual tmpind = tmppop.getIndividual(i);
			int fitVal = (int) (256*((tmpind.getFitness()-(maxFitness/4))/(3*maxFitness/4)));
			if (fitVal < 0) fitVal=0;
			String hex = Integer.toHexString(fitVal);
			if (fitVal < 16) hex = "0"+hex;
			System.out.println( "0 " + (i+32) + " #" + hex + hex + hex + " " + tmpind.getFitness());
		    }	  		   
	   */
	
		if (prob.numberOfObjectives()>1) // The multiobjective case
		{
			System.out.println("Evaluations: " + ((Problem)ea.getParam(CellularGA.PARAM_PROBLEM)).getNEvals() +" Time: "+(fin-inicio));
			System.out.println("Number of solutions in the Pareto front: " + ((Archive) ea.getParam(CellularGA.PARAM_SOLUTION_FRONT)).getNumStoredSols());
			((Archive) ea.getParam(CellularGA.PARAM_SOLUTION_FRONT)).printFile(prob.getClass().getName(),prob);
		}
		else
		{
			Double best = null;
			int contig = 0;
			if (Target.maximize)
				best = (Double) ((Statistic) ea.getParam(CellularGA.PARAM_STATISTIC)).getStat(SimpleStats.MAX_FIT_VALUE);
			else 
				best = (Double) ((Statistic) ea.getParam(CellularGA.PARAM_STATISTIC)).getStat(SimpleStats.MIN_FIT_VALUE);
			int evals = ((Problem) (ea.getParam(CellularGA.PARAM_PROBLEM))).getNEvals();
		    // Writes: best found solution, number of generations, elapsed time (mseconds)
			
			
			if(prob.getClass().getName().equalsIgnoreCase("problems.Combinatorial.DNAFragmentAssembling"))
			{
				// Get the best Individual
				int pos = ((Integer)((Statistic)ea.getParam(EvolutionaryAlg.PARAM_STATISTIC)).getStat(SimpleStats.MAX_FIT_POS)).intValue();
				Individual bestInd = ((Population) ea.getParam(EvolutionaryAlg.PARAM_POPULATION)).getIndividual(pos);
				// Evaluate it without weights
				problems.Combinatorial.DNAFragmentAssembling dna = (problems.Combinatorial.DNAFragmentAssembling) (ea.getParam(CellularGA.PARAM_PROBLEM));
				contig = dna.evalContigs(bestInd);
				//System.out.println("Number of contig: " + contig);
			}
			// In the case of SAT problem, we should compute the best fitness without weights:
			else if(prob.getClass().getName().equalsIgnoreCase("problems.Combinatorial.SAT"))
			{
				// Get the best Individual
				int pos = ((Integer)((Statistic)ea.getParam(EvolutionaryAlg.PARAM_STATISTIC)).getStat(SimpleStats.MAX_FIT_POS)).intValue();
				Individual bestInd = ((Population) ea.getParam(EvolutionaryAlg.PARAM_POPULATION)).getIndividual(pos);
				// Evaluate it without weights
				problems.Combinatorial.SAT sat = (problems.Combinatorial.SAT) (ea.getParam(CellularGA.PARAM_PROBLEM));
				best = new Double(sat.evalCount(bestInd));
			}
			if (((Boolean)ea.getParam(EvolutionaryAlg.PARAM_VERBOSE)).booleanValue())
                        {				
                                /////////////////// Codigo agregado /////////////////
                                if(prob.getClass().getName().equalsIgnoreCase("problems.Combinatorial.CSP") ||
                                   prob.getClass().getName().equalsIgnoreCase("problems.Combinatorial.CTDC") ||
                                   prob.getClass().getName().equalsIgnoreCase("problems.Combinatorial.CTDC2"))                                
                                {
                                    int pos = ((Integer)((Statistic)ea.getParam(EvolutionaryAlg.PARAM_STATISTIC)).getStat(SimpleStats.MAX_FIT_POS)).intValue();
                                    Individual bestInd;
                                    if(prob.getClass().getName().equalsIgnoreCase("problems.Combinatorial.CSP"))
                                        bestInd = (BinaryIndividual) ((Population) ea.getParam(EvolutionaryAlg.PARAM_POPULATION)).getIndividual(pos);                                                                                                                                                                                                                               
                                    else
                                        bestInd = (PermutationIndividual) ((Population) ea.getParam(EvolutionaryAlg.PARAM_POPULATION)).getIndividual(pos);
                                    prob.exportarIndividuo(bestInd,prefix+instancia+"/"+"Layout_"+algoritmo+'_'+instancia+'_'+ejecucion+".txt");
                                    System.out.println("Solution: Best  Generations  Evaluations  Time (ms)  Problem");                                    
                                     // Codigo agregado para escribir en archivo de salida
                                    bw = null;                                
                                    try {
                                        bw = new BufferedWriter(new FileWriter(resultados, true));
                                        bw.write(ejecucion+";"+prob.getMaxFitness()+";"+best+";"+best.equals(prob.getMaxFitness())+";"+(Integer) ea.getParam(CellularGA.PARAM_GENERATION_NUMBER)+ ";"+ evals +";"+(fin-inicio));
                                        bw.newLine();
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
                                    //////////////////////
                                }
                                /////////////////////////////////////////////////////
                        }
			if(prob.getClass().getName().equalsIgnoreCase("problems.Combinatorial.DNAFragmentAssembling"))
			{
				System.out.println(best + " " + contig + " " + (Integer) ea.getParam(CellularGA.PARAM_GENERATION_NUMBER)+" "+ evals +" "+(fin-inicio) + " "
						+ ((Problem) ea.getParam(CellularGA.PARAM_PROBLEM)).getClass().getName());
				// Get the best Individual
				int pos = ((Integer)((Statistic)ea.getParam(EvolutionaryAlg.PARAM_STATISTIC)).getStat(SimpleStats.MAX_FIT_POS)).intValue();
				PermutationIndividual bestInd = (PermutationIndividual) ((Population) ea.getParam(EvolutionaryAlg.PARAM_POPULATION)).getIndividual(pos);
				int len = bestInd.getLength();
				for (int i=0; i<len; i++)
				{
					System.out.print(bestInd.getIntegerAllele(i) + " ");
				}
				System.out.println();
			}
			else 
				System.out.println(best + " " + (Integer) ea.getParam(CellularGA.PARAM_GENERATION_NUMBER)+" "+ evals +" "+(fin-inicio) + " " 
						+ ((Problem) ea.getParam(CellularGA.PARAM_PROBLEM)).getClass().getName());
		}

    }

    private void writeLine(String line)
    {
            if (verbose)
                    System.out.println(line);
    }
    
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
                        // Get the worst Individual
			pos = ((Integer)((Statistic)ea.getParam(EvolutionaryAlg.PARAM_STATISTIC)).getStat(SimpleStats.MIN_FIT_POS)).intValue();
			Individual worstInd = ((Population) ea.getParam(EvolutionaryAlg.PARAM_POPULATION)).getIndividual(pos);
                        Double avg_fit= ((Double)((Statistic)ea.getParam(EvolutionaryAlg.PARAM_STATISTIC)).getStat(SimpleStats.AVG_FIT)).doubleValue();
			Problem prob = (Problem)ea.getParam(EvolutionaryAlg.PARAM_PROBLEM);
			if (prob.numberOfObjectives() == 1)
                        {         
//                                writeLine(bestInd.toString());
				writeLine("Generation: "+(Integer)ea.getParam(CellularGA.PARAM_GENERATION_NUMBER)+"; Best individual: "+df.format(((Double)bestInd.getFitness()).doubleValue()));
                                // Codigo agregado para escribir en archivo de salida
                                if(experimento)
                                {
                                    BufferedWriter bw = null;                                
                                    try {
                                        bw = new BufferedWriter(new FileWriter(curvas_conv, true));
                                        bw.write((Integer)ea.getParam(CellularGA.PARAM_GENERATION_NUMBER)+";"+df.format(((Double)bestInd.getFitness()).doubleValue())+";"+df.format(((Double)worstInd.getFitness()).doubleValue())+";"+df.format((Math.round(avg_fit))));
                                        bw.newLine();
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
                                    //////////////////////
                                }
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
    
    public EvolutionaryAlg getEA()
    {
    	return ea;
    }
    
    public void setEA(EvolutionaryAlg ea)
    {
    	this.ea = ea;
    }
    
}
