package logic;

import model.Vertex;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * Some Description
 *
 * @author maciej.niemczyk@voipfuture.com
 */
public class PrintUtils {

    /**
     * Gibt die String represenation des Graphen aus
     *
     * @param graph der Graph als Vektror von Knoten mit adjazenten
     * @author Maciek
     */
    public static String printGraph(ArrayList<Vertex> graph) {
        StringBuffer out = new StringBuffer();
        out.append("printGraph():\n");
        for (int i = 0; i < graph.size(); i++) {
            Vertex v = graph.get(i);
            String name = v.getName().isPresent() ? String.format("(%s) ", v.getName().get()) : "";
            int aCount = v.getAdjazete().values().size();
            int rCount = v.getRemoved().values().size();
            String count = String.format("(%d/%d)", aCount, aCount + rCount);
            out.append(String.format("\n_________Knoten %s %sist adjazent %s zu: ", v.getId(), name, count));
            Iterator ita = v.getAdjazete().values().iterator();
            out.append(printHashtable(v.getAdjazete()));
            out.append("\nund nicht zu: ");
            out.append(printHashtable(v.getRemoved()) + "\n");
        }
        return out.toString();
    }

    private static String printHashtable(Hashtable<Integer, Vertex> adjazete) {
        Iterator ita = adjazete.values().iterator();
        StringBuffer out = new StringBuffer();
        out.append("printHashtable():\n");
        while (ita.hasNext()) {
            Vertex va = (Vertex) ita.next();
            if (va != null) {
                String name = va.getName().isPresent() ? String.format(" (%s)", va.getName().get()) : "";
                out.append(va.getId() + name);
            }
            if (ita.hasNext()) {
                out.append(", ");
            }
        }
        return out.toString();
    }

    /**
     * Gibt die Clique aus
     *
     * @param clique
     * @author Maciek
     */
    public static String printClique(TreeMap clique) {
        StringBuffer out = new StringBuffer();
        out.append("printClique():\n");
        Iterator it = clique.values().iterator();
        while (it.hasNext()) {
            Vertex v = (Vertex) it.next();
            out.append(v.getId() + ", ");
        }
        return out.toString();
    }

    /**
     * Überwacht die Popularitätsveränderungen der Clique
     *
     * @param clique die Clique als Vektror von Knoten mit POpularitäten
     * @author Maciek
     */
    public static String printCliqueTree(TreeMap clique) {
        StringBuffer out = new StringBuffer();
        out.append("printCliqueTree():\n");
        Iterator it = clique.values().iterator();
        while (it.hasNext()) {
            Vertex v = (Vertex) it.next();
            String name = v.getName().isPresent() ? String.format("(%s) ", v.getName().get()) : "";
            out.append(v.getId() + " Knoten " + name + "hat die Popularität: " + v.getPopularity() + "\n");
        }
        return out.toString();
    }

    /**
     * Gibt die intbuckets aus
     *
     * @param intbuckets
     * @return
     */
    public static String printBuckets(int[] intbuckets) {
        StringBuffer out = new StringBuffer();
        out.append("printBuckets():\n");
        for (int i = 0; i < intbuckets.length; i++) {
            out.append(intbuckets[i] + ", ");
        }
        return out.toString();
    }

    /**
     * Gibt die Knoten aus
     *
     * @param max
     * @return
     */
    public static String printTreeMap(TreeMap<Integer, Vertex> max) {
        Iterator it = max.values().iterator();
        StringBuffer out = new StringBuffer();
        out.append("printTreeMap():\n");
        while (it.hasNext()) {
            Vertex v = (Vertex) it.next();
            String name = v.getName().isPresent() ? String.format(" (%s)", v.getName().get()) : "";
            out.append(v.getId() + name);
            if (it.hasNext()) {
                out.append(", ");
            }
        }
        return out.toString();
    }


}
