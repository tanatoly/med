package com.rafael.med;

import java.util.ArrayList;
import java.util.List;

public final class Room
{
	public final String id;
	public final List<Bed> beds = new ArrayList<>();
	
	public Room(String id) 
	{
		this.id = id;
	}

	public void addBed(Bed bed)
	{
		beds.add(bed);
	}
}