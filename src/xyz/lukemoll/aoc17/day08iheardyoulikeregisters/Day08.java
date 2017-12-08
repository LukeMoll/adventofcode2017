package xyz.lukemoll.aoc17.day08iheardyoulikeregisters;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xyz.lukemoll.aoc17.utils.PuzzleInputReader;

public class Day08 {

	public static void main(String[] args) {	
		String[] input = PuzzleInputReader.getPuzzleInputLines("day08.txt");
		
		Map<String, Integer> finalRegisters = processInstructions(input);
		Entry<String, Integer> endMax = finalRegisters.entrySet().stream().filter(e -> !e.getKey().equals(KEY_MAX)).max((e1, e2) -> (int)Math.signum(e1.getValue() - e2.getValue())).get();
		System.out.println("Maxiumum at end is '" + endMax.getKey() + "': " + endMax.getValue());// Part 1
		System.out.println("Maxiumum of all time is " + finalRegisters.get(KEY_MAX));// Part 2
	}
	
	static Map<String, Integer> processInstructions(String[] instructions) {
		Map<String, Integer> registers = new HashMap<String, Integer>();
		Pattern instructionPattern = Pattern.compile("^([a-z]+)\\s(inc|dec)\\s(-?\\d+)\\sif\\s([a-z]+)\\s(<|>|[=><!]=)\\s(-?\\d+)$");
		for(String s : instructions) {
			Matcher match = instructionPattern.matcher(s);
			if(!match.matches()) {
				throw new Error("Instruction does not match pattern.");
			}
			// Else...
			String 	address1 = match.group(1), address2 = match.group(4), 
					operator = match.group(2), comparison = match.group(5);
			int arithVal = Integer.parseInt(match.group(3)), compVal = Integer.parseInt(match.group(6));
			registers = executeOne(address1, operator, arithVal, address2, comparison, compVal, registers);
		}
		
		
		return Collections.unmodifiableMap(registers);
	}
	
	static Map<String, Integer> executeOne(String address1, String operator, int arithVal, String address2, String comparison, int compVal, Map<String, Integer> registers) {
		BiPredicate<Integer, Integer> comparisonPred = getBiPredicate(comparison);
		int lhs = registers.getOrDefault(address2, 0);
		if(comparisonPred.test(lhs, compVal)) {
			Integer value;
			
			if(operator.equals("inc")) {
				value = registers.getOrDefault(address1, 0) + arithVal;
			}
			else if(operator.equals("dec")) {
				value = registers.getOrDefault(address1, 0) - arithVal;	
			}
			else {
				throw new Error("Invalid operator");
			}
			
			registers.put(address1, value);
			registers = updateMax(registers, value);
			return registers;
		}
		return registers;
		
	}
	
	static BiPredicate<Integer, Integer> getBiPredicate(String comparison) {
		switch(comparison) {
			case ">":
				return (a1, a2) -> a1 > a2;
			case "<":
				return (a1, a2) -> a1 < a2;
			case "==":
				return (a1, a2) -> a1.equals(a2);
			case "!=": 
				return (a1, a2) -> !a1.equals(a2);
			case ">=":
				return (a1, a2) -> a1 >= a2;
			case "<=":
				return (a1, a2) -> a1 <= a2;
			default:
				throw new Error("Invalid comparison type!");
		}
	}
	
	static final String KEY_MAX = "__MAX__";
	
	static Map<String, Integer> updateMax(Map<String, Integer> registers, int potentialMax) {
		if(registers.containsKey(KEY_MAX)) {
			if(potentialMax > registers.get(KEY_MAX)) {
				registers.put(KEY_MAX, potentialMax);
			}
		}
		else {
			// no maximum
			// we can't use .getOrDefault(,0) because there might never be a positive value
			registers.put(KEY_MAX, potentialMax);
		}
		return registers;
	}
	
}
