package ro.ubbcluj;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
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

        List<String> programProcessResult = processProgramFromFile("inputus.txt");
        FormalModel fm = new FormalModel(grammar, programProcessResult);
        fm.parseInput();
        fm.createOutput();
        System.out.println(fm.output);
        System.out.println(fm.getOutputAsTable());
    }

    private static List<String> processProgramFromFile(String fileName) {
        List<String> fileList = null;
        try {
            fileList = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);

            StringBuilder builder = new StringBuilder();
            fileList.forEach(line->{builder.append(line).append(" ");});
            String finalString = builder.toString().replaceAll("  +", " ");
            return Arrays.asList(finalString.split(" "));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void testGrammarProcessing(){
        Grammar grammar = new Grammar();
        grammar.readFiniteAutomataFromFile("g2.txt");
        System.out.println(grammar.toString());
    }
}
