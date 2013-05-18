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

import logic.data.generic.Tree;
import logic.data.fol.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import logic.data.generic.Pair;
import logic.data.fol.syntax.QuantifiedFormula.Quantifier;

/**
 * A single (possibly negative) literal formula, not an atom.
 * 
 */
public class AtomicFormula extends Formula {

    public final static PredicateSymbol TRUE_PREDICATE = new PredicateSymbol("@TRUE", 0, ParserUtils.MAX_SYMBOL_ID + 1);
    public final static Literal TRUE_LITERAL = new Literal(true, TRUE_PREDICATE, new Term[0]);
    public final static AtomicFormula TRUE = new AtomicFormula(TRUE_LITERAL) {

        ConnectedFormula flattened = new ConnectedFormula(ConnectedFormula.Connective.AND);

        @Override
        public ConnectedFormula flatten() {
            return flattened;
        }
    };
    public final static AtomicFormula FALSE = new AtomicFormula(TRUE_LITERAL.negate()) {

        ConnectedFormula flattened = new ConnectedFormula(ConnectedFormula.Connective.AND, new ConnectedFormula(ConnectedFormula.Connective.OR));

        @Override
        public ConnectedFormula flatten() {
            return flattened;
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AtomicFormula)) {
            return false;
        }
        final AtomicFormula other = (AtomicFormula) obj;
        if (this.literal != other.literal && (this.literal == null || !this.literal.equals(other.literal))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + (this.literal != null ? this.literal.hashCode() : 0);
        return hash;
    }
    public Literal literal;

    public AtomicFormula(Literal lit) {
        this.literal = lit;
    }

    @Override
    public Formula pushNegation(boolean neg) {
        if (neg) {
            return this;
        } else {
            return new AtomicFormula(literal.negate());
        }
    }

    @Override
    public Pair<List<Pair<Quantifier, VariableSymbol>>, Formula> extractQuantifier() {
        List<Pair<Quantifier, VariableSymbol>> list = new ArrayList<Pair<QuantifiedFormula.Quantifier, VariableSymbol>>();

        return new Pair(list, this);
    }

    @Override
    public Formula replace(VariableSymbol snd, Term nv) {
        Substitution sub = new Substitution();
        sub.put(snd.id, nv);
        return new AtomicFormula(sub.sub(literal));
    }

    @Override
    public ConnectedFormula flatten() {
        return new ConnectedFormula(ConnectedFormula.Connective.AND, new ConnectedFormula(ConnectedFormula.Connective.OR, this));
    }

    @Override
    public String toString() {
        return literal.toString();
    }

    @Override
    public List<VariableSymbol> fv() {
        List<VariableSymbol> fvl = new ArrayList<VariableSymbol>();
        HashSet<VariableSymbol> vset = new HashSet<VariableSymbol>();
        DataUtil.FV(literal, vset);
        fvl.addAll(vset);
        return fvl;
    }
}
