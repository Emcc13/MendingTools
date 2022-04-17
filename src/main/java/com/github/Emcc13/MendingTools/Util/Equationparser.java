package com.github.Emcc13.MendingTools.Util;

import java.util.Map;

public class Equationparser {
    private int pos = -1, ch;
    private String str;

    private Map<String, Double> variables;

    public Equationparser(String str) {
        this.str = str;
    }

    public Equationparser(String str, Map<String, Double> variables) {
        this.str = str;
        this.variables = variables;
    }

    void nextChar() {
        ch = (++pos < str.length()) ? str.charAt(pos) : -1;
    }

    boolean eat(int charToEat) {
        while (ch == ' ') nextChar();
        if (ch == charToEat) {
            nextChar();
            return true;
        }
        return false;
    }

    double parse() {
        nextChar();
        double x = parseExpression();
        if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
        return x;
    }

    // Grammar:
    // expression = term | expression `+` term | expression `-` term
    // term = factor | term `*` factor | term `/` factor
    // factor = `+` factor | `-` factor | `(` expression `)`
    //        | number | functionName factor | factor `^` factor

    double parseExpression() {
        double x = parseTerm();
        for (; ; ) {
            if (eat('+')) x += parseTerm(); // addition
            else if (eat('-')) x -= parseTerm(); // subtraction
            else return x;
        }
    }

    double parseTerm() {
        double x = parseFactor();
        for (; ; ) {
            if (eat('*')) x *= parseFactor(); // multiplication
            else if (eat('/')) x /= parseFactor(); // division
            else return x;
        }
    }

    double parseFactor() {
        if (eat('+')) return parseFactor(); // unary plus
        if (eat('-')) return -parseFactor(); // unary minus

        double x;
        int startPos = this.pos;
        if (eat('(')) { // parentheses
            x = parseExpression();
            eat(')');
        } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
            while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
            x = Double.parseDouble(str.substring(startPos, this.pos));
        } else if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch=='%' || ch=='#') { // functions
            while ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch=='%' || ch=='#') nextChar();
            String func = str.substring(startPos, this.pos);
            switch (func) {
                case "sqrt":
                    x = parseFactor();
                    x = Math.sqrt(x);
                    break;
                case "sin":
                    x = parseFactor();
                    x = Math.sin(x);
                    break;
                case "cos":
                    x = parseFactor();
                    x = Math.cos(x);
                    break;
                case "tan":
                    x = parseFactor();
                    x = Math.tan(x);
                    break;
                case "sign":
                    x = parseFactor();
                    x = x > 0.0 ? 1.0 : 0.0;
                    break;
                default:
                    Double tmpX = variables.get(func);
                    if (tmpX != null)
                        x = tmpX;
                    else
                        x = 0;
            }
        } else {
            throw new RuntimeException("Unexpected: " + (char) ch);
        }

        if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

        return x;
    }

    public static double eval(final String str) {
        return (new Equationparser(str)).parse();
    }

    public static double eval(final String str, Map<String, Double> variables) {
        return (new Equationparser(str, variables)).parse();
    }
}
