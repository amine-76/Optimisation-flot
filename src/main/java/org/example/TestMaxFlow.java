package org.example;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

public class TestMaxFlow {

    public static void main(String[] args) {
        // Utilisation de l'UI Swing pour l'affichage du graphe
        System.setProperty("org.graphstream.ui", "swing");

        // Création du graphe
        Graph g = new SingleGraph("test");

        // Ajouter les nœuds
        g.addNode("s");
        g.addNode("1");
        g.addNode("2");
        g.addNode("3");
        g.addNode("4");
        g.addNode("t");

        // Ajouter les arêtes avec leurs capacités
        g.addEdge("e1", "s", "1", true).setAttribute("cap", 10);
        g.addEdge("e2", "s", "3", true).setAttribute("cap", 6);
        g.addEdge("e3", "1", "3", true).setAttribute("cap", 3);
        g.addEdge("e4", "1", "2", true).setAttribute("cap", 8);
        g.addEdge("e5", "1", "4", true).setAttribute("cap", 2);
        g.addEdge("e6", "3", "4", true).setAttribute("cap", 6);
        g.addEdge("e7", "2", "4", true).setAttribute("cap", 1);
        g.addEdge("e8", "2", "t", true).setAttribute("cap", 6);
        g.addEdge("e9", "4", "t", true).setAttribute("cap", 10);

        // Ajouter un label aux nœuds
        g.nodes().forEach(n -> n.setAttribute("ui.label", n.getId()));

        // Définir les styles CSS pour l'affichage
        g.setAttribute("ui.stylesheet", "node { fill-color: black; text-alignment: above; text-size: 20px; }");

        // Affichage du graphe
        g.display();

        // Instancier l'algorithme de flux maximum
        MaxFlow mf = new MaxFlow();
        mf.setCapacityAttribute("cap");
        mf.init(g);
        mf.setSource(g.getNode("s"));
        mf.setSink(g.getNode("t"));
        mf.compute();

        // Affichage du flux total calculé
        System.out.println("Flux total : " + mf.getFlow());

        // Mettre à jour les labels des arêtes avec flux et capacité
        g.edges().forEach(e -> {
            double flow = mf.getFlow(e);  // Flux sur l'arête
            double cap = mf.getCapacity(e);  // Capacité de l'arête

            // Mettre à jour les labels pour afficher le flux et la capacité
            e.setAttribute("ui.label", String.format("%.2f / %.2f", flow, cap));

            // Colorier les arêtes saturées en rouge
            if (flow == cap) {
                e.setAttribute("ui.style", "fill-color: red;");
            } else {
                e.setAttribute("ui.style", "fill-color: black;");
            }
        });
    }
}
