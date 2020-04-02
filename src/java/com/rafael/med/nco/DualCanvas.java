package com.rafael.med.nco;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class DualCanvas extends CanvasView
{

	public DualCanvas(Aircraft aircraft, Color background, StackPane parent)
	{
		super(aircraft, background, parent);
	}

	@Override
	protected void draw()
	{
		boolean isDrawRadar = aircraft.isRadarHasChange.compareAndSet(true, false);
		if(isDrawRadar)
		{
			backgroundDraw(false);
			drawRadar(false);
		}
		
		if(aircraft.isSaHasChange.compareAndSet(true, false))
		{
			if(!isDrawRadar)
			{
				backgroundDraw(false);
			}
			drawSa(false);
		}	
	}

	@Override
	protected boolean select(double x, double y) 
	{
		return false;
	}
}
