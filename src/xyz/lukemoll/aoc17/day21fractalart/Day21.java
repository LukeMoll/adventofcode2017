package xyz.lukemoll.aoc17.day21fractalart;

import java.util.*;
import java.util.stream.Collectors;
import xyz.lukemoll.aoc17.utils.PuzzleInputReader;

public class Day21 {

	public static void main(String[] args) {
		Map<Pattern, Pattern> rules = getRules(PuzzleInputReader.getPuzzleInputLines("day21.txt"));
		System.out.println(rules.size() + " rules (" + rules.entrySet().stream().distinct().count() + " distinct)");
		String[] grid = Pattern.STARTING_PATTERN.pattern;
		int iterations = 18;
		int count = 0;
		for(int i=0; i<iterations;) {
			grid = iterate(grid, rules);
			count = (int) Arrays.stream(grid).flatMapToInt(line -> line.chars()).filter(chr -> chr == (int) Pattern.CHAR_ON).count();
			System.out.println("After " + ++i + " iterations: " + grid.length + "x" + grid.length + ":\t" + count + " pixels on");
		}
	}
	
	static String[] iterate(String[] grid, Map<Pattern, Pattern> rules) {
		return join(split(grid).stream()
				.map(p -> rules.get(p))
				.collect(Collectors.toList()));
	}
	
	static String[] join(List<Pattern> orderedPatterns) {
		int tiles = (int) Math.sqrt(orderedPatterns.size());
		int tileSize = orderedPatterns.get(0).size;
		String[] grid = new String[tiles * tileSize];
		for(int tileRow=0; tileRow < tiles; tileRow++) {
			for(int innerRow=0; innerRow < tileSize; innerRow++) {
				String rowStr = "";
				for(int tileCol=0; tileCol < tiles; tileCol++) {
					 rowStr += orderedPatterns.get((tileRow * tiles) + tileCol).pattern[innerRow];
				}
				grid[(tileRow * tileSize) + innerRow] = rowStr;
			}
		}
		return grid;
	}
	
	static List<Pattern> split(String[] grid) {
		ArrayList<Pattern> orderedPatterns = new ArrayList<Pattern>();
		int size = grid.length;
		if(size % 2 != 0 && size % 3 != 0)
			throw new Error("unexpected tilesize");
		//else
		int tileSize = size % 2 == 0?2:3;
		for(int y=0; y<size; y+=tileSize) {
			for(int x=0; x<size; x+=tileSize) {
				String[] miniGrid = new String[tileSize];
				for(int i=0; i< tileSize; i++) {
					miniGrid[i] = grid[y+i].substring(x, x+tileSize);						
				}
				orderedPatterns.add(new Pattern(miniGrid, tileSize));
			}				
		}
		return orderedPatterns;
	}
	
	static Map<Pattern, Pattern> getRules(String[] input) {
		Map<Pattern, Pattern> rules = new HashMap<Pattern, Pattern>();
		for(String line : input) {
			String[] parts = line.split("\\s?=>\\s?");
			Pattern[] inputTransformations = new Pattern(parts[0]).getTransformations();
			Pattern output = new Pattern(parts[1]);
			for(Pattern inPatt : inputTransformations) { 
					rules.put(inPatt, output);					
			}
		}
		return rules;
	}
	
	static class Pattern {
		final String[] pattern;
		final int size;
		Pattern(String rule) {
			String[] split = rule.split("/");
			this.size = split[0].length();
			this.pattern = split;
		}
		Pattern(String[] pattern, int size) {
			this.pattern = pattern;
			this.size = size;
		}
		static final Pattern STARTING_PATTERN = new Pattern(".#./..#/###");
		static final char CHAR_ON = '#', CHAR_OFF='.';
		public boolean hasNull() {
			return Arrays.stream(pattern).flatMapToInt((String line) -> line.chars()).filter(c -> c==0).count() > 0;
		}
		@Override
		public int hashCode() {
			return Objects.hash(this.toString());
		}
		@Override
		public boolean equals(Object obj) {
			return 	Pattern.class.isInstance(obj) &&
					this.size == ((Pattern) obj).size &&
					Arrays.deepEquals(this.pattern, ((Pattern) obj).pattern);
		}
		@Override
		public String toString() {
			return "[" + this.size + "] " + Arrays.stream(this.pattern).collect(Collectors.joining("/", "", ""));
		}
		private Pattern flipV() {
			char[][] newPattern = new char[size][size];
			if(size == 2) {
				newPattern[0][0] = pattern[1].charAt(0);
				newPattern[0][1] = pattern[1].charAt(1);
				newPattern[1][0] = pattern[0].charAt(0);
				newPattern[1][1] = pattern[0].charAt(1);
			}
			else if(size == 3) {
				newPattern[0][0] = pattern[2].charAt(0);
				newPattern[0][1] = pattern[2].charAt(1);
				newPattern[0][2] = pattern[2].charAt(2);
				newPattern[1] = pattern[1].toCharArray();
				newPattern[2][0] = pattern[0].charAt(0);
				newPattern[2][1] = pattern[0].charAt(1);
				newPattern[2][2] = pattern[0].charAt(2);
			}
			else {
				throw new Error("Can't transform greater than size 3");
			}
			return new Pattern(Arrays.stream(newPattern).map(charArr -> new String(charArr)).toArray(n -> new String[n]), size);
		}
		private Pattern flipH() {
			char[][] newPattern = new char[size][size];
			if(size == 2) {
				newPattern[0][0] = pattern[0].charAt(1);
				newPattern[0][1] = pattern[0].charAt(0);
				newPattern[1][0] = pattern[1].charAt(1);
				newPattern[1][1] = pattern[1].charAt(0);
			}
			else if(size == 3) {
				newPattern[0][0] = pattern[0].charAt(2);
				newPattern[0][1] = pattern[0].charAt(1);
				newPattern[0][2] = pattern[0].charAt(0);
				newPattern[1][0] = pattern[1].charAt(2);
				newPattern[1][1] = pattern[1].charAt(1);
				newPattern[1][2] = pattern[1].charAt(0);
				newPattern[2][0] = pattern[2].charAt(2);
				newPattern[2][1] = pattern[2].charAt(1);
				newPattern[2][2] = pattern[2].charAt(0);
			}
			else {
				throw new Error("Can't transform greater than size 3");
			}
			return new Pattern(Arrays.stream(newPattern).map(charArr -> new String(charArr)).toArray(n -> new String[n]), size);
		}
		private Pattern rotCW() {
			char[][] newPattern = new char[size][size];
			if(size == 2) {
				newPattern[0][0] = pattern[1].charAt(0);
				newPattern[0][1] = pattern[0].charAt(0);
				newPattern[1][0] = pattern[1].charAt(1);
				newPattern[1][1] = pattern[0].charAt(1);
			}
			else if(size == 3) {
				newPattern[0][0] = pattern[2].charAt(0);
				newPattern[0][1] = pattern[1].charAt(0);
				newPattern[0][2] = pattern[0].charAt(0);
				newPattern[1][0] = pattern[2].charAt(1);
				newPattern[1][1] = pattern[1].charAt(1);
				newPattern[1][2] = pattern[0].charAt(1);
				newPattern[2][0] = pattern[2].charAt(2);
				newPattern[2][1] = pattern[1].charAt(2);
				newPattern[2][2] = pattern[0].charAt(2);
			}
			else {
				throw new Error("Can't transform greater than size 3");
			}
			return new Pattern(Arrays.stream(newPattern).map(charArr -> new String(charArr)).toArray(n -> new String[n]), size);
		}	
		Pattern[] getTransformations() {
			Set<Pattern> transformations = new HashSet<Pattern>();
			Pattern rotated = this;
			for(int i=0;i<4;i++) {
				transformations.add(rotated);
				transformations.add(rotated.flipH());
				transformations.add(rotated.flipV());
				transformations.add(rotated.flipH().flipV());
				rotated = rotated.rotCW();
			}
			return transformations.toArray(new Pattern[transformations.size()]);
		}
	}
}