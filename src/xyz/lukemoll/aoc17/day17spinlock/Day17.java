package xyz.lukemoll.aoc17.day17spinlock;

import java.util.LinkedList;

import xyz.lukemoll.aoc17.utils.PuzzleInputReader;

public class Day17 {

	public static void main(String[] args) {
		int input = Integer.parseInt(PuzzleInputReader.getPuzzleInputLine("day17.txt"));
		// Part 1
		LinkedList<Integer> buffer = spinlock(2018,input);
		int index = buffer.indexOf(2017);
		System.out.println("Number next to 2017: " + buffer.get(index+1));
		// Part 2
		System.out.println("Last inserted next to 0: " + getLastInsertedAt(0, (long) 5e7, input));
	}
	
	public static LinkedList<Integer> spinlock(int iterations, int input) {
		LinkedList<Integer> buffer = new LinkedList<Integer>();
		int i = 0;
		int skip = input;
		buffer.add(0,i++);
		int nextIndex = 0;
		for(;i<iterations;i++) {
			buffer.add(++nextIndex,i);
			if(i==0) {
				nextIndex = 1;
			}
			else {
				nextIndex = (nextIndex + skip) % buffer.size();
			}
		}

		return buffer;
	}
	
	public static int getLastInsertedAt(int position, long d, int input) {
		// We could greatly accelerate this by running backwards and finding the *first*
		// inserted at pos 0, but we would need to find the final position after 5e7 iterations
		int currentPosition = 0;
		int skip = input;
		int length = 1;
		int lastInserted = 0;
		for(; length <= d; length++) {
			currentPosition = (currentPosition + skip) % length;
			if(currentPosition == position) {
				lastInserted = length;
			}
			currentPosition++;
		}
		return lastInserted;
	}

}
