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

public class Literal {

    protected static final int ID_NEG = -1;
    protected static final int ID_POS = -2;
    public boolean sign;

    public int rank;
    public Literal orig; // the linked list of original literals before replacing variables with the special constant symbol

    public PredicateSymbol val;
    public Term[] subtrees;

    public Literal(boolean b, PredicateSymbol symbol, Term[] name) {
        val = symbol;
        subtrees = name;
        this.sign = b;
    }

    private Literal(boolean b, PredicateSymbol predicateSymbol, Term[] subtrees, int[] nkey) {
        this(b, predicateSymbol, subtrees);
        this.key = nkey;
    }

    public int atomSize() {
        return this.key.length - 1;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = Arrays.hashCode(getKey());
        result = PRIME * result + (sign ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Literal)) {
            return false;
        }
        final Literal other = (Literal) obj;
        if (!Arrays.equals(this.getKey(), other.getKey())) {
            return false;
        }
        return true;
    }
    public int[] key = null;

    public int[] getKey() {
        if (key != null) {
            return key;
        }
        int[][] keys = new int[this.subtrees.length][];
        int size = 1;

        for (int i = 0; i < this.subtrees.length; i++) {
            keys[i] = Term.getKey((Term) this.subtrees[i]);
            size += keys[i].length;
        }

        size++;

        int[] akey = new int[size];

        int k = 0;
        akey[k++] = this.val.id;
        for (int i = 0; i < subtrees.length; i++) {
            System.arraycopy(keys[i], 0, akey, k, keys[i].length);
            k += keys[i].length;
        }
        if (this.sign == false) {
            akey[k] = ID_NEG;
        } else {
            akey[k] = Literal.ID_POS;
        }
        key = akey;
        return akey;

    }

    public int size() {
        return getKey().length;
    }

//    public int compareTo(Literal literal) {
//        int d = this.size() - literal.size();
//        if (d != 0) {
//            return d;
//        } else {
//            int[] a = this.getKey();
//            int[] b = literal.getKey();
//            int i = 0;
//            int n = a.length;
//            do {
//                d = a[i] - b[i];
//                i++;
//            } while (i < n && d == 0);
//        }
//        return d;
//    }
    public boolean complementary(Literal literal) {
        if (this.sign == literal.sign) {
            return false;
        }
        int d = this.size() - literal.size();
        if (d != 0) {
            return false;
        } else {
            int[] a = this.getKey();
            int[] b = literal.getKey();
            int i = 0;

            int n = a.length;

            do {
                if (a[i] != b[i]) {
                    return false;
                }
                i++;
            } while (i < n - 1);
        }
        return true;
    }
    public Literal negated = null;

    public Literal negate() {
        if (negated == null) {
            int[] nkey = Arrays.copyOf(getKey(), getKey().length);
            nkey[nkey.length - 1] = (key[key.length - 1] != Literal.ID_POS) ? Literal.ID_POS : Literal.ID_NEG;
            negated = new Literal(!this.sign, (PredicateSymbol) this.val, this.subtrees, nkey);
            negated.rank = rank;
        }
        return negated;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(this.val);
        if (subtrees.length > 0) {
            s.append("(");
            for (Term sub : subtrees) {
                s.append(sub.toString());
                s.append(",");
            }
            s.deleteCharAt(s.length() - 1);
            s.append(")");
        }

    return (this.sign ? "" : "~") + s.toString();
    }
}
