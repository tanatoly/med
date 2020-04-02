package com.rafael.med.nco;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.configuration2.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawersStack;
import com.rafael.med.common.ByteBufferRing;
import com.rafael.med.common.Constants;
import com.rafael.med.common.MessageBuilder;
import com.rafael.med.common.MessageHandler;
import com.rafael.med.common.Scheduler;
import com.rafael.med.common.Threads;
import com.rafael.med.common.Utilities;
import com.rafael.med.common.ByteBufferRing.WaitStrategyType;

import javafx.application.Platform;

public class NcoManager
{
	
	public static enum Mode
	{
		MASTER, SLAVE, STANDALONE
	}
	
	
	private static final Logger log = LogManager.getLogger();
	
	public static final long SCHEDULER_PERIOD_IN_MS = 20;
	
	
	
	public static final NcoManager INSTANCE = new NcoManager();
	
	public final AtomicBoolean isOnceStarted = new AtomicBoolean(false);

	public BnetController 		bnetController;
	public BackboneController 	backboneController;
	
	final AtomicBoolean isRunning = new AtomicBoolean(false);

	public Mode mode;
	public Mode selectedModeBeforeMessage;
	private Scheduler scheduler;
	private String backboneLocalHost;
	public byte[] backboneLocalAddress;
	private long startTime = 0;
	public String backboneLocalPort;
	private NcoView ncoView;
	private Configuration configuration;
		
	private Map<String, Aircraft> aircrafts = new HashMap<>();
	
	public void init(Configuration configuration, NcoView ncoView, List<String> args)
	{
		this.configuration	= configuration;
		this.ncoView 		= ncoView;
	}
	
	public void start(String bnetLocalHost, String bnetLocalPort, String bnetRemoteHost, String bnetRemotePort, String backboneLocalHost, String backboneLocalPort, String backboneMulticast) throws Exception
	{
		log.trace("starting start process");
		
		this.backboneLocalHost	= backboneLocalHost;
		this.backboneLocalPort	= backboneLocalPort;
		
		try 
		{
			backboneLocalAddress = InetAddress.getByName(backboneLocalHost).getAddress();
		} 
		catch (UnknownHostException e) 
		{
			throw new IllegalArgumentException(e);
		}
		
		if(!isOnceStarted.get())
		{
			ByteOrder byteOrder = configuration.getString("byteOrder", Constants.Strings.LITTLE).equalsIgnoreCase(Constants.Strings.LITTLE) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
			int maxBufferSize 	= configuration.getInt("maxBufferSize", Constants.MAX_UPD_SIZE);
			int ringSize 		= configuration.getInt("ringSize", 1024);
			
			WaitStrategyType waitStrategy = ByteBufferRing.waitStrategyByName(configuration.getString("waitStrategy", WaitStrategyType.BLOCKING.name()));
			ByteBufferRing.IN inRing = new ByteBufferRing.IN(ringSize,  maxBufferSize, byteOrder, false, Threads.newSingleThreadExecutor("ring"), false, waitStrategy, (event, sequence, endOfBatch) -> 
			{
				ByteBuffer buffer 				= event.buffer;
				Object	source	  				= event.arg1;
				MessageHandler messageHandler 	= (MessageHandler) event.arg2;
				messageHandler.handle(buffer, source);
			});
			log.debug("RING CREATED -> byteOrder = {}, maxBufferSize = {}, ringSize = {}, waitStrategy = {}", byteOrder, maxBufferSize, ringSize, waitStrategy);

			bnetController 		= new BnetController(inRing, configuration, byteOrder, maxBufferSize, bnetLocalHost, bnetLocalPort, bnetRemoteHost, bnetRemotePort, new MessageHandler() 
			{
				@Override
				public void handle(ByteBuffer buffer, Object source) throws Exception
				{	
					backboneController.toOutgoingMessage(BackboneMessageBuilder.BNET_DELEGATION, null, buffer);
				}
			}, isRunning);
			backboneController 	= new BackboneController(inRing, configuration, byteOrder, maxBufferSize, backboneLocalHost, backboneLocalPort, backboneMulticast, new MessageHandler() 
			{
				@Override
				public void handle(ByteBuffer buffer, Object source) throws Exception
				{
					byte[] aircraftIp = new byte[4];
					buffer.get(aircraftIp);
					final String targetHost = Utilities.getByAdressWithoutException(aircraftIp);
					final boolean isLocal = targetHost.equals(backboneLocalHost);
					
					int tos 				= Byte.toUnsignedInt(buffer.get());
					int counter				= Byte.toUnsignedInt(buffer.get());
					int opcode				= Byte.toUnsignedInt(buffer.get());
					int length				= Byte.toUnsignedInt(buffer.get());
					
					log.debug("message from address = {} , target aircraft = {} , isLocal = {} , opcode = {} , buffer = {}", source.equals(targetHost), isLocal, opcode , buffer );
					
					handleNcoMessage(buffer, aircraftIp, targetHost, isLocal, opcode);
				}
			}, isRunning);	
			log.debug("CONTROLLERS CREATED");
			bnetController.start();
			backboneController.start();
			
			log.debug("CONTROLLERS STARTED");
			
			
			scheduler = new Scheduler(isRunning, inRing,  byteOrder, SCHEDULER_PERIOD_IN_MS, new MessageHandler()
			{
				@Override
				public void handle(ByteBuffer buffer, Object source) throws Exception 
				{
					long counter = buffer.getLong();
					
					for (Aircraft aircraft : aircrafts.values())
					{
						if(aircraft != null)
						{
							aircraft.onTimerClick(counter);
						}
					}
				}
			});
			log.debug("SCHEDULER CREATED");
			
			Threads.newScheduledThreadPool(1, "keep-alive").scheduleAtFixedRate(new Runnable() 
			{
				@Override
				public void run() 
				{
					backboneController.toOutgoingMessage(BackboneMessageBuilder.KEEP_ALIVE, null, startTime);
				}
			}, 1000, 2000, TimeUnit.MILLISECONDS);
			isOnceStarted.set(true);
			log.info("NCO APPLICATION STARTED");
		}
		startTime = System.currentTimeMillis();
		isRunning.set(true);
		
		

	}
	
	public void stop() throws Exception
	{
		isRunning.set(false);
		ncoView.startStopScenarioButton.setDisable(true);
	}
	

	private void changeMode(Mode mode) 
	{
		this.mode = mode;
		
		scheduler.stop();
		for (Aircraft aircraft : aircrafts.values())
		{	
			aircraft.reset();
			aircraft.view.scenarioButton.setDisable(mode == Mode.SLAVE);
		}
		
		
		ncoView.startStopScenarioButton.setGraphic(ncoView.startScenarioText);
		ncoView.startStopScenarioButton.setDisable(true);
		ncoView.slaveButton.setSelected(false);
		ncoView.masterButton.setSelected(false);
		ncoView.standaloneButton.setSelected(false);
		
		if(mode == Mode.SLAVE)
		{
			ncoView.slaveButton.setSelected(true);
		}
		else if(mode == Mode.MASTER)
		{
			ncoView.masterButton.setSelected(true);
			ncoView.startStopScenarioButton.setDisable(false);
		}
		else if(mode == Mode.STANDALONE)
		{
			ncoView.standaloneButton.setSelected(true);
		}
		log.info("CHANGED MODE = {}", mode);
		
	}
	
	
	public void handleNcoMessage(ByteBuffer buffer, byte[] aircraftIp, String targetHost ,boolean isLocal, int opcode)
	{
		if(opcode == 200)
		{	
			final long remoteStartTime 		= buffer.getLong();
			final int remoteModeInt			= buffer.getInt();
			final Mode remoteMode			= Mode.values()[remoteModeInt];
			
			Platform.runLater(() ->
			{
				Aircraft aircraft = aircrafts.get(targetHost);
				if(aircraft == null)
				{
					aircraft = new Aircraft(aircraftIp, targetHost);
					aircrafts.put(targetHost, aircraft);
					ncoView.aircraftPane.addAircraft(aircraft);
					aircraft.reset();
					aircraft.view.scenarioButton.setDisable(mode == Mode.SLAVE);
					log.info("ADDED NEW AIRCTRAFT FROM ADDRESS = {} , IS LOCAL = {}", targetHost,isLocal);
					if(isLocal)
					{
						changeMode(remoteMode);
					}	
				}
				if(isLocal && mode != remoteMode)
				{
					changeMode(remoteMode);
				}
				else if(!isLocal && remoteStartTime > startTime && remoteMode == Mode.MASTER && (mode == Mode.MASTER || mode == Mode.STANDALONE))
				{
					changeMode(Mode.SLAVE);								
				}
			});
		}
		else if(opcode == 210) // start command
		{
			Platform.runLater(() -> 
			{
				log.info("GOT SCENARIO MESSAGE START FROM {}", targetHost);
				long startScenarioTime = System.currentTimeMillis();
				for (Aircraft aircraft : aircrafts.values()) 
				{
					aircraft.startScenario(startScenarioTime);
				}
				ncoView.startStopButton.setDisable(true);
				ncoView.startStopScenarioButton.setGraphic(ncoView.stopScenarioText);
				ncoView.startStopScenarioButton.setDisable(!isLocal);
				scheduler.start();
				
			});
		}
		else if(opcode == 211) // stop command
		{
			Platform.runLater(() -> 
			{
				log.info("GOT SCENARIO MESSAGE STOP FROM {}", targetHost);
				scheduler.stop();
				for (Aircraft aircraft : aircrafts.values()) 
				{
					aircraft.stopScenario();
				}
				ncoView.startStopButton.setDisable(false);
				ncoView.startStopScenarioButton.setGraphic(ncoView.startScenarioText);
				ncoView.startStopScenarioButton.setDisable(!isLocal);
				log.info("SCENARIO STOPPED");
			});
		}
		else
		{
			Aircraft aircraft = aircrafts.get(targetHost);
			if(aircraft != null)
			{
				if((opcode == 201 || opcode == 202) && aircraft.isScenarioMessageExists.compareAndSet(false, true))
				{
					Platform.runLater(() -> 
					{
						aircraft.view.scenarioRunner.setVisible(true);
					});
				}
				aircraft.messageHandler.handleMessage(opcode, buffer);
			}
		}
	}

	
	public void sendToBnet(Aircraft aircraft , MessageBuilder messageBuilder,Object target,Object... params)
	{
		if(aircraft.host.equals(backboneLocalHost))
		{
			bnetController.toOutgoingMessage(messageBuilder, target, params);
		}
	}
		
	public void onScenarioFinished(Aircraft aircraft) 
	{
		aircraft.isScenarioRunning.set(false);
		log.info("SCEANARIO ON {} FINISHED", aircraft);
		boolean isAllFinished = true;
		for (Aircraft currAir : aircrafts.values())
		{	
			isAllFinished = (isAllFinished && !currAir.isScenarioRunning.get())  || ((isAllFinished && !currAir.isScenarioMessageExists.get())) ;
		}
		if(isAllFinished)
		{
			backboneController.toOutgoingMessage(BackboneMessageBuilder.STOP_SCENARIO, null, backboneLocalHost);
		}
	}
	
	
	

	public void showRightPanel(Aircraft aircraft) 
	{
		if(mode == NcoManager.Mode.MASTER)
		{
			((JFXDrawersStack)ncoView.cardPane).toggle((JFXDrawer) ncoView.cardPane.getUserData(),true);
			ncoView.closeButton.setDisable(false);
		}
		
	}
	
}
