package view;

import logic.GraphUtils;
import logic.MaxClique;
import model.Vertex;
import model.data.Examples;
import model.data.ExamplesNames;
import model.enums.CommentLevel;

import java.util.Collection;

/**
 * A demo of a algorithm to calculate the maximal Clique within polynomial time.
 *
 * @author Maciej Niemczyk (Maciej@gmx.de)
 */
public class Main {

    static MaxClique logic = new MaxClique(CommentLevel.NORMAL, true);

    public static void main(String[] args) {

        //GraphUtils.randomGraph(1792)
        Collection<Vertex> graph = GraphUtils.importMatrix(Examples.family, ExamplesNames.family, true);
        Collection<Vertex> maxclique = logic.findMaxClique(graph);

    }

}
