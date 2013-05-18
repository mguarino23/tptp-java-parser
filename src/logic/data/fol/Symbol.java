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

public class Symbol implements Comparable<Symbol> {

	private static final boolean SHID = false;//true;

	public String name;
	
	public int id;

	public Symbol(String name, int id) {
		super();
		this.name = name;
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || !(obj instanceof Symbol))
			return false;
		final Symbol other = (Symbol) obj;
		if (id != other.id)
			return false;
//		if (name == null) {
//			if (other.name != null)
//				return false;
//		} else if (!name.equals(other.name))
//			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.name+(SHID?"["+this.id+"]":"");
	}

	public int compareTo(Symbol arg0) {
		return this.id-arg0.id;
	}
}
