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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 *
 *
 */
public interface ClauseInterface extends Iterable<Literal> {


    public boolean isEmpty();

    public List<Literal> toList();

    boolean add(Literal e);

    int size();

    void addAllTo(Collection<Literal> c);

    void addAllFrom(Collection<Literal> c);

    void addAll(ClauseInterface c);



    void setReferences(int ref);

    int getReferences();

    void decReferences();

    void incReferences();

    Literal get(int i);

    boolean contains(Literal l);

    int getRank();

    void setRank(int rank);

    public void computeRank();
    public void setAllLiteralRank();

    public void setUnit();

    public boolean isUnit();
}
