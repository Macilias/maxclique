package view;

import logic.maximalecliquen.MaxClique;
import logic.maximalecliquen.Vertex;
import model.Examples;
import model.enums.CommentLevel;

import java.util.Collection;

/**
 * Some Description
 *
 * @author Maciej Niemczyk (Maciej@gmx.de)
 */
public class Main {

    static MaxClique logic = new MaxClique(CommentLevel.VERBOSE, true);

    public static void main(String[] args) {

        //logic.randomGraph(1792)
        Collection<Vertex> graph = logic.importMatrix(Examples.adM1);
        Collection<Vertex> maxclique = logic.findmaxcliqueHASHandHEAP(graph);

    }

}
