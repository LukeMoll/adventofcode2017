package xyz.lukemoll.aoc17.day18duet;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xyz.lukemoll.aoc17.utils.PuzzleInputReader;

public class Day18 {

	static abstract class AbstractMachine {
		boolean running = false;
		int programcounter = 0;
		// Addresses happen only to be single characters, but we're prepared anyway :D
		Map<String, Long> registers = new HashMap<String, Long>();
		Consumer<AbstractMachine>[] program;
		
		abstract void snd(long value);
		abstract void rcv(String op1);
		
		Pattern immediatePattern = Pattern.compile("^-?\\d+$"); 
		long getRegisterOrValue(String input) {
			Matcher m = immediatePattern.matcher(input);
			if(m.matches()) {
				return Integer.parseInt(input);
			}
			else {
				return registers.getOrDefault(input, 0l);
			}
		}
		
		public AbstractMachine(Consumer<AbstractMachine>[] program) {
			this.program = Arrays.copyOf(program, program.length);
		}
		
		public void run() {
			running = true;
			while(running) {
				program[programcounter].accept(this);
				programcounter++;
				if(programcounter < 0 || programcounter >= program.length) {
					System.out.println("Program ran-out");
					running = false;
				}
			}
		}
	}
	
	static class SoundMachine extends AbstractMachine {
		private long soundfrequency = 0;

		public SoundMachine(Consumer<AbstractMachine>[] program) {
			super(program);
		}

		@Override
		void snd(long value) {
			this.soundfrequency = value;
			
		}

		@Override
		void rcv(String op1) {
			if(this.getRegisterOrValue(op1) != 0) {
				System.out.println("Recovered frequency: " + this.soundfrequency);
				this.running = false;
			}
		}
	}
	
	static class DuetMachine extends AbstractMachine {
		public final int ID;
		int messagesSent = 0;
		
		public BlockingQueue<Long> messageQueue = new LinkedBlockingQueue<Long>();
		
		public boolean waiting = false;
		
		DuetMachine partner;
		
		public DuetMachine(Consumer<AbstractMachine>[] program, int id) {
			super(program);
			this.ID = id;
			this.registers.put("p", new Long(ID));
		}

		public void setPartner(DuetMachine p) {
			this.partner = p;
		}
		
		@Override
		void snd(long value) {
			partner.messageQueue.add(new Long(value));
			partner.waiting = false;
			this.messagesSent++;
		}

		@Override
		void rcv(String op1) {
			Long value;
			if(this.messageQueue.isEmpty()) {
				if(partner.running && !partner.waiting ) {
					this.waiting = true;
					try {
						value = this.messageQueue.take();
						this.waiting = false;
					} catch (InterruptedException e) {
						System.out.println("Interrupted in " + this.ID);
						e.printStackTrace();
						return;
					}				
				}
				else {
					// Partner is either halted or waiting
					// In either case we should halt.
					this.running = false;
					partner.running = false;
					partner.messageQueue.add(-0L);// clear .take() if partner is waiting
					return;
				}
			}
			else {
				// assert !registers.isEmpty()
				try {
					value = messageQueue.remove();
				}
				catch(NoSuchElementException e) {
					System.out.println("Expected element to be present in " + this.ID);
					e.printStackTrace();
					return;
				}
			}
			this.registers.put(op1, value);
		}
		
	}
	
	static Pattern INSTRUCTION_SND = Pattern.compile("^snd\\s(-?[a-z0-9]+)$");
	static Pattern INSTRUCTION_SET = Pattern.compile("^set\\s([a-z]+)\\s(-?[a-z0-9]+)$");
	static Pattern INSTRUCTION_ADD = Pattern.compile("^add\\s([a-z]+)\\s(-?[a-z0-9]+)$");
	static Pattern INSTRUCTION_MUL = Pattern.compile("^mul\\s([a-z]+)\\s(-?[a-z0-9]+)$");
	static Pattern INSTRUCTION_MOD = Pattern.compile("^mod\\s([a-z]+)\\s(-?[a-z0-9]+)$");
	static Pattern INSTRUCTION_RCV = Pattern.compile("^rcv\\s(-?[a-z0-9]+)$");
	static Pattern INSTRUCTION_JGZ = Pattern.compile("^jgz\\s(-?[a-z0-9]+)\\s(-?[a-z0-9]+)$");
	
	public static Consumer<AbstractMachine> decode(String instruction) {
		Matcher match = INSTRUCTION_SND.matcher(instruction);
		if(match.matches()) {
			String op1 = match.group(1);
			return m -> m.snd(m.getRegisterOrValue(op1));
		}
		else if((match=INSTRUCTION_SET.matcher(instruction)).matches()) {
			String 	op1 = match.group(1),
					op2 = match.group(2);
			return m -> m.registers.put(op1, m.getRegisterOrValue(op2));
		}
		else if((match=INSTRUCTION_ADD.matcher(instruction)).matches()) {
			String 	op1 = match.group(1),
					op2 = match.group(2);
			return m -> m.registers.put(op1, m.registers.getOrDefault(op1, 0l) + m.getRegisterOrValue(op2));
		}
		else if((match=INSTRUCTION_MUL.matcher(instruction)).matches()) {
			String 	op1 = match.group(1),
					op2 = match.group(2);
			return m -> m.registers.put(op1, m.registers.getOrDefault(op1, 0l) * m.getRegisterOrValue(op2));
		}
		else if((match=INSTRUCTION_MOD.matcher(instruction)).matches()) {
			String 	op1 = match.group(1),
					op2 = match.group(2);
			return m -> m.registers.put(op1, m.registers.getOrDefault(op1, 0l) % m.getRegisterOrValue(op2));
		}
		else if((match=INSTRUCTION_RCV.matcher(instruction)).matches()) {
			String 	op1 = match.group(1);			
			return m -> {
				m.rcv(op1);
			};
		}
		else if((match=INSTRUCTION_JGZ.matcher(instruction)).matches()) {
			String 	op1 = match.group(1),
					op2 = match.group(2);
			return m -> {
				if(m.getRegisterOrValue(op1) > 0) {
						int jumpAmount = (int) m.getRegisterOrValue(op2);
						if(jumpAmount != 0)
							m.programcounter += jumpAmount - 1;
					}
				};
			// We always increment PC so jumps must go one "behind" the instruction they
			// want to jump to.
		}
		else {
			throw new Error("Unrecognised instruction: " + instruction);
		}
	}
	
	public static void main(String[] args) {
		String[] input = PuzzleInputReader.getPuzzleInputLines("day18.txt");
		@SuppressWarnings("unchecked") // n -> new Consumer<AbstractMachine[n] not allowed :/
		Consumer<AbstractMachine>[] program = Arrays.stream(input).map(Day18::decode).toArray(n -> new Consumer[n]);
		
		//Part 1
		SoundMachine m = new SoundMachine(program);
		m.run();
		
		//Part 2
		DuetMachine 	d0 = new DuetMachine(program, 0),
						d1 = new DuetMachine(program, 1);
		d0.setPartner(d1);
		d1.setPartner(d0);

		new Thread(() -> {
			d0.run();
		}, "d0 thread").start();
		
		new Thread( () -> {
			d1.run();
			System.out.println("Program 1 sent " + d1.messagesSent + " values");
		}, "d1 thread").start();
	}
}