package logic.maximalecliquen;

import java.util.Hashtable;

/**
 * Some Description
 *
 * @author Maciej Niemczyk (Maciej@gmx.de)
 */
public class VertexImpl implements java.lang.Comparable<Vertex>, Vertex {

    public VertexImpl(int id){
        this.id = id;
    }
    public int id = 0;
    private int popularity = 0;

    Hashtable<Integer, Vertex> adjazete = new Hashtable<>();

    Hashtable<Integer, Vertex> removed = new Hashtable();

    @Override
    public Hashtable<Integer, Vertex> getAdjazete() {
        return adjazete;
    }

    @Override
    public Hashtable<Integer, Vertex> getRemoved() {
        return removed;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean adjazent(Vertex v){
        if(this.adjazete.containsKey(v.getId())){
            return true;
        }
        return false;
    }

    @Override
    public boolean isRemoved(Vertex v){
        if(this.removed.containsKey(v.getId())){
            return true;
        }
        return false;
    }

    @Override
    public int getPopularity() {
        return popularity;
    }

    @Override
    public void incPopularity() {
        this.popularity = popularity+1;
    }

    @Override
    public void decPopularity() {
        this.popularity = popularity-1;
    }

    @Override
    public void resPopularity() {
        this.popularity = 0;
    }

    @Override
    public int compareTo(Vertex o) {
        int comp = 0;
        if(this.getPopularity()<o.getPopularity()) comp = -1;
        if(this.getPopularity()==o.getPopularity()) comp = 0;
        if(this.getPopularity()<o.getPopularity())  comp = 1;
        return comp;
    }

}
