package xyz.lukemoll.aoc17.day03spiralmemory;

import java.util.*;
import java.util.function.*;

import xyz.lukemoll.aoc17.utils.PuzzleInputReader;

public class Day03 {

	public static void main(String[] args) {	
		int input = Integer.parseInt(PuzzleInputReader.getPuzzleInputLine("day03.txt"));
		System.out.println("Steps from " + input + " to 1: " + getSteps(input));
		enumerateGrid(input);
	}

	// Part 1
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
	
	
	public static int getSteps(int addr) {
		int shell = getShell(addr);	
		int steps = getToOrigin(addr, shell) + getToCentre(addr, shell);
		return steps;
	}
	
	// Part 2
	static class Point {
		final int X, Y;
		public Point(int x, int y) {this.X = x; this.Y = y;}
		public String toString() {return "(" + X + "," + Y + ")";}
		@Override
		public boolean equals(Object other) {
			if(other.getClass() == this.getClass()) {
				Point otherPoint = (Point) other;
				return otherPoint.X == this.X && otherPoint.Y == this.Y;
			}
			else return false;
		}
		@Override
		public int hashCode() {
			return this.X * this.Y;
		}
	}
	
	enum Direction {
		UP, DOWN, LEFT, RIGHT
	}
	
	private static boolean pt2_running = true;
	
	public static void enumerateGrid(int haltValue) {
		
		Map<Point, Integer> map = new HashMap<Point, Integer>();
		Point previous = new Point(0,0);
		map.put(previous, 1);
		Consumer<Integer> whenFound = val -> {
			if(pt2_running) {
				pt2_running = false;
				System.out.println("First value greater than " + haltValue + ": " + val);				
			}
		};
		for(int i=1; pt2_running; i += 2) {
			previous = addElements(map, Direction.RIGHT,i, previous, haltValue, whenFound);
			previous = addElements(map, Direction.UP, 	i, previous, haltValue, whenFound);
			previous = addElements(map, Direction.LEFT, i+1, previous, haltValue, whenFound);
			previous = addElements(map, Direction.DOWN, i+1,previous, haltValue, whenFound);
		}
	}
	
	public static Point addElements(Map<Point, Integer> map, Direction dir, int count, Point argPrevious, int haltValue, Consumer<Integer> whenFound) {
		Function<Point, Point> next = nextPoint(dir);
		Point loopPrevious = argPrevious;
		int sum;
		for(int i=0; i<count; i++) {
			loopPrevious = next.apply(loopPrevious);
			sum = getSum(map, loopPrevious);
			if(!map.containsKey(loopPrevious)) {
				map.put(loopPrevious, sum);				
			}
			if(sum > haltValue) {
				whenFound.accept(sum);
				return loopPrevious;
			}
		}
		return loopPrevious;
	}
	
	public static Function<Point, Point> nextPoint(Direction dir) {
		switch(dir) {
		case UP:
			return prev -> new Point(prev.X, prev.Y + 1);
		case DOWN:
			return prev -> new Point(prev.X, prev.Y - 1);
		case LEFT:
			return prev -> new Point(prev.X - 1, prev.Y);
		case RIGHT:
			return prev -> new Point(prev.X + 1, prev.Y);
		default: //Unreachable
			System.out.println("Uhh what");
			return prev -> prev;
		}
	}
	
	public static Point[] getNeighbours(Point p) {
		return new Point[] {
				new Point(p.X + 1, p.Y + 0),
				new Point(p.X + 1, p.Y + 1),
				new Point(p.X + 0, p.Y + 1),
				new Point(p.X - 1, p.Y + 1),
				new Point(p.X - 1, p.Y + 0),
				new Point(p.X - 1, p.Y - 1),
				new Point(p.X + 0, p.Y - 1),
				new Point(p.X + 1, p.Y - 1),
		};
	}
	
	public static int getSum(Map<Point, Integer> map, Point p) {
		Point[] neighbours = Arrays.stream(getNeighbours(p))
				.filter(n -> map.containsKey(n)).toArray(length -> new Point[length]);
		return Arrays.stream(neighbours)
				.mapToInt(n -> map.get(n))
				.sum();
	}
		
}