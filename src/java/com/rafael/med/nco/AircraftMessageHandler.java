package com.rafael.med.nco;

import java.nio.ByteBuffer;

import com.rafael.med.common.BitByteUtils;
import com.rafael.med.nco.Aircraft.SAInputTarget;
import com.rafael.med.nco.Aircraft.SAMember;
import com.rafael.med.nco.Aircraft.SAObject;
import com.rafael.med.nco.Aircraft.SATrack;

public class AircraftMessageHandler 
{
	
	private Aircraft aircraft;
	
	public AircraftMessageHandler(Aircraft aircraft)
	{
		this.aircraft = aircraft;
	}
	
	public void handleMessage(int opcode, ByteBuffer buffer)
	{
		
		
	//	System.out.println(" GOT OPCODE = " + opcode) ;
		if(opcode == 104)
		{
			networkTrack(buffer);
		}
		else if(opcode == 101)
		{
			networkMemberHighPriority(buffer);
		}
		else if(opcode == 110)
		{
			syncTime(buffer);
		}
		else if(opcode == 111)
		{
			uplinkStatus(buffer);
		}
		else if(opcode == 113)
		{
			trackData(buffer);
		}
		else if(opcode == 114)
		{
			targetPreLaunchRx(buffer);
		}
		else if(opcode == 115)
		{
			targetPostLaunchRx(buffer);
		}
		else if(opcode == 116)
		{
			lockedTargetData(buffer);
		}
		else if(opcode == 102)
		{
			networkMemberLowPriority(buffer);
		}
		else if(opcode == 103)
		{
			networkMemberExtended(buffer);
		}
		else if(opcode == 105)
		{
			rwr(buffer);
		}
		else if(opcode == 106)
		{
			waypointRxSync(buffer);
		}
		else if(opcode == 107)
		{
			waypointRxDesignation(buffer);
		}
		else if(opcode == 108)
		{
			presetSmsTextGet(buffer);
		}
		else if(opcode == 109)
		{
			smsRx(buffer);
		}
		else if(opcode == 112)
		{
			uplinkData(buffer);
		}
		
		else if(opcode == 201)
		{
			inputRowRadar(buffer);	
		}
		else if(opcode == 202)
		{
			inputRowAvion(buffer);	
		}
		else if(opcode == 212)
		{
			viewUpdate(buffer);	
		}
	}

	private void viewUpdate(ByteBuffer buffer) 
	{
		int newCanvasZoom 			= buffer.getInt();
		int inputTargetTarckId 		= buffer.getInt();
		int selectedSaObjectId 		= buffer.getInt();
		int q1SaTarck 				= buffer.getInt();
		int q2SaTrack 				= buffer.getInt();
		
		aircraft.onCanvasZoom(newCanvasZoom);
		
		SAInputTarget saInputTarget = aircraft.saInputTargets.get(inputTargetTarckId);
		aircraft.onRadarSelect(saInputTarget);
		
		
		SAObject saObject = aircraft.saObjects.get(selectedSaObjectId);
		aircraft.onSaSelect(saObject);

		
		saObject = aircraft.saObjects.get(q1SaTarck);
		aircraft.onQ1Select((SATrack) saObject);
		
		saObject = aircraft.saObjects.get(q2SaTrack);
		aircraft.onQ2Select((SATrack) saObject);

	}

	private void inputRowAvion(ByteBuffer buffer)
	{
		
		int uniqueSensorTrackId = buffer.getInt();
		int uniqueSensorId		= buffer.getInt();
		int valid				= buffer.getInt();
		double LastUpdSystTime	= buffer.getDouble();
		aircraft.saInputSelf.mc_time_tag				= buffer.getDouble() + (aircraft.startScenarioTime/1000.0);
		aircraft.saInputSelf.latitude					= buffer.getDouble();
		aircraft.saInputSelf.lontitude					= buffer.getDouble();
		aircraft.saInputSelf.altitude					= buffer.getDouble();
		double nSpeed			= buffer.getDouble();
		double veln				= buffer.getDouble();
		double vele				= buffer.getDouble();
		double veld				= buffer.getDouble();
		double [] cov 			= new double[36];
		for (int i = 0; i < cov.length; i++) 
		{
			cov[i] = buffer.getDouble();
		}
		
		aircraft.saInputSelf.to							= buffer.getDouble() + (aircraft.startScenarioTime/1000.0);
		aircraft.saInputSelf.heading					= buffer.getDouble();
		aircraft.saInputSelf.pitch						= buffer.getDouble();
		aircraft.saInputSelf.roll						= buffer.getDouble();
		
		
		
		
	}

	private void inputRowRadar(ByteBuffer buffer) 
	{
		aircraft.isRadarHasChange.set(true);
		
		
		double t					= buffer.getDouble();
		double trackTime			= buffer.getDouble();
		int uniqueSensorTrackId 	= buffer.getInt();
		int uniqueSensorId			= buffer.getInt();
		double trackQual			= buffer.getDouble();
		int positionInputType		= buffer.getInt();
		double lat					= buffer.getDouble();
		double lon					= buffer.getDouble();
		double alt					= buffer.getDouble();
		double [] velocity				= new double[3];
		for (int i = 0; i < velocity.length; i++) 
		{
			velocity[i] = buffer.getDouble();
		}
		double [] stateCov 				= new double[81];
		for (int i = 0; i < stateCov.length; i++) 
		{
			stateCov[i] = buffer.getDouble();
		}
		double rcs					= buffer.getDouble();
		double plotTime				= buffer.getDouble();
		double plotAzimuth			= buffer.getDouble();
		double plotElevation		= buffer.getDouble();
		double plotRange			= buffer.getDouble();
		double plotDopp				= buffer.getDouble();
		double plotRangeVar			= buffer.getDouble();
		double plotAzVar			= buffer.getDouble();
		double plotElVar			= buffer.getDouble();
		double plotDoppVar			= buffer.getDouble();
		int radMod					= buffer.getInt();
		double plotSNR				= buffer.getDouble();
		
		
		Integer trackId = (int) (BitByteUtils.int0(uniqueSensorTrackId) & 0xF);
		
		
		
		//System.out.println("--------------------------- " +  Integer.toHexString(trackId));
		
		SAInputTarget saInputTarget = aircraft.saInputTargets.get(trackId);
		if(saInputTarget == null)
		{
			saInputTarget = new SAInputTarget();
			aircraft.saInputTargets.put(trackId, saInputTarget);
			aircraft.saInputTargetsList.add(saInputTarget);
		}
		saInputTarget.lastLocalUpdateTime = System.currentTimeMillis();
		
		
		saInputTarget.mc_time_tag = t + (aircraft.startScenarioTime/1000.0);
		saInputTarget.track_id = trackId;
		saInputTarget.range = plotRange;
		saInputTarget.azimuth = plotAzimuth;
		saInputTarget.isDt    = true;
				
		
		saInputTarget.velocity_x = velocity[0];
		saInputTarget.velocity_y = velocity[1];
		saInputTarget.velocity_z = velocity[2];
	}

	private void uplinkData(ByteBuffer buffer)
	{
		// TODO Auto-generated method stub
		
	}

	private void smsRx(ByteBuffer buffer) {
		// TODO Auto-generated method stub
		
	}

	private void presetSmsTextGet(ByteBuffer buffer) 
	{
		int messageId 	= Byte.toUnsignedInt(buffer.get());
		char[] message 	= new char[45];
		for (int i = 0; i < message.length; i++)
		{
			message[i] = (char) buffer.get();
		}
		
	}

	private void waypointRxDesignation(ByteBuffer buffer) {
		// TODO Auto-generated method stub
		
	}

	private void waypointRxSync(ByteBuffer buffer) {
		// TODO Auto-generated method stub
		
	}

	private void rwr(ByteBuffer buffer) {
		// TODO Auto-generated method stub
		
	}

	private void networkMemberExtended(ByteBuffer buffer)
	{
		/*
		 	Global id	32
			Display id	16
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

	private void networkMemberLowPriority(ByteBuffer buffer) 
	{
		/*
		 	Global id	32
			Display id	16
			AC type	7
			Formation group	1
			Callsign ID	16
			Callsign	48
			RO Global ID	32
			Buddy Global ID	32
			BIMG	4
			SIMG	4
			TIMG	4
			RO display id	16
			Buddy display id	16

		 */
		
	}

	private void lockedTargetData(ByteBuffer buffer) {
		// TODO Auto-generated method stub
		
	}

	private void targetPostLaunchRx(ByteBuffer buffer) {
		// TODO Auto-generated method stub
		
	}

	private void targetPreLaunchRx(ByteBuffer buffer) {
		// TODO Auto-generated method stub
		
	}

	private void trackData(ByteBuffer buffer) {
		// TODO Auto-generated method stub
		
	}

	private void uplinkStatus(ByteBuffer buffer) {
		// TODO Auto-generated method stub
		
	}

	private void syncTime(ByteBuffer buffer) {
		// TODO Auto-generated method stub
		
	}

	private void networkMemberHighPriority(ByteBuffer buffer)
	{
		
		
		Integer globalId = buffer.getInt();
		
		SAObject saObject = aircraft.saObjects.get(globalId);
		if(saObject == null)
		{
			saObject = new SAMember(globalId);
			aircraft.saObjects.put(globalId, saObject);
		}
		
		SAMember saMember = (SAMember) saObject;
		saMember.lastLocalUpdateTime = System.currentTimeMillis();
	
		saMember.displayId 				= buffer.getShort();
		saMember.sequence  				= buffer.getShort();
		saMember.total					= buffer.getShort();	
		buffer.get(saMember.timeTag);
		
		saMember.azimuth					= NcoUtil.toScale32(BitByteUtils.toUnsignedShort(buffer.getShort()), 180.0, 16);
		if(saMember.azimuth > 180)
		{
			saMember.azimuth = saMember.azimuth -360;
		}
		
		
		saMember.elevation				= NcoUtil.toScale16(buffer.getShort(), 90.0, 15);		
		saMember.range					= NcoUtil.toScale32(BitByteUtils.toUnsignedShort(buffer.getShort()), 256.0, 15);	
		saMember.heading				= NcoUtil.toScale32(BitByteUtils.toUnsignedShort(buffer.getShort()), 180.0, 16);
		
		byte param = buffer.get();
		
		saMember.operationalStatus    	= param & 0x03;
		saMember.inuStatus				= (param >> 3) & 0x01;
		saMember.radarStatus			= (param >> 4) & 0x01;
		saMember.rwrStatus				= (param >> 5) & 0x01;
		saMember.motherAc				= (param >> 6) & 0x01;
		
		if(saMember.motherAc == 1)
		{
			aircraft.memberSelf = saMember;
		}
		
		saMember.role					= buffer.get();
		saMember.q1LockGlobalId			= buffer.getInt();
		saMember.q2LockGlobalId			= buffer.getInt();
		saMember.radarLockGlobalId		= buffer.getInt();
		saMember.fuel					= BitByteUtils.toUnsignedByteAsInt(buffer.get()) * 50;
		
		byte param1 = buffer.get();
		saMember.bingo					= param1 & 0x01;
		saMember.tag					= (param1 >> 1) & 0xF;
		
		saMember.groundSpeed			= NcoUtil.toScale32(BitByteUtils.toUnsignedShort(buffer.getShort()),2500.0 , 16);
		saMember.mach					= NcoUtil.toScale32(BitByteUtils.toUnsignedShort(buffer.getShort()),2.048 , 16);
		saMember.altitude				= NcoUtil.toScale32(BitByteUtils.toUnsignedShort(buffer.getShort()),16384.0 , 15);
		
		//System.out.println("globalId = " + Integer.toHexString(globalId) + " az = " +saMember.azimuth + " , range = " + saMember.range + " h = " + saMember.heading);
			
		saMember.velocityX				= NcoUtil.toScale16(buffer.getShort(), 4.0, 15);
		saMember.velocityY 				= NcoUtil.toScale16(buffer.getShort(), 4.0, 15);
		saMember.velocityZ 				= NcoUtil.toScale16(buffer.getShort(), 4.0, 15);

			
		saMember.q1LockDisplayId			= buffer.getShort();
		saMember.q2LockDisplayId			= buffer.getShort();
		saMember.radarLockDisplayId			= buffer.getShort();
		
		aircraft.isSaHasChange.set(true);
		

	}

	private void networkTrack(ByteBuffer buffer) 
	{
		
		
		Integer globalId = buffer.getInt();
		
		SAObject saObject = aircraft.saObjects.get(globalId);
		if(saObject == null)
		{
			saObject = new SATrack(globalId);
			aircraft.saObjects.put(globalId, saObject);
		}
		
		SATrack saTrack = (SATrack) saObject;
		saTrack.lastLocalUpdateTime = System.currentTimeMillis();
	
		saTrack.displayId 				= buffer.getShort();
		saTrack.sequence  				= buffer.getShort();
		saTrack.total					= buffer.getShort();	
		buffer.get(saTrack.timeTag);
		
		byte param = buffer.get();
		
		saTrack.targetSource    		= param & 0x03;
		saTrack.lno						= (param >>2) & 0x01;
		saTrack.tag						= (param >>3) & 0x04;
		saTrack.valid					= (param >> 7)& 0x01;
		
		buffer.get();
			
		saTrack.azimuth					= NcoUtil.toScale32(BitByteUtils.toUnsignedShort(buffer.getShort()), 180.0, 16);
		if(saTrack.azimuth > 180)
		{
			saTrack.azimuth = saTrack.azimuth -360;
		}
		
		
		saTrack.elevation				= NcoUtil.toScale16(buffer.getShort(), 90.0, 15);		
		saTrack.range					= NcoUtil.toScale32(BitByteUtils.toUnsignedShort(buffer.getShort()), 256.0, 16);	
		
		short param1					= buffer.getShort();
		saTrack.heading					= BitByteUtils.toUnsignedInt(param1 & 0x1FF);
		saTrack.altitude				= NcoUtil.toScale32(BitByteUtils.toUnsignedShort((short) ((param >> 9) & 0x07)),10.0 , 7);
		
		//System.out.println("globalId = " + Integer.toHexString(globalId) + " az = " +saTrack.azimuth + " , range = " + saTrack.range + " h = " + saTrack.heading);
		
		saTrack.nolId					= buffer.get();
		buffer.get(saTrack.nol);
		saTrack.friendFoe				= buffer.getShort();
		saTrack.asLeaderGlobalId		= buffer.getInt();
		saTrack.velocityX				= NcoUtil.toScale16(buffer.getShort(), 4.0, 15);
		saTrack.velocityY 				= NcoUtil.toScale16(buffer.getShort(), 4.0, 15);
		saTrack.velocityZ 				= NcoUtil.toScale16(buffer.getShort(), 4.0, 15);

		aircraft.isSaHasChange.set(true);
		
	}
}
