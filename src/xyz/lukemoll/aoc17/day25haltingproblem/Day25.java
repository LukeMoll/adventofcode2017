package xyz.lukemoll.aoc17.day25haltingproblem;

import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;
import xyz.lukemoll.aoc17.utils.PuzzleInputReader;

public class Day25 {

	public static void main(String[] args) {
		String[] input = PuzzleInputReader.getPuzzleInputLines("day25.txt");
		Pattern PATTERN_INITSTATE = Pattern.compile("Begin in state (\\w+)\\.");
		Pattern PATTERN_STEPS = Pattern.compile("Perform a diagnostic checksum after (\\d+) steps\\.");
		Matcher match_state = PATTERN_INITSTATE.matcher(input[0]);
		match_state.find();
		String initState = match_state.group(1);
		Matcher match_steps = PATTERN_STEPS.matcher(input[1]);
		match_steps.find();
		int steps = Integer.parseInt(match_steps.group(1));
		Map<String, Rule> rules = getRules(input); 
		TuringMachine m = new TuringMachine(initState, rules);
		for(int i=0; i<steps; i++) {
			m.iterate();
		}
		System.out.println("Tape contains " + m.tape.size() + "1's after " + steps + " steps.");
	}
	
	
	static final Pattern PATTERN_RULE = Pattern.compile("In state (\\w+):(?:\\s+If the current value is ([01]+):\\s+- Write the value ([01]+)\\.\\s+- Move one slot to the (left|right)\\.\\s+- Continue with state (\\w+)\\.)(?:\\s+If the current value is ([01]+):\\s+- Write the value ([01]+)\\.\\s+- Move one slot to the (left|right)\\.\\s+- Continue with state (\\w+)\\.)");
	static Map<String, Rule> getRules(String[] input) {
		Map<String, Rule> rules = new HashMap<String, Rule>();
		Matcher m = PATTERN_RULE.matcher(Arrays.stream(input).collect(Collectors.joining("\n")));
		while(m.find()) {
			Rule r = Rule.parse(m.toMatchResult());
			rules.put(r.STATE, r);
		}
		return Collections.unmodifiableMap(rules);
	}

	static class TuringMachine {
		final Set<Integer> tape = new HashSet<Integer>();
		int tapePosition = 0;
		String state;
		final Map<String, Rule> rules;
		public TuringMachine(String initialState, Map<String, Rule> rules) {
			this.state = initialState;
			this.rules = rules;
		}
		public void iterate() {
			if(!rules.containsKey(state)) {throw new Error("No rule for state '" + state + "'");}
			Rule r = rules.get(state);
			if(tape.contains(new Integer(tapePosition))) {// Tape reads "1"
				write(r.ONE.WRITE_VALUE);
				tapePosition += r.ONE.TAPE_DIRECTION;
				state = r.ONE.STATE_CHANGE;
			}
			else {// Tape reads "0"
				write(r.ZERO.WRITE_VALUE);
				tapePosition += r.ZERO.TAPE_DIRECTION;
				state = r.ZERO.STATE_CHANGE;
			}
		}
		void write(int i) {
			if(i==1) {
				tape.add(new Integer(tapePosition));
			}
			else {
				tape.remove(new Integer(tapePosition));
			}
		}
	}
	
	static class Rule {
		static class Action {
			public static final int TAPE_LEFT = -1, TAPE_RIGHT = 1;
			public final int WRITE_VALUE;
			public final int TAPE_DIRECTION;
			public final String STATE_CHANGE;
			public Action(int writeVal, int tapeDir, String stateChange) {
				this.WRITE_VALUE = writeVal;
				this.TAPE_DIRECTION = tapeDir;
				this.STATE_CHANGE = stateChange;
			}
		}
		public final String STATE;
		public final Action ZERO, ONE;
		Rule(String state, Action zero, Action one) {
			this.STATE = state;
			this.ZERO = zero;
			this.ONE = one;
		}
		public static Rule parse(MatchResult m) {
			String state = m.group(1);
			Action zero= new Action(Integer.parseInt(m.group(3)),
									m.group(4).equalsIgnoreCase("left")?-1:1,
									m.group(5));
			Action one = new Action(Integer.parseInt(m.group(7)),
									m.group(8).equalsIgnoreCase("left")?-1:1,
									m.group(9));
			return new Rule(state, zero, one);
		}
	}
	
}