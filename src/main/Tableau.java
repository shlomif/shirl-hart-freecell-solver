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
//src/main/tableau.java
package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

public class Tableau {

	int[][] tableau;

	private static final int MAXCOLS = 8;
	private static final int MAXROWS = 21;
	private static final int HOMEOFFSET = 4;

	public static int rank (int ascii) { return ascii     & 15; }
	public static int suit (int ascii) { return ascii >> 4 & 3; }
	public static boolean oppositeColors (int src, int dst) {return (src & 16) != (dst & 16); }

	public static boolean inSequence(int src, int dst) {
		return rank(src) + 1 == rank(dst) &&  oppositeColors(src, dst);
	}

	public static int fromCard (String str) {
		if (str.trim().isEmpty())
			return 0;
		else
			return fromRank.get(str.substring(0,1)) +
				   fromSuit.get(str.substring(1,2)) * 16 + 64;
	}

	private static final int COLUMNS_HEIGHT_OFFSET = 1;

	private static final Pattern expression = Pattern.compile("(..) ?");

	private static final Map<String, Integer> fromSuit =
				ImmutableMap.of("D", 0, "C", 1, "H", 2, "S", 3, " ", 0);

	private static final Map<String, Integer> fromRank = new ImmutableMap.Builder<String, Integer>()
		.putAll(ImmutableMap.of("A",  1, "2",  2, "3",  3, "4",  4, "5",  5))
		.putAll(ImmutableMap.of("6",  6, "7",  7, "8",  8, "9",  9, "T", 10))
		.putAll(ImmutableMap.of("J", 11, "Q", 12, "K", 13, " ",  0))
		.build();

	private static final Map<Integer, String> toSuit =
				ImmutableMap.of(0, "D", 1, "C", 2, "H", 3, "S");

	private static final Map<Integer, String> toRank = new ImmutableMap.Builder<Integer, String>()
		.putAll(ImmutableMap.of( 1, "A",  2, "2",  3, "3",  4, "4",  5, "5"))
		.putAll(ImmutableMap.of( 6, "6",  7, "7",  8, "8",  9, "9", 10, "T"))
		.putAll(ImmutableMap.of(11, "J", 12, "Q", 13, "K"))
		.build();

	private static final Map<Integer, String> ABCD =
				ImmutableMap.of(0, "a", 1, "b", 2, "c", 3, "d");

//			  card->key     A    2    3    4    5    6    7    8    9    T    J    Q    K
//			    ....0000 0001 0010 0011 0100 0101 0110 0111 1000 1001 1010 1011 1100 1101
//		 64	  D 0100....    A    B    C    D    E    F    G    H    I    J    K    L    M
//		 80	  C 0101....    Q    R    S    T    U    V    W    X    Y    Z    [    \    ]
//		 96	  H 0110....    a    b    c    d    e    f    g    h    i    j    k    l    m
//		112	  S 0111....    q    r    s    t    u    v    w    x    y    z    {    |    }


	public Tableau (){

		tableau = new int[8][21];
	}

	public void deal (long seed){
		int[] deck = new int[52];
		for (int i = 0; i < 52; i++){
			deck[i] = i;
		}

		int wLeft = 52;
		for (int i = 0; i < 52; i++){
			seed = (seed * 214013L + 2531011L) % 2147483648L; // (long)Math.pow(2,31);
			int j = (int)(seed >> 16) % wLeft;
			tableau[i % 8][COLUMNS_HEIGHT_OFFSET + i / 8] = 65 + deck[j] / 4 + 16 * // switch C & D
					(deck[j] % 4 == 0 ? 1 : deck[j] % 4 == 1 ? 0 : deck[j] % 4);
			deck[j] = deck[--wLeft];
		}
	}

	public int[] heuristic () {

		Helper h = new Helper(tableau);

		int score = 64;
		for (int c = HOMEOFFSET; c < MAXCOLS; c++){
			score -= rank(tableau[c][0]);
		}
		score -= h.ecount;
		score -= h.fcount;

		int seq = 0, brk = 0;
		for (int c = 0; c < MAXCOLS; c++){
			if (h.z[c] > 1){
				for (int r = 1; r < h.z[c]; r++){
					int dst = tableau[c][r];
					int src = tableau[c][r + 1];
					brk += inSequence(src, dst)  ? 0 : 1;
					seq += rank(src) < rank(dst) ? 0 : 1;
				} // major sequence break if src >= dst
			}
		}

        int recurse = 0;
//		for (int c = 0; c < MAXCOLS; c++){
//		        for (int r = 1; r  < h.z[c]; r++){
//		                int rank = rank(tableau[c][r]);
//		                recurse += (14 - rank) * (h.z[c] - r);
//		        }
//		}

        return new int[]{score + brk, score + brk + seq, score + recurse / 16};
	}

	@Override
	public String toString () {
		StringBuilder sb = new StringBuilder();
		for (int r = 0; r < MAXROWS; r++) {
			boolean done = true;
			for (int c = 0; c < MAXCOLS; c++){
				if (tableau[c][r] == 0) {
					sb.append("   ");
				} else {
					sb.append(toRank.get( tableau[c][r]       & 15))
					  .append(toSuit.get((tableau[c][r] >> 4) &  3))
					  .append(" ");
					done = false;
				}
			}
			sb.append("\r\n");
			if (done && r > 0) break;
		}
		sb.setLength(sb.length() - 28);
		return sb.toString();
	}

	public void fromString(String input) {
		String[] str = input.split("\r\n");
		int r = COLUMNS_HEIGHT_OFFSET;
		for (int i = 0; i < str.length; i++) {
			Matcher matcher = expression.matcher(str[i]);
			int c = 0;
			while (matcher.find()) {
				tableau[c][r] = fromCard(matcher.group());
				c++;
			}
			r++;
		}

//		# fix home if out of order
//
//	    my %home = map {
//	        my $card = $self->[$_][0];
//	        suit($card) + 4, $card;
//	    } 4 .. 7;
//	    foreach ( 4 .. 7 ) {
//	        $self->[$_][0] = exists( $home{$_} ) ? $home{$_} : 0;
//	    }
//	    $self;

	}

	public void fromToken (Entry entry){
		String[] cascades = entry.key.split(" ");
		for (int i = 0; i < cascades[0].length(); i++){
			tableau[entry.value.token[i]][0] = cascades[0].charAt(i);
		}

		int offset = cascades[0].length() - 1;
		for (int i = 1; i < cascades.length; i++){
			for (int j = 0; j < cascades[i].length(); j++){
				tableau[entry.value.token[i + offset]][j + 1] = cascades[i].charAt(j);
			}
		}
	}

	public void play (Move move){
		tableau[move.dstCol][move.dstRow] = tableau[move.srcCol][move.srcRow];
		tableau[move.srcCol][move.srcRow] = 0;
	}

	public void undo (ArrayList<Move> node){
		ListIterator<Move> i = node.listIterator(node.size());
		while (i.hasPrevious()){
			Move move = i.previous();
			tableau[move.srcCol][move.srcRow] = tableau[move.dstCol][move.dstRow];

			if (move.dstRow == 0
			&&  move.dstCol >= HOMEOFFSET
			&&  rank(tableau[move.dstCol][0]) > 1) { // homecell > Ace
				tableau[move.dstCol][move.dstRow]--;
			} else {
				tableau[move.dstCol][move.dstRow] = 0;
			}
		}
	}

	public void autoplay (ArrayList<Move> node){
		int safe = 1;
		while (safe == 1) {
			safe = 0;
			for (int c = 0; c < HOMEOFFSET; c++) {
				int src = tableau[c][0];
				if (src == 0)
					continue;

				if (rank(src) == rank(tableau[suit(src) + HOMEOFFSET][0]) + 1
				&& (rank(src) < 3 || adjacentHomecells(src))) {
					Move move = new Move(c, 0, suit(src) + HOMEOFFSET, 0, "afh");
					node.add(move);
					play(move);
					safe = 1;
				}
			}

			Helper h = new Helper(tableau);

			for (int c = 0; c < MAXCOLS; c++) {
				int r = h.z[c];
				if (r == 0)
					continue;
				int src = tableau[c][r];

				if (rank(src) == rank(tableau[suit(src) + HOMEOFFSET][0]) + 1
				&& (rank(src) < 3 || adjacentHomecells(src))) {
					Move move = new Move(c, r, suit(src) + HOMEOFFSET, 0, "ach");
					node.add(move);
					play(move);
					safe = 1;
				}
			}
		}
	}

	public boolean adjacentHomecells (int src){
		boolean ok = false;
		if ((src & 16) == 0
		&& rank(src) <= rank(tableau[5][0]) + 1
		&& rank(src) <= rank(tableau[7][0]) + 1
		||  (src & 16) != 0
		&& rank(src) <= rank(tableau[4][0]) + 1
		&& rank(src) <= rank(tableau[6][0]) + 1)
			ok = true;
		return ok;
	}

	public ArrayList<ArrayList<Move>> generateNodelist2 (boolean winxpopt){
		ArrayList<ArrayList<Move>> nodelist = new ArrayList<ArrayList<Move>>();
		ArrayList<Move> node;

		Helper h = new Helper(tableau);

		// generate nodelist, src is freecell
		for (int c = 0; c < HOMEOFFSET; c++){
			int src = tableau[c][0];
			if (src == 0)
				continue;

			if (rank(src) - 1 == rank(tableau[suit(src) + HOMEOFFSET][0])){
				node = new ArrayList<Move>();
				node.add(new Move(c, 0, suit(src) + HOMEOFFSET, 0, "fh"));
				nodelist.add(node);
			}

			if (h.ecount > 0){
				node = new ArrayList<Move>();
				node.add(new Move(c, 0, h.eindex, 1, "fe"));
				nodelist.add(node);
			}

			for (int j = 0; j < MAXCOLS; j++){
				if (h.z[j] == 0)
					continue;
				int dst = tableau[j][h.z[j]];

				if (inSequence(src, dst)){
					node = new ArrayList<Move>();
					node.add(new Move(c, 0, j, h.z[j] + 1, "fc"));
					nodelist.add(node);
				}
			}
		}

		// generate nodelist, src is column
		for (int c = 0; c < MAXCOLS; c++) {
			if (h.z[c] == 0)
				continue;
			int src = tableau[c][h.z[c]];

			if (rank(src) - 1 == rank(tableau[suit(src) + HOMEOFFSET][0])){
				node = new ArrayList<Move>();
				node.add(new Move(c, h.z[c], suit(src) + HOMEOFFSET, 0, "ch"));
				nodelist.add(node);
			}

			if (h.fcount > 0){
				node = new ArrayList<Move>();
				node.add(new Move(c, h.z[c], h.findex, 0, "cf"));
				nodelist.add(node);
			}
			// super move to empty
			if (h.ecount > 0){
				for (int k = h.z[c]; k > 1; k--){ // don't move whole column to empty
					if (!( h.z[c] == k || inSequence(tableau[c][k + 1], tableau[c][k]) )){
						break;
					}
					if ((winxpopt ? 1 : h.ecount) * (h.fcount + 1) > h.z[c] - k ) {
						node = new ArrayList<Move>();  								// e*(f+1)
						for (int x = k, y = 1; x <= h.z[c]; ){
							node.add(new Move(c, x++, h.eindex, y++, "ce"));
						}
						nodelist.add(node);
					}
				}
			}
			// super move to column
			for (int j = 0; j < MAXCOLS; j++) {
				if (c == j
				||  h.z[j] == 0)
					continue;

				for (int k = h.z[c]; k > 0; k--){
					if (!( h.z[c] == k || inSequence(tableau[c][k + 1], tableau[c][k]) )){
						break;
					}

					if (inSequence(tableau[c][k], tableau[j][h.z[j]])
					&& ((winxpopt ? Math.min(1, h.ecount) : h.ecount) + 1) * (h.fcount + 1) > h.z[c] - k){
						node = new ArrayList<Move>(); 								// (e+1)*(f+1)
						for (int x = k, y = h.z[j] + 1; x <= h.z[c]; ){
							node.add(new Move(c, x++, j, y++, "cc"));
						}
						nodelist.add(node);
						break;
					}
				}
			}
		}
		return nodelist;
	}

	public String notation (Entry entry){
		List<String> result = new ArrayList<String>();
		Tableau note = new Tableau();

		// dclone tableau
		for (int c = 0; c < MAXCOLS; c++)
			for (int r = 0; r < MAXROWS; r++)
				note.tableau[c][r] = this.tableau[c][r];

		// standard notation
		String stdSrc = new String();
		String stdDst = new String();

		// descriptive notation
		List<String> dscSrc = new ArrayList<String>();
		String dscDst = new String();

		// autoplay notation
		Map<Integer, ArrayList<String>> autoPlay = new HashMap<Integer, ArrayList<String>>();
		List<String> autoSrc = new ArrayList<String>();

		boolean first = true;
		for (Move move : entry.value.node){
			if (first) {
				first   = false;
				stdSrc  = move.srcRow > 0          ? Integer.toString(move.srcCol + 1)
												   : ABCD.get(move.srcCol);

				stdDst  = move.dstRow > 0          ? Integer.toString(move.dstCol + 1)
						: move.dstCol < HOMEOFFSET ? ABCD.get(move.dstCol)
												   : "h";

				dscDst  = move.dstRow == 1         ? "e"
						: stdDst.matches("\\d")    ? note.toCard(move.dstCol, move.dstRow - 1)
						: stdDst.equals("h")       ? "h"
												   : "f";
			}

			int homeSuit = suit(note.tableau[move.srcCol][move.srcRow]);
			if(move.origin.startsWith("a")){ // autoplay to home
				if ( autoPlay.containsKey(homeSuit) ){
					autoPlay.get(homeSuit).add(note.toCard(move.srcCol,move.srcRow));
				} else {
					ArrayList<String> cardArray = new ArrayList<String>();
					cardArray.add(note.toCard(move.srcCol, move.srcRow));
					autoPlay.put(homeSuit, cardArray);
				}
			} else {
				dscSrc.add(note.toCard(move.srcCol, move.srcRow));
			}
			note.play(move);
		}
		for (Map.Entry<Integer, ArrayList<String>> suitArray : autoPlay.entrySet()){
			ArrayList<String> cardArray = suitArray.getValue();
			autoSrc.add(cardArray.get(0) + (cardArray.size() > 1 ?
				"-" + Iterables.getLast(cardArray) : ""));
		}

		result.add(Integer.toString(entry.value.depth));
		result.add(stdSrc + stdDst);
		result.add(dscSrc.get(0) + (dscSrc.size() > 1 ?
			"-" + Iterables.getLast(dscSrc) : ""));
		result.add(dscDst);
		result.add(autoSrc.size() > 0 ?
			Joiner.on(';').join(autoSrc.toArray()) : "");

		return Joiner.on('|').join(result.toArray());
	}

	public String toCard(Integer c, Integer r){
		return  toRank.get( tableau[c][r]       & 15) +
				toSuit.get((tableau[c][r] >> 4) &  3);
	}

	public class Helper {
		int eindex = -1; // first empty column index
		int ecount =  0; // count of empty columns
		int findex = -1; // first freecell index
		int fcount =  0; // count of empty freecells
		int[] z = new int[MAXCOLS]; // cascade depth

		public Helper(int[][] tableau) {
			for (int c = 0; c < MAXCOLS; c++) {
				for (int r = 1; r < MAXROWS; r++) {
					if (tableau[c][r] == 0)
						break;
					z[c] = r;
				}

				if (c < HOMEOFFSET
				&& tableau[c][0] == 0) {
					fcount++;
					if (findex < 0)
						findex = c;
				}

				if (tableau[c][1] == 0) {
					ecount++;
					if (eindex < 0)
						eindex = c;
				}
			}
		}
	}
}
