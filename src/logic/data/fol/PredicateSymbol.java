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

public class PredicateSymbol extends Symbol {
	public int arity;

	public PredicateSymbol(String name, int arity, int id) {
		super(name, id);
		this.arity = arity;
	}
}
