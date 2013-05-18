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

public class Term {

    int[] key;

    public Symbol val;
    public Term[] subtrees;
    
    public Term(Symbol val, Term[] subtrees) {
        this.val = val;
        this.subtrees= subtrees;
    }

    public static final int[] getKey(Term t) {
        if (t.key != null) {
            return t.key;
        }
        int[][] keys = new int[t.subtrees.length][];
        int size = 1;

        for (int i = 0; i < t.subtrees.length; i++) {
            keys[i] = getKey((Term) t.subtrees[i]);
            size += keys[i].length;
        }

        int[] akey = new int[size];

        int k = 0;
        akey[k++] = t.val.id;
        for (int i = 0; i < t.subtrees.length; i++) {
            System.arraycopy(keys[i], 0, akey, k, keys[i].length);
            k += keys[i].length;
        }

        t.key = akey;

        return akey;

    }

    public int[] getKey() {
        return getKey(this);
    }
    int size = -1;

    public int size() {
        if (size == -1) {
            size = getKey().length;
        }
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Term)) {
            return false;
        }
        final Term other = (Term) obj;
        if (!Arrays.equals(this.getKey(), other.getKey())) {
            return false;
        }
        return true;
    }
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

    return s.toString();
    }
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Arrays.hashCode(this.getKey());
        return hash;
    }
}
