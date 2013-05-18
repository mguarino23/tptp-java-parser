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

import logic.data.fol.*;
import java.util.ArrayList;
import java.util.List;
import logic.data.generic.Pair;

/**
 *
 * 
 */
public class QuantifiedFormula extends Formula {
    public enum Quantifier {
        FORALL, EXISTS;
    }
    public Quantifier q;
    public List<VariableSymbol> vars;
    public Formula f;

    public QuantifiedFormula(Quantifier q, List<VariableSymbol> vars, Formula f) {
        this.q = q;
        this.vars = vars;
        this.f = f;
    }

    @Override
    public Formula pushNegation(boolean negation) {
        Formula nf = f.pushNegation(negation);
        if(negation) {
            return new QuantifiedFormula(q, vars, nf);
        } else {
            return new QuantifiedFormula(q==Quantifier.FORALL?Quantifier.EXISTS:Quantifier.FORALL, vars, nf);
        }
    }

    @Override
    public Pair<List<Pair<Quantifier, VariableSymbol>>, Formula> extractQuantifier() {
        List<Pair<Quantifier, VariableSymbol>> list = new ArrayList<Pair<QuantifiedFormula.Quantifier, VariableSymbol>>();
        Pair<List<Pair<Quantifier, VariableSymbol>>, Formula> ret = f.extractQuantifier();
        for(VariableSymbol v : vars) {
            list.add(new Pair(q,v));
        }
        list.addAll(ret.fst());
        return new Pair(list, ret.snd());
    }

    @Override
    public Formula replace(VariableSymbol snd, Term nv) {
        for(VariableSymbol vs : vars) {
            if(vs.equals(snd)) {
                return this;
            }
        }
        return new QuantifiedFormula(q, vars, f.replace(snd, nv));
    }

    @Override
    public ConnectedFormula flatten() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        return(q == Quantifier.FORALL?"!":"?")+vars+":"+f;
    }

    @Override
    public List<VariableSymbol> fv() {
        List<VariableSymbol> l = this.f.fv();
        l.removeAll(this.vars);
        return l;
    }

}
