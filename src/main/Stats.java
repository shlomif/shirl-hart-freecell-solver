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
