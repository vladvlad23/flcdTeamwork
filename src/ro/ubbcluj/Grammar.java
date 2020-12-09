package ro.ubbcluj;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Grammar {

    public boolean toString;
    private Set<String> nonTerminals;
    private Set<String> terminals;
    private List<SyntacticalRule> syntacticalRules;
    private String syntacticalConstruct;


    class SyntacticalRule {
        private String leftSide;
        private List<String> rightSide;

        public SyntacticalRule() {
            //ThisCommentIsNeededForMe
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

            syntacticalConstruct = fileList.get(2);

            syntacticalRules = new ArrayList<>();

            for (int i = 3; i < fileList.size(); i++) {

                String[] ruleSides = fileList.get(i).split("->");

                syntacticalRules.addAll(
                Arrays.stream(ruleSides[1].split("\\|")).map(rightSideOfRule ->{
                    SyntacticalRule rule = new SyntacticalRule();
                    rule.setLeftSide(ruleSides[0]);
                    rule.setRightSide(Arrays.stream(rightSideOfRule.split(" ")).collect(Collectors.toList()));
                    Collections.reverse(rule.getRightSide());
                    return rule;
                }).collect(Collectors.toList()));
            }


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

    public List<SyntacticalRule> getSyntacticalRules() {
        return syntacticalRules;
    }

    public void setSyntacticalRules(List<SyntacticalRule> syntacticalRules) {
        this.syntacticalRules = syntacticalRules;
    }

    public String getSyntacticalConstruct() {
        return syntacticalConstruct;
    }

    public void setSyntacticalConstruct(String syntacticalConstruct) {
        this.syntacticalConstruct = syntacticalConstruct;
    }

    @Override
    public String toString() {
        return "Grammar{" +
                "toString=" + toString +
                ", nonTerminals=" + nonTerminals +
                ", terminals=" + terminals +
                ", syntacticalRules=" + syntacticalRules +
                ", syntacticalConstruct='" + syntacticalConstruct + '\'' +
                '}';
    }
}
