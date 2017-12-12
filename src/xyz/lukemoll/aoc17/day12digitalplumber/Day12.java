package xyz.lukemoll.aoc17.day12digitalplumber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import xyz.lukemoll.aoc17.utils.PuzzleInputReader;

public class Day12 {

	public static void main(String[] args) {
		String[] input = PuzzleInputReader.getPuzzleInputLines("day12.txt");
		Map<Integer, Node> nodes = getAllNodes(input);
		
		// Part 1
		Set<Integer> connected = getConnectedTo(0, nodes);
		System.out.println(connected.size() + " programs in the same group as 0");
		
		// Part 2
		ArrayList<Set<Integer>> groups = splitToGroups(nodes);
		System.out.println(groups.size() + " groups overall"); 
	}
	
	private static class Node {
		private final int address;
		private final int[] connected;
		
		Node(int address, int[] connected) {
			this.address = address;
			this.connected = connected;
		}
		
		public boolean isConnectedTo(int address) {
			for(int connectedAddr: this.connected) {
				if(address==connectedAddr) {
					return true;
				}
			}
			return false;
		}
		
		private static Pattern linePattern = Pattern.compile("^(\\d+)\\s<->\\s(.*)$");
		public static Node parseLine(String line) {
			Matcher m = linePattern.matcher(line);
			
			if(m.matches()) {
				int address = Integer.parseInt(m.group(1));
				int[] connected = Arrays.stream(m.group(2).split(","))
						.map(s->s.trim())
						.mapToInt(Integer::parseInt).toArray();
				return new Node(address, connected);
			}
			else {
				throw new Error("Line does not match pattern!");
			}
	
		}
		
	}
	
	
	
	public static Set<Integer> getConnectedTo(int connectedAddress, Map<Integer, Node> nodes) {
		// connected serves as a list of visited nodes
		Set<Integer> connected = new HashSet<Integer>();	
		connected.add(connectedAddress);
		getConnectedTo(connectedAddress, nodes, connected);	
		return connected;
	}
	
	public static Set<Integer> getConnectedTo(Integer startingAddress, Map<Integer, Node> nodes, Set<Integer> visited) {
		Node n = nodes.get(startingAddress);
		visited.add(startingAddress);
		for(Integer child : n.connected) {
			if(!visited.contains(child)) {
				getConnectedTo(child, nodes, visited);
			}
		}
		return visited;
	}
	
	public static Map<Integer, Node> getAllNodes(String[] lines) {
		Map<Integer, Node> nodes = getExplicitNodes(lines);
		Map<Integer, Node> finalNodes = new HashMap<Integer, Node>(nodes);
		
		int[] implicitAddrs = nodes.entrySet().stream().map(e -> e.getValue().connected)
				.flatMapToInt(connected -> Arrays.stream(connected))
				.distinct()
				.filter(addr -> (finalNodes.get(addr) == null)).toArray();
		
		for(int addr : implicitAddrs) {
			int[] connected = finalNodes.entrySet().stream()
					.filter(e -> e.getValue().isConnectedTo(addr))
					.mapToInt(e -> e.getValue().address).toArray();
			finalNodes.put(addr, new Node(addr, connected));
		}
		return finalNodes;
	}
	
	public static Map<Integer, Node> getExplicitNodes(String[] lines) {
		Map<Integer, Node> nodes = new HashMap<Integer, Node>();
		
		for(String line : lines) {
			Node n = Node.parseLine(line);
			nodes.put(n.address, n);
		}
		
		return nodes;
	}
	
	public static ArrayList<Set<Integer>> splitToGroups(Map<Integer, Node> nodes) {
		// nodes is always non-empty
		Node a = nodes.get(nodes.entrySet().iterator().next().getKey());
		Set<Integer> connectedToA = getConnectedTo(a.address, nodes);
		connectedToA.add(a.address);
		nodes.entrySet().removeIf(e -> connectedToA.contains(e.getKey()));
		
		ArrayList<Set<Integer>> allGroups = nodes.size() > 0
				? splitToGroups(nodes)
				: new ArrayList<Set<Integer>>();;
		allGroups.add(connectedToA);
		return allGroups;
	}

	
}
