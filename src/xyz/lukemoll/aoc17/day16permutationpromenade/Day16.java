package xyz.lukemoll.aoc17.day16permutationpromenade;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import xyz.lukemoll.aoc17.utils.PuzzleInputReader;

public class Day16 {

	public static void main(String[] args) {
		String[] instructions = PuzzleInputReader.getPuzzleInputLine("day16.txt").split(",");
		System.out.println(stringify(execute(getCharRange('a',16),instructions)));// part 1

		int[] positionalChanges = getPositionalChanges(instructions, 16);
		Character[] valueChanges = getValueChanges(instructions,16);
		Character[] program = getCharRange('a',16);
		Instant before = Instant.now();
		for(int i=0; i<1e9; i++) {
			program = apply(program, positionalChanges, valueChanges);
		}
		Instant after = Instant.now();
		System.out.println(stringify(program));
		System.out.println(before.until(after, ChronoUnit.MILLIS) + "ms");
	}
	
	public static Character[] getCharRange(char starting, int count) {
		return IntStream.range(starting, starting + count).mapToObj(i -> (char) i).toArray((int l) -> {return new Character[l];});
	}
	
	public static Character[] execute(Character[] starting, String[] instructions) {
		Character[] programs = Arrays.copyOf(starting, starting.length);
		for(String i : instructions) {
			if(i.startsWith("s")) {
				programs = spin(programs, Integer.parseInt(i.substring(1)));
			}
			else if(i.startsWith("x")) {
				String[] params = i.substring(1).split("/");
				programs = exchange(programs, Integer.parseInt(params[0]), Integer.parseInt(params[1]));
			}
			else if(i.startsWith("p")) {
				String[] params = i.substring(1).split("/");
				programs = partner(programs, params[0].charAt(0), params[1].charAt(0));
			}
			else {System.out.println("Unrecognised instruction: " + i);}
		}
		return programs;		
	}
	
	public static Character[] spin(Character[] programs, int count) {
		Character[] newProgs = new Character[programs.length];
		System.arraycopy(programs, programs.length - count, newProgs, 0, count);
		System.arraycopy(programs, 0, newProgs, count, programs.length - count);
		return newProgs;
	}
	
	public static Character[] exchange(Character[] programs, int addr1, int addr2) {
		Character temp = programs[addr1];
		programs[addr1] = programs[addr2];
		programs[addr2] = temp;
		return programs;
	}
	
	public static Character[] partner(Character[] programs, Character prog1, Character prog2) {
		int addr1 = -1, addr2 = -1;
		for(int i=0; i<programs.length; i++) {
			if(programs[i].equals(prog1)) {addr1 = i;}
			else if(programs[i].equals(prog2)) {addr2 = i;}
		}
		if(addr1 >= 0 && addr2 >= 0) {
			return exchange(programs, addr1, addr2);
		}
		else {
			throw new Error("Elements not found");
		}
	}
	
	public static Character[] apply(Character[] programs, int[] positionalChanges, Character[] valueChanges) {
		Character[] newPrograms = new Character[programs.length];
		int baseCode = (int) 'a';
		for(int old=0; old<programs.length; old++) {
			newPrograms[positionalChanges[old]] = valueChanges[programs[old]-baseCode];
		}
		return newPrograms;
	}
	
	
	public static int[] getPositionalChanges(String[] instructions, int length) {
		Character[] indicies = getCharRange((char) 0, length);
		for(String i : instructions) {
			if(i.startsWith("s")) {
				indicies = spin(indicies, Integer.parseInt(i.substring(1)));
			}
			else if(i.startsWith("x")) {
				String[] params = i.substring(1).split("/");
				indicies = exchange(indicies, Integer.parseInt(params[0]), Integer.parseInt(params[1]));
			}
		}
		return invert(Arrays.stream(indicies).mapToInt(c -> c.charValue()).toArray());
	}
	
	public static int[] invert(int[] array) {
		int[] inverted = new int[array.length];
		for(int i=0; i<array.length; i++) {
			inverted[array[i]] = i;
		}
		return inverted;
	}
	
	public static Character[] getValueChanges(String[] instructions, int length) {
		Character[] values = getCharRange('a', length);
		for(String i : instructions) {
			if(i.startsWith("p")) {
				String[] params = i.substring(1).split("/");
				values = partner(values, params[0].charAt(0), params[1].charAt(0));
			}
		}
		return values;
	}
	
	public static String stringify(Character[] array) {
		return Arrays.stream(array).map(c -> c.toString()).collect(Collectors.joining());
	}
}
