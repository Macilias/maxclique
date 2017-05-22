package logic;

import model.Vertex;
import model.VertexImpl;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

import static logic.PrintUtils.printBuckets;
import static logic.PrintUtils.printGraph;

/**
 * Some Description
 *
 * @author maciej.niemczyk@voipfuture.com
 */
public class GraphUtils {

    private static final Logger LOG = Logger.getLogger(GraphUtils.class);


    public static Collection<Vertex> importMatrix(int[][] matrix) {
        return importMatrix(matrix, new String[matrix[0].length], false);
    }


    /**
     * Transferiert ein 2-dimensionales int array in einen Graphen
     *
     * @param matrix als doppeldimensionales array
     * @return Graph inform eines Vektors (Objektorientiert)
     * @author Maciek
     */
    public static Collection<Vertex> importMatrix(int[][] matrix, String[] names, boolean aquivalenz) {
        ArrayList<Vertex> graph = new ArrayList<>();
        if (names == null || names.length != matrix[0].length) {
            LOG.error("names are not valid");
            names = new String[matrix[0].length];
        }
        // create vertex's
        for (int i = 0; i < matrix.length; i++) {
            Vertex v = new VertexImpl(i + 1, names[i]);
            graph.add(v);
        }
        // create connections
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (graph.get(i) != null) {
                    Vertex vi = graph.get(i);
                    Vertex vj = graph.get(j);
                    if (matrix[i][j] == 1) {
                        vi.getAdjazete().put(vj.getId(), vj);
                        if (aquivalenz) {
                            vj.getAdjazete().put(vi.getId(), vi);
                            if (vj.getRemoved().contains(vi)) {
                                vj.getRemoved().remove(vi.getId());
                            }
                        }
                    } else {
                        if (i != j) {
                            vi.getRemoved().put(vj.getId(), vj);
                        }
                    }
                }
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("UNSORTED GRAPH:");
            LOG.debug( printGraph(graph) );
        }
        return graph;
    }

    public static ArrayList<Vertex> bucketsort(ArrayList<Vertex> graph, int[] intbuckets) {
        // histogramm erstellen
        ArrayList<ArrayList<Vertex>> buckets = initializeEmptyBuckets(graph.size());
        for (Vertex v : graph) {
            int size = v.getAdjazete().values().size();
            buckets.get(size).add(v);
            intbuckets[size]++;
        }
        // aufsummieren der Anzahl
        for (int j = intbuckets.length - 2; j > 0; j--) {
            intbuckets[j] = intbuckets[j] + intbuckets[j + 1];
        }
        // sortieren
        ArrayList<Vertex> sortedGraph = new ArrayList<>();
        for (int k = buckets.size(); k > 0; k--) {
            Iterator<Vertex> it = (buckets.get(k - 1)).iterator();
            while (it.hasNext()) sortedGraph.add(it.next());
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("SORTED GRAPH:");
            LOG.debug( printGraph( sortedGraph ));
            LOG.debug("SUMMED INTBUCKETS:");
            LOG.debug( printBuckets( intbuckets ));
        }
        graph = null;
        return sortedGraph;
    }

    private static ArrayList<ArrayList<Vertex>> initializeEmptyBuckets(int size) {
        ArrayList<ArrayList<Vertex>> buckets = new ArrayList<>(size);
        int i = 0;
        while (i < size) {
            buckets.add(i, new ArrayList<>());
            i++;
        }
        return buckets;
    }

    public static ArrayList<Vertex> resetPopularity(ArrayList<Vertex> graph) {
        for (int n = 0; n < graph.size(); n++) {
            (graph.get(n)).resPopularity(); //Neue Clique - neue Popularität
        }
        return graph;
    }

    public static boolean isClique(TreeMap tmx) {
        boolean cliqueStehtFest = true;
        if (!tmx.isEmpty()) {
            Iterator itcheck = tmx.values().iterator();
            while (itcheck.hasNext()) {
                Vertex vc = (Vertex) itcheck.next();
                if ((vc).getPopularity() != tmx.values().size()) {
                    cliqueStehtFest = false;
                }
            }
        }
        return cliqueStehtFest;
    }

    /**
     * Erzeugt einen zufälligen Graphen mit vorgegebener Anzhal von Knoten
     *
     * @param groesse als int die die Grösse des Graphen kennzeichnet
     * @author Maciek
     */
    public static int[][] randomGraph(int groesse) {
        int[][] randomGraph = new int[groesse][groesse];
        for (int i = 0; i < groesse; i++) {
            for (int j = 0; j < i; j++) {
                int random = (int) Math.rint(Math.random());
                randomGraph[i][j] = random;
                randomGraph[j][i] = random;
            }
        }
        return randomGraph;
    }
}
