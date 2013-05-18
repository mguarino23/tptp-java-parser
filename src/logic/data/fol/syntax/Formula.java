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
import java.util.List;
import logic.data.generic.Pair;
import logic.data.fol.syntax.QuantifiedFormula.Quantifier;

/**
 *
 * 
 */
public abstract class Formula {

    public abstract ConnectedFormula flatten();
    public abstract Formula pushNegation(boolean negation);
    public abstract Pair<List<Pair<Quantifier,VariableSymbol>>,Formula> extractQuantifier();

    public abstract Formula replace(VariableSymbol snd, Term nv);
    public abstract List<VariableSymbol> fv();
}
