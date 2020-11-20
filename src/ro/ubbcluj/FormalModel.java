package ro.ubbcluj;

import java.util.List;
import java.util.Objects;
import java.util.Stack;

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
            this.position = 1;
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


    public void expand() {
        if (grammar.getNonTerminals().contains(state.getInputStack().peek())) {
            grammar.getSyntacticalRules();
            Grammar.SyntacticalRule ruleToBeUsed = grammar.getSyntacticalRules()
                    .stream()
                    .filter(syntacticalRule -> Objects.equals(syntacticalRule.getLeftSide(), state.getInputStack().peek()))
                    .findFirst().orElse(null);

            state.getWorkingStack().push("1"); //delimiter
            ruleToBeUsed.getRightSide().forEach(symbol -> {
                state.getWorkingStack().push(symbol);
            });

            state.getInputStack().push("1"); //delimiter
            state.getInputStack().push(ruleToBeUsed.getLeftSide());
        }
    }

    public void advance() {
        if (grammar.getTerminals().contains(state.getInputStack().peek())) {
            state.setPosition(state.getPosition() + 1);
            state.getWorkingStack().push();
        }
    }


    public void monentaryInsuccess() {
        if (grammar.getTerminals().contains(state.getInputStack().peek())) {
            state.setStatus("b");
        }
    }

    public void back() {
        if (Objects.equals(state.getStatus(), "b")) {
            state.setPosition(state.getPosition() - 1);
            state.getInputStack().push();
        }
    }

    public void anotherTry() {
        if (grammar.getNonTerminals().contains(state.getInputStack().peek())) {

        }
    }

    public void success() {
        state.setStatus("f");
    }


}
