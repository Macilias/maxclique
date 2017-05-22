package logic.maximalecliquen;

import model.enums.CommentLevel;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Some Description
 *
 * @author Maciej Niemczyk (Maciej@gmx.de)
 */
public class MaxClique {

    private static final Logger LOG = Logger.getLogger("MaxClique");

    //0 heiss wirklich 0 bis auf die ausgabe der Maximalen Clique
    //1 heisst normal performant, beginn neuer Suche wird angedeutet
    final boolean maxElseAll;

    public MaxClique(CommentLevel commentLevel, boolean onlyMax) {
        /**
         * FINEST  -> TRACE -> VERBOSE
         * FINER   -> DEBUG -> VERBOSE
         * FINE    -> DEBUG -> VERBOSE
         * CONFIG  -> INFO  -> NORMAL
         * INFO    -> INFO  -> NORMAL
         * WARNING -> WARN  -> QUIET
         * SEVERE  -> ERROR -> QUIET
         */
        switch (commentLevel) {
            case QUIET:
                LOG.setLevel(Level.SEVERE);
                break;
            case NORMAL:
                LOG.setLevel(Level.INFO);
                break;
            case VERBOSE:
                LOG.setLevel(Level.ALL);
                break;
            default:
                LOG.setLevel(Level.INFO);
        }
        this.maxElseAll = onlyMax;
    }

    /**
     * Transferiert ein 2-dimensionales int array in einen Graphen
     *
     * @param matrix als doppeldimensionales array
     * @return Graph inform eines Vektors (Objektorientiert)
     * @author Maciek
     */
    public Collection<Vertex> importMatrix(int[][] matrix) {
        ArrayList<Vertex> graph = new ArrayList<>();
        for (int i = 0; i < matrix.length; i++) {
            Vertex v = new VertexImpl(i + 1);
            graph.add(v);
        }
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (graph.get(i) != null) {
                    Vertex v = (Vertex) graph.get(i);
                    if (matrix[i][j] == 1) {
                        v.getAdjazete().put(((Vertex) graph.get(j)).getId(), graph.get(j));
                    } else {
                        if (i != j) v.getRemoved().put(((Vertex) graph.get(j)).getId(), graph.get(j));
                    }
                }
            }
        }
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("UNSORTIERTER GRAPH:");
            LOG.finest(printGraph(graph));
        }
        return graph;
    }

    public ArrayList<Vertex> bucketsort(ArrayList<Vertex> graph, int[] intbuckets) {
        // histogramm erstellen
        ArrayList<ArrayList<Vertex>> buckets = initializeEmptyBuckets(graph.size());
        for (Vertex v : graph) {
            int size = v.getAdjazete().values().size();
            buckets.get(size).add(v);
            intbuckets[size]++;
        }
        // aufsummieren der anzahl
        for (int j = intbuckets.length - 2; j > 0; j--) {
            intbuckets[j] = intbuckets[j] + intbuckets[j + 1];
        }
        // sortieren
        ArrayList<Vertex> sortedGraph = new ArrayList<>();
        for (int k = buckets.size(); k > 0; k--) {
            Iterator<Vertex> it = (buckets.get(k - 1)).iterator();
            while (it.hasNext()) sortedGraph.add(it.next());
        }
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("SORTIERTER GRAPH:");
            LOG.finest( printGraph(sortedGraph));
            LOG.finest("Aufsummerte intbuckets:");
            LOG.finest( printBuckets(intbuckets));
        }
        graph = null;
        return sortedGraph;
    }

    private ArrayList<ArrayList<Vertex>> initializeEmptyBuckets(int size) {
        ArrayList<ArrayList<Vertex>> buckets = new ArrayList<>(size);
        int i = 0;
        while (i < size) {
            buckets.add(i, new ArrayList<>());
            i++;
        }
        return buckets;
    }


    /**
     * Findet die Maximale Clique durch betrachten der adjazenten
     * eines Knoten als seine Clique und anschliessenden auschluss von
     * Knoten die untereinander nicht adjazent sind.
     *
     * @param graph_coll als Collection<Vertex> von Knoten mit Adjazenten
     * @return die maximale Clique als Collection<Vertex>
     * @author Maciek
     */
    public Collection<Vertex> findmaxcliqueHASHandHEAP(Collection<Vertex> graph_coll) {
        ArrayList<Vertex> graph = new ArrayList<>(graph_coll);
        // Grad = Anzahl von Verbindungen mit anderen Knoten + der Knoten selbs
        // Falls alle Cliquen gewünscht werten enhält die ArrayList<Vertex>
        // die Cliquen als TreeMaps sonst die Knoten der Grössten Clique
        int maxGrad = graph.size();
        if (maxGrad == 0) {
            LOG.warning("Der graph war leer");
            return new ArrayList<Vertex>();
        }
        if (maxElseAll) {
            int[] intbuckets = new int[graph.size()];
            graph = bucketsort(graph, intbuckets);
            maxGrad = (graph.get(0).getAdjazete().values().size()) + 1;
            int anz = 0;
            if (maxGrad - 1 > 0) anz = intbuckets[maxGrad - 1];
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Der Höchstmögliche MaxGrad währe = " + maxGrad);
            }
            while (anz < maxGrad - 1) {
                maxGrad--;
                anz = 0;
                if (maxGrad - 1 > 0) anz = intbuckets[maxGrad - 1];
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.finest("Zu diesen Grad gibt es nicht genug andere Cliqunmitglieder = " + intbuckets[maxGrad - 1]);
                    LOG.finest("reicht  " + maxGrad + "? mit nun " + anz + " möglichen Cliquen Kandidaten?");
                }
            }
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Der neue MaxGrad beträgt = " + maxGrad);
            }
        }
        TreeMap<Integer, Vertex> max = new TreeMap<>();
        TreeMap<Integer, Vertex> tmx = new TreeMap<>();
        //Alle Knoten bilden eigene Cliquen die geprüft werden müssen
        //Sollte jedoch schon eine Clique mit maximaler Anzahl Adjazenten
        //bereits gefunden sein, so kann das Ergebniss nicht mehr
        //verbessert weredn.
        for (int i = 0; i < graph.size() && max.size() < maxGrad; i++) {
            Vertex v = graph.get(i);
            if (maxElseAll) {
                if (v.getAdjazete().values().size() + 1 < maxGrad) {
                    if (LOG.isLoggable(Level.FINEST)) {
                        LOG.finest("Bisheriger MaxGrad = " + maxGrad);
                        LOG.finest("Aktuelle AdjAnzahl = " + v.getAdjazete().values().size());
                    }
                    maxGrad = v.getAdjazete().values().size() + 1;
                    if (max.size() >= maxGrad) break;
                }
            }
            tmx.put(v.getId(), v);
            LOG.finer("__Beginne neue MaxCliquenSuche bei " + v.getId() + "ten Knoten!__");
            LOG.finer(i + " von " + graph.size());
            LOG.finest(" bei " + v.getAdjazete().values().size() +
                    " Adjazenten und Profezeihung einer höchstens " + maxGrad +
                    " elemtrigen Clique");
            LOG.finest("Seine Adjazenten sind: ");
            Enumeration a = v.getAdjazete().elements();
            StringBuffer logBuf = new StringBuffer();
            while (a.hasMoreElements()) {
                Vertex adj = (Vertex) a.nextElement();
                if (LOG.isLoggable(Level.FINEST)) logBuf.append(adj.getId() + ", ");
                tmx.put(adj.getId(), adj);
            }
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest(logBuf.toString());
                LOG.finest("Profezeihung: die maximale " + "Clique kann höchstens " + maxGrad + " elementrig sein!");
                LOG.finest("Jetzt werden die Members geprüft: ");
            }
            Iterator it1tmx = tmx.values().iterator();
            while (it1tmx.hasNext()) {
                Vertex mem = (Vertex) it1tmx.next();
                mem.incPopularity(); //Der Knoten kennst sich schliesslich auch
                LOG.finest("Ist der " + mem.getId() + "te adjazent zu anderen Members?");
                // prove member
                for (Object o : tmx.values()) {
                    Vertex othermem = (Vertex) o;
                    if (!mem.equals(othermem)) {
                        LOG.finest("Zu " + othermem.getId() + "ten adjazent?: ");
//                        if (commentLevel > 2) if ((othermem.id) < 10) System.out.print(" ");
                        if (!mem.adjazent(othermem)) {
                            othermem.decPopularity();
                            LOG.finest("NEIN -> Entfernten Popularität sinkt: "
                                    + othermem.getPopularity());
                        } else {
                            othermem.incPopularity();
                            LOG.finest("JA   -> Adjazenten Popularität steigt: "
                                    + othermem.getPopularity());
                        }
                    }
                }
            }
            // Eine Clique wird gefunden wenn die Popularität der Mitglieder
            // gleich der Grösse der Clique -1  ist
            boolean cliqueStehtFest = isClique(tmx);

            while (!cliqueStehtFest) {
                //Suche des unpoulärsten Knotens:
                Map.Entry ent = tmx.firstEntry();
                Vertex mem = (Vertex) ent.getValue();
                Iterator ittmx = tmx.values().iterator();
                while (ittmx.hasNext()) {
                    Vertex other = (Vertex) ittmx.next();
                    if (mem.getPopularity() > other.getPopularity()) mem = other;
                }
                //Rekonstruktion des Zusatands vor seiner Bewertung:
                LOG.finest("Mitunter die schlechteste Bewertung hatte der " + mem.getId() + "te Knoten");
                LOG.finest("Nun wird der Zustand vor seiner Bewertung rekostruiert");
                //Proove Popularity
                ittmx = tmx.values().iterator();
                while (ittmx.hasNext()) {
                    Vertex othermem = (Vertex) ittmx.next();
                    if (!mem.equals(othermem)) {
                        LOG.finest("Bei " + othermem.getId() + " ");
                        if (!mem.adjazent(othermem)) {
                            othermem.incPopularity();
                            LOG.finest("steigt die Popularität: " + othermem.getPopularity());
                        } else {
                            //Seine Positiven Bewertungen sind ebenfalls unerwünscht
                            othermem.decPopularity();
                            LOG.finest("sinkt die Popularität: " + othermem.getPopularity());
                        }
                    }
                }
                LOG.finest("Der Knoten wird aus der Clique enfertnt");
                tmx.remove(mem.getId());
                LOG.finest( printCliqueTree(tmx));
                cliqueStehtFest = isClique(tmx);
            }
            //Resete die Popularitäten:
            resetPopularity(graph);
            //compare Cliques
            StringBuffer msg = new StringBuffer();
            msg.append("Bisher ist die Gösste Clique max mit " + max.values().size() + " Knoten");
            String add = (tmx.values().size() == max.values().size()) ? "ebenfalls ":"";
            msg.append("Unsere neue hat " + add + tmx.size() + " Knoten");
            LOG.fine(msg.toString());
            LOG.finest(printClique(tmx));
            if (tmx.values().size() > max.values().size()) {
                LOG.fine("Max wird also abgelöst");
                max = tmx;
                tmx = new TreeMap<>();
            } else {
                LOG.fine("Max bleit also die Grösste Clique");
                tmx = new TreeMap<>();
            }
        }
        LOG.info("Die Maximale Clique enthällt " + max.values().size() + " Elemente");
        LOG.info(printResultingMaxValues(max));
        return max.values();
    }

    private void resetPopularity(ArrayList<Vertex> graph) {
        for (int n = 0; n < graph.size(); n++) {
            (graph.get(n)).resPopularity(); //Neue Clique - neue Popularität
        }
    }

    private boolean isClique(TreeMap tmx) {
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
     * Gibt die String represenation des Graphen aus
     *
     * @param graph der Graph als Vektror von Knoten mit adjazenten
     * @author Maciek
     */
    public String printGraph(ArrayList<Vertex> graph) {
        StringBuffer out = new StringBuffer();
        for (int i = 0; i < graph.size(); i++) {
            Vertex v = graph.get(i);
            out.append(v.getId() + " Knoten ist adjazent zu: ");
            Iterator ita = v.getAdjazete().values().iterator();
            while (ita.hasNext()) {
                Vertex va = (Vertex) ita.next();
                if (va != null) {
                    out.append(va.getId() + ",");
                }
            }
            out.append("Insgesammt: " + v.getAdjazete().values().size() + " Stück \n");
            out.append("\n");
            out.append("und ist enfernt zu: ");
            Iterator ite = v.getRemoved().values().iterator();
            while (ite.hasNext()) {
                Vertex ve = (Vertex) ite.next();
                if (ve != null) {
                    out.append(ve.getId() + ",");
                }
            }
            out.append("Insgesammt: " + v.getRemoved().values().size() + " Stück");
            out.append("\n");
        }
        return out.toString();
    }

    /**
     * Gibt die Clique aus
     *
     * @param clique
     * @author Maciek
     */
    public String printClique(TreeMap clique) {
        StringBuffer out = new StringBuffer();
        out.append("PRINT CLIQUE");
        Iterator it = clique.values().iterator();
        while (it.hasNext()) {
            Vertex v = (Vertex) it.next();
            out.append(v.getId() + ", ");
        }
        out.append("\n");
        return out.toString();
    }

    /**
     * Überwacht die Popularitätsveränderungen der Clique
     *
     * @param clique die Clique als Vektror von Knoten mit POpularitäten
     * @author Maciek
     */
    public String printCliqueTree(TreeMap clique) {
        StringBuffer out = new StringBuffer();
        out.append("PRINT CLIQUE");
        Iterator it = clique.values().iterator();
        while (it.hasNext()) {
            Vertex v = (Vertex) it.next();
            out.append(v.getId() + " Knoten hat die Popularität: " + v.getPopularity() + "\n");
        }
        return out.toString();
    }

    /**
     * Gibt die intbuckets aus
     *
     * @param intbuckets
     * @return
     */
    public String printBuckets(int[] intbuckets) {
        StringBuffer out = new StringBuffer();
        for (int i = 0; i < intbuckets.length; i++) {
            out.append(intbuckets[i] + ", ");
        }
        return out.toString();
    }

    private String printResultingMaxValues(TreeMap<Integer, Vertex> max) {
        Iterator it = max.values().iterator();
        StringBuffer buffer = new StringBuffer();
        while (it.hasNext()) {
            Vertex v = (Vertex) it.next();
            buffer.append(v.getId() + ", ");
        }
        return buffer.toString();
    }

    /**
     * Erzeugt einen Zufälligen Graphen mit vorgegebener Anzhal von Knoten
     *
     * @param groesse als int die die Grösse des Graphen kennzeichnet
     * @author Maciek
     */
    public int[][] randomGraph(int groesse) {
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

    /**
     * Findet die Maximale Clique durch betrachten der adjazenten
     * eines Knoten als seine Clique und anschliessenden Auschluss von
     * Knoten die untereinander nicht adjazent sind.
     *
     * @param graph als Vektror von Knoten mit Adjazenten
     * @return die maximale Clique als Vektor von Knoten
     * @author Maciek
     */
    public ArrayList<Vertex> findmaxcliqueHASH(ArrayList<Vertex> graph) {
        //Durchsuch den Graphen nach höchsten adjazenten Grad
        //Grad = Anzahl von Verbindungen mit anderen Knoten
        int maxGrad = getMaxGrad(graph);
        ArrayList<Vertex> max = new ArrayList<>();
        ArrayList<Vertex> tmx = new ArrayList<>();
        //Alle Knoten bilden eigene Cliquen die geprüft werden müssen
        //Sollte jedoch schon eine Clique mit maximaler Anzahl adjazenten (= maxGrad)
        //bereits gefunden sein, so kann das Ergebniss nicht mehr
        //verbessert weredn.
        for (int i = 0; i < graph.size() && max.size() < maxGrad; i++) {
            Vertex v = graph.get(i);
            tmx.add(v);
            LOG.info("__Beginne neue MaxCliquenSuche bei " + v.getId() + "ten Knoten!__");
            Enumeration a = v.getAdjazete().elements();
            StringBuffer buffer = new StringBuffer();
            while (a.hasMoreElements()) {
                Vertex adj = (Vertex) a.nextElement();
                buffer.append(adj.getId() + ", ");
                tmx.add(adj);
            }
            LOG.info(buffer.toString());
            for (int k = 1; k < tmx.size(); k++) {
                Vertex mem = tmx.get(k);
                mem.incPopularity(); //Der Knoten kennst sich schliesslich auch
                for (int l = 0; l < tmx.size(); l++) {
                    Vertex othermem = tmx.get(l);
                    if (!mem.equals(othermem)) {
                        if (!mem.adjazent(othermem)) {
                            othermem.decPopularity();
                        } else {
                            othermem.incPopularity();
                        }
                    }
                }
            }
            // Eine Clique stellt sich ein, wenn die Popularität der Mitglieder
            // gleich der Grösse der Clique -1 ist
            // (Dies Könnte noch beschleunigt werden durch HEAP da das kleinste Element vorne währe
            // und man somit sofort den Gegenbeis (m.popularity != tmx.size) hätte, und es später
            // schneller enfernen könnte)
            boolean CliqueStehtFest = isClique(tmx);
            while (!CliqueStehtFest) {
                // Suche des unpoulärsten Knotens:
                // DESWEGEN AUCH CLIQUEN ALS HEAP SPEICHERN:
                Vertex mem = tmx.get(0);
                for (int k = 1; k < tmx.size(); k++) {
                    Vertex temp = tmx.get(k);
                    if (temp.getPopularity() < mem.getPopularity()) mem = temp;
                }
                // Rekonstruktion des Zusatands vor seiner Bewertung:("Nun wird der Zustand vor seiner Bewertung rekostruiert");
                // Proove Popularity
                for (int l = 0; l < tmx.size(); l++) {
                    Vertex othermem = tmx.get(l);
                    if (!mem.equals(othermem)) {
                        if (!mem.adjazent(othermem)) {
                            othermem.incPopularity();
                        } else {
                            // Seine Positiven Bewertungen sind ebenfalls unerwünscht
                            othermem.decPopularity();
                        }
                    }
                }
                tmx.remove(mem);
                CliqueStehtFest = true;
                for (int m = 0; m < tmx.size(); m++) {
                    if (((Vertex) tmx.get(m)).getPopularity() != tmx.size() - 1) CliqueStehtFest = false;
                }
            }
            // Resete die Popularitäten:
            resetPopularity(graph);
            // compare Cliques
            LOG.info("Bisher ist die Gösste Clique max mit " + max.size() + " Knoten");
            LOG.info("Unsere neue hat " + tmx.size() + " Knoten");
            if (tmx.size() > max.size()) {
                LOG.info("Max wird also abgelöst");
                max = tmx;
                tmx = new ArrayList<>();
            } else {
                LOG.info("Max bleit also die Grösste Clique");
                tmx = new ArrayList<>();
            }
        }
        LOG.info("Die Maximale Clique enthällt " + max.size() + " Elemente");
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < max.size(); i++) {
            Vertex v = max.get(i);
            buffer.append(v.getId() + ", ");
        }
        LOG.info(buffer.toString());
        return max;
    }

    private boolean isClique(ArrayList<Vertex> tmx) {
        boolean CliqueStehtFest = true;
        for (int m = 0; m < tmx.size() && CliqueStehtFest; m++) {
            if ((tmx.get(m)).getPopularity() != tmx.size() - 1) {
                CliqueStehtFest = false;
                break;
            }
        }
        return CliqueStehtFest;
    }

    private int getMaxGrad(ArrayList<Vertex> graph) {
        int maxGrad = 0;
        for (int g = 0; g < graph.size(); g++) {
            Vertex v = graph.get(g);
            if (v.getAdjazete().size() > maxGrad) maxGrad = v.getAdjazete().size();
        }
        return maxGrad;
    }

}
