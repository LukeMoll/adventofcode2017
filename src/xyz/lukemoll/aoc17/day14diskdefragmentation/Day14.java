package xyz.lukemoll.aoc17.day14diskdefragmentation;

import java.util.Arrays;
import xyz.lukemoll.aoc17.day10knothash.Day10;
import xyz.lukemoll.aoc17.utils.PuzzleInputReader;

public class Day14 {
	
	/* 0		1		binary from hex string
	 * Free		Used	defrag meaning
	 * .		#		e
	 * true		false
	 */

	public static void main(String[] args) {
		String input = PuzzleInputReader.getPuzzleInputLine("day14.txt");
		Boolean[][] grid = getRows(input);

		
		// Part 1
		long total = Arrays.stream(grid)
				.flatMap((Boolean[] row) -> Arrays.stream(row))
				.filter(bit -> !bit.booleanValue())// We want the count of the USED (false) bits
				.count();
		System.out.println(total + " used bits");
		
		// Part 2 is a 4-neighbourhood counting algorithm	
		System.out.println(countRegions(grid));
	}
	
	
	public static String knotHash(String input) {
		return Day10.toHexString(Day10.getDenseHash(
				Day10.knotHash(
						Day10.getStartingList(255), 
						Day10.toIntLengths(
								(input).toCharArray()), 
						64)));
	}
	
	public static Boolean[] toBitsViaHex(String hex) {
		Boolean[] bits = new Boolean[hex.length() * 4];
		for(int i=0;i<hex.length();i++) {
			System.arraycopy(to4Bits(hex.charAt(i)), 0, bits, i*4, 4);
		}
		return bits;
	}
	
	public static Boolean[] to4Bits(char hexDigit) {
		String binaryString = String.format("%04d", Integer.parseInt(Integer.toString(Integer.parseInt(hexDigit + "", 16), 2)));
		return binaryString.chars().mapToObj(c -> {
			switch(c) {
			case'0':
				return Boolean.TRUE;
			case'1':
				return Boolean.FALSE;
			default:
				System.out.println(c);
				return null;
			}
		}).filter(e -> e!=null).toArray(l -> new Boolean[l]);
	}
		
	public static Boolean[][] getRows(String input) {
		Boolean[][] rows = new Boolean[128][128];
		
		for(int i=0;i<128;i++) {
			rows[i] = toBitsViaHex(knotHash(input + "-" + i));
		}
		
		return rows;
	}
	
	public static int countRegions(Boolean[][] grid) {
		int count = 0;
		
		Boolean[][] counted = new Boolean[grid.length][grid.length];
		for(int y=0;y<counted.length;y++) {
			for(int x=0; x<counted[y].length; x++) {
				counted[y][x] = Boolean.FALSE;
			}
		}
		for(int x=0;x<grid.length;x++) {
			for(int y=0; y< grid.length; y++) {
				if(!counted[y][x] && !grid[y][x]) {
					counted = countUsed(grid, x,y,counted);
					count++;
				}
			}
		}
		
		return count;
	}
	
	public static Boolean[][] countUsed(Boolean[][] grid, int x, int y, Boolean[][] counted) {
		if(grid[y][x] || counted[y][x]) {// If the space is free or has been counted before
			counted[y][x] = true;
			return counted;
			// This is a dead end
		}
		// Else...
		counted[y][x] = true;
		if(y > 0) {
			counted = countUsed(grid, x, y-1, counted);
		}
		if(y < grid.length -1) {
			counted = countUsed(grid, x, y + 1, counted);
		}
		if(x > 0) {
			counted = countUsed(grid, x-1 , y, counted);
		}
		if(x < grid[y].length - 1) {
			counted = countUsed(grid, x+1, y, counted);
		}
		
		return counted;
	}
	
}