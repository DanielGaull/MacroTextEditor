package com.danieljgaull.texteditor.texteditor.modes;

import com.danieljgaull.texteditor.texteditor.data.Variable;
import com.danieljgaull.texteditor.texteditor.expressions.Ast;

import java.util.ArrayList;
import java.util.List;

public class Mode {

    private String name;
    private List<ModeVariable> variables;
    private List<KeyBind> keyBinds;
    private Ast prefixExpr;
    private Ast suffixExpr;

    public Mode(String name) {
        this(name, new ArrayList<>(), new ArrayList<>(), Ast.string(""), Ast.string(""));
    }
    public Mode(String name, List<ModeVariable> variables, List<KeyBind> keyBinds, Ast prefixExpr, Ast suffixExpr) {
        this.name = name;
        this.variables = variables;
        this.keyBinds = keyBinds;
        this.prefixExpr = prefixExpr;
        this.suffixExpr = suffixExpr;
    }

    public String getName() {
        return name;
    }

    public List<ModeVariable> getVariables() {
        return variables;
    }

    public List<KeyBind> getKeyBinds() {
        return keyBinds;
    }

    public Ast getPrefixExpr() {
        return prefixExpr;
    }

    public Ast getSuffixExpr() {
        return suffixExpr;
    }
}
