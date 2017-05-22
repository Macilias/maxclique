package logic;

import model.Vertex;
import model.enums.CommentLevel;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Enumeration;

import static logic.PrintUtils.*;
import static logic.GraphUtils.*;

/**
 * Some Description
 *
 * @author maciej.niemczyk@voipfuture.com
 */
public class MaxClique_old {

    private static final Logger LOG = Logger.getLogger(MaxClique_old.class);

    final boolean maxElseAll;

    public MaxClique_old(CommentLevel commentLevel, boolean onlyMax) {
        BasicConfigurator.configure();
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
                LOG.setLevel(Level.WARN);
                break;
            case NORMAL:
                LOG.setLevel(Level.INFO);
                break;
            case VERBOSE:
                LOG.setLevel(Level.DEBUG);
                break;
            default:
                LOG.setLevel(Level.INFO);
        }
        this.maxElseAll = onlyMax;
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
            graph = resetPopularity(graph);
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
        LOG.warn("Die Maximale Clique enthällt " + max.size() + " Elemente");
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < max.size(); i++) {
            Vertex v = max.get(i);
            buffer.append(v.getId() + ", ");
        }
        LOG.warn(buffer.toString());
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
