package xyz.lukemoll.aoc17.day05twistytrampolines;

import java.util.Arrays;
import java.util.function.IntFunction;

import xyz.lukemoll.aoc17.utils.PuzzleInputReader;

public class Day05 {

	public static void main(String[] args) {
		int[] inputList = Arrays.stream(PuzzleInputReader.getPuzzleInputLines("day05.txt"))
				.mapToInt(line -> Integer.parseInt(line)).toArray();
		
		IntFunction<Integer> part1 = e -> e + 1;
		IntFunction<Integer> part2 = e -> e >= 3?e-1:e+1;
		
		System.out.println("Part 1: " + escape(inputList, part1) + " steps to exit.");
		System.out.println("Part 2: " + escape(inputList, part2) + " steps to exit.");
	}
	
	public static int escape(int[] argList, IntFunction<Integer> modify) {
		// Arrays are references, we don't want to mutate the original
		int[] list = Arrays.copyOf(argList, argList.length);
		int steps = 0;
		int pointer = 0;
		while(pointer<list.length) {
			int incPointer = list[pointer];
			list[pointer] = modify.apply(list[pointer]);
			pointer += incPointer; //increment by value *before* list was incremented
			steps++;
		}
		return steps;
	}

}
