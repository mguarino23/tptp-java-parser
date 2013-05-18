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

public class DataUtil {

    public static List<String> variables(List<Clause> cl) {
        List<String> vname = new ArrayList<String>();
        for (Clause clause : cl) {
            for (Literal l : clause) {
                variables(l, vname);
            }

        }
        return vname;
    }

    public static List<String> variables(Clause clause) {
        List<String> vname = new ArrayList<String>();
        for (Literal l : clause) {
            variables(l, vname);
        }

        return vname;
    }
    private static void variables(Literal l, List<String> vname) {
        int[] keys = l.getKey();
        for (int k : keys) {
            if (ParserUtils.isVariable(k)) {
                Symbol v = ParserUtils.sTable[k];
                if (!vname.contains(v.name)) {
                    vname.add(v.name);
                }

            }

        }
    }
    public static Clause newClause() {
        return new Clause();
    }
    public static final Clause EMPTY_CLAUSE = DataUtil.newClause();

        public static List<VariableSymbol> FV(List<Clause> cl) {
        List<String> vname = variables(cl);
        List<VariableSymbol> vs = new ArrayList<VariableSymbol>();
        for (String v : vname) {
            vs.add(ParserUtils.vsmap.get(v));
        }
        return vs;
    }
    public static List<VariableSymbol> FV(Iterable<Literal> c) {
        List<VariableSymbol> list = new ArrayList<VariableSymbol>();
        Set<VariableSymbol> set = new HashSet<VariableSymbol>();
        for (Literal l : c) {
            FV(l, list, set);
        }

        return list;
    }

    public static void FV(Literal c, Set<VariableSymbol> vset) {
        int[] keys = c.getKey();
        for (int k : keys) {
            if (ParserUtils.isVariable(k)) {
                vset.add((VariableSymbol) ParserUtils.sTable[k]);

            }

        }
    }

    public static void FV(Literal c, List<VariableSymbol> vset) {
        int[] keys = c.getKey();
        for (int k : keys) {
            if (ParserUtils.isVariable(k)) {
                final VariableSymbol vs = (VariableSymbol) ParserUtils.sTable[k];
                if (!vset.contains(vs)) {
                    vset.add(vs);
                }
            }

        }
    }

    public static void FV(Literal c, List<VariableSymbol> vlist, Set<VariableSymbol> vset) {
        int[] keys = c.getKey();
        for (int k : keys) {
            if (ParserUtils.isVariable(k)) {
                final VariableSymbol v = (VariableSymbol) ParserUtils.sTable[k];
                if (!vset.contains(v)) {
                    vset.add(v);
                    vlist.add(v);
                }
            }

        }
    }

}
