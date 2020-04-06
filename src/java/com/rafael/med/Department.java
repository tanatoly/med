package com.rafael.med;

import java.util.ArrayList;
import java.util.List;

public final class Department
{
	public final String id;
	public final List<Room> rooms = new ArrayList<>();
	public DepartmentView view;
	public Department(String id)
	{
		this.id = id;
	}
	public void addRoom(Room room)
	{
		rooms.add(room);
	}
}