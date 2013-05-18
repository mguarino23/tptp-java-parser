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
package logic.function.syntax;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import java.util.logging.Level;
import java.util.logging.Logger;
import logic.data.generic.Pair;

import logic.data.fol.Clause;
import logic.data.fol.Literal;
import logic.data.fol.syntax.QuantifiedFormula.Quantifier;
import logic.data.fol.ParserUtils;

import java.util.ArrayList;
import java.util.List;
import logic.data.fol.DataUtil;
import logic.data.program.ProgramParametersDyn;
import logic.data.fol.syntax.AtomicFormula;
import logic.data.fol.syntax.ConnectedFormula;
import logic.data.fol.syntax.Formula;
import logic.data.fol.FunctionSymbol;
import logic.data.fol.syntax.QuantifiedFormula;
import logic.data.fol.Term;
import logic.data.generic.Tree;
import logic.data.fol.VariableSymbol;

public class ParserProcedure {

    public List<Clause> run(Reader input, String path) throws IOException, TokenStreamException {
        TokenStream st = new TokenStream(input);

        String token = st.nextToken();

        List<Clause> included = new ArrayList<Clause>();
        while (token.equals("include")) {
            if (path == null) {
                System.out.print("Please specify include path: ");
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                path = reader.readLine();
            }
            st.skipOnce("(");
            String incl = st.nextToken();
            st.skipOnce(")");
            st.skip(".");
            st.skip("0");
            ParserProcedure parser = new ParserProcedure();
            FileReader fr = new FileReader(path + incl);
            List<Clause> cl = parser.run(fr, path);
            included.addAll(cl);
            token = st.nextToken();

        }
        String t;

        st.push();
        List<Clause> list = new ArrayList<Clause>();
        t = st.nextToken();
        int conjectures = 0;
        while (t.equals("fof") || t.equals("cnf")) {
            if (t.equals("fof")) {
                st.skipOnce("(");
                st.nextToken();// name
                st.skipOnce(",");
                boolean donNotNegate = true;
                String role = st.nextToken();// role
                if (role.equals("conjecture")) {
                    if (conjectures == 0) {
                        donNotNegate = false;
                        ProgramParametersDyn.setHasConjecture(true);
                        conjectures++;
                    } else {
                        //System.err.println("ERROR: more than one conjectures.");
                        System.out.println("SZS status GaveUp for " + ProgramParametersDyn.getFileName());
                        System.exit(0);
                    }
                }
                st.skipOnce(",");
                st.skipOnce("(");// begin fof
                skolemize(formula(st), list, donNotNegate);
                st.skipOnce(")");
                st.skipOnce(")");
                st.skipOnce("0");
                t = st.nextToken();
            } else {
                st.skipOnce("(");
                st.nextToken();// name
                st.skipOnce(",");
                st.nextToken();// role
                st.skipOnce(",");
                st.skipOnce("(");// begin cnf
                t = st.nextToken();
                if (!t.equals(")")) {
                    st.push();
                    Clause clause = new Clause();
                    do {
                        Literal lit = parseInfixLiteral(st);
                        if (lit != null) {
                            clause.add(lit);
                        }
                        t = st.nextToken();
                    } while (t.equals(",") || t.equals("|"));
                    list.add(clause);
                } else {
                    list.add(new Clause());
                } // end of cnf
                st.skipOnce(")");
                //st.skip(".");
                st.skip("0");
                if (!st.moreTokens()) {
                    t = null;

                    break;
                }
                t = st.nextToken();
            }
        }
        if (t != null) {
            // TODO handle unsupported format
// commented for TPTP
            //System.out.println("WARNING: Unsupported format: " + t);
            //System.exit(-1);
        }
        included.addAll(list);
        list = included;
        /* // commented for TPTP
        for (Clause c : list) {
        System.out.println(c);
        } */
        return list;



    }

    private Formula unary(TokenStream st) {
        try {
            Formula f;

            String tk = st.nextToken();
            boolean neg = true;
            while (tk.equals("~")) {
                neg = !neg;
                tk = st.nextToken();
            }
            st.push();
            Formula fst;
            String ntk = st.nextToken();
            if (ntk.equals("?") || ntk.equals("!")) {
                st.push();
                f = quantified(st);
                fst = !neg ? new ConnectedFormula(ConnectedFormula.Connective.NOT, f) : f;
            } else if (ntk.equals("(")) {
                st.push();
                f = parenthesized(st);
                fst = !neg ? new ConnectedFormula(ConnectedFormula.Connective.NOT, f) : f;
            } else if (ntk.equals("$")) {
                tk = st.nextToken();
                if (tk.equals("true")) {
                    fst = AtomicFormula.TRUE;
                } else {
                    fst = AtomicFormula.FALSE;
                }
            } else {
                st.push();
                Literal lit = parseInfixLiteral(st);
                if (!neg) {
                    lit.sign = !lit.sign;
                }
                fst = new AtomicFormula(lit);
            }
            return fst;
        } catch (IOException ex) {
            Logger.getLogger(ParserProcedure.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private Formula formula(TokenStream st) {
        try {
            Formula fst = unary(st);
            String tk = st.nextToken();
            if (tk.equals("&") || tk.equals("|") || tk.equals("=>")) {
                ConnectedFormula.Connective con = null;
                if (tk.equals("&")) {
                    con = ConnectedFormula.Connective.AND;
                } else if (tk.equals("|")) {
                    con = ConnectedFormula.Connective.OR;
                } else if (tk.equals("=>")) {
                    con = ConnectedFormula.Connective.IMP;
                }
                return new ConnectedFormula(con, fst, formula(st));
            } else if (tk.equals("<=")) {
                return new ConnectedFormula(ConnectedFormula.Connective.IMP, formula(st), fst);
            } else if (tk.equals("<=>")) {
                Formula snd = formula(st);
                return new ConnectedFormula(ConnectedFormula.Connective.AND,
                        new ConnectedFormula(ConnectedFormula.Connective.IMP, fst, snd),
                        new ConnectedFormula(ConnectedFormula.Connective.IMP, snd, fst));
            } else if (tk.equals("<~>")) {
                Formula snd = formula(st);
                return new ConnectedFormula(ConnectedFormula.Connective.OR,
                        new ConnectedFormula(ConnectedFormula.Connective.AND, new ConnectedFormula(ConnectedFormula.Connective.NOT, fst), snd),
                        new ConnectedFormula(ConnectedFormula.Connective.AND, new ConnectedFormula(ConnectedFormula.Connective.NOT, snd), fst));
            } else if (tk.equals("~|") || tk.equals("~&")) {
                Formula snd = formula(st);
                return new ConnectedFormula(ConnectedFormula.Connective.NOT,
                        new ConnectedFormula(tk.equals("~|") ? ConnectedFormula.Connective.OR : ConnectedFormula.Connective.AND, snd, fst));
            } else {
                st.push();
                return fst;
            }
        } catch (IOException ex) {
            Logger.getLogger(ParserProcedure.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private Formula parenthesized(TokenStream st) {
        try {
            if (st.nextToken().equals("(")) {
                Formula f = formula(st);
                st.skipOnce(")");
                return f;
            } else {
                st.push();
                return null;
            }
        } catch (TokenStreamException ex) {
            Logger.getLogger(ParserProcedure.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ParserProcedure.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private Literal parseInfixLiteral(TokenStream st) throws IOException {
        ParserUtils.setTentative();
        Literal l = ParserUtils.parseLiteral(st);
        String t = st.nextToken();
        if (t.equals("=")) {
            if (!ProgramParametersDyn.isEquality()) {
                ProgramParametersDyn.setEquality(true);
                ParserUtils.addEqualityPredicate();
            }
            Term x = ParserUtils.demoteToTerm(l);
            Term y = ParserUtils.parse(st);

            l = new Literal(true, ParserUtils.getEqualityPredicate(), new Term[]{x, y});
        } else if (t.equals("!=")) {
            if (!ProgramParametersDyn.isEquality()) {
                ProgramParametersDyn.setEquality(true);
                ParserUtils.addEqualityPredicate();
            }
            Term x = ParserUtils.demoteToTerm(l);
            Term y = ParserUtils.parse(st);
            l = new Literal(false, ParserUtils.getEqualityPredicate(), new Term[]{x, y});
        } else {
            st.push();
        }
        ParserUtils.setNonTentative();
        return l;
    }

    private Formula quantified(TokenStream st) {
        String q;
        VariableSymbol v;
        try {
            q = st.nextToken();
            if (q.equals("!") || q.equals("?")) {
                //universal or existential
                QuantifiedFormula.Quantifier quan = q.equals("!") ? QuantifiedFormula.Quantifier.FORALL : QuantifiedFormula.Quantifier.EXISTS;
                List<VariableSymbol> vars = new ArrayList<VariableSymbol>();
                Formula f;
                st.skipOnce("[");
                while (true) {
                    v = (VariableSymbol) ParserUtils.parse(st).val;
                    vars.add(v);
                    if (st.nextToken().equals("]")) {
                        break;
                    }
                    st.push();
                    st.skipOnce(",");
                }
                st.skipOnce(":");
                f = unary(st);
                return new QuantifiedFormula(quan, vars, f);
            } else {
                st.push();
                return null;
            }
        } catch (TokenStreamException ex) {
            Logger.getLogger(ParserProcedure.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ParserProcedure.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     *
     * @param formula
     * @param list
     * @param negate true do not negate
     *              false negate
     */
    private void skolemize(Formula formula, List<Clause> list, boolean doNotNegate) {
        System.out.println("formula: " + (doNotNegate ? "" : "~") + formula);
        //push neagtion and extract quantifier
        Pair<List<Pair<Quantifier, VariableSymbol>>, Formula> ret = formula.pushNegation(doNotNegate).extractQuantifier();
System.out.println("negated pnf: "+ret);
        //skolemize
        Quantifier q;
        VariableSymbol v;
        List<Term> vl = new ArrayList<Term>();
        Formula f = ret.snd();
        FunctionSymbol func;
        Term term;
        for (Pair<Quantifier, VariableSymbol> vs : ret.fst()) {
            q = vs.fst();
            v = vs.snd();
            if (q == Quantifier.FORALL) {
                vl.add(new Term(v, new Term[0]));
            } else {
                func = ParserUtils.newFunc(vl.size());
                term = new Term(func, vl.toArray(new Term[0]));
                f = f.replace(v, term);
            }
        }

        // to conjuctive form
        ConnectedFormula cf = f.flatten();

        // to clauses
        for (Formula sf : cf.subs) {
            ConnectedFormula df = (ConnectedFormula) sf;
            if (df.subs.length == 1 && df.subs[0].equals(AtomicFormula.TRUE)) {
                continue;
            } else if (df.subs.length == 1 && df.subs[0].equals(AtomicFormula.FALSE)) {
                list.clear();
                list.add(DataUtil.EMPTY_CLAUSE);
                break;
            } else {
                Clause c = new Clause();
                for (Formula ssf : df.subs) {
                    AtomicFormula af = (AtomicFormula) ssf;
                    if(!af.equals(AtomicFormula.FALSE))
                    c.add(af.literal);
                }
                if (!list.contains(c)) {
                    list.add(c);
                    System.out.println("clause: " + c);
                }
            }
        }

        System.out.println("finished");
    }
}
