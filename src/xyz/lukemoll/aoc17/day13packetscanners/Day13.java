package xyz.lukemoll.aoc17.day13packetscanners;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import xyz.lukemoll.aoc17.utils.PuzzleInputReader;

public class Day13 {

	static class PacketScanner {
		private final int range;
		private int position;
		private boolean reversing = false;
		
		public PacketScanner(int depth) {
			this.range = depth;
			this.position = 0;
		}
		
		public boolean isActive() {
			return position == 0;
		}
		
		public void increment() {
			this.position += reversing?-1:1;
			if(this.position==this.range-1 && !reversing) {
				this.reversing = true;
			}
			else if(this.position == 0 && reversing) {
				this.reversing = false;
			}
		}

		public int getRange() {
			return this.range;
		}
	}
	
	public static void main(String[] args) {
		String[] input = PuzzleInputReader.getPuzzleInputLines("day13.txt");
		int severity = getTotalSeverity(input, 0);
		System.out.println("Total severity for 0 delay: " + severity); // Part 1
		System.out.println("Minimum delay for 0 severity: " + getMinimumDelay(input)); // Part 2
	}
	
	public static int getTotalSeverity(String[] input, int delay) {
		Map<Integer, PacketScanner> scanners = parseScanners(input);
		int stop = scanners.keySet().stream().mapToInt(I -> (int)I).max().getAsInt();
		int p = -1 - delay, severity = 0;
		for(;p<=stop;) {
			p++;
			severity += checkScanners(scanners, p);
			scanners.values().stream().forEach(s -> s.increment());
		}
		return severity;
	}
	
	public static boolean verify(String[] input, int delay) {
		Map<Integer, PacketScanner> scanners = parseScanners(input);
		long items = scanners.entrySet().stream().filter(e -> {
			return (e.getKey()+delay)%(-2 + 2*e.getValue().getRange()) == 0;
		}).count();	
		
		return items == 0;
	}
	
	// So horribly inefficient :c
	public static int getMinimumDelay(String[] input) {
		int delay = -1;
		do {
			delay++;
		}
		while(!verify(input, delay));
		return delay;
	}
	
	public static int checkScanners(Map<Integer, PacketScanner> scanners, int packetDepth) {
		PacketScanner ps = scanners.get(packetDepth);
		if(ps == null) {
			return 0;
		}
		else {
			if(ps.isActive()) {
				return packetDepth * ps.getRange();
			}
			else {
				return 0;
			}
		}
	}
	
	public static Map<Integer, PacketScanner> parseScanners(String[] lines) {
		Map<Integer, PacketScanner> scanners = new HashMap<Integer, PacketScanner>();
		
		for(String line : lines) {
			String[] parts = line.split(":");
			scanners.put(Integer.parseInt(parts[0].trim()),
						new PacketScanner(Integer.parseInt(parts[1].trim())));
		}
		
		return scanners;
	}

}
