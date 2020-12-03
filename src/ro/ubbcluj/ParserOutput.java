package ro.ubbcluj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ParserOutput {

    private Node root;

    class Node {
        private Node leftSibling;
        private Node rightSibling;
        private List<Node> children;

        private String information;

        @Override
        public String toString() {
            return "Node{" +
                    "children=" + children +
                    ", information='" + information + '\'' +
                    '}';
        }

        public boolean isLeaf() {
            return children.size() == 0;
        }
    }

    public List<String> getFrontier() {
        return traverseTree(root);
    }

    private List<String> traverseTree(Node node) { //todo maybe iterative?
        if (node.isLeaf()) {
            return new ArrayList<>(Collections.singleton(node.information));
        }
        List<String> childList = new ArrayList<>();
        for (Node child : node.children) {
            childList.addAll(traverseTree(child));
        }
        return childList;
    }


    @Override
    public String toString() {
        return "ParsingTree{" +
                "root=" + root +
                '}';
    }
}

/*
 * todo
 * Aici vad ca lipsesc cateva functii. Ca sa avem un output gen "good" sau "bad" noi va trebui sa ajungem cu formal modelul
 * in success status, sau cu parser output tree sa avem frontier == inputWord. Partea aia trebuie implementata, si orice altceva
 * mai cere profa la labul asta
 */

