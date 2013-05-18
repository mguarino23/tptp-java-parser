/*
 Copyright 2006, 2007, 2008, 2009 Hao Xu
 xuh@cs.unc.edu
 
 This file is part of OSHL-S.

 OSHL-S is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 OSHL-S is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Foobar; if not, write to the Free Software
 Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package logic.data.fol;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import logic.function.syntax.TokenStream;

import logic.data.program.ProgramParametersDyn;

public class ParserUtils {

    public static final int EQUALITY_PREDICATE_ID = 512*1024;
    public static final int SPECIAL_CONSTANT_ID = 512 * 1024-1;
    public static final int MAX_SYMBOL_ID = 1024*1024;
    public static final int VAR_START_ID = 512*1024 + 1;
    public static Map<String, FunctionSymbol> fsmap = new HashMap<String, FunctionSymbol>();
    public static Map<String, VariableSymbol> vsmap = new HashMap<String, VariableSymbol>();
    public static Map<String, PredicateSymbol> psmap = new HashMap<String, PredicateSymbol>();
    private static int id = 0, vid = VAR_START_ID;
    {
        sTable[SPECIAL_CONSTANT_ID] = new FunctionSymbol("?", 0, SPECIAL_CONSTANT_ID);
    }

    public static int getVid() {
        return vid;
    }
    private static boolean tentative;
    private static PredicateSymbol tentativePredicate;
    public static Symbol[] sTable = new Symbol[MAX_SYMBOL_ID + 1];

    public static void setTentative() {
        ParserUtils.tentative = true;
    }

    public static void clearTentative() {
        if (tentativePredicate != null) {
            psmap.remove(tentativePredicate.name);
            sTable[tentativePredicate.id] = null;
        }
    }

    public static void setNonTentative() {
        tentative = false;
        tentativePredicate = null;
    }

    public static void addFunctionSymbol(String f, int arity) {
        FunctionSymbol fs = new FunctionSymbol(f, arity, id*128+arity);
        id++;
        fsmap.put(f, fs);
        ProgramParametersDyn.addFunctionSymbol(fs);
        sTable[fs.id] = fs;

    }

    public static void addVariableSymbol(String v) {
        VariableSymbol vs = new VariableSymbol(v, vid++);
        vsmap.put(v, vs);
        sTable[vs.id] = vs;

    }

    public static void addPredicateSymbol(String v, int arity) {
        PredicateSymbol ps = new PredicateSymbol(v, arity, id*128+arity);
        id++;
        psmap.put(v, ps);
        sTable[ps.id] = ps;
        if (tentative) {
            tentativePredicate = ps;
        }

    }
    
    public static VariableSymbol newVar(String v) {
        String vn = v.contains("@")?v.substring(0, v.indexOf("@")):v;
        vn+="@"+(vid - VAR_START_ID);
        addVariableSymbol(vn);
        return vsmap.get(vn);
    }
    public static FunctionSymbol newFunc(int arity) {
        String vn = "func@"+id;
        addFunctionSymbol(vn, arity);
        return fsmap.get(vn);
    }

    public static Term parse(String s) {
        TokenStream ts = new TokenStream(s);
        try {
            return parse(ts);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Literal parseLiteral(String s) {
        TokenStream ts = new TokenStream(s);
        try {
            return parseLiteral(ts);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Term parse(TokenStream ts) throws IOException {
        String v = ts.nextToken();

        if (vsmap.containsKey(v) || Character.isUpperCase(v.charAt(0))) {
            if (!vsmap.containsKey(v)) {
                addVariableSymbol(v);
            }
            return new Term(vsmap.get(v), new Term[0]);
        } else {
            String token = ts.nextToken();
            List<Term> tl = new ArrayList<Term>();
            int ari = 0;
            if (token.equals("(")) {
                token = ts.nextToken();
                if (!token.equals(")")) {
                    ts.push();
                    do {
                        tl.add(parse(ts));
                        ari++;
                        token = ts.nextToken();
                    } while (token.equals(","));
                }
                if (!token.equals(")")) {
                //error
                }
            } else {
                ts.push();
            }


            if (!fsmap.containsKey(v)) {
                addFunctionSymbol(v, ari);
            }
            FunctionSymbol fs = fsmap.get(v);
            if (fs.arity != ari) {
            //error
            }
            Term[] subtrees = tl.toArray(new Term[0]);
            return new Term(fs, subtrees);

        }
    }

    public static Literal parseLiteral(TokenStream ts) throws IOException {
        String v = ts.nextToken();
        boolean sign;
        if (v.equals("~")) {
            sign = false;
        } else {
            sign = true;
        }
        if (!sign) {
            v = ts.nextToken();
        }
        List<Term> tl = new ArrayList<Term>();
        int ari = 0;
        String token = ts.nextToken();
        if (token.equals("(")) {
            token = ts.nextToken();
            if (!token.equals(")")) {
                ts.push();
                do {
                    tl.add(parse(ts));
                    ari++;
                    token = ts.nextToken();
                } while (token.equals(","));
            }
            if (!token.equals(")")) {
            //error
            }
        } else {
            ts.push();
        }
        if (!psmap.containsKey(v)) {
            addPredicateSymbol(v, ari);
        }
        PredicateSymbol ps = psmap.get(v);
        Term[] subtrees = tl.toArray(new Term[0]);
        return new Literal(sign, ps, subtrees);

    }

    public static Term demoteToTerm(Literal l) {
        PredicateSymbol ps = (PredicateSymbol) l.val;
        String name = ps.name;
        Symbol f;
        if (Character.isUpperCase(name.charAt(0))) {
            if (!vsmap.containsKey(name)) {
                addVariableSymbol(name);
            }

            f = vsmap.get(name);
        } else {
            if (!fsmap.containsKey(name)) {
                addFunctionSymbol(name, ps.arity);
            }

            f = fsmap.get(name);
        }
        clearTentative();

        Term t = new Term(f, l.subtrees);
        return t;


    }

    public static void addEqualityPredicate() {
        psmap.put("=", new PredicateSymbol("=", 2, EQUALITY_PREDICATE_ID));
        sTable[EQUALITY_PREDICATE_ID] = getEqualityPredicate();
    }

    public static PredicateSymbol getEqualityPredicate() {
        return psmap.get("=");
    }

    public static boolean isVariable(int id) {
        return id > EQUALITY_PREDICATE_ID;
    }
}
