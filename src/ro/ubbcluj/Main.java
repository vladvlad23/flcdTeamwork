package ro.ubbcluj;

import java.util.Collections;
import java.util.List;

public class Main {

    public static void main(String[] args) {
//        testGrammarProcessing();
        showGrammar();
    }

    public static void showGrammar(){
        Grammar grammar = new Grammar();
        grammar.readFiniteAutomataFromFile("g2.txt");

        FormalModel fm = new FormalModel(grammar, List.of("define", "1", "integer"));
        fm.parseInput();
        fm.createOutput();
        System.out.println(fm.output);
        System.out.println(fm.getOutputAsTable());
    }

    private static List<String> processProgramFromFile() {
        return Collections.EMPTY_LIST;
    }

    public static void testGrammarProcessing(){
        Grammar grammar = new Grammar();
        grammar.readFiniteAutomataFromFile("g2.txt");
        System.out.println(grammar.toString());
    }
}
