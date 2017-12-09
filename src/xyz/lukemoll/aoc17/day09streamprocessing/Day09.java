package xyz.lukemoll.aoc17.day09streamprocessing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xyz.lukemoll.aoc17.utils.PuzzleInputReader;

public class Day09 {

	public static void main(String[] args) {
		String input = PuzzleInputReader.getPuzzleInputLine("day09.txt");
		
		System.out.println("Total score for all groups: " + getTotal(cleanString(input)));
		System.out.println("Total length of garbage removed: " + getGarbageLength(input));
	}
	
	private static Pattern cancelPattern = Pattern.compile("!.");
	private static Pattern garbagePattern = Pattern.compile("<(.*?)>");
	public static String cleanString(String input) {
		String afterCanceled = cancelPattern.matcher(input).replaceAll("");
		String noGarbage = garbagePattern.matcher(afterCanceled).replaceAll("");
		return noGarbage;
	}
	
	public static int getGarbageLength(String input) {
		String afterCanceled = cancelPattern.matcher(input).replaceAll("");
		Matcher m = garbagePattern.matcher(afterCanceled);
		int length = 0;
		
		while(m.find()) {
			length += m.group(1).length();
		}
		
		return length;
	}
	
	public static int getTotal(String cleanInput) {
		// Once the input is clean, we can treat the input like a 3-instruction language.
		// Where '{' increases the level, '}' adds the level to the accumulator and 
		// decreases the level, and ',' is a no-op.
		int total = 0;
		int level = 0;
		for(char c : cleanInput.toCharArray()) {
			switch(c) {
			case'{':
				level++;
				break;
			case'}':
				total += level;
				level--;
				break;
			case',':
				break;
			default:
				System.out.println("Unexpected char: " + c);
			}
		}
		return total;
	}

}
