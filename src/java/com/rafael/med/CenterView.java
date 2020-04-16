package com.rafael.med;

public interface CenterView 
{
	void update(boolean isToFront);
	
	default void onToBack()
	{
		
	}
}
