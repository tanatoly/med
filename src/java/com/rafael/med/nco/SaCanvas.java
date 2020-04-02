package com.rafael.med.nco;

import java.util.Collection;

import com.rafael.med.nco.Aircraft.SAObject;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class SaCanvas extends CanvasView
{
	public SaCanvas(Aircraft aircraft, Color background, StackPane parent)
	{
		super(aircraft, background, parent);
	}

	@Override
	protected void draw()
	{
		if(aircraft.isSaHasChange.compareAndSet(true, false))
		{
			backgroundDraw(false);
			drawSa(true);
		}
	}

	@Override
	protected boolean select(double clickX, double clickY) 
	{
		boolean isIn = false;
		Collection<SAObject> saObjects = aircraft.saObjects.values();
		
		for (SAObject saObject : saObjects) 
		{
			double azimuth		= saObject.azimuth;
			double range		= saObject.range;
			
			double rangeInPixel = range * pixelInOneKilometre;
			double x = calculateX(azimuth, rangeInPixel);
			double y = calculateY(azimuth, rangeInPixel);
			
			isIn = clickX > (x - radarR) && clickX < (x + radarR) && clickY < (y + radarR) && clickY > (y - radarR);
		
			if(isIn)
			{
				if(aircraft.selectedSaObject == null || aircraft.selectedSaObject != saObject)
				{
					aircraft.selectedSaObjectId.set(saObject.globalId);
				}
				else
				{
					aircraft.selectedSaObjectId.set(0);
				}	
				break;
			}
		}
		return isIn;
	}
}
