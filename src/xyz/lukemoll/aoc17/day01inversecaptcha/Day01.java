package xyz.lukemoll.aoc17.day01inversecaptcha;

import xyz.lukemoll.aoc17.utils.PuzzleInputReader;

public class Day01 {

	public static void main(String[] args) {
		String input = PuzzleInputReader.getPuzzleInputLine("day01.txt");
		System.out.println("Part One:\t" + getSumNext(input));
		System.out.println("Part Two:\t" + getSumOpposite(input));
	}
	
	// Part 1
	public static int getSumNext(String inputStr) {
		char[] inputChars = inputStr.toCharArray();
		int total = 0;
		for(int i=0; i<inputChars.length; i++) {
			
			if(inputChars[i] == inputChars[(i+1) % inputChars.length]) {
				total += Integer.parseInt(inputChars[i] + "");
			}
		}
		return total;
		
	}
	
	// Part 2
	public static int getSumOpposite(String inputStr) {
		int interval = inputStr.length()/2;
		char[] inputChars = inputStr.toCharArray();
		int total = 0;
		for(int i=0; i<inputChars.length; i++) {
			
			if(inputChars[i] == inputChars[(i+interval) % inputChars.length]) {
				total += Integer.parseInt(inputChars[i] + "");
			}
		}
		return total;
	}

}
