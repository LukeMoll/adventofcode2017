package xyz.lukemoll.aoc17.day24electromagneticmoat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import xyz.lukemoll.aoc17.utils.PuzzleInputReader;

public class Day24 {

	public static void main(String[] args) {
		String[] input = PuzzleInputReader.getPuzzleInputLines("day24.txt");
		List<Component> components = Arrays.stream(input).map(Component::parse).collect(Collectors.toList());
		System.out.println("Enumerating...");
		Instant before = Instant.now();
		List<Bridge> possibleBridges = enumerateBridges(components, null, 0);
		Instant after = Instant.now();
		System.out.println(possibleBridges.size() + " bridges found! (" + before.until(after, ChronoUnit.MILLIS) + "ms)");
		// Part 1
		Bridge strongest = possibleBridges.stream()
			.sorted((b1,b2)-> (int)Math.signum(b2.getScore() - b1.getScore()))
			.findFirst().get();
		// Part 2
		Bridge longest = possibleBridges.stream()
				.sorted((b1,b2) -> {
					int byLength = (int) Math.signum(b2.getLength() - b1.getLength());
					return byLength!=0?byLength:(int) Math.signum(b2.getScore() - b1.getScore());
				}).findFirst().get();
		
		System.out.println("Strongest bridge: " + strongest);
		System.out.println("Longest bridge: " + longest);
	}
	
	static List<Bridge> enumerateBridges(List<Component> availableComponents, Component root, int startsWith) {
		List<Bridge> bridges = new ArrayList<Bridge>();
		Component[] possibleNext = availableComponents.parallelStream()
				.filter(c -> c.LEFT == startsWith || c.RIGHT == startsWith)
				.toArray(n -> new Component[n]);
		if(possibleNext.length > 0) {
			for(Component c : possibleNext) {
				List<Component> aC = new ArrayList<Component>(availableComponents);
				aC.remove(c);
				int sW = startsWith == c.LEFT?c.RIGHT:c.LEFT;
				List<Bridge> possibleBridges = enumerateBridges(aC, c,sW);
				for(Bridge b : possibleBridges) {
					b.addToFront(c);
					bridges.add(b);
				}
			}			
		}
		else {
			bridges.add(new Bridge());
		}
		return bridges;
	}
	
	static class Bridge {
		private final LinkedList<Component> components = new LinkedList<Component>();
		private int strength;
		public Bridge() {
			strength = 0;
		}
		public void addToFront(Component c) {
			components.addFirst(c);
			this.strength += c.SCORE;
		}
		@Override
		public String toString() {
			return "(" + this.strength + ")[" + this.getLength() + "] " + components.stream().map(c -> c.toString())
					.collect(Collectors.joining("--", "", ""));
		}
		public int getScore() {return strength;}
		public int getLength() {return components.size();}
	}
	static class Component {
		public final int LEFT, RIGHT, SCORE;
		private Component(int left, int right) {
			this.LEFT = left > right?left:right;
			this.RIGHT = left > right?right:left;
			this.SCORE = LEFT + RIGHT;// Save ourselves a calculation later
		}
		static Component parse(String line) {
			String[] split = line.split("/");
			return new Component(	Integer.parseInt(split[0].trim()),
									Integer.parseInt(split[1].trim()));
		}
		@Override
		public String toString() {
			return this.LEFT + "/" + this.RIGHT;
		}
	}
}