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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import logic.data.generic.Pair;
import logic.data.program.ProgramParameters;

public class Clause extends java.util.ArrayList<Literal> implements ClauseInterface {

    private HashSet<Literal> lset = new HashSet<Literal>();
    public int rank = 0;

    public Clause(int rank) {
        this.rank = rank;
    }

    public Clause() {
    }

    public int getRank() {
        return rank;
    }

    /** this method should be called only after the clause is fully constructed. */
    public void setRank(int rank) {
        this.rank = rank;
        for (Literal l : this) {
            l.rank = rank;
        }
    }

    @Override
    public void computeRank() {
        int maxRank = 0;
        for (int i = 0; i < this.size(); i++) {
            final int rank1 = get(i).rank;
            if (maxRank < rank1) {
                maxRank = rank1;
            }
        }
        rank = maxRank + 1;
    }

    @Override
    public void setAllLiteralRank() {
        for (int i = 0; i < this.size(); i++) {
            get(i).rank = rank;
        }
    }


    @Override
    public void addAll(ClauseInterface c) {
        for (Literal l : c) {
            add(l);
        }
    }

    @Override
    public boolean add(Literal e) {
        if (!lset.contains(e)) {
            lset.add(e);
            return super.add(e);
        } else {
            return false;
        }
    }

    @Override
    public void addAllTo(Collection<Literal> c) {
        c.addAll(this);
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        int size = this.size();
        if (!(obj instanceof ClauseInterface) || ((ClauseInterface) obj).size() != size) {
            return false;
        }
        final Iterator<Literal> itr = ((ClauseInterface) obj).iterator();
        final Iterator<Literal> itr2 = this.iterator();
        for (int i = 0; i < size; i++) {
            if (!itr2.next().equals(itr.next())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        final Iterator<Literal> itr2 = this.iterator();
        int hash = 5, size = this.size();
        hash = 37 * hash;
        for (int i = 0; i < size; i++) {
            hash += itr2.next().hashCode();
        }
        hash = 37 * hash + size;
        return hash;
    }

    @Override
    public void addAllFrom(Collection<Literal> c) {
        for (Literal l : c) {
            add(l);
        }
    }
    int ref = 0;

    @Override
    public int getReferences() {
        return ref;
    }

    @Override
    public void setReferences(int ref) {
        this.ref = ref;
    }

    @Override
    public void incReferences() {
        ref++;
    }

    @Override
    public void decReferences() {
        ref--;
    }

    @Override
    public String toString() {
        return "<" + rank + ">" + super.toString() + (ref == -1 ? "" : "->" + ref);
    }


    @Override
    public List<Literal> toList() {
        return this;
    }


    public boolean contains(Literal l) {
        return super.contains(l);
    }


    boolean unit = false;
    @Override
    public void setUnit() {
        unit = true;
    }

    @Override
    public boolean isUnit() {
        return unit;
    }
}

