package com.danieljgaull.texteditor.texteditor.modes;

import com.danieljgaull.texteditor.texteditor.data.Variable;

import java.util.ArrayList;
import java.util.List;

public class Mode {

    private String name;
    private List<Variable> variables;
    private String prefixExpr;
    private String suffixExpr;

    public Mode(String name) {
        this(name, new ArrayList<>(), "", "");
    }
    public Mode(String name, List<Variable> variables, String prefixExpr, String suffixExpr) {
        this.name = name;
        this.variables = variables;
        this.prefixExpr = prefixExpr;
        this.suffixExpr = suffixExpr;
    }

    public String getName() {
        return name;
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public String getPrefixExpr() {
        return prefixExpr;
    }

    public String getSuffixExpr() {
        return suffixExpr;
    }

}
