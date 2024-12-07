package org.example;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceDGS;

public class Main {
    public static void main(String[] args) {
        System.setProperty("org.graphstream.ui", "swing"); // Utilisation de l'UI Swing

        // Création du graphe
        Graph graph = new SingleGraph("Graph DGS");

        try {
            // Utilisation de FileSourceDGS pour lire le fichier DGS
            FileSource fileSource = new FileSourceDGS();
            fileSource.addSink(graph); // Connecter le lecteur au graphe

            // Lecture et traitement du fichier
            fileSource.readAll("src/main/resources/reseaux.dgs");
            // Désactiver le layout automatique
            // Configuration du style pour afficher les labels
            graph.setAttribute("ui.stylesheet",
                    "edge { text-size: 14; text-alignment: along; text-background-mode: plain; } " +
                            "node {" +
                            "   size: 30px, 30px; " +         // Taille des nœuds (largeur, hauteur)
                            "   shape: circle; " +           // Forme du nœud (cercle, rectangle, etc.)
                            "   text-size: 18; " +           // Taille du texte du label
                            "   text-alignment: center; " +  // Aligner le texte au centre du nœud
                            "   text-mode: normal; " +       // Afficher le texte en mode normal
                            "   fill-color: lightblue; " +   // Couleur de remplissage des nœuds
                            "   stroke-mode: plain; " +      // Bordure simple
                            "   stroke-color: black; " +     // Couleur des bordures
                            "}"
            );
            graph.nodes().forEach(node -> node.setAttribute("ui.label", node.getId()));
            graph.edges().forEach(edge -> edge.setAttribute("ui.label", edge.getAttribute("cap")));
            graph.setAutoCreate(false);
            graph.setStrict(false);
            graph.display(false); // "false" désactive le layout automatique

            // Positionner les nœuds selon les coordonnées `xyz`
            graph.nodes().forEach(node -> {
                if (node.hasAttribute("xyz")) {
                    double[] xyz = (double[]) node.getAttribute("xyz");
                    node.setAttribute("xy", xyz[0], xyz[1]); // Utilise les coordonnées x et y
                }
            });


            // Afficher le graphe
            graph.display();
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement du fichier DGS : " + e.getMessage());
        }

        MaxFlow mf = new MaxFlow();
        mf.setCapacityAttribute("cap");
        mf.init(graph);
        mf.setSource(graph.getNode("S"));
        mf.setSink(graph.getNode("P"));
        mf.compute();

        System.out.println(mf.getFlow());
        // Mettre à jour les labels des arêtes avec flux et capacité
        graph.edges().forEach(e -> {
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