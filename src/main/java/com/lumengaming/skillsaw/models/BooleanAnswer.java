/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.models;

/**
 *
 * @author prota
 */
public enum BooleanAnswer {

    Yes("1", "yes", "true", "yep", "yeah", "accept", "allow"),
    No("0", "no", "false", "nope", "deny", "reject", "decline", "refuse"),
    Ask("?", "maybe", "null", "ask", "prompt", "reset", "unknown", "off"),;

    private final String[] aliases;

    BooleanAnswer(String... aliases) {
        this.aliases = aliases;
    }

    public String getShortLabel() {
        return aliases[0];
    }

    public boolean isMatch(String alias) {
        for (String a : aliases) {
            if (a.equalsIgnoreCase(alias)) {
                return true;
            }
        }
        return false;
    }

    public static BooleanAnswer fromArg(String s) {
        if (s == null) {
            return BooleanAnswer.Ask;
        }
        for (BooleanAnswer e : BooleanAnswer.values()) {
            if (e.isMatch(s)) {
                return e;
            }
        }
        return BooleanAnswer.Ask;
    }
}
