package ro.ubbcluj;

import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Stack;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import ro.ubbcluj.Grammar.SyntacticalRule;

public class FormalModel {
    private Grammar grammar;
    private State state;
    private List<String> inputString;

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

    public boolean parseInput() {
        while (!Objects.equals(state.getStatus(), "t") &&
                !Objects.equals(state.getStatus(), "e")) {

            if (Objects.equals(state.getStatus(), "q")) {
                if (state.getInputStack().isEmpty() &&
                        state.getPosition() == inputString.size() + 1) {
                    success();
                }
                else {
                    if (grammar.getNonTerminals().contains(state.getInputStack().peek())) {
                        expand();
                    } else {
                        if (grammar.getTerminals().contains(state.getInputStack().peek()) && //check if top of input stack is terminal
                                state.getInputStack().peek().equals(inputString.get(state.getPosition()))) { //check if it matches input string
                            advance();
                        } else {
                            momentaryInsuccess();
                        }
                    }
                }
            }
            else {
                if(state.getStatus().equals("r")){
                    if (grammar.getTerminals().contains(state.getWorkingStack().peek())) { //check if top of working stack is terminal
                        back();
                    }
                }
                else{
                    if (isProductionString(state.getInputStack().peek())) {
                        anotherTry(); //todo double-triple-check for errors
                    }
                    else {
                        if(state.getPosition() == 1 &&
                                grammar.getNonTerminals().contains(state.getInputStack().peek()) &&
                                Objects.equals(state.getInputStack().peek(), grammar.getSyntacticalConstruct())){
                            //todo Andu vrea sa faca incantatii, vrajitorii, magii cu if-uri si construct-uri

                        }
                    }
                }
            }

        }
    }


    public void expand() {
        if (Objects.equals(state.getStatus(), "q") && //if everything is ok
                grammar.getNonTerminals().contains(state.getInputStack().peek())) {
            grammar.getSyntacticalRules();

            OptionalInt indexOfRule = IntStream.range(0, grammar.getSyntacticalRules().size())
                    .filter(index -> Objects.equals(grammar.getSyntacticalRules().get(index).getLeftSide(), state.getInputStack().peek()))
                    .findFirst();

            Grammar.SyntacticalRule ruleToBeUsed = grammar.getSyntacticalRules().get(indexOfRule.getAsInt());

            ruleToBeUsed.getRightSide().forEach(symbol -> {
                state.getInputStack().push(symbol);
            });

            state.getInputStack().push("-" + indexOfRule + "-");
        }
    }

    public void advance() {
        if (Objects.equals(state.getStatus(), "q") && //if everything is ok
                grammar.getTerminals().contains(state.getInputStack().peek()) && //check if top of input stack is terminal
                state.getInputStack().peek().equals(inputString.get(state.getPosition()))) { //check input string for validity

            state.setPosition(state.getPosition() + 1);
            state.getWorkingStack().push(state.getInputStack().pop());
        }
    }


    public void momentaryInsuccess() {
        if (Objects.equals(state.getStatus(), "q") && //if everything is ok
                grammar.getTerminals().contains(state.getInputStack().peek()) && //check if top of input stack is terminal
                !state.getInputStack().peek().equals(inputString.get(state.position))) { //check input string for invalidity

            state.setStatus("r");
        }
    }

    public void back() {
        if (Objects.equals(state.getStatus(), "b") //check if we need to "backtrack"
                && grammar.getTerminals().contains(state.getWorkingStack().peek())) { //check if top of working stack is terminal

            state.setPosition(state.getPosition() - 1);
            state.getInputStack().push(state.getWorkingStack().pop());
        }
    }

    public void anotherTry() {
        if (isProductionString(state.getInputStack().peek())) {

            List<SyntacticalRule> syntacticalRules = grammar.getSyntacticalRules();
            int ruleIndex = parseProductionString(state.getWorkingStack().peek());

            syntacticalRules.get(ruleIndex).getRightSide().forEach(symbol -> {
                state.getInputStack().pop();
            });
            state.workingStack.pop();

            IntStream.range(ruleIndex + 1, syntacticalRules.size())
                    .filter(index -> syntacticalRules.get(index).getLeftSide().equals(syntacticalRules.get(ruleIndex).getLeftSide()))
                    .findFirst().ifPresentOrElse(
                    newRuleIndex -> {
                        state.getWorkingStack().push("-" + newRuleIndex + "-");

                        syntacticalRules.get(newRuleIndex).getRightSide().forEach(symbol -> {
                            state.getInputStack().push(symbol);
                        });
                    },
                    () -> {
                        if (state.getPosition() == 1
                                && state.getInputStack().peek().equals(grammar.getSyntacticalConstruct())) {
                            state.setStatus("e");
                        } else {
                            state.getInputStack().push(syntacticalRules.get(ruleIndex).getLeftSide());
                        }
                    }
            );

        }
    }

    private boolean isProductionString(String str) {
        return str.matches("-[0-9]-");
    }

    private int parseProductionString(String str) {
        return Integer.parseInt(str.subSequence(1, state.getWorkingStack().peek().length() - 1).toString());
    }

    public void success() {
        state.setStatus("t");
    }


}
