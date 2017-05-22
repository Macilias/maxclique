package logic.maximalecliquen;

/**
 * Some Description
 *
 * @author Maciej Niemczyk (Maciej@gmx.de)
 */
public class EdgeImpl implements Edge {

    private Vertex v1;
    private Vertex v2;

    @Override
    public Vertex getV1() {
        return v1;
    }

    @Override
    public void setV1(Vertex v1) {
        this.v1 = v1;
    }

    @Override
    public Vertex getV2() {
        return v2;
    }

    @Override
    public void setV2(Vertex v2) {
        this.v2 = v2;
    }
}
