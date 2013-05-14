/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package problems.Combinatorial.Resources;

/**
 *
 * @author Administrator
 */
public class Pair<K, V> {

    private K first;
    private V second;

    public static <K, V> Pair<K, V> createPair(K key, V value) {
        return new Pair<>(key,value);
    }

    public Pair(K first, V second) {
        this.first = first;
        this.second = second;
    }

    public K getFirst() {
        return first;
    }
    
    public void setFirst(K first) {
        this.first=first;
    }    

    public V getSecond() {
        return second;
    }
    
    public void setSecond(V second) {
        this.second=second;
    }        
}