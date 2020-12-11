package ro.ubbcluj;

import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ro.ubbcluj.Grammar.SyntacticalRule;

public class FormalModel {
    private final Grammar grammar;
    public final State state; //todo sue Andu
    private List<String> inputString;
    public ParserOutput output;

    public FormalModel(Grammar grammar, List<String> inputString) {
        this.grammar = grammar;
        state = new State();
        this.inputString = inputString;
    }


    class State {
        private String status;
        private int position;
        private Stack<String> workingStack;
        private Stack<String> inputStack;

        public State() {
            this.status = "q";
            this.position = 0;
            workingStack = new Stack<>();
            inputStack = new Stack<>();
            inputStack.push(grammar.getSyntacticalConstruct());
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public Stack<String> getWorkingStack() {
            return workingStack;
        }

        public void setWorkingStack(Stack<String> workingStack) {
            this.workingStack = workingStack;
        }

        public Stack<String> getInputStack() {
            return inputStack;
        }

        public void setInputStack(Stack<String> inputStack) {
            this.inputStack = inputStack;
        }
    }

    class ParserOutput {
        private int createIndex = 0;

        private Node root;

        class Node {
            private List<Node> children;

            private String information;

            private int number;

            @Override
            public String toString() {
                return getString(1);
            }

            private String getString(int depth) {
                StringBuilder sb = new StringBuilder();

                sb.append(information).append("(").append(number).append(")\n");

                if(children == null) return sb.toString();
                String spaceyBoy = IntStream.range(0, depth).mapToObj(i -> "      ").collect(Collectors.joining(""));

                children.forEach(child ->sb.append(spaceyBoy).append(child.getString(depth + 1)));

                return sb.toString();
            }

            private String getString2(int daddyNumber, int siblingNumber) {
                StringBuilder sb = new StringBuilder();

                if(number == 1)
                    sb.append(number).append(" ").append(information).append(" ").append("-").append(" ").append("-").append("\n");
                else {
                    sb.append(number).append(" ").append(information).append(" ").append(daddyNumber).append(" ");
                    if(siblingNumber != -1)
                        sb.append(siblingNumber);
                    else
                        sb.append("-");
                    sb.append("\n");
                }
                if(children == null) return sb.toString();

                int sib = -1;
                for (int i = 0; i < children.size(); i++) {
                    if(i != 0) sib = children.get(i - 1).number;

                    sb.append(children.get(i).getString2(number, sib));
                }

                return sb.toString();
            }
            public boolean isLeaf() {
                return children.size() == 0;
            }
        }

        public void createTree() {
            if (!state.getStatus().equals("f")) {
                System.out.println("Failed to parse");
                System.exit(0);
            }

            root = buildTree(state.getWorkingStack());
        }

        private Node buildTree(Stack<String> workingStack) {
            String topItem = workingStack.get(createIndex);

            createIndex++;
            if (isProductionString(topItem)) {
                SyntacticalRule production = grammar.getSyntacticalRules().get(parseProductionString(topItem));

                Node newBoy = new Node();
                newBoy.information = production.getLeftSide();
                newBoy.number = createIndex;
                newBoy.children = new ArrayList<>();

                production.getRightSide().forEach(item -> newBoy.children.add(buildTree(workingStack)));

                return newBoy;
            } else {
                Node newBoy = new Node();

                newBoy.information = topItem;
                newBoy.number = createIndex;

                return newBoy;
            }
        }

        private List<String> traverseTree(Node node) {
            if (node.isLeaf()) {
                return new ArrayList<>(Collections.singleton(node.information));
            }
            List<String> childList = new ArrayList<>();
            for (Node child : node.children) {
                childList.addAll(traverseTree(child));
            }
            return childList;
        }

        public List<String> getFrontier() {
            return traverseTree(root);
        }

        @Override
        public String toString() {
            return root.toString();
        }
    }

    public void parseInput() {
        while (!Objects.equals(state.getStatus(), "f") &&
                !Objects.equals(state.getStatus(), "e")) {

            if (isContinueStatus()) {
                if (isFinalValidState()) {
                    success();
                } else if (isTopInputStackNonTerminal()) {
                    expand();
                } else if (isTopInputStackTerminal() && inputStackMatchesInputString()) {
                    advance();
                } else {
                    momentaryInsuccess();
                }
            } else if (isBackStatus()) {
                if (isTopWorkingStackTerminal()) {
                    back();
                } else
                    anotherTry();
            }
        }
    }

    public void createOutput() {
        output = new ParserOutput();
        output.createTree();
    }

    public String getOutputAsTable(){
        return output.root.getString2(0, -1);
    }

    private boolean isContinueStatus() {
        return Objects.equals(state.getStatus(), "q");
    }

    private boolean isBackStatus() {
        return state.getStatus().equals("b");
    }

    private boolean isTopWorkingStackTerminal() {
        if (state.getWorkingStack().empty()) return false;
        return grammar.getTerminals().contains(state.getWorkingStack().peek());
    }

    private boolean inputStackMatchesInputString() {
        if (state.getInputStack().empty()) return false;
        if (state.getPosition() == inputString.size()) return false;
        return state.getInputStack().peek().equals(inputString.get(state.getPosition()));
    }

    private boolean isTopInputStackNonTerminal() {
        if (state.getInputStack().empty()) return false;
        return grammar.getNonTerminals().contains(state.getInputStack().peek());
    }

    private boolean isTopInputStackTerminal() {
        if (state.getInputStack().empty()) return false;
        return grammar.getTerminals().contains(state.getInputStack().peek());
    }

    private boolean isFinalValidState() {
        return state.getInputStack().isEmpty() &&
                state.getPosition() == inputString.size();
    }


    public void expand() {
        List<Grammar.SyntacticalRule> syntacticalRules = grammar.getSyntacticalRules();

        int indexOfRule = IntStream.range(0, syntacticalRules.size())
                .filter(index -> Objects.equals(syntacticalRules.get(index).getLeftSide(), state.getInputStack().peek()))
                .findFirst().getAsInt();


        Grammar.SyntacticalRule ruleToBeUsed = syntacticalRules.get(indexOfRule);

        state.getInputStack().pop();

        ruleToBeUsed.getRightSide().forEach(symbol -> state.getInputStack().push(symbol));

        state.getWorkingStack().push("-" + indexOfRule + "-");
    }

    public void advance() {
        state.setPosition(state.getPosition() + 1);
        state.getWorkingStack().push(state.getInputStack().pop());
    }


    public void momentaryInsuccess() {
        state.setStatus("b");
    }

    public void back() {
        state.setPosition(state.getPosition() - 1);
        state.getInputStack().push(state.getWorkingStack().pop());
    }

    public void anotherTry() {
        List<SyntacticalRule> syntacticalRules = grammar.getSyntacticalRules();

        if (!state.getInputStack().empty()
                && state.getPosition() == 0
                && state.getInputStack().peek().equals(grammar.getSyntacticalConstruct())) {
            state.setStatus("e");
            return;
        }

        int ruleIndex = parseProductionString(state.getWorkingStack().peek());

        syntacticalRules.get(ruleIndex).getRightSide().forEach(symbol -> state.getInputStack().pop());
        state.workingStack.pop();

        IntStream.range(ruleIndex + 1, syntacticalRules.size())
                .filter(index -> syntacticalRules.get(index).getLeftSide().equals(syntacticalRules.get(ruleIndex).getLeftSide()))
                .findFirst()
                .ifPresentOrElse(
                        newRuleIndex -> useNewProduction(syntacticalRules, newRuleIndex),
                        () -> state.getInputStack().push(syntacticalRules.get(ruleIndex).getLeftSide())
                );
    }

    public void success() {
        state.setStatus("f");
    }

    private void useNewProduction(List<SyntacticalRule> syntacticalRules, int newRuleIndex) {
        state.setStatus("q");

        state.getWorkingStack().push("-" + newRuleIndex + "-");

        syntacticalRules.get(newRuleIndex).getRightSide().forEach(symbol -> state.getInputStack().push(symbol));
    }

    private boolean isProductionString(String str) {
        return str.matches("-[0-9]+-");
    }

    private int parseProductionString(String str) {
        return Integer.parseInt(str.subSequence(1, str.length() - 1).toString());
    }

}
