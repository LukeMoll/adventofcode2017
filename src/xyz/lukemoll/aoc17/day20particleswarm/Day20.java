package xyz.lukemoll.aoc17.day20particleswarm;

import java.util.*;
import java.util.regex.*;
import xyz.lukemoll.aoc17.utils.PuzzleInputReader;

public class Day20 {

	public static void main(String[] args) {
		Particle[] ps = Arrays.stream(PuzzleInputReader.getPuzzleInputLines("day20.txt"))
						.map(Particle::parseFromLine).toArray(n -> new Particle[n]);
		ArrayList<Particle> particles = new ArrayList<Particle>(Arrays.asList(ps));
		
		// Part 1
		int index = particles.indexOf(particles.stream().min((Particle a, Particle b) -> (int) Math.signum(a.acceleration.manhattan() - b.acceleration.manhattan())).get());
		System.out.println("Particle " + index + " stays closest to the origin.");
		
		// Part 2
		int iterations = (int) 1e3;
		for(int i=0; i<iterations; i++) {runTick(particles);}
		System.out.println("After " + iterations + " ticks, " + particles.size() + " particles remain.");

	}
	
	static int runTick(ArrayList<Particle> particles) {
		// Need to specify ArrayList as removeAll is optional for List
		for(Particle p : particles) {
			p.velocity.X += p.acceleration.X;
			p.velocity.Y += p.acceleration.Y;
			p.velocity.Z += p.acceleration.Z;
			
			p.position.X += p.velocity.X;
			p.position.Y += p.velocity.Y;
			p.position.Z += p.velocity.Z;
		}
		Set<Particle> toRemove = new HashSet<Particle>();
		for(Particle p : particles) {
			for(Particle q : particles) {
				if(p!=q && p.position.equals(q.position)) {
					toRemove.add(p);
					toRemove.add(q);
				}
			}
		}
		particles.removeAll(toRemove);
		return toRemove.size();
	}
	
	static class Vector3D {
		public double X, Y, Z;
		public Vector3D(double X, double  Y, double Z) {
			this.X = X;
			this.Y = Y;
			this.Z = Z;
		}
		public double manhattan() {return Math.abs(X) + Math.abs(Y) + Math.abs(Z);}
		@Override
		public boolean equals(Object other) {
			return Vector3D.class.isInstance(other) &&
					((Vector3D) other).X == this.X &&
					((Vector3D) other).Y == this.Y &&
					((Vector3D) other).Z == this.Z;
		}
		@Override
		public int hashCode() {// Useful to have for HashSet
			return Objects.hash(X,Y,Z);
		}
		
	}
	
	static class Particle {
		private Vector3D position, velocity, acceleration;
		private Particle(double sX, double sY, double sZ, double vX, double vY, double vZ, double aX, double aY, double aZ) {
			this.position = new Vector3D(sX,sY,sZ);
			this.velocity = new Vector3D(vX,vY,vZ);
			this.acceleration = new Vector3D(aX,aY,aZ);
		}
		static Pattern lineRegex = Pattern.compile("^p=<(-?\\d+),(-?\\d+),(-?\\d+)>,\\s?v=<(-?\\d+),(-?\\d+),(-?\\d+)>,\\s?a=<(-?\\d+),(-?\\d+),(-?\\d+)>$");
		public static Particle parseFromLine(String line) {
			Matcher m = lineRegex.matcher(line);
			if(m.matches()) {
				double sX = Double.parseDouble(m.group(1));
				double sY = Double.parseDouble(m.group(2));
				double sZ = Double.parseDouble(m.group(3));
				
				double vX = Double.parseDouble(m.group(4));
				double vY = Double.parseDouble(m.group(5));
				double vZ = Double.parseDouble(m.group(6));
				
				double aX = Double.parseDouble(m.group(7));
				double aY = Double.parseDouble(m.group(8));
				double aZ = Double.parseDouble(m.group(9));
				return new Particle(sX,sY,sZ,vX,vY,vZ,aX,aY,aZ);
			}
			else {
				throw new Error("Invalid format: " + line);
			}
		}		
	}
}