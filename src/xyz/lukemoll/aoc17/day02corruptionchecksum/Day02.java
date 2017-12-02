package xyz.lukemoll.aoc17.day02corruptionchecksum;

import java.util.Arrays;

import xyz.lukemoll.aoc17.utils.PuzzleInputReader;

public class Day02 {

	public static void main(String[] args) {
		String[] lines = PuzzleInputReader.getPuzzleInputLines("day02.txt");
		int[][] rows = Arrays.stream(lines).map(Day02::parseLine).toArray(size -> new int[size][]);
		int checksum = Arrays.stream(rows).mapToInt(Day02::getRowDiff).sum();
		int divisionsum = Arrays.stream(rows).mapToInt(Day02::getRowDivision).sum();
		System.out.println("Sum of differences:\t" + checksum);
		System.out.println("Sum of divisions:\t" + divisionsum);
	}
	
	// Part 1
	public static int getRowDiff(int[] row) {
		int min = row[0], max = row[0];
		for(int i : row) {
			if(i < min) {
				min = i;
			}
			else if(i > max) {
				max = i;
			}
		}
		return max - min;
	}
	
	public static int[] parseLine(String line) {
		String[] split = line.split("\\s");
		int[] numbers = new int[split.length];
		for(int i=0; i < split.length; i++) {
			numbers[i] = Integer.parseInt(split[i]);
		}
		return numbers;
	}
	
	// Part 2
	public static int getRowDivision(int[] row) {
		for(int x : row) {
			for(int y : row) {
				if(x%y == 0 && x != y) {
					return x / y;
				}
			}
		}
		System.out.println("No divisible value found!");
		return -1;
	}

}
