package ro.ubbcluj;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        Grammar grammar = new Grammar();
        grammar.readFiniteAutomataFromFile("g1.txt");
        System.out.println("ceva");
        
        FormalModel fm = new FormalModel(grammar, List.of("a", "a", "c", "b", "c"));
        fm.parseInput();
        fm.createOutput();
        System.out.println(fm.output);
        System.out.println(fm.getOutputAsTable());
    }
}
