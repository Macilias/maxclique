package logic;

import model.Vertex;
import model.enums.CommentLevel;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.*;

import static logic.GraphUtils.*;
import static logic.PrintUtils.*;


/**
 * Some Description
 *
 * @author Maciej Niemczyk (Maciej@gmx.de)
 */
public class MaxClique {

    private static final Logger LOG = Logger.getLogger(MaxClique.class);

    final boolean maxElseAll;

    public MaxClique(CommentLevel commentLevel, boolean onlyMax) {
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
     * eines Knoten als seine Clique und anschliessenden auschluss von
     * Knoten die untereinander nicht adjazent sind.
     *
     * @param graph_coll als Collection<Vertex> von Knoten mit Adjazenten
     * @return die maximale Clique als Collection<Vertex>
     * @author Maciek
     */
    public Collection<Vertex> findMaxClique(Collection<Vertex> graph_coll) {
        ArrayList<Vertex> graph = new ArrayList<>(graph_coll);
        // Grad = Anzahl von Verbindungen mit anderen Knoten + der Knoten selbs
        // Falls alle Cliquen gewünscht werten enhält die ArrayList<Vertex>
        // die Cliquen als TreeMaps sonst die Knoten der Grössten Clique
        int maxGrad = graph.size();
        if (maxGrad == 0) {
            LOG.warn("Der graph war leer");
            return new ArrayList<Vertex>();
        }
        if (maxElseAll) {
            int[] intbuckets = new int[graph.size()];
            graph = bucketsort(graph, intbuckets);
            maxGrad = (graph.get(0).getAdjazete().values().size()) + 1;
            int anz = 0;
            if (maxGrad - 1 > 0) anz = intbuckets[maxGrad - 1];
            if (LOG.isDebugEnabled()) {
                LOG.debug("Der Höchstmögliche MaxGrad währe = " + maxGrad);
            }
            while (anz < maxGrad - 1) {
                maxGrad--;
                anz = 0;
                if (maxGrad - 1 > 0) anz = intbuckets[maxGrad - 1];
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Zu diesen Grad gibt es nicht genug andere Cliqunmitglieder = " + intbuckets[maxGrad - 1]);
                    LOG.debug("reicht  " + maxGrad + "? mit nun " + anz + " möglichen Cliquen Kandidaten?");
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Der neue MaxGrad beträgt = " + maxGrad);
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
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Bisheriger MaxGrad = " + maxGrad);
                        LOG.debug("Aktuelle AdjAnzahl = " + v.getAdjazete().values().size());
                    }
                    maxGrad = v.getAdjazete().values().size() + 1;
                    if (max.size() >= maxGrad) break;
                }
            }
            tmx.put(v.getId(), v);
            LOG.info("[" + i + " von " + graph.size() + "] Beginne neue MaxCliquenSuche bei " + v.getId() + "ten Knoten!");
            LOG.debug(" bei " + v.getAdjazete().values().size() +
                    " Adjazenten und Profezeihung einer höchstens " + maxGrad + " elemtrigen Clique");
            LOG.debug("Seine Adjazenten sind: ");
            Enumeration a = v.getAdjazete().elements();
            StringBuffer logBuf = new StringBuffer();
            while (a.hasMoreElements()) {
                Vertex adj = (Vertex) a.nextElement();
                if (LOG.isDebugEnabled()) logBuf.append(adj.getId() + ", ");
                tmx.put(adj.getId(), adj);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(logBuf.toString());
                LOG.debug("Profezeihung: die maximale " + "Clique kann höchstens " + maxGrad + " elementrig sein!");
                LOG.debug("Jetzt werden die Members geprüft: ");
            }
            Iterator it1tmx = tmx.values().iterator();
            while (it1tmx.hasNext()) {
                Vertex mem = (Vertex) it1tmx.next();
                mem.incPopularity(); //Der Knoten kennst sich schliesslich auch
                LOG.debug("Ist der " + mem.getId() + "te adjazent zu anderen Members?");
                // prove member
                for (Object o : tmx.values()) {
                    Vertex othermem = (Vertex) o;
                    if (!mem.equals(othermem)) {
                        String space = "Zu " + othermem.getId() + "ten adjazent?: " + (othermem.getId() < 10 ? " " : "");
                        if (!mem.adjazent(othermem)) {
                            othermem.decPopularity();
                            LOG.debug(space + "NEIN -> Entfernten Popularität sinkt: " + othermem.getPopularity());
                        } else {
                            othermem.incPopularity();
                            LOG.debug(space + "JA   -> Adjazenten Popularität steigt: " + othermem.getPopularity());
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
                LOG.debug("Mitunter die schlechteste Bewertung ("+ mem.getPopularity() +") hatte der " + mem.getId() + "te Knoten");
                LOG.debug("Nun wird der Zustand vor seiner Bewertung rekostruiert");
                //Proove Popularity
                ittmx = tmx.values().iterator();
                while (ittmx.hasNext()) {
                    Vertex othermem = (Vertex) ittmx.next();
                    if (!mem.equals(othermem)) {
                        if (!mem.adjazent(othermem)) {
                            othermem.incPopularity();
                            LOG.debug("Bei " + othermem.getId() + " steigt die Popularität auf: " + othermem.getPopularity());
                        } else {
                            //Seine Positiven Bewertungen sind ebenfalls unerwünscht
                            othermem.decPopularity();
                            LOG.debug("Bei " + othermem.getId() + " sinkt die Popularität auf: " + othermem.getPopularity());
                        }
                    }
                }
                LOG.debug("Der Knoten wird aus der Clique enfertnt");
                tmx.remove(mem.getId());
                LOG.info( printCliqueTree(tmx));
                cliqueStehtFest = isClique(tmx);
            }
            //Resete die Popularitäten:
            graph = resetPopularity(graph);
            //compare Cliques
            if (LOG.isDebugEnabled()) {
                StringBuffer msg = new StringBuffer();
                msg.append("Bisher ist die größte Clique max mit " + max.values().size() + " Knoten");
                String add = (tmx.values().size() == max.values().size()) ? "ebenfalls ":"";
                msg.append("\nUnsere neue hat " + add + tmx.size() + " Knoten");
                LOG.debug(msg.toString());
                LOG.debug( printClique( tmx ));
            }
            if (tmx.values().size() > max.values().size()) {
                LOG.debug("Max wird also abgelöst");
                max = tmx;
                tmx = new TreeMap<>();
            } else {
                LOG.debug("Max bleit die größte Clique");
                tmx = new TreeMap<>();
            }
        }
        LOG.warn("Die Maximale Clique enthällt " + max.values().size() + " Elemente");
        LOG.info( printTreeMap(max) );
        return max.values();
    }



}
