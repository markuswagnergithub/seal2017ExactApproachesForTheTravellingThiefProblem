package ttp.moea;

import java.util.Collections;
import java.util.NavigableMap;

import pwt.exact.dp.PackingPlan;
import ttp.problem.Tour;

public class Individual {
	public final Tour.Essence tour;
	public final NavigableMap<Long, PackingPlan> front;
	
	public Individual(Tour.Essence tour, NavigableMap<Long, PackingPlan> front){
		this.tour = tour;
		this.front = Collections.unmodifiableNavigableMap(front);
	}

}
