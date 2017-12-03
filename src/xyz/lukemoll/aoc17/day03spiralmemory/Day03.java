package xyz.lukemoll.aoc17.day03spiralmemory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import xyz.lukemoll.aoc17.day03spiralmemory.Day03.IntPair;
import xyz.lukemoll.aoc17.utils.PuzzleInputReader;

public class Day03 {

	public static void main(String[] args) {
		int input = Integer.parseInt(PuzzleInputReader.getPuzzleInputLine("day03.txt"));
		System.out.println("Steps from " + input + " to 1: " + getSteps(input)); // Part 1
	}

	public static int getSteps(int addr) {
		int shell = getShell(addr);	
		int steps = getToOrigin(addr, shell) + getToCentre(addr, shell);
		return steps;
	}
	
	public static int[] getAxes(int shell) {
		int aZero = (int) Math.pow(shell - 2, 2) + (shell - 1)/2;
		return Arrays.stream(new int[]{0,1,2,3})
				.map(n -> aZero + n * (shell - 1))
				.toArray(); 
	}
	
	public static int getShell(int addr) {
		int i = 1;
		// if addr is greater than i^2
		// keep going
		for(;i * i < addr; i +=2) {}
		return i;
	}
		
	public static int getToOrigin(int addr, int shell) {
		if (addr <=1) {
			return 0;
		}
		// else
		return Math.floorDiv(shell, 2);
	}

	public static int getToCentre(int addr, int shell) {
		return Arrays.stream(getAxes(shell))
				.map(axis -> Math.abs(addr - axis))
				.min().getAsInt();
	}
	
	
}

