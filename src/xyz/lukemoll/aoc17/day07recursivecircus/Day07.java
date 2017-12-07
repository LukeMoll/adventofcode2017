package xyz.lukemoll.aoc17.day07recursivecircus;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xyz.lukemoll.aoc17.utils.PuzzleInputReader;

public class Day07 {

	private static class ProgramNode {
		private final String name;
		private final int weight;
		private final ProgramNode[] children;
		
		ProgramNode(String name, int weight, ProgramNode[] children) {
			this.name = name;
			this.weight = weight;
			this.children = children;
		}
		
		private static final Pattern linePattern = Pattern.compile("^([a-z]+)\\s\\((\\d+)\\)(?:\\s->\\s(.*))?$");

		public static ProgramNode buildNode(String line, Map<String,String> treeList) {
			Matcher m = linePattern.matcher(line);
			if(m.matches()) {
				String name = m.group(1);
				int weight = Integer.parseInt(m.group(2));
				ProgramNode[] children;
				if(m.group(3) != null) {
					children = Arrays.stream(m.group(3).split(","))
						.map(s -> s.trim())
						.map((String childName) -> treeList.get(childName))
						.map(childLine -> buildNode(childLine, treeList))
						.toArray(n -> new ProgramNode[n]);
				}
				else {
					children = new ProgramNode[0];
				}
				return new ProgramNode(name,weight,children);
			}
			else {
				throw new Error("Line did not match");
			}
		}
		
		public static Map<String, String> toMap(String[] lines) {
			HashMap<String, String> treeList = new HashMap<String, String>();
			for(String line : lines) {
				try {
					Matcher m = linePattern.matcher(line);
					m.find();
					treeList.put(m.group(1), line);
				}
				catch(IllegalStateException e) {
					System.out.println(line + "\t" + line.length());
					System.exit(-1);
				}
			}
			return Collections.unmodifiableMap(treeList);
		}
		
		public static String getStartingLine(Map<String,String> m) {
			return getStartingLine(m, m.entrySet().iterator().next().getKey());
		}
		
		public static String getStartingLine(Map<String,String> m, String name) {
			for(Entry<String, String> e : m.entrySet()) {
				if(e.getValue().contains(name + ",")) {
					return getStartingLine(m, e.getKey());
				}
			}
			// No entries are the parent of our name
			return m.get(name);
		}
		
		public static ProgramNode getInbalancedNode(ProgramNode startingNode) {
			if(startingNode.areChildrenBalanced()) {
				return startingNode;
			}
			return getInbalancedNode(Arrays.stream(startingNode.children)
					.filter(c -> !c.isBalanced())
					.findFirst() // only one child should be inbalanced
					.orElse(startingNode));			
		}
		
		public boolean isParentOf(ProgramNode n) {
			for(ProgramNode child : this.children) {
				if (n.equals(child)) {
					return true;
				}
			}
			return false;
		}
		
		public String getName() {
			return name;
		}

		public int getWeight() {
			return weight;
		}

		public int getTotalWeight() {
			return this.weight + Arrays.stream(this.children)
				.mapToInt(child -> child.getTotalWeight())
				.sum();
		}
		
		public boolean isBalanced() {
			int[] childWeights = Arrays.stream(this.children)
					.mapToInt(child -> child.getTotalWeight()).toArray();
			if(childWeights.length == 0) {
				return true;
			}
			else {
				int first = childWeights[0];
				for(int weight : childWeights) {
					if(weight != first) {
						return false;
					}
				}
				return true;
			}
		}
		
		public boolean areChildrenBalanced() {
			for(ProgramNode child : this.children) {
				if(!child.isBalanced()) {
					return false;
				}
			}
			return true;
		}
		
		public ProgramNode[] getChildren() {
			return children;
		}
		
		public String toString() {
			String output = this.name;
			if(!this.isBalanced()) {
				output += "*";
			}
			output += String.join("", Collections.nCopies(10 - this.name.length(), " "));
			output += "\t(" + this.getWeight() + ")";
			
			if(this.children.length > 0) {
				output += "\t" + this.getTotalWeight();
			}
			
			return output;
		}
		
	}

	public static void main(String[] args) {
		String[] input = PuzzleInputReader.getPuzzleInputLines("day07.txt");
		Map<String,String> lines = ProgramNode.toMap(input);
		ProgramNode bottomProgram = ProgramNode.buildNode(ProgramNode.getStartingLine(lines), lines);
		
		System.out.println("Bottom program: " + bottomProgram.getName()); // Part 1
		System.out.println();
		ProgramNode inbalancedNode = ProgramNode.getInbalancedNode(bottomProgram);
		System.out.println("Imbalanced node: " + inbalancedNode);
		System.out.println("Children:");
		Arrays.stream(inbalancedNode.getChildren()).forEach(child -> System.out.println(" "+ child));
	}

}
