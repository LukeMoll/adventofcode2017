package xyz.lukemoll.aoc17.day10knothash;

import java.util.Arrays;
import java.util.stream.IntStream;

import xyz.lukemoll.aoc17.utils.PuzzleInputReader;

public class Day10 {

	public static void main(String[] args) {
		String inputLine = PuzzleInputReader.getPuzzleInputLine("day10.txt");
		
		
		
		// Part 1
		int[] intLengths = Arrays.stream(inputLine.split(","))
				.mapToInt(s -> Integer.parseInt(s.trim())).toArray();
		int[] intList = knotHash(getStartingList(255), intLengths);
		System.out.println("Product of int list: " + intList[0] * intList[1]);
		
		// Part 2
		int[] charLengths = toIntLengths(inputLine.toCharArray());
		int[] charList = knotHash(getStartingList(255), charLengths, 64);
		System.out.println(toHexString(getDenseHash(charList)));
	}
	
	public static int[] knotHash(int[] list, int[] lengths) {
		return knotHash(list, lengths, 1);
	}
	
	public static int[] knotHash(int[] list, int lengths[], int iterations) {
		int currentPos = 0, skipSize = 0;
		
		for(int i=0; i<iterations; i++) {
			for(int length : lengths) {
				list = reverse(list, currentPos, length);
				currentPos = (currentPos + length + skipSize) % list.length;
				skipSize++;
			}
		}
		return list;
	}
	
	public static int[] reverse(int[] list, int start, int length) {
		int[] returnList = Arrays.copyOf(list, list.length);

		for(int i=0; i<length; i++) {
			int toIndex = (start+i)%list.length;
			int fromIndex = ((start+(length-1))-i)%list.length;
			returnList[toIndex] = list[fromIndex];
		}
		return returnList;
	}
	
	public static int[] getDenseHash(int[] sparseHash) {
		if(sparseHash.length != 256) {
			throw new Error("SparseHash must have 256 elements!");
		}
		int[] denseHash = new int[16];
		for(int i=0; i < denseHash.length; i++) {
			
			int runningHash = 0;
			for(int j=0;j<16;j++) {
				
				runningHash = runningHash ^ sparseHash[(16*i) + j]; 
			}
			denseHash[i] = runningHash;
		}
		return denseHash;
	}
	
	public static String toHexString(int[] denseHash) {
		StringBuilder output = new StringBuilder(denseHash.length * 2);
		for(int i : denseHash) {
			output.append(Integer.toHexString(i));
		}
		return output.toString();
	}
	
	public static int[] toIntLengths(char[] input) {
		return IntStream.concat(IntStream.range(0, input.length).
				map(i -> (int) input[i]), 
				IntStream.of(17, 31, 73, 47, 23))
				.toArray(); 
	}
	
	public static int[] getStartingList(int max) {
		return IntStream.rangeClosed(0, max).toArray();
	}

}
