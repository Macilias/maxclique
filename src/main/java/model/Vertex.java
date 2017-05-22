package model;

import java.util.Hashtable;
import java.util.Optional;

/**
 * Some Description
 *
 * @author maciej.niemczyk@voipfuture.com
 */
public interface Vertex {

    boolean adjazent(Vertex v);

    boolean isRemoved(Vertex v);

    int getId();

    int getPopularity();

    void incPopularity();

    void decPopularity();

    void resPopularity();

    Optional<String> getName();

    Hashtable<Integer, Vertex> getAdjazete();

    Hashtable<Integer, Vertex> getRemoved();

    int compareTo(Vertex o);
}
