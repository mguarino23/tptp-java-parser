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

import java.util.*;
import logic.data.program.ProgramParameters;

public class Substitution {

    Map<Integer, Term> varMap;// Term[] varMap = null;

    public Substitution() {
        varMap = new TreeMap<Integer, Term>();//Term[TermParser.getVid() - TermParser.VAR_START_ID];
    }


    public Literal sub(Literal t) {

        Term[] sts = new Term[t.subtrees.length];
        for (int i = 0; i < sts.length; i++) {
            sts[i] = sub((Term) t.subtrees[i]);
        }
        return new Literal(t.sign, (PredicateSymbol) t.val, sts);

    }


    public Term sub(Term t) {
        if (t.val instanceof VariableSymbol) {
            if (this.containsKey(t.val.id)) {
                return this.get(t.val.id);
            } else {
                return t;
            }
        } else {
            Term[] sts = new Term[t.subtrees.length];
            for (int i = 0; i < sts.length; i++) {
                sts[i] = sub((Term) t.subtrees[i]);
            }
            return new Term(t.val, sts);
        }
    }





    public final boolean containsKey(int val) {
        return varMap.containsKey(val);//this.varMap[val.id - TermParser.VAR_START_ID] != null;
    }

    public final Term get(int val) {
        return varMap.get(val);//this.varMap[val.id - TermParser.VAR_START_ID];
    }

    public final void put(int val, Term t) {
        Term temp = this.varMap.get(val);
        this.varMap.put(val, t);
    }


    public final void clear() {
        varMap.clear();
    }

}
