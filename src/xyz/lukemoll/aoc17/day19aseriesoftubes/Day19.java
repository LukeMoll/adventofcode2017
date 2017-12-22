package xyz.lukemoll.aoc17.day19aseriesoftubes;

import java.util.Arrays;
import xyz.lukemoll.aoc17.utils.PuzzleInputReader;

public class Day19 {

	public static void main(String[] args) {
		String[] lines = PuzzleInputReader.getPuzzleInputLines("day19.txt");	
		char[][] grid = Arrays.stream(lines).map(line -> line.toCharArray()).toArray(n -> new char[n][]);
		
		Result result = followPath(grid);
		System.out.println(result.PATH);// Part 1
		System.out.println(result.STEPS);// Part 2
	}
	
	static Result followPath(char[][] grid) {
		String topLine = new String(grid[0]);
		int y=0, x = topLine.indexOf("|");
		int steps = 0;
		Direction dir = Direction.DOWN;
		StringBuilder path = new StringBuilder(26);
		char current;
		do {
			current = grid[y][x];
			switch(current) {
				case'+': // Take the turning (should be exactly one choice)
					Direction temp = dir.opposite(); // Just so that comparison is "effectively final"
					dir = Arrays.stream(getDirections(grid,x,y)).filter(d -> !d.equals(temp))
							.findFirst().get();
				case'|':
				case'-': // Increment steps
					steps++;
				break;
				case' ': // Don't increment steps
					System.out.println("Encountered whitespace at " + x + "," + y);
				break;
				default: // Append to the path and increment steps
					//Should only be alphanumeric by now
					path.append(current);
					steps++;
				break;
			}
			x += dir.X;
			y += dir.Y;
		}
		while(current != ' ');

		return new Result(path.toString(), steps);
	}
	
	static Direction[] getDirections(char[][] grid, int x, int y) {
		return Arrays.stream(Direction.ALL).filter(d -> {
			return 	y + d.Y >= 0 && x + d.X >= 0 &&
					y + d.Y < grid.length &&
					x + d.X < grid[y + d.Y].length && 
					grid[y + d.Y][x + d.X] != ' ';
		}).toArray(n -> new Direction[n]);
	}
	
	private static class Result {
		final String PATH;
		final int STEPS;
		Result(String path, int steps) {
			this.PATH = path;
			this.STEPS = steps;
		}
	}
	
	public static class Direction {
		public final int X, Y;
		private Direction(int x,int y) {this.X = x; this.Y = y;}
		public static final  Direction UP = new Direction(0,-1),
								DOWN = new Direction(0,1),
								LEFT = new Direction(-1, 0),
								RIGHT = new Direction(1,0);
		public static final Direction[] ALL = new Direction[] {UP, DOWN, LEFT, RIGHT};
		@Override
		public boolean equals(Object obj) {
			return 	Direction.class.isInstance(obj) &&
					this.X == ((Direction) obj).X &&
					this.Y == ((Direction) obj).Y;
		}
		public Direction opposite() {return new Direction(this.X*-1, this.Y*-1);}
		public Direction left() {return new Direction(this.Y, -this.X);}
		public Direction right() {return new Direction(-this.Y, this.X);}
	}
}