package ro.ubbcluj;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        try {
            FileWriter fr1 = new FileWriter("out2.txt");
            FileWriter fr2 = new FileWriter("out1.txt");

            fr1.append(showGrammar());
            fr2.append(showGrammar2());

            fr1.close();
            fr2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String showGrammar() throws IOException {
        Grammar grammar = new Grammar();
        grammar.readFiniteAutomataFromFile("g2.txt");

        List<String> programProcessResult = processProgramFromFile("inputus.txt");
        FormalModel fm = new FormalModel(grammar, programProcessResult);
        fm.parseInput();
        fm.createOutput();
        return fm.output.toString() + " " +fm.getOutputAsTable();
    }

    public static String showGrammar2() throws IOException {
        Grammar grammar = new Grammar();
        grammar.readFiniteAutomataFromFile("g1.txt");

        List<String> programProcessResult = Arrays.asList("a", "a", "c", "b", "c");
        FormalModel fm = new FormalModel(grammar, programProcessResult);
        fm.parseInput();
        fm.createOutput();

        return fm.output.toString() + " " +fm.getOutputAsTable();
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
