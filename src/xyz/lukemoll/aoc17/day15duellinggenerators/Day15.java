package xyz.lukemoll.aoc17.day15duellinggenerators;

import java.util.function.BiPredicate;
import java.util.function.LongFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xyz.lukemoll.aoc17.utils.PuzzleInputReader;

public class Day15 {

	public static LongFunction<Long> genApt1 = x -> (x*16807)%2147483647;
	public static LongFunction<Long> genBpt1 = x -> (x*48271)%2147483647;
	public static BiPredicate<Long, Long> bitCompare = (x1,x2) -> {
		return (x1.longValue()&65535)==(x2.longValue()&65535);
	};
	
	public static LongFunction<Long> genApt2 = x -> {
		long next = x;
		do {
			next = (next*16807)%2147483647;
		} 
		while(next % 4 != 0);
		return next;
	};
	public static LongFunction<Long> genBpt2 = x -> {
		long next = x;
		do {
			next = (next*48271)%2147483647;
		} 
		while(next % 8 != 0);
		return next;
	};
	
	public static void main(String[] args) {
		String[] input = PuzzleInputReader.getPuzzleInputLines("day15.txt");
		Pattern p = Pattern.compile("(\\d+)$");
		Matcher matchA = p.matcher(input[0]),
				matchB = p.matcher(input[1]);
		matchA.find();
		matchB.find();
		int aStart = Integer.parseInt(matchA.group(1)),
			bStart = Integer.parseInt(matchB.group(1));
		// Part 1
		System.out.println("Part 1: " + getCount(aStart, bStart, genApt1, genBpt1, 40000000)); 
		// Part 2
		System.out.println("Part 2: " + getCount(aStart, bStart, genApt2, genBpt2, 5000000)); 
		
	}
	
	public static int getCount(long valA, long valB, LongFunction<Long> genA, LongFunction<Long> genB, int iterations) {
		int count = 0;
		for(int i=0; i<iterations; i++) {
			valA = genA.apply(valA);
			valB = genB.apply(valB);
			if(bitCompare.test(valA,valB)) {
				count++;
			}
		}
		return count;
	}
	
}
