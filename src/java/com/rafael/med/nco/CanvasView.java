package com.rafael.med.nco;

import java.util.Collection;
import java.util.Map;

import com.rafael.med.common.Constants;
import com.rafael.med.nco.Aircraft.SAInputTarget;
import com.rafael.med.nco.Aircraft.SAMember;
import com.rafael.med.nco.Aircraft.SAObject;
import com.rafael.med.nco.Aircraft.SATrack;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public abstract class CanvasView extends Canvas
{	
	
	protected static final Color A_BLUE = Color.rgb(0, 114, 255);
	private static final String ZOOM 	= "1 = %.1f km";
	
	protected Aircraft aircraft;
	protected Color background;
	protected GraphicsContext gc;
	
	protected double w;
	protected double h;
	

	protected boolean isOnceDraw = false;
	private StackPane parent;
	private final int cycleCount = 5;
	
	private final double ratioToScreen = 7;
	
	protected double ah;
	protected double aw;
	
	protected double headL;
	protected double lineL;
	
	protected double cx;
	protected double cy;
	protected double lockedR;
	protected double radarR;
	
	protected double pixelInOneKilometre ;
	
	private double [] fourPoints = new double[8];
	
	public final static double RADAR_RANGE = 200.0;
	
	protected int kscale = 0;
	private double currentRange = RADAR_RANGE / 10;
	
	public CanvasView(Aircraft aircraft, Color background, StackPane parent)
	{
		this.aircraft 	= aircraft;
		this.background	= background;
		this.parent = parent;
		this.gc = getGraphicsContext2D();
		widthProperty().bind(parent.widthProperty());
		heightProperty().bind(parent.heightProperty());
		
		setOnMouseClicked(event -> 
		{
			double x = event.getX();
			double y = event.getY();
			select(x, y);
		});
		
	}
	
	public void setCurrentZoom(int currentZoom)
	{
		double range =  RADAR_RANGE + ( currentZoom * 10 );
		pixelInOneKilometre = w / range ;
		currentRange = range / 2 / 5;
	}
	
	
	
	protected void backgroundDraw(boolean isSelfDraw)
	{
		if(!isOnceDraw)
		{
			isOnceDraw = true;
			w 		= getWidth();
			h 		= getHeight();
			cx 		= w/2;
			cy 		= h/2;
			ah 		= w / ratioToScreen;
			aw 		= ah * 2/3;
			headL 	= ah/3; 
			lineL   = ah * 4/9;
			lockedR = ah/8;
			radarR	= ah/4;
			
			pixelInOneKilometre = w / RADAR_RANGE;
			
			System.out.println(" w = " + getWidth() + " h = " + getHeight() + " pw = " + parent.getWidth() + " ph = " + parent.getHeight() + " ah = " +ah + " aw = " + aw);
		}
		gc.clearRect(0, 0, w, h);
		gc.setFill(background);
		gc.fillRect(0, 0, w, h);
		gc.setStroke(Constants.COLOR_50);
		gc.setLineWidth(1);
		gc.strokeLine(cx, 0, cx, h);
		gc.strokeLine(0, cy, w, cy);
		
		double r = w /(cycleCount *2);
		for (int i = 1; i <= cycleCount; i++) 
		{
			gc.strokeOval(cx -( r * i), cy -(r * i), r * i * 2, r * i * 2);
		}
		
		if(isSelfDraw)
		{
			gc.setStroke(A_BLUE);
			gc.setLineWidth(2);
			gc.strokeLine(cx - aw/3, cy, cx + aw/3, cy);
			gc.strokeLine(cx - aw/4, cy + ah/7, cx + aw/4, cy + ah/7);
			gc.strokeLine(cx, cy, cx, cy + ah * 1/3);
		}

		gc.setStroke(Constants.COLOR_90);
		gc.setFont(Font.font("Roboto Thin",FontWeight.THIN, 14));
		gc.strokeText(String.format(ZOOM, currentRange), w - 90 , h - 14);
		
		
		
		
		//drawTarget(300, 50, -80, Color.YELLOW, true, true);
//		saTarget(300, 50, -80, Color.YELLOW, true);
//		saHostileTarget(300, 300, -50, Color.AQUA ,false);
//		saFriendlyTarget(100, 100, 45, Color.ORANGE, false);
//		saNwMember(150, 270,45, Color.LIME, false);
//		saFormationMember(40, 200, -15, Color.WHITE, false, 1);
//		saUnidentifiedAircraft(300, 200, 80, Color.AQUAMARINE, false);
//		radarTarget(300, 100, Color.BLUEVIOLET, true, 2);
//		radarTarget(300, 300, Color.BLUEVIOLET, false, 2);
	}
	
	private double[] saDrawBase(double x ,double y, double headingInDegree, Color color, boolean isLocable) 
	{
		gc.setStroke(color);
		gc.setLineWidth(2);
		
		double angle = Math.toRadians(-(90 - headingInDegree));
		gc.strokeLine(x, y, x + headL * Math.cos(angle), y + headL * Math.sin(angle));
		
		/*
		 * 		     |
		 * 	  	 3 __|__ 4
		 * 	 	  |	    |
		   		 1|_____|2
		   		 
		*/
		
		// 1 point
		angle = Math.toRadians(-(90 - headingInDegree - 150) ); 
		fourPoints[0] = x + lineL * Math.cos(angle);
		fourPoints[1] = y + lineL * Math.sin(angle);
		
		// 2 point
		angle = Math.toRadians( -(90 - headingInDegree + 150) );
		fourPoints[2] = x + lineL * Math.cos(angle);
		fourPoints[3] = y + lineL * Math.sin(angle);
		
		
		
		angle =  Math.toRadians( 30);
		double l = lineL * Math.cos(angle);
				
		angle = Math.toRadians( -(90 - headingInDegree));
		// 3 point
		fourPoints[4] = fourPoints[0] + l * Math.cos(angle);
		fourPoints[5] = fourPoints[1] + l * Math.sin(angle);
		
		// 4 point
		fourPoints[6] = fourPoints[2] + l * Math.cos(angle);
		fourPoints[7] = fourPoints[3] + l * Math.sin(angle);
		
		if(isLocable)
		{
			gc.setFill(Color.WHITE);
			gc.fillOval(x -lockedR, y -lockedR , lockedR  * 2, lockedR *  2);
		}
		
		return fourPoints;
	}
	
	protected void saTarget(double x ,double y, double headingInDegree, Color color, boolean isLocable, boolean isQ1, boolean isQ2)
	{
		saDrawBase(x, y, headingInDegree, color, isLocable);
		gc.strokeLine(x, y, fourPoints[0] ,fourPoints[1] );
		gc.strokeLine(x, y, fourPoints[2] ,fourPoints[3] );
		gc.strokeLine(fourPoints[0] ,fourPoints[1], fourPoints[2] ,fourPoints[3] );
		
		
		if(isQ1)
		{
			double [] xs = new double[4];
			xs[0] = x;
			xs[1] = x + 30;
			xs[2] = x;
			xs[3] = x - 30;
			double [] ys = new double[4];
			ys[0] = y - 30;
			ys[1] = y;
			ys[2] = y + 30;
			ys[3] = y;
			
			gc.setStroke(Color.RED);
			gc.setLineDashes(10.0);
			gc.strokePolygon(xs, ys, 4);
			gc.setLineDashes(0.0);
		}
		if(isQ2)
		{
			double [] xs = new double[4];
			xs[0] = x;
			xs[1] = x + 30;
			xs[2] = x;
			xs[3] = x - 30;
			double [] ys = new double[4];
			ys[0] = y - 30;
			ys[1] = y;
			ys[2] = y + 30;
			ys[3] = y;
			
			gc.setStroke(Color.CORAL);
			gc.setLineDashes(10.0);
			gc.strokePolygon(xs, ys, 4);
			gc.setLineDashes(0.0);
		}
		
		
	}
	
	protected void saHostileTarget(double x ,double y, int headingInDegree, Color color, boolean isLocable,boolean isClicked)
	{
		saTarget(x, y, headingInDegree, color, isLocable,false, false);
		gc.setLineWidth(1);
		double l = lineL - 14;
		double angle = Math.toRadians( -(90 - headingInDegree + 180) );	
		double x0 = x + 8 * Math.cos(angle);
		double y0 = y + 8 * Math.sin(angle);
		
		gc.moveTo(x0, y0);
		
		angle = Math.toRadians( -(90 - headingInDegree + 150) );
		double x1 = x0 + l * Math.cos(angle);
		double y1 = y0 + l * Math.sin(angle);
		gc.strokeLine(x0, y0, x1 ,y1 );
		
		angle = Math.toRadians( -(90 - headingInDegree - 150) );
		double x2 = x0 + l * Math.cos(angle);
		double y2 = y0 + l * Math.sin(angle);
		gc.strokeLine(x0, y0, x2, y2);
		
		gc.strokeLine(x1, y1, x2, y2);
	}
	
	protected void saFriendlyTarget(double x ,double y, double headingInDegree , Color color, boolean isLocable,boolean isClicked )
	{
		saTarget(x, y, headingInDegree, color, isLocable,false, false);
		gc.strokeLine(fourPoints[0] ,fourPoints[1], fourPoints[4] ,fourPoints[5] );
		gc.strokeLine(fourPoints[2] ,fourPoints[3], fourPoints[6] ,fourPoints[7] );
		gc.strokeLine(fourPoints[4] ,fourPoints[5], fourPoints[6] ,fourPoints[7] );
	}
	
	protected void saNwMember(double x ,double y, double headingInDegree , Color color, boolean isLocable )
	{
		saDrawBase(x, y, headingInDegree, color, isLocable);
		gc.strokeLine(fourPoints[0] ,fourPoints[1], fourPoints[4] ,fourPoints[5] );
		gc.strokeLine(fourPoints[2] ,fourPoints[3], fourPoints[6] ,fourPoints[7] );
		gc.strokeLine(fourPoints[4] ,fourPoints[5], fourPoints[6] ,fourPoints[7] );
		gc.strokeLine(fourPoints[0] ,fourPoints[1], fourPoints[2] ,fourPoints[3] );
	}
	
	
	protected void saFormationMember(double x ,double y, double headingInDegree , Color color, boolean isLocable, int number )
	{
		saNwMember(x, y, headingInDegree, color, isLocable);
		
		gc.setLineWidth(1);
		double angle =  Math.toRadians( 30);
		double r = lineL * Math.cos(angle) / 2;
		
		angle = Math.toRadians( -(90 - headingInDegree + 180) );	
		double x0 = x + r * Math.cos(angle);
		double y0 = y + r * Math.sin(angle);
		gc.strokeOval(x0 - r, y0 - r, r  * 2, r  * 2);
		
		String str = String.valueOf(number);
		Text t = new Text(str);
		t.setFont(gc.getFont());
		double wt = t.getBoundsInLocal().getWidth();
		
		gc.strokeText(str, x0 - wt/2, y0 + gc.getFont().getSize()/2 - 2);
		
	}
	
	
	protected void saUnidentifiedAircraft(double x ,double y, int headingInDegree , Color color, boolean isLocable)
	{
		saDrawBase(x, y, headingInDegree, color, isLocable);
		gc.strokeLine(fourPoints[0] ,fourPoints[1], fourPoints[4] ,fourPoints[5] );
		gc.strokeLine(fourPoints[2] ,fourPoints[3], fourPoints[6] ,fourPoints[7] );
		gc.strokeLine(fourPoints[4] ,fourPoints[5], fourPoints[6] ,fourPoints[7] );		
	}
	

	
	protected void radarTarget(double x ,double y,  Color color, boolean isLocable, int trackId)
	{
		gc.setLineWidth(2);
		gc.setStroke(color);
		gc.strokeOval(x - radarR, y - radarR, radarR  * 2, radarR  * 2);
		
			
		double angle = Math.toRadians(-(90 - 45));
		gc.strokeLine(x, y, x + radarR * Math.cos(angle), y + radarR * Math.sin(angle));
		angle = Math.toRadians(-(90 - 45 - 90));
		gc.strokeLine(x, y, x + radarR * Math.cos(angle), y + radarR * Math.sin(angle));
		angle = Math.toRadians(-(90 + 45));
		gc.strokeLine(x, y, x + radarR * Math.cos(angle), y + radarR * Math.sin(angle));
		angle = Math.toRadians(-(90 + 45 + 90));
		gc.strokeLine(x, y, x + radarR * Math.cos(angle), y + radarR * Math.sin(angle));
		
		if(isLocable)
		{
			double [] xs = new double[4];
			xs[0] = x;
			xs[1] = x + 30;
			xs[2] = x;
			xs[3] = x - 30;
			double [] ys = new double[4];
			ys[0] = y - 30;
			ys[1] = y;
			ys[2] = y + 30;
			ys[3] = y;
			
			gc.setStroke(Color.AQUA);
			gc.setLineDashes(5.0);
			gc.strokePolygon(xs, ys, 4);
			gc.setStroke(Color.AQUA);
			gc.setLineDashes(0.0);
		}
			
			
	}
	
	protected void drawRadar(boolean isDrawId) 
	{
		Collection<SAInputTarget> tracks = aircraft.saInputTargets.values();
		for (SAInputTarget saInputTarget : tracks) 
		{
			int trackId 		= saInputTarget.track_id;
			double range		= saInputTarget.range;
			double azimuth		= saInputTarget.azimuth;
			boolean isLocable	= saInputTarget.isDt;
			boolean isRadarLock = saInputTarget.radar_lock == 1;
			
			
			double rangeInPixel = range * pixelInOneKilometre;
			double x = calculateX(azimuth, rangeInPixel);
			double y = calculateY(azimuth, rangeInPixel);
						
			radarTarget(x, y,  Color.WHITE, isRadarLock, trackId);
			if(isDrawId)
			{
				Paint color = gc.getStroke();
				String str = String.valueOf(trackId);
				Text t = new Text(str);
				t.setFont(gc.getFont());
				double wt = t.getBoundsInLocal().getWidth();
				gc.setStroke(Color.LIME);
				gc.strokeText(str, x  + ( radarR  + 6)  - wt/2, y  + gc.getFont().getSize()/2 - 2);
				gc.setStroke(color);
			}
		}
	}
	
	protected void drawSa(boolean isDrawId)
	{
		for (Map.Entry<Integer, SAObject> entry : aircraft.saObjects.entrySet())
		{
			Integer key 		= entry.getKey();
			SAObject saObject	= entry.getValue();
			
			int globalId		= saObject.globalId;
			int displayId		= saObject.displayId;
			double azimuth		= saObject.azimuth;
			double elevation	= saObject.elevation;
			double range 		= saObject.range;
			double heading		= saObject.heading;
			double velocityX	= saObject.velocityX;
			double velocityY	= saObject.velocityX;
			double velocityZ	= saObject.velocityZ;	
			
			
			double rangeInPixel = range * pixelInOneKilometre;
			double x = calculateX(azimuth, rangeInPixel);
			double y = calculateY(azimuth, rangeInPixel);
			
			
			double headingInDegree = heading;
			if(aircraft.memberSelf != null)
			{
				headingInDegree = heading - aircraft.memberSelf.heading;
			}
			
		
			
			if (saObject instanceof SATrack)
			{
				SATrack track = (SATrack) saObject;
				
				
				
				Color color = Color.WHITE;
				if(track.targetSource == 1)
				{
					color = Color.PURPLE.brighter().brighter();
				}
				else if(track.targetSource == 2)
				{
					color = Color.ORANGE;
				}
				
				
				boolean isLocable = track.lno == 1;
				saTarget(x, y, headingInDegree, color, isLocable , track.isQ1, track.isQ2);
				
				
			}
			else if (saObject instanceof SAMember)
			{
				SAMember member = (SAMember) saObject;
				
				
				
				if(member.formationGroup == 1 || member.motherAc == 1)
				{
					if(member.motherAc == 1)
					{
						headingInDegree = heading;
					}
					
					saFormationMember(x, y, headingInDegree, Color.DODGERBLUE, false, member.callsignId);
				}
				else
				{
					saNwMember(x, y, headingInDegree, Color.LIMEGREEN, false);
				}
			} 
			
			if(saObject.isUnderClick)
			{
				double [] xs = new double[4];
				xs[0] = x;
				xs[1] = x + 30;
				xs[2] = x;
				xs[3] = x - 30;
				double [] ys = new double[4];
				ys[0] = y - 30;
				ys[1] = y;
				ys[2] = y + 30;
				ys[3] = y;
				
				Paint color = gc.getStroke();
				gc.setStroke(Color.WHITE);
				gc.setLineDashes(5.0);
				gc.strokePolygon(xs, ys, 4);
				gc.setLineDashes(0.0);
				gc.setStroke(color);
			}
			
			if(isDrawId)
			{
				Paint color = gc.getStroke();
				String str = String.valueOf(displayId);
				Text t = new Text(str);
				t.setFont(gc.getFont());
				double wt = t.getBoundsInLocal().getWidth();
				gc.setStroke(Color.CYAN);
				gc.strokeText(str, x  + ( radarR  + 2)  - wt/2, y  + gc.getFont().getSize()/2 - 2);
				gc.setStroke(color);
			}
		}
	}

	
	protected double calculateX(double azimuth, double rangeInPixel) 
	{
//		if(azimuth >= -90 && azimuth <= 0)
//		{
//			return cx + (rangeInPixel * Math.sin(Math.toRadians(azimuth)));
//		}
//		else if(azimuth >= 0 && azimuth <= 90)
//		{
//			return cx + (rangeInPixel * Math.sin(Math.toRadians(azimuth)));
//		}
//		else if(azimuth >= 90 && azimuth <= 180)
//		{
//			return cx + (rangeInPixel * Math.sin(Math.toRadians(azimuth)));
//		}
//		else if(azimuth >= -180 && azimuth <= -90)
//		{
//			return cx + (rangeInPixel * Math.sin(Math.toRadians(azimuth)));
//		}
		return cx + (rangeInPixel * Math.sin(Math.toRadians(azimuth)));
	}
	
	
	protected double calculateY(double azimuth, double rangeInPixel) 
	{
//		if(azimuth >= -90 && azimuth <= 0)
//		{
//			return cy - (rangeInPixel * Math.cos(Math.toRadians(azimuth)));
//		}
//		else if(azimuth >= 0 && azimuth <= 90)
//		{
//			return cy - (rangeInPixel * Math.cos(Math.toRadians(azimuth)));
//		}
//		else if(azimuth >= 90 && azimuth <= 180)
//		{
//			return cy - (rangeInPixel * Math.cos(Math.toRadians(azimuth)));
//		}
//		else if(azimuth >= -180 && azimuth <= -90)
//		{
//			return cy - (rangeInPixel * Math.cos(Math.toRadians(azimuth)));
//		}
		return cy - (rangeInPixel * Math.cos(Math.toRadians(azimuth)));
		
	}

	
	protected abstract void draw();
	protected abstract boolean select(double x, double y);
	
	protected void reset()
	{
		gc.clearRect(0, 0, w, h);
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, w, h);
		isOnceDraw = false;
		currentRange = RADAR_RANGE / 10;
	}
	
public static void main(String[] args)
{
	for (int i = -180; i < 180; i=i+10)
	{
		System.out.println("angle = " + i +  " X sin = " + Math.sin(Math.toRadians(i)) + " Y cos = " +Math.cos(Math.toRadians(i)));
		
	}
}

}