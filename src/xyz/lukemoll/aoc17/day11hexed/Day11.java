package xyz.lukemoll.aoc17.day11hexed;

import xyz.lukemoll.aoc17.utils.PuzzleInputReader;

public class Day11 {

	public static void main(String[] args) {
		String input = PuzzleInputReader.getPuzzleInputLine("day11.txt");
		String[] stepArr = input.split(",");
		System.out.println(stepArr.length + " steps in total");
		System.out.println(getMinimumSteps(stepArr).minimumSteps + " steps in minimum path");
		System.out.println(getMinimumSteps(stepArr).maxminSteps + " steps away at furthest");
	}

	public static Day11.Result getMinimumSteps(String[] stepArr) {		
		int maxMinSteps = 0;
		int A=0,B=0,C=0;
		// Only have to iterate array once
		for(String s : stepArr) {
			switch(s) {
				case"nw":
					A++;
					break;
				case "se":
					A--;
					break;
				case "ne":
					B++;
					break;
				case "sw":
					B--;
					break;
				case "n":
					C++;
					break;
				case "s":
					C--;
					break;
			}
			int currentShortest = getShortestPath(A,B,C);
			if(currentShortest > maxMinSteps) {
				maxMinSteps = currentShortest;
			}
		}
		Result r = new Result();
		r.minimumSteps = getShortestPath(A,B,C);
		r.maxminSteps = maxMinSteps;
		return r;
	}
	
	public static int getShortestPath(int A, int B, int C) {
		int eastwest = B - A;
		float northsouth = C + (A + B)/2;
		return (int) (Math.abs(eastwest) + Math.abs(Math.abs(northsouth)-Math.abs(eastwest/2))); 
	}
		
	private static class Result {
		int minimumSteps;
		int maxminSteps;
	}
	
 }
