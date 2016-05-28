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
package main;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Stats {

	private final ArrayList<TreeMap<Integer, Integer>> scoresArray;
	private final int MAXSTATS;

	public Stats (int maxstats) {
		MAXSTATS = maxstats;
		scoresArray = new ArrayList<TreeMap<Integer, Integer>>();
		for (int i = 0; i < MAXSTATS; i++){
			scoresArray.add(new TreeMap<Integer, Integer>());
		}
	}

	public ArrayList<TreeMap<Integer, Integer>> get () {
		return scoresArray;
	}

	public void put (int[] score){
		for (int i = 0; i < MAXSTATS; i++){
			TreeMap<Integer, Integer> stat = scoresArray.get(i);
			if (stat.containsKey(score[i])){
				Integer n = stat.get(score[i]);
				n = Integer.valueOf(n.intValue() + 1);
				stat.put(score[i], n);
			} else {
				stat.put(score[i], 1);
			}
		}
	}

	public int[] findHiScores (){
		int[] hiscores = new int[MAXSTATS];
		for (int i = 0; i < MAXSTATS; i++){
			hiscores[i] = scoresArray.get(i).lastKey();
		}
		return hiscores;
	}

	public int[] findLoScores (){
		int[] loscores = new int[MAXSTATS];
		for (int i = 0; i < MAXSTATS; i++){
			loscores[i] = scoresArray.get(i).firstKey();
		}
		return loscores;
	}

	public int[] findMidScores (int maxnodes){
		int[] midcount = new int[MAXSTATS];
		int[] midscore = new int[MAXSTATS];
		for (int i = 0; i < MAXSTATS; i++) {
			TreeMap<Integer, Integer> sa = scoresArray.get(i);
			for (Map.Entry<Integer, Integer> scores : sa.entrySet()) {
				if (midcount[i] <= maxnodes) {
					midcount[i] += scores.getValue();
					midscore[i]  = scores.getKey();
				} else {
					break;
				}
			}
		}
		return midscore;
	}
}
