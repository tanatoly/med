package com.rafael.med.nco;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rafael.med.AppManager;
import com.rafael.med.common.Utilities;
import com.rafael.med.nco.ScenarioManager.ScenarioRow.ScenarioRowAvion;
import com.rafael.med.nco.ScenarioManager.ScenarioRow.ScenarioRowNsa;
import com.rafael.med.nco.ScenarioManager.ScenarioRow.ScenarioRowRadar;

public class ScenarioManager 
{
	public static final class ScenarioRow
	{
		public static final class ScenarioRowRadar
		{
			public double t;
			public double trackTime;
			public int uniqueSensorTrackId;
			public int uniqueSensorId;
			public double trackQual;
			public int positionInputType;
			public double lat;
			public double lon;
			public double alt;
			public double [] velocity				= new double[3];
			public double [] stateCov 				= new double[81];
			public double rcs;
			public double plotTime;
			public double plotAzimuth;
			public double plotElevation;
			public double plotRange;
			public double plotDopp;
			public double plotRangeVar;
			public double plotAzVar;
			public double plotElVar;
			public double plotDoppVar;
			public int radMod;
			public double plotSNR;
			@Override
			public String toString() {
				return "ScenarioRowRadar [t=" + t + ", trackTime=" + trackTime + ", uniqueSensorTrackId="
						+ uniqueSensorTrackId + ", uniqueSensorId=" + uniqueSensorId + ", trackQual=" + trackQual
						+ ", positionInputType=" + positionInputType + ", lat=" + lat + ", lon=" + lon + ", alt=" + alt
						+ ", velocity=" + Arrays.toString(velocity) + ", rcs=" + rcs + ", plotTime=" + plotTime
						+ ", plotAzimuth=" + plotAzimuth + ", plotElevation=" + plotElevation + ", plotRange="
						+ plotRange + ", plotDopp=" + plotDopp + ", plotRangeVar=" + plotRangeVar + ", plotAzVar="
						+ plotAzVar + ", plotElVar=" + plotElVar + ", plotDoppVar=" + plotDoppVar + ", radMod=" + radMod
						+ ", plotSNR=" + plotSNR + "]";
			}
			
		}
		
		public static final class ScenarioRowAvion
		{
			public int uniqueSensorTrackId;
			public int uniqueSensorId;
			public int valid;
			public double LastUpdSystTime;
			public double t;
			public double lat;
			public double lon;
			public double alt;
			public double nSpeed;
			public double veln;
			public double vele;
			public double veld;
			public double [] cov 					= new double[36];
			public double to;
			public double nYaw;
			public double nPitch;
			public double nRoll;
			@Override
			public String toString() {
				return "ScenarioRowAvion [uniqueSensorTrackId=" + uniqueSensorTrackId + ", uniqueSensorId="
						+ uniqueSensorId + ", valid=" + valid + ", LastUpdSystTime=" + LastUpdSystTime + ", t=" + t
						+ ", lat=" + lat + ", lon=" + lon + ", alt=" + alt + ", nSpeed=" + nSpeed + ", veln=" + veln
						+ ", vele=" + vele + ", veld=" + veld + ", to=" + to + ", nYaw=" + nYaw + ", nPitch=" + nPitch
						+ ", nRoll=" + nRoll + "]";
			}
			
			
		}
		
		public static final class ScenarioRowNsa
		{
			public int uniqueSensorTrackId;
			public int uniqueSensorId;
			public double trackQual;
			public int sensorSource;
			public int systemTrackGlobalId;
			public int systemTrackId4View;
			public int nextId4ViewIndx;
			public double lastId4ViewTime;
			public double systemFirstDetecTime;
			public int leaderUniqueSensorTrackId;
			public double leaderFirstDetecTime;
			public int classification;
			public double lastUpdSystTime;
			public double t;
			public double lat;
			public double lon;
			public double alt;
			public double nSpeed;
			public double veln;
			public double vele;
			public double veld;
			public double [] cov 					= new double[6];
			public double rangFromSns;
		}
		
		public static final class LaunchMissileData
		{
			public int systemTrackGlobalId;
			public int launchInit;
			public double lat;
			public double lon;
			public double alt;
		}
		
		
		public double 							nSystemTime;
		public int 								nWaitingMessagesNum;
		public Aircraft.TargetType 				targetType;
		
		public ScenarioRowRadar 				rowRadar;
		public ScenarioRowAvion 				rowAvion;
		public ScenarioRowNsa 					rowNsa;
		public LaunchMissileData				launchMissileData = new LaunchMissileData();
		
		public long timestampInMs;

		public void toBufferRadar(ByteBuffer buffer) 
		{
			buffer.putDouble(rowRadar.t);
			buffer.putDouble(rowRadar.trackTime);
			buffer.putInt(rowRadar.uniqueSensorTrackId);
			buffer.putInt(rowRadar.uniqueSensorId);
			buffer.putDouble(rowRadar.trackQual);
			buffer.putInt(rowRadar.positionInputType);
			buffer.putDouble(rowRadar.lat);
			buffer.putDouble(rowRadar.lon);
			buffer.putDouble(rowRadar.alt);
			
			for (int i = 0; i < rowRadar.velocity.length; i++)
			{
				buffer.putDouble(rowRadar.velocity[i]);
			}
			for (int i = 0; i < rowRadar.stateCov.length; i++)
			{
				buffer.putDouble(rowRadar.stateCov[i]);
			}
			
			buffer.putDouble(rowRadar.rcs);
			buffer.putDouble(rowRadar.plotTime);
			buffer.putDouble(rowRadar.plotAzimuth);
			buffer.putDouble(rowRadar.plotElevation);
			buffer.putDouble(rowRadar.plotRange);
			buffer.putDouble(rowRadar.plotDopp);
			buffer.putDouble(rowRadar.plotRangeVar);
			buffer.putDouble(rowRadar.plotAzVar);
			buffer.putDouble(rowRadar.plotElVar);
			buffer.putDouble(rowRadar.plotDoppVar);
			buffer.putInt(rowRadar.radMod);
			buffer.putDouble(rowRadar.plotSNR);
		}
		
		
		public void toBufferAvion(ByteBuffer buffer) 
		{
			buffer.putInt(rowAvion.uniqueSensorTrackId);
			buffer.putInt(rowAvion.uniqueSensorId);
			buffer.putInt(rowAvion.valid);
			buffer.putDouble(rowAvion.LastUpdSystTime);
			buffer.putDouble(rowAvion.t);
			buffer.putDouble(rowAvion.lat);
			buffer.putDouble(rowAvion.lon);
			buffer.putDouble(rowAvion.alt);
			buffer.putDouble(rowAvion.nSpeed);
			buffer.putDouble(rowAvion.veln);
			buffer.putDouble(rowAvion.vele);
			buffer.putDouble(rowAvion.veld);
			for (int i = 0; i < rowAvion.cov.length; i++)
			{
				buffer.putDouble(rowAvion.cov[i]);
			}
			buffer.putDouble(rowAvion.to);
			buffer.putDouble(rowAvion.nYaw);
			buffer.putDouble(rowAvion.nPitch);
			buffer.putDouble(rowAvion.nRoll);			
		}
	}
	
	
	
	
	private static final Logger log = LogManager.getLogger();
	
	public static final ScenarioManager INSTANCE = new ScenarioManager();
	
	
	public void readFile(File file, Aircraft aircraft)
	{
		try
		{
			
			ByteBuffer buffer = Utilities.readFile(file, ByteOrder.LITTLE_ENDIAN);
			buffer.flip();

			while(buffer.hasRemaining())
			{
				ScenarioRow scenarioRow = new ScenarioRow();

				scenarioRow.nSystemTime 									= buffer.getDouble();
				scenarioRow.nWaitingMessagesNum								= buffer.getInt();
				int type													= buffer.getInt();
				scenarioRow.targetType										= Aircraft.TargetType.values()[type];

				//System.out.println("TYPE INT  = " + type + " -> " + scenarioRow.targetType);

				if (Aircraft.TargetType.RADAR == scenarioRow.targetType)
				{
					scenarioRow.rowRadar 									= new ScenarioRowRadar();

					scenarioRow.rowRadar.t 									= buffer.getDouble();                          
					scenarioRow.rowRadar.trackTime 							= buffer.getDouble();                          
					scenarioRow.rowRadar.uniqueSensorTrackId				= buffer.getInt();                             
					scenarioRow.rowRadar.uniqueSensorId						= buffer.getInt();                             
					scenarioRow.rowRadar.trackQual 							= buffer.getDouble();  
					scenarioRow.rowRadar.positionInputType					= (int) buffer.getLong();
					scenarioRow.rowRadar.lat 								= buffer.getDouble();
					scenarioRow.rowRadar.lon 								= buffer.getDouble();
					scenarioRow.rowRadar.alt 								= buffer.getDouble();
					for (int i = 0; i < scenarioRow.rowRadar.velocity.length; i++) 
					{
						scenarioRow.rowRadar.velocity[i]					= buffer.getDouble();
					}
					for (int i = 0; i < scenarioRow.rowRadar.stateCov.length; i++)
					{
						scenarioRow.rowRadar.stateCov[i]					= buffer.getDouble();
					}                                                                                                           

					scenarioRow.rowRadar.rcs 								= buffer.getDouble();                               
					scenarioRow.rowRadar.plotTime 							= buffer.getDouble();                               
					scenarioRow.rowRadar.plotAzimuth 						= buffer.getDouble();                               
					scenarioRow.rowRadar.plotElevation 						= buffer.getDouble();                               
					scenarioRow.rowRadar.plotRange 							= buffer.getDouble();                               
					scenarioRow.rowRadar.plotDopp 							= buffer.getDouble();                               
					scenarioRow.rowRadar.plotRangeVar 						= buffer.getDouble();                               
					scenarioRow.rowRadar.plotAzVar 							= buffer.getDouble();                               
					scenarioRow.rowRadar.plotElVar 							= buffer.getDouble();                               
					scenarioRow.rowRadar.plotDoppVar 						= buffer.getDouble();                               
					scenarioRow.rowRadar.radMod								= (int) buffer.getLong();                                  
					scenarioRow.rowRadar.plotSNR 							= buffer.getDouble();   

					scenarioRow.timestampInMs = (long) (scenarioRow.rowRadar.t * 1000);
					aircraft.radarScenarioRows.add(scenarioRow);
					aircraft.radarScenarioRowsSize++;

					//System.out.println(scenarioRow.rowRadar.t + " -> " + scenarioRow.timestampInMs);

					//System.out.println(scenarioRow.rowRadar);

				} 
				else if (Aircraft.TargetType.AVION == scenarioRow.targetType)
				{
					scenarioRow.rowAvion			= new ScenarioRowAvion();

					scenarioRow.rowAvion.uniqueSensorTrackId				= buffer.getInt();
					scenarioRow.rowAvion.uniqueSensorId						= buffer.getInt();
					scenarioRow.rowAvion.valid								= (int) buffer.getLong();
					scenarioRow.rowAvion.LastUpdSystTime 					= buffer.getDouble();
					scenarioRow.rowAvion.t 									= buffer.getDouble();
					scenarioRow.rowAvion.lat 								= buffer.getDouble();
					scenarioRow.rowAvion.lon 								= buffer.getDouble();
					scenarioRow.rowAvion.alt 								= buffer.getDouble();
					scenarioRow.rowAvion.nSpeed								= buffer.getDouble();
					scenarioRow.rowAvion.veln								= buffer.getDouble();
					scenarioRow.rowAvion.vele								= buffer.getDouble();
					scenarioRow.rowAvion.veld								= buffer.getDouble();
					for (int i = 0; i < scenarioRow.rowAvion.cov.length; i++)
					{
						scenarioRow.rowAvion.cov[i]							= buffer.getDouble();
					}
					scenarioRow.rowAvion.to									= buffer.getDouble();
					scenarioRow.rowAvion.nYaw								= buffer.getDouble();
					scenarioRow.rowAvion.nPitch								= buffer.getDouble();
					scenarioRow.rowAvion.nRoll								= buffer.getDouble();


					scenarioRow.timestampInMs = (long) (scenarioRow.rowAvion.t * 1000);

					aircraft.avionScenarioRows.add(scenarioRow);
					aircraft.avionScenarioRowsSize++;

					//System.out.println(scenarioRow.rowAvion.t + " -> " + scenarioRow.timestampInMs);
					//System.out.println(scenarioRow.rowAvion);
				}
				else if (Aircraft.TargetType.NSA == scenarioRow.targetType)
				{
					scenarioRow.rowNsa										= new ScenarioRowNsa();

					scenarioRow.rowNsa.uniqueSensorTrackId 					= buffer.getInt();
					scenarioRow.rowNsa.uniqueSensorId 						= buffer.getInt();
					scenarioRow.rowNsa.trackQual 							= buffer.getDouble();
					scenarioRow.rowNsa.sensorSource							= buffer.getInt();
					scenarioRow.rowNsa.systemTrackGlobalId					= buffer.getInt();
					scenarioRow.rowNsa.systemTrackId4View 					= buffer.getInt();
					scenarioRow.rowNsa.nextId4ViewIndx 						= buffer.getInt();
					scenarioRow.rowNsa.lastId4ViewTime 						= buffer.getDouble();
					scenarioRow.rowNsa.systemFirstDetecTime 				= buffer.getDouble();
					scenarioRow.rowNsa.leaderUniqueSensorTrackId 			= (int) buffer.getLong();
					scenarioRow.rowNsa.leaderFirstDetecTime 				= buffer.getDouble();
					scenarioRow.rowNsa.classification 						= (int) buffer.getLong();
					scenarioRow.rowNsa.lastUpdSystTime 						= buffer.getDouble();
					scenarioRow.rowNsa.t 									= buffer.getDouble();
					scenarioRow.rowNsa.lat 									= buffer.getDouble();
					scenarioRow.rowNsa.lon 									= buffer.getDouble();
					scenarioRow.rowNsa.alt 									= buffer.getDouble();
					scenarioRow.rowNsa.nSpeed								= buffer.getDouble();
					scenarioRow.rowNsa.veln									= buffer.getDouble();
					scenarioRow.rowNsa.vele									= buffer.getDouble();
					scenarioRow.rowNsa.veld									= buffer.getDouble();
					for (int i = 0; i < scenarioRow.rowNsa.cov.length; i++)
					{
						scenarioRow.rowNsa.cov[i]							= buffer.getDouble();
					}
					scenarioRow.rowNsa.rangFromSns							= buffer.getDouble();

					scenarioRow.timestampInMs = (long) (scenarioRow.rowNsa.t * 1000);
				}

				scenarioRow.launchMissileData.systemTrackGlobalId			= buffer.getInt();
				scenarioRow.launchMissileData.launchInit					= buffer.getInt();
				scenarioRow.launchMissileData.lat 							= buffer.getDouble();
				scenarioRow.launchMissileData.lon 							= buffer.getDouble();
				scenarioRow.launchMissileData.alt 							= buffer.getDouble();
				//scenarioRow.netLockId										= buffer.getInt();
				//count++;
			}
		}
		catch (Exception e)
		{
			AppManager.INSTANCE.showError(AppNco.class, log, "FAILED READ SCENARIO FILE", e);
		}
	}
	
	
	
	
//	
//	
//	public static void main(String[] args) 
//	{
//		try 
//		{
//			
//			File file = new File("SAin_1.bin");
//			
//			for (int i = 0; i < 10; i++) 
//			{
//				long b = System.nanoTime();
//				ScenarioManager.INSTANCE.readFile(file,new Aircraft(null,null));
//				System.out.println((System.nanoTime() - b )/1_000_000);
//			}
//			
//			
//		} 
//		catch (Exception e) 
//		{
//			e.printStackTrace();
//		}
//	}
}
