package ro.ubbcluj;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Grammar {

    private Set<String> nonTerminals;
    private Set<String> terminals;
    private Set<SyntacticalRule> syntacticalRules;
    private String syntacticalConstruct;


    class SyntacticalRule {
        private String leftSide;
        private List<String> rightSide;

        public SyntacticalRule() {
        }

        public String getLeftSide() {
            return leftSide;
        }

        public void setLeftSide(String leftSide) {
            this.leftSide = leftSide;
        }

        public List<String> getRightSide() {
            return rightSide;
        }

        public void setRightSide(List<String> rightSide) {
            this.rightSide = rightSide;
        }
    }

    void readFiniteAutomataFromFile(String fileName) {
        try {
            List<String> fileList = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
            nonTerminals = Arrays.stream(fileList.get(0).split(" ")).collect(Collectors.toSet());
            terminals = Arrays.stream(fileList.get(1).split(" ")).collect(Collectors.toSet());
            syntacticalRules = Arrays.stream(fileList.get(2).split(" ")).map(transitionString -> {
                String[] ruleSplit = transitionString.split("-");
                SyntacticalRule syntacticalRule = new SyntacticalRule();
                syntacticalRule.setLeftSide(ruleSplit[0]);
                syntacticalRule.setRightSide(ruleSplit[1].codePoints()
                        .mapToObj(c -> String.valueOf((char) c)).collect(Collectors.toList()));
                return syntacticalRule;
            }).collect(Collectors.toSet());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getNonTerminals() {
        return nonTerminals;
    }

    public void setNonTerminals(Set<String> nonTerminals) {
        this.nonTerminals = nonTerminals;
    }

    public Set<String> getTerminals() {
        return terminals;
    }

    public void setTerminals(Set<String> terminals) {
        this.terminals = terminals;
    }

    public Set<SyntacticalRule> getSyntacticalRules() {
        return syntacticalRules;
    }

    public void setSyntacticalRules(Set<SyntacticalRule> syntacticalRules) {
        this.syntacticalRules = syntacticalRules;
    }

    public String getSyntacticalConstruct() {
        return syntacticalConstruct;
    }

    public void setSyntacticalConstruct(String syntacticalConstruct) {
        this.syntacticalConstruct = syntacticalConstruct;
    }
}
