/*
 * Copyright 2013 Shirl Hart
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the the Artistic License (2.0). You may obtain a copy
 * of the full license at:
 *
 * http://www.perlfoundation.org/artistic_license_2_0
 *
 * Any use, modification, and distribution of the Standard or Modified
 * Versions is governed by this Artistic License. By using, modifying or
 * distributing the Package, you accept this license. Do not use, modify, or
 * distribute the Package, if you do not accept this license.
 *
 * If your Modified Version has been derived from a Modified Version made by
 * someone other than you, you are nevertheless required to ensure that your
 * Modified Version complies with the requirements of this license.
 *
 * This license does not grant you the right to use any trademark, service
 * mark, tradename, or logo of the Copyright Holder.
 *
 * This license includes the non-exclusive, worldwide, free-of-charge patent
 * license to make, have made, use, offer to sell, sell, import and otherwise
 * transfer the Package with respect to any patent claims licensable by the
 * Copyright Holder that are necessarily infringed by the Package. If you
 * institute patent litigation (including a cross-claim or counterclaim)
 * against any party alleging that the Package constitutes direct or
 * contributory patent infringement, then this Artistic License to you shall
 * terminate on the date that such litigation is filed.
 *
 * Disclaimer of Warranty: THE PACKAGE IS PROVIDED BY THE COPYRIGHT HOLDER
 * AND CONTRIBUTORS "AS IS' AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES.
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT ARE DISCLAIMED TO THE EXTENT PERMITTED BY
 * YOUR LOCAL LAW.  UNLESS REQUIRED BY LAW, NO COPYRIGHT HOLDER OR
 * CONTRIBUTOR WILL BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, OR
 * CONSEQUENTIAL DAMAGES ARISING IN ANY WAY OUT OF THE USE OF THE PACKAGE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * */
//src/main/Entry.java
package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;

public class Entry {
	private static final int MAXCOLS = 8;
	private static final int MAXROWS = 21;

	public String key;
	public Value value;

	public Entry (String[] line){
		this.key = line[0];
		this.value = new Value();

		for (int i = 1; i < line.length; i++) 
			value.token[i - 1] = Integer.parseInt(line[i]);
	}

	public Entry (String key, Value value){
		this.key = key;
		this.value = value;
	}
	
	public Entry (Tableau tableau) {
		Map<String, Integer> map = new TreeMap<String, Integer>();
		StringBuilder sb = new StringBuilder();
		this.value = new Value();
		this.value.token = new int[16];
		
		for (int c = 0; c < MAXCOLS; c++) {
			int i = tableau.tableau[c][0];
			if (i != 0){
				map.put(Character.toString((char) i), c);
			}
		}
		
		int j = 0;
		for (Map.Entry<String, Integer> entry : map.entrySet()){
			value.token[j++] = entry.getValue();
		}
		key = Joiner.on("").join(map.keySet());
		map.clear();
		
		for (int c = 0; c < MAXCOLS; c++) {
			for (int r = 1; r < MAXROWS; r++) {
				int i = tableau.tableau[c][r];
				if (i == 0)	break;
				sb.append((char) i);
			}

			if (sb.length() > 0) {
				map.put(sb.toString(), c);
				sb.setLength(0);
			}
		}

		for (Map.Entry<String, Integer> entry : map.entrySet()){
			value.token[j++] = entry.getValue();
		}
		key += " " + Joiner.on(" ").join(map.keySet());
	}
	public class Value {
		public int[] token;
		public int depth;
		public int level;
		public int[] score;
		public ArrayList<Move> node;

		@Override 
		public String toString (){
			return Objects.toStringHelper(this)
				.add("token", Arrays.toString(token))
				.add("depth", depth)
				.add("level", level)
				.add("score", Arrays.toString(score))
				.add("node", node)
				.toString();
		}
	}
}
