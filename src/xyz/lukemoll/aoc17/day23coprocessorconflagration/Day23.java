package xyz.lukemoll.aoc17.day23coprocessorconflagration;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.regex.*;
import xyz.lukemoll.aoc17.day18duet.Day18.AbstractMachine;
import xyz.lukemoll.aoc17.utils.PuzzleInputReader;

public class Day23 {

	static class Coprocessor extends AbstractMachine {

		public int mulCount = 0;
		public Coprocessor(Consumer<AbstractMachine>[] program, boolean debugMode) {
			super(program);
			if(!debugMode) {
				this.registers.put("a", 1L);
			}
		}

		public void snd(long value) {
			this.mulCount++;
		}
		
		public void rcv(String op1) {}// Don't need this
	}
	
	static Pattern INSTRUCTION_SET = Pattern.compile("^set\\s([a-z]+)\\s(-?[a-z0-9]+)$");
	static Pattern INSTRUCTION_SUB = Pattern.compile("^sub\\s([a-z]+)\\s(-?[a-z0-9]+)$");
	static Pattern INSTRUCTION_MUL = Pattern.compile("^mul\\s([a-z]+)\\s(-?[a-z0-9]+)$");
	static Pattern INSTRUCTION_JNZ = Pattern.compile("^jnz\\s(-?[a-z0-9]+)\\s(-?[a-z0-9]+)$");
	public static Consumer<AbstractMachine> decode(String instruction) {
		Matcher match = INSTRUCTION_SET.matcher(instruction);
		if(match.matches()) {
			String 	op1 = match.group(1),
					op2 = match.group(2);
			return c -> c.registers.put(op1, c.getRegisterOrValue(op2));
		}
		else if((match = INSTRUCTION_SUB.matcher(instruction)).matches()) {
			String 	op1 = match.group(1),
					op2 = match.group(2);
			return c -> c.registers.put(op1, c.registers.getOrDefault(op1, 0l) - c.getRegisterOrValue(op2));
		}
		else if((match = INSTRUCTION_MUL.matcher(instruction)).matches()) {
			String 	op1 = match.group(1),
					op2 = match.group(2);
			return c -> {
				c.snd(0);// Just reusing code
				c.registers.put(op1, c.registers.getOrDefault(op1, 0l) * c.getRegisterOrValue(op2));
			};
		}
		else if((match = INSTRUCTION_JNZ.matcher(instruction)).matches()) {
			String 	op1 = match.group(1),
					op2 = match.group(2);
			return c -> {
				if(c.getRegisterOrValue(op1) != 0) {
						int jumpAmount = (int) c.getRegisterOrValue(op2);
						if(jumpAmount != 0)
							c.programcounter += jumpAmount - 1;
					}
				};
		}
		else {
			throw new Error("No match!");
		}
	}
	
	static int getNumbersWithFactors(int bstart, int c, int difference) {
		int h =0;
		for(int b=bstart; b <= c; b += difference)  {
			h += hasFactors(b);
		}
		return h;
	}
	
	static int hasFactors(int b) {
		for(int d=2; d!=b; d++) {
	        for(int e=2; e<=(b/d); e++) {
	            if(d*e == b) {
	                return 1;// Normally f would be set to 0 and h would be incremented consequently.
	            }
	        }
	    }
		return 0;
	}
	
	public static void main(String[] args) {
		String[] input = PuzzleInputReader.getPuzzleInputLines("day23.txt");
		
		// Part 1
		@SuppressWarnings("unchecked") // Can't do n -> new Consumer<AbstractMachine>[n]
		Consumer<AbstractMachine>[] program = Arrays.stream(input)
				.map(Day23::decode).toArray(n -> new Consumer[n]);
		Coprocessor pt1 = new Coprocessor(program, true);
		pt1.run();
		System.out.println("Part 1: " + pt1.mulCount);
		
		// Part 2
		if(program.length == 32) {
			Consumer<AbstractMachine>[] header = Arrays.copyOf(program, 8);
			Coprocessor pt2 = new Coprocessor(header, false);
			pt2.run();
			int difference = 17;
			if(input[30].startsWith("sub b -")) {
				difference = Integer.parseInt(input[30].substring(7));
			}
			int h = getNumbersWithFactors(	pt2.registers.get("b").intValue(),
											pt2.registers.get("c").intValue(),
											difference);
			System.out.println(h + " numbers with factors");
		}
		else {
			System.out.println("Unexpected program length; problem may have changed");
		}
	}
}