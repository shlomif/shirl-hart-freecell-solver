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
