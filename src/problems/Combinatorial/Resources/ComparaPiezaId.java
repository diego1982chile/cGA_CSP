/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package problems.Combinatorial.Resources;

import java.util.Comparator;

/**
 *
 * @author Administrator
 */
public class ComparaPiezaId implements Comparator<Pieza>{            

    @Override
    public int compare(Pieza o1, Pieza o2) {
        return o1.getId()-o2.getId();
    }
}
