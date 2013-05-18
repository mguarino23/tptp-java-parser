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
package logic.data.fol.syntax;

import logic.data.fol.ParserUtils;
import logic.data.generic.Tree;
import logic.data.fol.*;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import logic.data.generic.Pair;
import logic.data.fol.syntax.QuantifiedFormula.Quantifier;

/**
 *
 * 
 */
public class ConnectedFormula extends Formula {

    public enum Connective {

        AND(2, "&"), OR(2, "|"), NOT(1, "~"), IMP(2, "=>");
        public int arity;
        public String notation;

        Connective(int a, String notation) {
            this.arity = a;
            this.notation = notation;
        }
    }
    public Connective con;
    public Formula[] subs;

    public ConnectedFormula(Connective con, Formula... subs) {
        this.con = con;
        this.subs = subs;
    }

    @Override
    public Formula pushNegation(boolean negation) {
        Formula[] f;
        f = new Formula[subs.length];
        switch (con) {
            case AND:
            case OR:
                for (int i = 0; i < subs.length; i++) {
                    f[i] = subs[i].pushNegation(negation);
                }
                return new ConnectedFormula((!negation && con == Connective.AND) || (negation && con == Connective.OR) ? Connective.OR : Connective.AND, f);
            case IMP:
                f[0] = subs[0].pushNegation(!negation);
                f[1] = subs[1].pushNegation(negation);
                return new ConnectedFormula(negation ? Connective.OR : Connective.AND, f);
            case NOT:
                return subs[0].pushNegation(!negation);
        }
        return null;
    }

    @Override
    public Pair<List<Pair<Quantifier, VariableSymbol>>, Formula> extractQuantifier() {
        List<Pair<Quantifier, VariableSymbol>> list = new ArrayList<Pair<QuantifiedFormula.Quantifier, VariableSymbol>>();
        Formula[] fs = new Formula[subs.length];
        for (int i = 0; i < subs.length; i++) {
            Pair<List<Pair<Quantifier, VariableSymbol>>, Formula> ret1 = subs[i].extractQuantifier();
            Term nv;
            Formula f1 = ret1.snd();
            for (Pair<Quantifier, VariableSymbol> vs : ret1.fst()) {
                final VariableSymbol newVar = ParserUtils.newVar(vs.snd().name);
                nv = new Term(newVar, new Term[0]);
                list.add(new Pair(vs.fst(), newVar));
                f1 = f1.replace(vs.snd(), nv);
            }
            fs[i] = f1;
        }
        return new Pair(list, new ConnectedFormula(con, fs));

    }

    @Override
    public Formula replace(VariableSymbol snd, Term nv) {
        Formula[] subr = new Formula[subs.length];
        for (int i = 0; i < subs.length; i++) {
            subr[i] = subs[i].replace(snd, nv);
        }
        return new ConnectedFormula(con, subr);

    }

    @Override
    public ConnectedFormula flatten() {
        List<Formula> list = new ArrayList<Formula>();
        List<Formula> list3;
        ConnectedFormula f;
        ConnectedFormula f2or;
        ConnectedFormula f3or;
        if (this.con == Connective.AND) {
            for (int i = 0; i < subs.length; i++) {
                f = subs[i].flatten();
                ArrayList<Formula> fSubsCopy = new ArrayList<Formula>(Arrays.asList(((ConnectedFormula) f).subs));
                if (contradictionAnd(list, fSubsCopy)) {
                    System.out.println(AtomicFormula.FALSE.flatten());
                    return AtomicFormula.FALSE.flatten();
                }
                list.addAll(fSubsCopy);
            }
            return new ConnectedFormula(Connective.AND, list.toArray(new Formula[0]));
        } else if (this.con == Connective.OR) {
            if (subs.length == 0) {
                // error
                return null;
            }
            List<Formula> list2 = new ArrayList<Formula>();
            for (int i = 0; i < subs.length; i++) {
                list2.clear();
                f = subs[i].flatten();
                // FALSE = &(|())
                if (f.subs.length == 0) {// TRUE = &()
                    list.clear();
                    return AtomicFormula.TRUE.flatten();
                }
                list3 = Arrays.asList(f.subs);
                if (i == 0) {
                    list2.addAll(list3);
                } else {
                    for (Formula f2 : list) {
                        for (Formula f3 : list3) {
                            f2or = (ConnectedFormula) f2;
                            f3or = (ConnectedFormula) f3;
                            ArrayList<Formula> f3orSubsCopy = new ArrayList<Formula>(Arrays.asList(f3or.subs));
                            ConnectedFormula addF;
                            if (contradictionOr(f2or.subs, f3orSubsCopy)) {
//                                addF = new ConnectedFormula(Connective.OR, AtomicFormula.TRUE);
//                                if(!list2.contains(addF)) {
//                                    list2.add(addF);
//                                }
                                continue;
                            }

                            Formula[] nsubs = new Formula[f2or.subs.length + f3orSubsCopy.size()];
                            System.arraycopy(f2or.subs, 0, nsubs, 0, f2or.subs.length);
                            System.arraycopy(f3orSubsCopy.toArray(new Formula[0]), 0, nsubs, f2or.subs.length, f3orSubsCopy.size());
                            addF = new ConnectedFormula(Connective.OR, nsubs);
                            if (!list2.contains(addF)) {
                                list2.add(addF);
                            }
                        }
                    }
                }
                list3 = list;
                list = list2;
                list2 = list3;
            }

            return new ConnectedFormula(Connective.AND, list.toArray(new Formula[0]));
        } else {
            //error;
            return null;
        }
    }

    public static boolean contradictionOr(Formula[] list, List<Formula> list2) {
        for (Formula f : list) {
            AtomicFormula af = (AtomicFormula) f;
            Iterator<Formula> itr = list2.iterator();
            while (itr.hasNext()) {
                AtomicFormula af2 = (AtomicFormula) itr.next();
                if (af.literal.equals(af2.literal.negate())) {
                    return true;
                } else if (af.literal.equals(af2)) {
                    itr.remove();
                }
            }
        }
        return false;

    }

    public static boolean contradictionAnd(List<Formula> list, List<Formula> list2) {
        for (Formula f : list) {
            ConnectedFormula cf = (ConnectedFormula) f;
            if (cf.subs.length == 1) {
                AtomicFormula af = (AtomicFormula) cf.subs[0];
                Iterator<Formula> itr = list2.iterator();
                while (itr.hasNext()) {
                    ConnectedFormula cf2 = (ConnectedFormula) itr.next();
                    if (cf2.subs.length == 1) {
                        AtomicFormula af2 = (AtomicFormula) cf2.subs[0];
                        if (af.literal.equals(af2.literal.negate())) {
                            return true;
                        } else if (af.literal.equals(af2)) {
                            itr.remove();
                        }
                    }
                }
            }
        }
        return false;

    }

    @Override
    public String toString() {
        switch (con) {
            case AND:
            case OR:
                String str = "";
                for (int i = 0; i < subs.length; i++) {
                    if (i != 0) {
                        str += con.notation;
                    }
                    str += subs[i];
                }
                return "(" + str + ")";
            case IMP:
                return "(" + subs[0] + con.notation + subs[1] + ")";
            case NOT:
                return "(" + con.notation + subs[0] + ")";
        }
        return null;
    }

    @Override
    public List<VariableSymbol> fv() {
        List<VariableSymbol> l, ll;
        l = this.subs[0].fv();
        for (int i = 1; i < this.subs.length; i++) {
            ll = this.subs[i].fv();
            ll.removeAll(l);
            l.addAll(ll);
        }
        return l;
    }
}
