package logic.maximalecliquen;

import java.util.Hashtable;

/**
 * Some Description
 *
 * @author Maciej Niemczyk (Maciej@gmx.de)
 */
public class Vertex implements java.lang.Comparable<Vertex> {

    public Vertex(int id){
        this.id = id;
    }
    public int id = 0;
    private int popularity = 0;

    Hashtable adjazete = new Hashtable();

    public boolean adjazent(Vertex v){
        if(this.adjazete.containsKey(v.id)){ return true;}
        else{ return false;}
    }

    Hashtable removed = new Hashtable();

    public boolean isRemoved(Vertex v){
        if(this.removed.containsKey(v.id)){
            return true;
        }
        return false;
    }

    public int getPopularity() {
        return popularity;
    }

    public void incPopularity() {
        this.popularity = popularity+1;
    }

    public void decPopularity() {
        this.popularity = popularity-1;
    }

    public void resPopularity() {
        this.popularity = 0;
    }

    public int compareTo(Vertex o) {
        int comp = 0;
        if(this.getPopularity()<o.getPopularity()) comp = -1;
        if(this.getPopularity()==o.getPopularity()) comp = 0;
        if(this.getPopularity()<o.getPopularity())  comp = 1;
        return comp;
    }

}
