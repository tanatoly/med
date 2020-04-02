package com.rafael.med.nco;

import java.nio.ByteBuffer;

import com.rafael.med.common.MessageBuilder;
import com.rafael.med.nco.ScenarioManager.ScenarioRow;


public enum BackboneMessageBuilder implements MessageBuilder
{
	BNET_DELEGATION(0) //opcode from source 
	{
		@Override
		public void buildMessage(ByteBuffer buffer, Object... params)
		{
			ByteBuffer source 		 = (ByteBuffer) params[0];
		
			buffer.put(NcoManager.INSTANCE.backboneLocalAddress);
			buffer.put(source);
		}
	},
	SA_INPUT_RADAR(201)
	{
		@Override
		public void buildMessage(ByteBuffer buffer, Object... params)
		{
			ScenarioRow scenarioRow = (ScenarioRow) params[0];
			byte[] ip = (byte[]) params[1];
			buffer.put(ip);
			
			header(buffer);
			scenarioRow.toBufferRadar(buffer);
			
			buffer.put(7, (byte) buffer.position());
		}
	},
	SA_INPUT_AVIONICS(202)
	{
		@Override
		public void buildMessage(ByteBuffer buffer, Object... params)
		{
			ScenarioRow scenarioRow = (ScenarioRow) params[0];
			byte[] ip = (byte[]) params[1];
			buffer.put(ip);
			header(buffer);
			
			scenarioRow.toBufferAvion(buffer);
			buffer.put(7, (byte) buffer.position());
		}
	}, 
	KEEP_ALIVE(200)
	{
		@Override
		public void buildMessage(ByteBuffer buffer, Object... params)
		{
			long startTime			 = (long) params[0];	
			
			buffer.put(NcoManager.INSTANCE.backboneLocalAddress);
			header(buffer);
			
			buffer.putLong(startTime);
			buffer.putInt(NcoManager.INSTANCE.selectedModeBeforeMessage.ordinal());
			
			buffer.put(7, (byte) buffer.position());
		}
	}, 
	START_SCENARIO(210)
	{
		@Override
		public void buildMessage(ByteBuffer buffer, Object... params)
		{
			buffer.put(NcoManager.INSTANCE.backboneLocalAddress);
			header(buffer);
			buffer.put(7, (byte) buffer.position());
		}
	},
	STOP_SCENARIO(211)
	{
		@Override
		public void buildMessage(ByteBuffer buffer, Object... params)
		{
			buffer.put(NcoManager.INSTANCE.backboneLocalAddress);
			header(buffer);
			buffer.put(7, (byte) buffer.position());
		}
	},
	
	VIEW_UPDATE(212)
	{
		@Override
		public void buildMessage(ByteBuffer buffer, Object... params)
		{
			
			Aircraft aircraft = (Aircraft) params[0];
			buffer.put(aircraft.ip);
			header(buffer);
			
			buffer.putInt(aircraft.currnetCanvasZoom.get());
			buffer.putInt(aircraft.selectedInputTargetId.get()); 
			buffer.putInt(aircraft.selectedSaObjectId.get()); 
			buffer.putInt(aircraft.q1TrackId.get()); 
			buffer.putInt(aircraft.q2TrackId.get()); 
			
			buffer.put(7, (byte) buffer.position());
		}
	}
	
	
	;
	
	
	
	
	public long counter;
	public byte opcode;
	private BackboneMessageBuilder(int opcode)
	{
		this.opcode = (byte) opcode;

	}
	
	@Override
	public Object getOpcodeName()
	{
		return this;
	}
	
	protected void header(ByteBuffer buffer)
	{
		buffer.put((byte) counter++);
		buffer.put((byte) 255);
		buffer.put(opcode);
		buffer.put((byte) 0);
	}

	@Override
	public String toString()
	{
		return name() +  " (" + opcode + ")";
	}
}
