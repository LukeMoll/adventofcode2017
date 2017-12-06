package xyz.lukemoll.aoc17.day06memoryreallocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

import xyz.lukemoll.aoc17.utils.PuzzleInputReader;

public class Day06 {

	private static class Result {
		public int iterationsToRepeated;
		public int cyclesInLoop;
	}

	public static void main(String[] args) {
		int[] banks = Arrays.stream(PuzzleInputReader.getPuzzleInputLine("day06.txt").split("\t"))
				.mapToInt(e -> Integer.parseInt(e)).toArray();
		
		Result r = getIterations(banks);
		System.out.println(r.iterationsToRepeated);
		System.out.println(r.cyclesInLoop);
		
	}
	
	public static int getNextBank(int[] banks) {
		int max = 0;
		for(int i=0; i<banks.length; i++) {
			if(banks[i] > banks[max]) {
				max = i;
			}
		}
		return max;
	}
	
	public static int[] balanceOnce(int[] argBanks) {
		int[] banks = Arrays.copyOf(argBanks, argBanks.length);
		int bank = getNextBank(banks);
		
		int toDivide = banks[bank];
		int i = (bank + 1) % banks.length;
		banks[bank] = 0;
		while(toDivide > 0) {
			banks[i]++;
			toDivide--;
			i = (i+1) % banks.length;
		}
		
		return banks;
	}
	
	static Result getIterations(int[] argBanks) {
		// It's ugly, but at least ArrayLists properly compare.
		int[] banks = Arrays.copyOf(argBanks, argBanks.length);
		HashMap<Integer,ArrayList<Integer>> seenBefore = new HashMap<Integer,ArrayList<Integer>>();
		int i=0;
		do {			
			seenBefore.put(new Integer(i),toArrayList(banks));
			banks = balanceOnce(banks);
			i++;			
		}
		while(!seenBefore.containsValue(toArrayList(banks)));
		Result r = new Result();
		r.iterationsToRepeated = i;
		r.cyclesInLoop = i - getKey(toArrayList(banks), seenBefore);
		return r;
	}
	
	public static int getKey(ArrayList<Integer> value, HashMap<Integer,ArrayList<Integer>> map) {
		for(Entry<Integer,ArrayList<Integer>> e : map.entrySet()) {
			if(e.getValue().equals(value)) {
				return e.getKey();
			}
		}
		return -1;
	}
	
	public static ArrayList<Integer> toArrayList(int[] banks) {
		ArrayList<Integer> a = new ArrayList<Integer>();
		for(int b : banks) {
			a.add(new Integer(b));
		}
		return a;
	}
	
}
