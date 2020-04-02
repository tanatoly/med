package com.rafael.med.nco;

import java.util.Collection;

import com.rafael.med.nco.Aircraft.SAInputTarget;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class RadarCanvas extends CanvasView
{
	
	public RadarCanvas(Aircraft aircraft, Color background, StackPane parent)
	{
		super(aircraft, background, parent);
	}

	@Override
	protected void draw()
	{
		if(aircraft.isRadarHasChange.compareAndSet(true, false))
		{
			backgroundDraw(true);
			drawRadar(true);				
		}
	}

	@Override
	protected boolean select(double clickX, double clickY) 
	{
		boolean isIn = false;
		Collection<SAInputTarget> tracks = aircraft.saInputTargets.values();
		
		for (SAInputTarget saInputTarget : tracks) 
		{
			double range		= saInputTarget.range;
			double azimuth		= saInputTarget.azimuth;	
			double rangeInPixel = range * pixelInOneKilometre;
			double x = calculateX(azimuth, rangeInPixel);
			double y = calculateY(azimuth, rangeInPixel);
			
			isIn = clickX > (x - radarR) && clickX < (x + radarR) && clickY < (y + radarR) && clickY > (y - radarR);
		
			if(isIn)
			{
				if(aircraft.selectedSaInputTarget == null || aircraft.selectedSaInputTarget != saInputTarget)
				{
					aircraft.selectedInputTargetId.set(saInputTarget.track_id);
				}
				else
				{
					aircraft.selectedInputTargetId.set(0);
				}		
				break;
			}
		}
		return isIn;
	}
}
