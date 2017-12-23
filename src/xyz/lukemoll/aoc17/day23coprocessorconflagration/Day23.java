package xyz.lukemoll.aoc17.day23coprocessorconflagration;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xyz.lukemoll.aoc17.day18duet.Day18;
import xyz.lukemoll.aoc17.day18duet.Day18.AbstractMachine;
import xyz.lukemoll.aoc17.utils.PuzzleInputReader;

public class Day23 {

	static class Coprocessor extends Day18.AbstractMachine {

		public int mulCount = 0;
		
		public Coprocessor(Consumer<AbstractMachine>[] program) {
			super(program);
		}
		
		public Coprocessor(Consumer<AbstractMachine>[] program, boolean debugMode) {
			super(program);
			if(!debugMode) {
				this.registers.put("a", 1L);
			}
		}

		public void snd(long value) {
			this.mulCount++;
		}
		// Don't need this
		public void rcv(String op1) {
//			dump();
		}
		
		public void dump() {
			System.out.println("-");
			for(String s : new String[]{"a","b","c","d","e","f","g","h"}) {
				System.out.println(" " + s + ": " + this.getRegisterOrValue(s));
			}
		}
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
				c.rcv("");
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
	
	public static void main(String[] args) {
		Consumer<AbstractMachine>[] program = Arrays.stream(PuzzleInputReader.getPuzzleInputLines("day23.txt"))
				.map(Day23::decode).toArray(n -> new Consumer[n]);
		Coprocessor pt1 = new Coprocessor(program, true);
		pt1.run();
		System.out.println(pt1.mulCount);

	}

}
