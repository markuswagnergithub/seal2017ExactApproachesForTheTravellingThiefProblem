package ttp.dp;

import java.math.BigInteger;
import java.util.Objects;

public final class Key {
	public final int destination;
	public final BigInteger stops;
	
	private final int hashcode;
	
	public Key(int destination, BigInteger stops){
		this.stops = stops;
		this.destination = destination;
		this.hashcode = Objects.hash(this.destination, this.stops);
	}

	@Override
	public int hashCode() {
		return hashcode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this.hashcode != obj.hashCode()){
			return false;
		}
		
		if (!(obj instanceof Key)){
			return false;
		}
		Key k = (Key)obj;

		if (this.destination != k.destination){
			return false;
		}
		if (!this.stops.equals(k.stops)){
			return false;
		}
		
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{dest:" + destination);
		sb.append(", stops:" + stops.toString(2) + "}");
		return sb.toString();
	}
	
}
