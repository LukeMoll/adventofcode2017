package xyz.lukemoll.aoc17.day22sporificavirus;

import java.util.*;
import java.util.stream.Collectors;
import xyz.lukemoll.aoc17.day19aseriesoftubes.Day19.Direction;
import xyz.lukemoll.aoc17.utils.PuzzleInputReader;

public class Day22 {

	public static void main(String[] args) {
		String[]  input = PuzzleInputReader.getPuzzleInputLines("day22.txt");
		Coordinate startingCoordinate = getStartingCoordinate(input);
		Set<Coordinate> initialInfected = getInitialMap(input);
		int infectedPt1 = runBurstsPt1(initialInfected, startingCoordinate, 10000);
		System.out.println("Part 1: " + infectedPt1 + " infection bursts");
		int infectedPt2 = runBurstsPt2(initialInfected, startingCoordinate, 10000000);
		System.out.println("Part 2: " + infectedPt2 + " infection bursts");
	}
	
	static int runBurstsPt1(Set<Coordinate> initailInfected, Coordinate starting, int iterations) {
		Set<Coordinate> infected = new HashSet<Coordinate>(initailInfected);
		Coordinate current = starting;
		Direction direction = Direction.UP;
		int infectionBursts = 0;
		for(int i=0;i<iterations; i++) {
			direction = (infected.contains(current))?direction.right():direction.left();
			if(infected.contains(current)) {
				infected.remove(current);
			}
			else {
				infected.add(current);
				infectionBursts++;
			}
			current = new Coordinate(current.X + direction.X, current.Y + direction.Y);			
		}
		return infectionBursts;
	}
	
	static int runBurstsPt2(Set<Coordinate> infected, Coordinate starting, int iterations) {
		Map<Coordinate,NodeState> map = infected.stream().collect(Collectors.toMap((Coordinate c) -> c, c -> NodeState.INFECTED));
		Coordinate current = starting;
		Direction direction = Direction.UP;
		int infectionBursts = 0;
		for(int i=0; i<iterations; i++) {
			NodeState state = map.getOrDefault(current, NodeState.CLEAN);
			switch(state) {
			case CLEAN:
				direction = direction.left();
				break;
			case WEAKENED:
				infectionBursts++;
				break;
			case INFECTED:
				direction = direction.right();
				break;
			case FLAGGED:
				direction = direction.opposite();
				break;
			}
			map.put(current, state.next());
			current = new Coordinate(current.X + direction.X, current.Y + direction.Y);
		}
		return infectionBursts;
	}
	
	static Coordinate getStartingCoordinate(String[] input) {
		int y = (int) Math.ceil(input.length/2);
		int x = (int) Math.ceil(input[y].length()/2);
		return new Coordinate(x,y);
	}
	
	static Set<Coordinate> getInitialMap(String[] input) {
		final char CHAR_INFECTED = '#';
		HashSet<Coordinate> infected = new HashSet<Coordinate>();
		for(int y=0; y<input.length; y++) {
			for(int x=0; x<input[y].length(); x++) {
				if(input[y].charAt(x) == CHAR_INFECTED) {
					infected.add(new Coordinate(x,y));
				}
			}
		}
		return infected;
	}
	
	enum NodeState {
		CLEAN(0), WEAKENED(1), INFECTED(2), FLAGGED(3);
		private final static NodeState[] STATES = {CLEAN, WEAKENED, INFECTED, FLAGGED};
		private final int v;
		private NodeState(int v) {this.v=v;}
		public NodeState next() {return STATES[(this.v+1)%4];}
	}
	
	static class Coordinate {
		final int X, Y;
		Coordinate(int x, int y) {
			this.X = x;
			this.Y = y;
		}
		@Override
		public boolean equals(Object obj) {
			return 	Coordinate.class.isInstance(obj) &&
					((Coordinate) obj).X == this.X &&
					((Coordinate) obj).Y == this.Y;
		}
		@Override
		public int hashCode() {
			return Objects.hash(X, Y);
		}
		@Override
		public String toString() {
			return "(" + this.X + "," + this.Y + ")";
		}
	}

}
