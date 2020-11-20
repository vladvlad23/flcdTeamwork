package ro.ubbcluj;

public class Main {

    public static void main(String[] args) {
        Grammar grammar = new Grammar();
        grammar.readFiniteAutomataFromFile("g1.txt");
        System.out.println("ceva");
    }
}
