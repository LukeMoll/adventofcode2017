package xyz.lukemoll.aoc17.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class PuzzleInputReader {

	public static String getPuzzleInputLine(String filename) {
		File inputFile = new File("input/" + filename);
		try {			
			BufferedReader br = new BufferedReader(new FileReader(inputFile));
			String input = br.readLine();
			br.close();
			return input;
		}
		catch(FileNotFoundException e) {
			System.out.println("File not found: " + inputFile.getAbsolutePath());
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String[] getPuzzleInputLines(String filename) {
		File inputFile = new File("input/" + filename);
		try {			
			BufferedReader br = new BufferedReader(new FileReader(inputFile));
			String line;
			ArrayList<String> lines = new ArrayList<String>();
			while((line = br.readLine()) != null) {
				lines.add(line);
			}
			
			br.close();
			return lines.toArray(new String[lines.size()]);
		}
		catch(FileNotFoundException e) {
			System.out.println("File not found: " + inputFile.getAbsolutePath());
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
