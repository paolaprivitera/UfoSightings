package it.polito.tdp.ufo.model;

import java.time.Year;

public class AnnoCount {
	private Year year;
	private Integer count;
	
	public AnnoCount(Year year, Integer count) {
		this.year = year;
		this.count = count;
	}

	public Year getYear() {
		return year;
	}

	public void setYear(Year year) {
		this.year = year;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}
	
	// Devo generare HashCode e equals?
	// devo pormi le seguenti domande?
	// Usero' questa classe all'interno di un grafo?
	// Usero' questa classe in una HashMap o in un Set?
	// No, quindi non mi servono
	
	// Poiche' questi dati li devo mettere all'interno di un menu' a tendina
	// genero il toString()
	
	@Override
	public String toString() {
		return year+" ("+count+")";
	}
	
}
