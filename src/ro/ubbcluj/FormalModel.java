package ro.ubbcluj;

import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Stack;
import java.util.stream.IntStream;

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


    public boolean parseInput() {
        while (!Objects.equals(state.getStatus(), "t") &&
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
                }
            } else if (isProductionString(state.getInputStack().peek())) {
                anotherTry(); //todo double-triple-check for errors
            } else if (state.getPosition() == 1 &&
                    isTopInputStackNonTerminal() &&
                    Objects.equals(state.getInputStack().peek(), grammar.getSyntacticalConstruct())) {
                //todo Andu vrea sa faca incantatii, vrajitorii, magii cu if-uri si construct-uri

            }
        }
        return false;
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

    private boolean isContinueStatus() {
        return Objects.equals(state.getStatus(), "q");
    }

    private boolean isBackStatus() {
        return state.getStatus().equals("r");
    }

    private boolean isTopWorkingStackTerminal() {
        return grammar.getTerminals().contains(state.getWorkingStack().peek());
    }

    private boolean inputStackMatchesInputString() {
        return state.getInputStack().peek().equals(inputString.get(state.getPosition()));
    }

    private boolean isTopInputStackNonTerminal() {
        return grammar.getNonTerminals().contains(state.getInputStack().peek());
    }

    private boolean isTopInputStackTerminal() {
        return grammar.getTerminals().contains(state.getInputStack().peek());
    }

    private boolean isFinalValidState() {
        return state.getInputStack().isEmpty() &&
                state.getPosition() == inputString.size() + 1;
    }


    public void expand() {
        OptionalInt indexOfRule = IntStream.range(0, grammar.getSyntacticalRules().size())
                .filter(index -> Objects.equals(grammar.getSyntacticalRules().get(index).getLeftSide(), state.getInputStack().peek()))
                .findFirst();

        Grammar.SyntacticalRule ruleToBeUsed = grammar.getSyntacticalRules().get(indexOfRule.getAsInt());

        ruleToBeUsed.getRightSide().forEach(symbol -> {
            state.getInputStack().push(symbol);
        });

        state.getInputStack().push("-" + indexOfRule + "-");
    }

    public void advance() {
        state.setPosition(state.getPosition() + 1);
        state.getWorkingStack().push(state.getInputStack().pop());
    }


    public void momentaryInsuccess() {
        state.setStatus("r");
    }

    public void back() {
        state.setPosition(state.getPosition() - 1);
        state.getInputStack().push(state.getWorkingStack().pop());

    }

    public void anotherTry() {
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
