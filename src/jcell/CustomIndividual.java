/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jcell;

import java.util.Random;

/**
 *
 * @author diego
 */
public class CustomIndividual extends PermutationIndividual{
   
   public boolean alleles2[]; // binary chromosome
      
   // Create a new individual with 10 empty binary genes (to be initialized)
   public CustomIndividual()
   {
      this(10);
   }
   
   // Create a new individual with 'len' empty binary genes (to be initialized)
   public CustomIndividual(int len)
   {
      super(len);
      alleles2 = new boolean[len];
   }       
   
   // Get the allele value in position 'locus'
   // Returns a Boolean object
   public Object getAllele2(int locus)
   {
      return new Boolean(alleles2[locus]);
   }      
   
   // Get the allele value in position 'locus'
   // Returns a boolean value
   public boolean getBooleanAllele(int locus)
   {
      return alleles2[locus];
   }
   
   // Set the allele value in position 'locus'
   // The parameters are a Boolean object and the 'locus' position
   public void setAllele2(int locus, Object allele)
   {    
       alleles2[locus] = ((Boolean)allele).booleanValue();
   }
   
   // Set the allele value in position 'locus'
   // The parameters are a boolean value and the 'locus' position
   public void setBooleanAllele(int locus, boolean allele)
   {
      alleles2[locus] = allele;
   }
   
   public int getIntValue(int gene, int geneSize, int maxValue, int minValue)
   // Returns the integer number represented
   // as a binary chain between begin and end
   // both of them included
   {
	  int value=0;
	  
	  int end = gene+geneSize;
	  
	  for (int i=gene; i<end; i++)
	  {
		   if (alleles2[i])
		       value++;
		   value<<=1;
	  }
	  
	  value>>=1;

	 double val = (double)value / Math.pow(2,geneSize);
         val = val * (maxValue - minValue);
         value = (int) Math.round(val) + minValue;
     
	 return value;	       
   }
   
   // Flips the value of an allele
   public void mutate2(Random r, int locus)
   {
      alleles2[locus] = !alleles2[locus];
   }
   
      // Swap positions i y j
   public void swap2(int i, int j)
   {
      boolean tmp = alleles2[i];
      alleles2[i] = alleles2[j];
      alleles2[j] = tmp;
   }
   
   // Removes the ith gene and inserts it in position j
   public void relocate2(int i, int j)
   {
      int aux; 
      boolean v;
      
      if (i == j)
         return;
      
      aux = i;
      v = alleles2[i];
      if (i < j)
         while (aux < j)
         {
            alleles2[aux] = alleles2[aux+1];
            aux++;
         }
      else
         while (aux > j)
         {
            alleles2[aux] = alleles2[aux-1];
            aux--;
         }
      alleles2[j] = v;
   }
   
   // Modify the chromosome length
   public void setLength(int len) 
   {
      super.setLength(len);
      alleles2 = new boolean[len];
   }
   
   // Set random values to every gene
   public void setRandomValues(Random r)
   {
      for (int i=0; i<len; i++)
         alleles2[i] = r.nextBoolean();
      
      int i, j, tmp;
      
      for (i=0; i<len; i++)
         alleles[i] = i;
      for (i=1; i<len; i++)
      {
         j = r.nextInt(i+1);
         swap(i,j);
      }
   }
   
   // Copy the values of individual 'ind' to this one 
   public void copyIndividual(Individual ind)
   {
      CustomIndividual bInd = (CustomIndividual)ind;
      
      for (int i=0; i<len; i++)
         alleles2[i] = bInd.getBooleanAllele(i);
      fitness = bInd.getFitness();
   }
   
   // Returns the decimal value of the number represented
   // as a binary chain bstarting in 'locus' with length 'len' 
   public long binaryToDecimal(int locus, int len)
   {
      long ac = 1L, sum = 0L;
      int i = locus + len - 1;      
      
      while (i >= locus)
      {
         sum += ac * (alleles2[i]?1L:0L);
         ac *= 2L;
         i--;
      }
      
      return sum;
   }   
   
   // Creates an string with the individual information
   public String toString()
   {
      StringBuffer sb = new StringBuffer("[");
      
      for (int i=0; i<len; i++)
         sb.append(alleles2[i]?"1":"0");
      
      sb.append("] [");            
      
      for (int i=0; i<len; i++)
         sb.append(alleles[i]+" ");
      
      sb.append("] Fitness: "+fitness);
      
      return sb.toString();
   }
   
   // Check if obj has the same values than this one
   public boolean isEqualTo(Individual obj)
   {
      CustomIndividual ind;
      
      if (!(obj instanceof CustomIndividual))
         return false;
         
      ind = (CustomIndividual)obj;
      
      if (!this.identicalFitness(ind))
			return false;
      
      if (ind.getLength() != len)
         return false;
         
      for (int i=0; i<len; i++)
         if (ind.getBooleanAllele(i) != alleles2[i])
            return false;
      return true;
   }
      
   // returns a new individual with the same values as this one
   public Object clone()
   {
      CustomIndividual ind;
      
      ind = (CustomIndividual)super.clone();
      ind.alleles2 = (boolean[])alleles2.clone();
      
      return ind;
   }      
}
