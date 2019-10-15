package ttp.tour;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import common.tools.Util;
import ttp.problem.Ttp;

public class TspSolution {
	private final List<Integer> permutation;
	private final Ttp ttp;
	
	private int fitness = -1;

	public TspSolution(List<Integer> permutation, Ttp ttp) {
		this.permutation = Collections.unmodifiableList(align(new ArrayList<>(permutation)));
		this.ttp = ttp;
	}

	public TspSolution(TspSolution tspSolution) {
		this.permutation = Collections.unmodifiableList(new ArrayList<>(tspSolution.permutation));
		this.ttp = tspSolution.ttp;
	}
	
	
	public static ArrayList<Integer> align(ArrayList<Integer> permutation){
		if (permutation.get(0) == 1){
			return permutation;
		}
		int idxOfStart = permutation.indexOf(1);
		ArrayList<Integer> res = new ArrayList<>();
		res.addAll(permutation.subList(idxOfStart, permutation.size()));
		res.addAll(permutation.subList(0, idxOfStart));
		return res;	
	}

	public List<Integer> getPermutation() {
		return permutation;
	}
	
	public Ttp getProblem(){
		return ttp;
	}
	
	public TspSolution reverse(){
		ArrayList<Integer> p = new ArrayList<>(permutation);
		Util.reverse(p, 1, p.size() - 1);
		return new TspSolution(p, this.ttp);
	}

	public boolean areNeighbouring(Integer cityA, Integer cityB) {
		int diff = Math.abs(this.permutation.indexOf(cityA) - this.permutation.indexOf(cityB));
		
		if (diff  == 1 || diff == this.permutation.size() - 1)
			return true;
		else
			return false;
	}

	public int getFitness() {
		if (ttp == null) {
			throw new RuntimeException("Null Problem");
		}
		// Check that we have not already evaluated this individual.
		if (fitness >= 0) {
			return fitness;
		}
		
		int totalDistance = 0;
		for (int i = 0; i < this.permutation.size() - 1; i++) {
		    totalDistance = totalDistance
			    + ttp.distance(permutation.get(i), permutation.get(i + 1));
		}
		totalDistance = totalDistance
		        + ttp.distance(permutation.get(permutation.size() - 1), permutation.get(0));
		
		// Cache this fitness to avoid doing many evaluations.
		fitness = totalDistance;
		return fitness;
	}

	@Override
	public String toString() {
		return "TspSolution [permutation=" + permutation + ", ttp=" + ttp.name + ", fitness=" + fitness + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(permutation.hashCode(), ttp.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TspSolution other = (TspSolution) obj;
		if (fitness != other.fitness)
			return false;
		if (permutation == null) {
			if (other.permutation != null)
				return false;
		} else if (!permutation.equals(other.permutation))
			return false;
		if (ttp == null) {
			if (other.ttp != null)
				return false;
		} else if (!ttp.equals(other.ttp))
			return false;
		return true;
	}
	
	
	

}
