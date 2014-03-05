
/**
 * @author Sergio Romero
 *
 * Order preserving resombination operator
 * 
 */

package operators.recombination;

import java.util.Random;
import jcell.Target;
import jcell.Individual;
import jcell.Operator;
import jcell.CustomIndividual;

public class CustomOx implements Operator
{
   private boolean boolv[]; // chosen alleles
   private Random r;
   
   public CustomOx(Random r)
   {
      this.r = r;
   }
   
   public Object execute(Object o)
   {
      Individual iv[] = (Individual[])o;
      CustomIndividual i1, i2, auxInd, newInd;
      int j, i, cut1, cut2, len = iv[0].getLength();
      
      newInd = (CustomIndividual)iv[0].clone();
      if (boolv == null || boolv.length != len)
         boolv = new boolean[len];
      for (i=0; i<len; i++)
         boolv[i] = false;
         
      i1 = (CustomIndividual)iv[0];
      i2 = (CustomIndividual)iv[1];
      cut1 = r.nextInt(len);
      do{
    	  cut2 = r.nextInt(len);  
      } while (cut1==cut2);
      
      
      if (cut1 > cut2)
      {
         i = cut1;
         cut1 = cut2;
         cut2 = i;
      }
      
//      System.out.println("cut1="+cut1+" cut2="+cut2);
      
      if (!Target.isBetterOrEqual(i1, i2))
      {
         auxInd = i1;
         i1 = i2;
         i2 = auxInd;
      }
      if ((cut2 - cut1) > len/2)
      {
         auxInd = i1;
         i1 = i2;
         i2 = auxInd;
      }
      for (i=cut1+1; i<cut2+1; i++)
      {
         newInd.setIntegerAllele(i,i2.getIntegerAllele(i));
         newInd.setBooleanAllele(i,i2.getBooleanAllele(i));
         boolv[i2.getIntegerAllele(i)] = true; // used alleles
      }
      i = (cut2+1) % len;
      j = i;
      
      // Assigns alleles to genes preserving the order
      while (i != (cut1+1)%len)
      {
         if (boolv[i1.getIntegerAllele(j)])
            j = (j+1) % len;
         else
         {
            newInd.setIntegerAllele(i,i1.getIntegerAllele(j));
            newInd.setBooleanAllele(i,i1.getBooleanAllele(j));
            j = (j+1) % len;
            i = (i+1) % len;
         }
      }
      
      return newInd;
   }
}