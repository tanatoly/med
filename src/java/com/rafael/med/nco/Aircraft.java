package com.rafael.med.nco;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rafael.med.nco.NcoManager.Mode;
import com.rafael.med.nco.ScenarioManager.ScenarioRow;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Aircraft 
{
	private static final Logger log = LogManager.getLogger();
	
	public static enum AircraftMode
	{
		RADAR, SA, DUAL;
	}
	
	public enum TargetType
	{
		NONE, AVION, NSA, RADAR;
	}

	public static final class SAInputSelf
	{
		public double mc_time_tag;
		public double to;
		//public StringProperty gps_date                            = new SimpleStringProperty();
		public short validity_word;
		public double roll;
		public double pitch;
		public double heading; //
		public double velocity_n;
		public double velocity_e;
		public double velocity_u;
		public double latitude;
		public double lontitude;
		public double altitude;
		public double pos_err;
		public double mach;
		public double true_aoa;
		public double ground_speed;
		public double barometer_altitude;
		public StringProperty fuel								  = new SimpleStringProperty();
		public StringProperty ac_type							  = new SimpleStringProperty();
		public StringProperty bingo								  = new SimpleStringProperty();
		public StringProperty station_selected_and_master_arm	  = new SimpleStringProperty();
		public StringProperty operational_status				  = new SimpleStringProperty();
		public StringProperty inu_status						  = new SimpleStringProperty();
		public StringProperty radar_status						  = new SimpleStringProperty();
		public StringProperty rwr_status						  = new SimpleStringProperty();
		public StringProperty declutter							  = new SimpleStringProperty();
		public StringProperty role								  = new SimpleStringProperty();
		public StringProperty buddy_global_id					  = new SimpleStringProperty();
		public StringProperty buddy_display_id					  = new SimpleStringProperty();
		public StringProperty bimg								  = new SimpleStringProperty();
		public StringProperty simg								  = new SimpleStringProperty();
		public StringProperty timg								  = new SimpleStringProperty();
		public StringProperty tag								  = new SimpleStringProperty();
		public StringProperty chaff_amount					      = new SimpleStringProperty(); 
		public StringProperty flar_amount					      = new SimpleStringProperty(); 
		public StringProperty aa_bvr_msl					      = new SimpleStringProperty(); 
		public StringProperty aa_a4m_msl					      = new SimpleStringProperty(); 
		public StringProperty ag_pgm_wpn					      = new SimpleStringProperty(); 
		public StringProperty ag_wpn						      = new SimpleStringProperty(); 
		public StringProperty station_1_weapon_code			      = new SimpleStringProperty(); 
		public StringProperty station_2_weapon_code			      = new SimpleStringProperty(); 
		public StringProperty station_3_weapon_code			      = new SimpleStringProperty(); 
		public StringProperty station_4_weapon_code			      = new SimpleStringProperty(); 
		public StringProperty station_5_weapon_code			      = new SimpleStringProperty(); 
		public StringProperty station_6_weapon_code			      = new SimpleStringProperty(); 
		public StringProperty station_7A_weapon_code		      = new SimpleStringProperty(); 
		public StringProperty station_7B_weapon_code		      = new SimpleStringProperty(); 
		public StringProperty station_8A_weapon_code		      = new SimpleStringProperty(); 
		public StringProperty station_8B_weapon_code		      = new SimpleStringProperty(); 
		public StringProperty station_9_weapon_code			      = new SimpleStringProperty(); 
		public StringProperty station_10_weapon_code		      = new SimpleStringProperty(); 
		public StringProperty station_11_weapon_code		      = new SimpleStringProperty(); 
		public StringProperty station_12_weapon_code		      = new SimpleStringProperty(); 
		public StringProperty launch_gun_trigr				      = new SimpleStringProperty(); 
		public StringProperty gun_ammunition				      = new SimpleStringProperty(); 
		
		
		public void clear()
		{
			mc_time_tag = 0.0;
			to = 0.0;
			validity_word = 0;
			roll = 0.0;
			pitch = 0.0;
			heading = 0.0; //
			velocity_n = 0.0;
			velocity_e = 0.0;
			velocity_u = 0.0;
			latitude = 0.0;
			lontitude = 0.0;
			altitude = 0.0;
			pos_err = 0.0;
			mach = 0.0;
			true_aoa = 0.0;
			ground_speed = 0.0;
			barometer_altitude = 0.0;
		}
	}
	
	public static final class SAInputTarget
	{
		public double mc_time_tag; 
		public int track_id; 
		public int radar_mode; 
		public int valid; 
		public int accuracy; 
		public double azimuth; 
		public double elevation; 
		public double range; 
		public double target_velocity; 
		public double horisontal_angle_of_velocity_vector; 
		public double vertical_angle_of_velocity_vector; 
		public double horisontal_velocity; 
		public double azimuth_angular_velocity; 
		public double elevation_angular_velocity;
		public boolean isDt;
		public double velocity_x;
		public double velocity_y;
		public double velocity_z;
		public double closing_speed;
		public short friend_foe_identification;
		public double horisontal_aspect_angle; 
		
		public int radar_lock; //from GUI
		public long lastLocalUpdateTime;
		
	}
	
	
	public static abstract class SAObject
	{
		public int globalId;
		public int displayId;
		public int sequence;
		public int total;
		public byte[] timeTag = new byte[8];
		public double azimuth;
		public double elevation;
		public double range;
		public double heading;
		public double altitude;
		public double velocityX;
		public double velocityY;
		public double velocityZ;
		public boolean isUnderClick;
		public long lastLocalUpdateTime;
		
		public SAObject(int globalId) 
		{
			this.globalId = globalId;
		}

	}
	
	public static final class SATrack extends SAObject
	{
		public int targetSource;
		public int lno;
		public int tag;
		public int valid;
		public int nolId;
		public byte[] nol = new byte[4];
		public int friendFoe;
		public int asLeaderGlobalId;
		public boolean isQ1;
		public boolean isQ2;
		
		public SATrack(int globalId)
		{
			super(globalId);
		}
		
	}
	
	public static final class SAMember extends SAObject
	{
		
		public int operationalStatus;
		public int inuStatus;
		public int radarStatus;
		public int rwrStatus;
		public int motherAc;
		public int role;
		public int q1LockGlobalId;
		public int q2LockGlobalId;
		public int radarLockGlobalId;
		
		public int fuel;
		public int bingo;
		public int tag;
		
		public short q1LockDisplayId;
		public short q2LockDisplayId;
		public short radarLockDisplayId;
		
		public double groundSpeed;
		public double mach;
		
		public SAMember(int globalId)
		{
			super(globalId);
		}
		
		
		
		/*
			
		AC type	7
		*/
		public byte formationGroup;//	1
		public int callsignId;//	16
		
		public String callsign;
		/*
		RO Global ID	32
		Buddy Global ID	32
		*/
		public int bimg;
		public int simg;
		public int timg;
		/*
		RO display id	16
		Buddy display id	16

		Legacy Freq1	48
		Leagcy Freq2	48
		MANET L Net	8
		MANET U1 Net	8
		MANET U2 Net	8
		Satcom mode	4
		Guard band	2
		Chaff amount	8
		Flare amount	8
		AA bvr msl	4
		AA a4m msl	4
		AG Pgm Wpn	4
		AG Wpn	4
		Station 1 Weapon Code	8
		Station 2 Weapon Code	8
		Station 3 Weapon Code	8
		Station 4 Weapon Code	8
		Station 5 Weapon Code	8
		Station 6 Weapon Code	8
		Station 7A Weapon Code	8
		Station 7B Weapon Code	8
		Station 8A Weapon Code	8
		Station 8B Weapon Code	8
		Station 9 Weapon Code	8
		Station 10 Weapon Code	8
		Station 11 Weapon Code	8
		Station 12 Weapon Code	8
		Launch gun trigr	16
		Gun ammunition	16
		*/
		
		
		
	}
	
	
	

/**********************************************************************************************************************/	
	
	// database
	public final Map<Integer,SAObject> 			saObjects 			= new ConcurrentHashMap<>(50);
	public final Map<Integer, SAInputTarget> 	saInputTargets 		= new ConcurrentHashMap<>(16);
	public final List<SAInputTarget>			saInputTargetsList  = new CopyOnWriteArrayList<>();
	public final SAInputSelf					saInputSelf			= new SAInputSelf();
	
	// scneraio 
	public AtomicBoolean isScenarioExists 							= new AtomicBoolean(false);
	public AtomicBoolean isScenarioRunning 							= new AtomicBoolean(false);
	public List<ScenarioRow> radarScenarioRows 						= new ArrayList<>(5000);
	public List<ScenarioRow> avionScenarioRows 						= new ArrayList<>(5000);
	public int radarScenarioRowsSize;
	public int avionScenarioRowsSize;
	public int radarScenarioIndex;
	public int avionScenarioIndex;
	private long currentScenarioTimeInMs;
	public long startScenarioTime;
	

	
	public int selfGlobalId;
	public final AtomicBoolean isRadarHasChange 					= new AtomicBoolean(false);
	public final AtomicBoolean isSaHasChange						= new AtomicBoolean(false);
	private int messageDTIndex;
	private int messageTWSIndex;
	public SAInputTarget selectedSaInputTarget;
	public SAObject selectedSaObject;
	public SATrack q1Track;
	public SATrack q2Track;
	public SAMember memberSelf;
	
	public final byte[] ip;
	public final String host;
	private InetSocketAddress standaloneSelfAddress;
	public final AircraftView view;
	public final AircraftMessageHandler messageHandler;
	
	public final AtomicBoolean isScenarioMessageExists = new AtomicBoolean(false);
	
	public AtomicInteger selectedInputTargetId		= new AtomicInteger(0);
	public AtomicInteger selectedSaObjectId			= new AtomicInteger(0);
	public AtomicInteger q1TrackId 					= new AtomicInteger(0);
	public AtomicInteger q2TrackId					= new AtomicInteger(0);
	public AtomicInteger currnetCanvasZoom 			= new AtomicInteger(0);
	
	
	public Aircraft(byte[] aircraftIp, String host)
	{
		this.ip 						= aircraftIp;
		this.host						= host;
		this.standaloneSelfAddress		= new InetSocketAddress(host, Integer.parseInt(NcoManager.INSTANCE.backboneLocalPort));
		this.messageHandler				= new AircraftMessageHandler(this);
		this.view						= new AircraftView(this);
	}
	
	public SAInputTarget getNextMessageDT()
	{	
		if(messageDTIndex >= saInputTargetsList.size())
		{
			messageDTIndex = 0;
		}
		return saInputTargetsList.get(messageDTIndex++);
	}
	
	
	public SAInputTarget getNextMessageTWS()
	{
		
		if(messageTWSIndex >= saInputTargetsList.size())
		{
			messageTWSIndex = 0;
		}
		return saInputTargetsList.get(messageTWSIndex++);
	}

	

	public void onTimerClick(long counter)
	{
		if(isScenarioExists.get() && isScenarioRunning.get())
		{
			scenarioProcess(counter);
			if(radarScenarioIndex == radarScenarioRowsSize && avionScenarioIndex == avionScenarioRowsSize)
			{
				NcoManager.INSTANCE.onScenarioFinished(this);
			}
		}
		
		
		
		NcoManager.INSTANCE.sendToBnet(this,BnetMessageBuilder.AIRCRAFT_ORIENTATION, null, this);
		
		if(counter%2 == 0 && !saInputTargetsList.isEmpty())
		{
			
			NcoManager.INSTANCE.sendToBnet(this,BnetMessageBuilder.TARGET_DT, null, this);
		}
		
		if(counter%4 == 0)
		{
			NcoManager.INSTANCE.sendToBnet(this,BnetMessageBuilder.AIRCRAFT_HYBRID_DATA, null, this);
		}
		if(counter%8 == 0 && !saInputTargetsList.isEmpty())
		{
			NcoManager.INSTANCE.sendToBnet(this,BnetMessageBuilder.TARGET_TWS, null, this);
		}
		if(counter%10 == 0)
		{
			if(NcoManager.INSTANCE.mode == Mode.MASTER)
			{
				NcoManager.INSTANCE.backboneController.toOutgoingMessage(BackboneMessageBuilder.VIEW_UPDATE, null, this);
			}
			checkAndRemove();
			Platform.runLater(() -> view.drawView());	
		}
				
		if(counter%16 == 0)
		{
			NcoManager.INSTANCE.sendToBnet(this,BnetMessageBuilder.AIRCRAFT_STATE, null, this);
		}
	}


	private void checkAndRemove()
	{
		for (Map.Entry<Integer, SAInputTarget> entry : saInputTargets.entrySet())
		{
			Integer trackId = entry.getKey();
			SAInputTarget inputTarget = entry.getValue();
			
			long dif = System.currentTimeMillis() - inputTarget.lastLocalUpdateTime;
			if(dif > 1_000)
			{
				saInputTargets.remove(trackId);
				saInputTargetsList.remove(inputTarget);
			}
		}
		
		for (Map.Entry<Integer, SAObject> entry : saObjects.entrySet())
		{
			Integer globalId 	= entry.getKey();
			SAObject saObject 	= entry.getValue();
			
			long dif = System.currentTimeMillis() - saObject.lastLocalUpdateTime;
			if(dif > 1_000)
			{
				saObjects.remove(globalId);
			}
		}
	}

	private void scenarioProcess(long counter)
	{
		currentScenarioTimeInMs = NcoManager.SCHEDULER_PERIOD_IN_MS * counter;
		
		boolean isContinue = true;
		if(!radarScenarioRows.isEmpty())
		{
			ScenarioRow radarScenarioRow = null;
			while(isContinue && radarScenarioIndex < radarScenarioRowsSize)
			{
				ScenarioRow currentRadarScenarioRow = radarScenarioRows.get(radarScenarioIndex);
				isContinue = currentRadarScenarioRow != null && currentRadarScenarioRow.timestampInMs <= currentScenarioTimeInMs;
				if(isContinue)
				{
					radarScenarioRow = currentRadarScenarioRow;
					radarScenarioIndex++;
				}
			}
			if(radarScenarioRow != null)
			{
				if(NcoManager.INSTANCE.mode == Mode.STANDALONE)
				{
					NcoManager.INSTANCE.backboneController.toOutgoingMessage(BackboneMessageBuilder.SA_INPUT_RADAR, standaloneSelfAddress, radarScenarioRow, ip);
				}
				else // MASTER
				{
					NcoManager.INSTANCE.backboneController.toOutgoingMessage(BackboneMessageBuilder.SA_INPUT_RADAR, null, radarScenarioRow, ip);
				}
			}
		}
		
		isContinue = true;
		if(!avionScenarioRows.isEmpty())
		{
			ScenarioRow avionScenarioRow = null;
			while(isContinue && avionScenarioIndex < avionScenarioRowsSize)  
			{
				ScenarioRow currentAvionScenarioRow = avionScenarioRows.get(avionScenarioIndex);
				isContinue = currentAvionScenarioRow != null && currentAvionScenarioRow.timestampInMs <= currentScenarioTimeInMs;
				if(isContinue)
				{
					avionScenarioRow = currentAvionScenarioRow;
					avionScenarioIndex++;
				}
			}
			if(avionScenarioRow != null)
			{
				if(NcoManager.INSTANCE.mode == Mode.STANDALONE)
				{
					NcoManager.INSTANCE.backboneController.toOutgoingMessage(BackboneMessageBuilder.SA_INPUT_AVIONICS, standaloneSelfAddress,  avionScenarioRow, ip);	
				}
				else // MASTER
				{
					NcoManager.INSTANCE.backboneController.toOutgoingMessage(BackboneMessageBuilder.SA_INPUT_AVIONICS, null, avionScenarioRow, ip);	
				}
			}
		}
	} 

	
	
	
	
	
	
	public void reset()
	{
		saObjects.clear();
		saInputTargets.clear();
		saInputTargetsList.clear();
		saInputSelf.clear();
		
		isScenarioExists.set(false);
		isScenarioRunning.set(false);
		radarScenarioRows.clear();
		avionScenarioRows.clear();
		radarScenarioIndex 			= 0;
		avionScenarioIndex 			= 0;
		radarScenarioRowsSize 		= 0;
		avionScenarioRowsSize 		= 0;
		currentScenarioTimeInMs 	= 0;
		startScenarioTime			= 0;
		
		isRadarHasChange.set(false);
		isSaHasChange.set(false);
		selfGlobalId = 0;
		messageDTIndex = 0;
		messageTWSIndex = 0;
		
		selectedSaInputTarget 	= null;
		selectedSaObject			= null;
		memberSelf				= null;
		
		selectedInputTargetId.set(0);
		selectedSaObjectId.set(0);
		q1TrackId.set(0);
		q2TrackId.set(0);
		
		

		
		isScenarioMessageExists.set(false);
		currnetCanvasZoom.set(0);;
		view.reset(true);
		
		log.info("RESET FOR {}" ,this);
	}

	
	
	
	public void stopScenario() 
	{
		this.startScenarioTime = 0;
		this.isScenarioRunning.set(false);
		
		
		view.zoomInButton.setDisable(true);
		view.zoomOutButton.setDisable(true);
		view.q1LockButton.setDisable(true);
		view.q2LockButton.setDisable(true);
		
		view.scenarioButton.setDisable(NcoManager.INSTANCE.mode == Mode.SLAVE);
		
		view.scenarioRunner.setVisible(false);
		view.rightPanelButton.setDisable(true);
		
		log.info("SCEANRIO FOR {} STOPPED", this );
	}

	public void startScenario(long startScenarioTime)
	{
		this.startScenarioTime = startScenarioTime;
		this.isScenarioRunning.set(true);
		
		
		saObjects.clear();
		saInputTargets.clear();
		saInputTargetsList.clear();
		saInputSelf.clear();
		
		avionScenarioIndex = 0;
		radarScenarioIndex = 0;
		currentScenarioTimeInMs 	= 0;
		isRadarHasChange.set(false);
		isSaHasChange.set(false);
		selfGlobalId = 0;
		messageDTIndex = 0;
		messageTWSIndex = 0;
		
		selectedSaInputTarget 	= null;
		selectedSaObject			= null;
		memberSelf				= null;
		selectedInputTargetId.set(0);
		selectedSaObjectId.set(0);
		q1TrackId.set(0);
		q2TrackId.set(0);
		
		
		view.reset(false);
		
		
		view.zoomInButton.setDisable(false);
		view.zoomOutButton.setDisable(false);
	
		view.saToggleButton.setSelected(true);
		view.scenarioButton.setDisable(true);
		view.scenarioRunner.setVisible(true);
		
		log.info("SCEANRIO FOR {} STARTED", this );
		
	}

	public void onRadarSelect(SAInputTarget selectedInputTarget)
	{
		if(selectedInputTarget == null)
		{
			for (SAInputTarget inputTarget : saInputTargets.values()) 
			{
				if(selectedInputTarget != inputTarget)
				{
					inputTarget.radar_lock = 0;
				}
			}
			selectedSaInputTarget =  null;
		}
		else if(selectedInputTarget != null && selectedInputTarget != selectedSaInputTarget)
		{
			selectedInputTarget.radar_lock = (selectedInputTarget.radar_lock == 0) ? 1 : 0 ;
			
			if(selectedInputTarget.radar_lock == 1)
			{	
				for (SAInputTarget inputTarget : saInputTargets.values()) 
				{
					if(selectedInputTarget != inputTarget)
					{
						inputTarget.radar_lock = 0;
					}
				}
				selectedSaInputTarget = selectedInputTarget;
			}
			else
			{
				selectedSaInputTarget =  null;
			}
			view.rightPanelButton.setDisable(! (NcoManager.INSTANCE.mode != Mode.SLAVE && selectedSaInputTarget != null));
		}
	}
	
	
	public void onSaSelect(SAObject selectedObject)
	{
		if(selectedObject == null)
		{
			for (SAObject sobject : saObjects.values()) 
			{
				if(sobject != selectedObject)
				{
					sobject.isUnderClick = false;
				}
			}
			selectedSaObject =  null;
		}
		else if(selectedObject != null && selectedObject != selectedSaObject)
		{
			selectedObject.isUnderClick = (selectedObject.isUnderClick) ? false : true;

			if(selectedObject.isUnderClick)
			{
				for (SAObject sobject : saObjects.values()) 
				{
					if(sobject != selectedObject)
					{
						sobject.isUnderClick = false;
					}
				}
				selectedSaObject = selectedObject;
			}
			else
			{
				selectedSaObject = null;
			}
		
			view.rightPanelButton.setDisable(!(NcoManager.INSTANCE.mode != Mode.SLAVE && selectedSaObject != null));
		}
		if(NcoManager.INSTANCE.mode == Mode.MASTER)
		{
			view.q1LockButton.setDisable(!(selectedObject != null && selectedObject instanceof SATrack && (q1Track == null || q1Track == selectedObject)));
			view.q2LockButton.setDisable(!(selectedObject != null && selectedObject instanceof SATrack && (q2Track == null || q2Track == selectedObject)));
		}
	}
	
	
	public void setQ(boolean isQ1)
	{
		if(isQ1) //q1
		{
			if(q1Track == null)
			{
				q1TrackId.set(selectedSaObject.globalId);
			}
			else // q1Track == selectedTrack
			{
				q1TrackId.set(0);
			}
		}
		else //q2
		{
			if(q2Track == null)
			{
				q2TrackId.set(selectedSaObject.globalId);
			}
			else // q2Track == selectedTrack
			{
				q2TrackId.set(0);
			}
		}
		
		//System.out.println("q1 = " + q1TrackId + " q2 = " +q2TrackId);
		
	}	
	
	
	public void onQ1Select(SATrack saObject)
	{
		if(q1Track != saObject)
		{	
			if(saObject == null && q1Track != null)
			{
				q1Track.isQ1 = false;
				q1Track = saObject;
				q1TrackId.set(0); 
			}
			else if(saObject != null && q1Track == null)
			{
				q1Track = saObject;
				q1Track.isQ1 = true;
				q1TrackId.set(q1Track.globalId); 
			}
			//System.out.println("-------------- q1 = " + q1Track);
		}
	}
	
	
	public void onQ2Select(SATrack saObject) 
	{
		if(q2Track != saObject)
		{	
			if(saObject == null && q2Track != null)
			{
				q2Track.isQ2 = false;
				q2Track = saObject;
				q2TrackId.set(0); 
			}
			else if(saObject != null && q2Track == null)
			{
				q2Track = saObject;
				q2Track.isQ2 = true;
				q2TrackId.set(q2Track.globalId); 
			}
			//System.out.println("-------------- q2 = " + q2Track);
		}
		
	}
	
	public void setCurrentZoom(boolean isIncrement)
	{
		if(isIncrement)
		{
			currnetCanvasZoom.incrementAndGet();
		}
		else
		{
			currnetCanvasZoom.decrementAndGet();
		}
	}
	
	public void onCanvasZoom(int newCanvasZoom) 
	{
		currnetCanvasZoom.set(newCanvasZoom);
		view.radarView.setCurrentZoom(newCanvasZoom);
		view.saView.setCurrentZoom(newCanvasZoom);
		view.dualView.setCurrentZoom(newCanvasZoom);
	}
	
	@Override
	public String toString() 
	{
		return "Aircraft [" + host + "]";
	}

	
}
