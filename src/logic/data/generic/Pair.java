/*
 Copyright 2006 Hao Xu
 xuh@cs.unc.edu

 This file is part of Functional Programming for Java or jfp for short.

 Jfp is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 Jfp is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Foobar; if not, write to the Free Software
 Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package logic.data.generic;

public class Pair<U, V> {

	U hd;

	V tl;

	public Pair(U hd, V tl) {
		this.hd = hd;
		this.tl = tl;
	}

	public
	Object[] toArray() {
		return new Object[]{this.hd, this.tl};
	}



	public
	int arity() {
		return 2;
	}

	public U fst(){
		return hd;
	}

	public V snd() {
		return tl;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result + ((hd == null) ? 0 : hd.hashCode());
		result = PRIME * result + ((tl == null) ? 0 : tl.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Pair other = (Pair) obj;
		if (hd == null) {
			if (other.hd != null)
				return false;
		} else if (!hd.equals(other.hd))
			return false;
		if (tl == null) {
			if (other.tl != null)
				return false;
		} else if (!tl.equals(other.tl))
			return false;
		return true;
	}

}
