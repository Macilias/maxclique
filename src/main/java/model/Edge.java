package model;

import model.Vertex;

/**
 * Some Description
 *
 * @author maciej.niemczyk@voipfuture.com
 */
public interface Edge {

    Vertex getV1();

    void setV1(Vertex v1);

    Vertex getV2();

    void setV2(Vertex v2);
}
