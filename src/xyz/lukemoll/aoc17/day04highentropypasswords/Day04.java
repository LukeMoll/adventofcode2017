package xyz.lukemoll.aoc17.day04highentropypasswords;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import xyz.lukemoll.aoc17.utils.PuzzleInputReader;

public class Day04 {
	
	static Pattern hasRepeatedWord = Pattern.compile("(?:\\s|^)([a-z]+)\\s(?:[a-z]+\\s)*\\1(?:\\s|$)");
	
	public static void main(String[] args) {
		String[] passphrases = PuzzleInputReader.getPuzzleInputLines("day04.txt");
		
		long limit = passphrases.length;
		String [] noRepeated = Arrays.stream(passphrases).limit(limit).filter(p -> 
				!hasRepeatedWord.matcher(p).find()).toArray(n -> new String[n]);
		System.out.println(noRepeated.length + " out of " + limit + " passphrases have no repeated words.");
		
		long noAnagrams = Arrays.stream(noRepeated).filter(hasNoAnagram).count();
		System.out.println(noAnagrams + " out of " + noRepeated.length + " have no anagrams.");
		
	}

	static Function<String, String> sort = s -> {
		char[] chars = s.toCharArray();
		Arrays.sort(chars);
		return new String(chars);
	};
	
	static Predicate<String> hasNoAnagram = p -> {
		String[] sorted = Arrays.stream(p.split("\\s")).map(sort).toArray(n -> new String[n]);
		for(String s : sorted) {
			int count = 0;
			for(String t : sorted) {
				if(s.equals(t)) {
					if(count == 0) {
						count++;
					}
					else {
						return false;
					}
				}
			}
		}
		return true;
	};
	
}
