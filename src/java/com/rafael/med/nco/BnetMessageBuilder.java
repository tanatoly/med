package com.rafael.med.nco;

import java.nio.ByteBuffer;

import com.rafael.med.common.MessageBuilder;
import com.rafael.med.nco.Aircraft.SAInputTarget;


public enum BnetMessageBuilder implements MessageBuilder
{
	AIRCRAFT_ORIENTATION(1)
	{
		@Override
		public void buildMessage(ByteBuffer buffer, Object... params)
		{
			Aircraft aircraft = (Aircraft) params[0];
			header(buffer);
			
			buffer.put(NcoUtil.time2mctime(aircraft.saInputSelf.to));
			buffer.putShort(NcoUtil.toDiscrete16(aircraft.saInputSelf.roll, 90.0, 15));
			buffer.putShort(NcoUtil.toDiscrete16(aircraft.saInputSelf.pitch, 90.0, 15));
			buffer.putShort(NcoUtil.toDiscrete16(aircraft.saInputSelf.heading, 90.0, 15));			
			footer(buffer);
		}
	},
	AIRCRAFT_HYBRID_DATA(2)
	{
		@Override
		public void buildMessage(ByteBuffer buffer, Object... params)
		{
			Aircraft aircraft = (Aircraft) params[0];
			header(buffer);
			
			
			buffer.put(NcoUtil.time2mctime(aircraft.saInputSelf.mc_time_tag));
			buffer.putShort(NcoUtil.time2mcdate(aircraft.saInputSelf.mc_time_tag));
			buffer.putShort(aircraft.saInputSelf.validity_word);
			buffer.putShort(NcoUtil.toDiscrete16(aircraft.saInputSelf.roll, 90.0, 15));
			buffer.putShort(NcoUtil.toDiscrete16(aircraft.saInputSelf.pitch, 90.0, 15));
			buffer.putShort(NcoUtil.toDiscrete16(aircraft.saInputSelf.heading, 90.0, 15));
			buffer.putInt(NcoUtil.swap(NcoUtil.toDiscrete32(aircraft.saInputSelf.velocity_n, 842.865, 31)));
			buffer.putInt(NcoUtil.swap(NcoUtil.toDiscrete32(aircraft.saInputSelf.velocity_e, 842.865, 31)));
			buffer.putShort(NcoUtil.toDiscrete16(aircraft.saInputSelf.velocity_u, 842.865, 15));
			buffer.putInt(NcoUtil.swap(NcoUtil.toDiscrete32(aircraft.saInputSelf.latitude, 90.0, 31)));
			buffer.putInt(NcoUtil.swap(NcoUtil.toDiscrete32(aircraft.saInputSelf.lontitude, 90.0, 31)));
			buffer.putShort(NcoUtil.toDiscrete16(aircraft.saInputSelf.altitude, 16384.0, 15));
			buffer.putShort(NcoUtil.toDiscrete16(aircraft.saInputSelf.pos_err, 32768.0, 15));
			buffer.putShort(NcoUtil.toDiscrete16(aircraft.saInputSelf.mach, 2.048, 15));
			buffer.putShort(NcoUtil.toDiscrete16(aircraft.saInputSelf.true_aoa, 90.0, 14));
			buffer.putShort(NcoUtil.toDiscrete16(aircraft.saInputSelf.ground_speed, 2500.0, 16));
			buffer.putShort(NcoUtil.toDiscrete16(aircraft.saInputSelf.barometer_altitude, 16384.0, 15));
			
			footer(buffer);
		}
	},
	AIRCRAFT_STATE(3)
	{
		@Override
		public void buildMessage(ByteBuffer buffer, Object... params)
		{
			Aircraft aircraft = (Aircraft) params[0];
			header(buffer);
			
			
			
//			Fuel	8
//			AC type	7
//			Bingo	1
//			Station selected and Master arm	16
//			Operational status	3
//			INU status	1
//			Radar status	1
//			RWR status	1
//			Declutter	2
//			Role	8
//			Buddy global ID	32
//			Buddy display ID	16
//			BIMG	4
//			SIMG	4
//			TIMG	4
//			Tag	4
			
			
			footer(buffer);

		}
	},
	STORE_DATA(4)
	{
		@Override
		public void buildMessage(ByteBuffer buffer, Object... params)
		{
			Aircraft aircraft = (Aircraft) params[0];
			header(buffer);
			
			
			
//			Chaff amount	8
//			Flare amount	8
//			AA bvr msl	4
//			AA a4m msl	4
//			AG Pgm Wpn	4
//			AG Wpn	4
//			Station 1 Weapon Code	8
//			Station 2 Weapon Code	8
//			Station 3 Weapon Code	8
//			Station 4 Weapon Code	8
//			Station 5 Weapon Code	8
//			Station 6 Weapon Code	8
//			Station 7A Weapon Code	8
//			Station 7B Weapon Code	8
//			Station 8A Weapon Code	8
//			Station 8B Weapon Code	8
//			Station 9 Weapon Code	8
//			Station 10 Weapon Code	8
//			Station 11 Weapon Code	8
//			Station 12 Weapon Code	8
//			Launch gun trigr	16
//			Gun ammunition	16
			
			
			footer(buffer);

		}
	},
	TARGET_TWS(5)
	{
		@Override
		public void buildMessage(ByteBuffer buffer, Object... params)
		{
			Aircraft aircraft = (Aircraft) params[0];
			header(buffer);
			
			SAInputTarget saInputTarget = aircraft.getNextMessageTWS();
			
			buffer.put(NcoUtil.time2mctime(saInputTarget.mc_time_tag));
			short param = 0;
			param = (short) ((saInputTarget.track_id & 0x1F));
			param |= (short) ((saInputTarget.radar_mode & 0x0F) << 5);
			saInputTarget.valid = 1;
			param |= (short) ((saInputTarget.valid & 0x01) << 9);
			param |= (short) ((saInputTarget.accuracy & 0x02) << 10);
			param |= (short) ((0 & 0x04) << 12);
			buffer.putShort(param);

			buffer.putShort(NcoUtil.toDiscrete16(saInputTarget.azimuth, 90.0, 15));
			buffer.putShort(NcoUtil.toDiscrete16(saInputTarget.elevation, 90.0, 15));
			buffer.putShort(NcoUtil.toDiscrete16(saInputTarget.range, 256.0, 15));
			buffer.putShort(NcoUtil.toDiscrete16(saInputTarget.target_velocity, 4.0, 15));
			buffer.putShort(NcoUtil.toDiscrete16(saInputTarget.horisontal_angle_of_velocity_vector, 90.0, 15));
			buffer.putShort(NcoUtil.toDiscrete16(saInputTarget.vertical_angle_of_velocity_vector, 90.0, 15));
			buffer.putShort(NcoUtil.toDiscrete16(saInputTarget.horisontal_velocity, 4.0, 15));
			buffer.putShort(NcoUtil.toDiscrete16(saInputTarget.azimuth_angular_velocity, 90.0, 15));
			buffer.putShort(NcoUtil.toDiscrete16(saInputTarget.elevation_angular_velocity, 90.0, 15));

			footer(buffer);
		}
	},
	TARGET_DT(6)
	{
		@Override
		public void buildMessage(ByteBuffer buffer, Object... params)
		{
			Aircraft aircraft = (Aircraft) params[0];
			header(buffer);
			
			SAInputTarget saInputTarget = aircraft.getNextMessageDT();
			
			buffer.put(NcoUtil.time2mctime(saInputTarget.mc_time_tag));
			short param = 0;
			param = (short) ((saInputTarget.track_id & 0x1F));
			param |= (short) ((saInputTarget.radar_mode & 0xF) << 5);
			saInputTarget.valid = 1;
			param |= (short) ((saInputTarget.valid & 0x01) << 9);
			param |= (short) ((saInputTarget.accuracy & 0x02) << 10);
			param |= (short) ((saInputTarget.radar_lock & 0x01) << 12);
			param |= (short) ((0 & 0x03) << 13);
			buffer.putShort(param);

			buffer.putShort(NcoUtil.toDiscrete16(saInputTarget.azimuth, 90.0, 15));
			buffer.putShort(NcoUtil.toDiscrete16(saInputTarget.elevation, 90.0, 15));
			buffer.putShort(NcoUtil.toDiscrete16(saInputTarget.range, 256.0, 15));
			buffer.putShort(NcoUtil.toDiscrete16(saInputTarget.target_velocity, 4.0, 15));
			buffer.putShort(NcoUtil.toDiscrete16(saInputTarget.horisontal_angle_of_velocity_vector, 90.0, 15));
			buffer.putShort(NcoUtil.toDiscrete16(saInputTarget.vertical_angle_of_velocity_vector, 90.0, 15));
			buffer.putShort(NcoUtil.toDiscrete16(saInputTarget.horisontal_velocity, 4.0, 15));
			buffer.putShort(NcoUtil.toDiscrete16(saInputTarget.azimuth_angular_velocity, 90.0, 15));
			buffer.putShort(NcoUtil.toDiscrete16(saInputTarget.elevation_angular_velocity, 90.0, 15));
			buffer.putShort(NcoUtil.toDiscrete16(saInputTarget.velocity_x, 4.0, 15));
			buffer.putShort(NcoUtil.toDiscrete16(saInputTarget.velocity_y, 4.0, 15));
			buffer.putShort(NcoUtil.toDiscrete16(saInputTarget.velocity_z, 4.0, 15));
			buffer.putShort(NcoUtil.toDiscrete16(saInputTarget.closing_speed, 4.0, 15));
			buffer.putShort(saInputTarget.friend_foe_identification);
			buffer.putShort(NcoUtil.toDiscrete16(saInputTarget.horisontal_aspect_angle, 90.0, 15));
			
			footer(buffer);

		}
	},
	;
	
	
	public long counter;
	public byte opcode;
	private BnetMessageBuilder(int opcode)
	{
		this.opcode = (byte) opcode;

	}
	
	protected void header(ByteBuffer buffer)
	{
		buffer.put((byte) counter++);
		buffer.put((byte) 255);
		buffer.put(opcode);
		buffer.put((byte) 0);
	}
	
	protected void footer(ByteBuffer buffer)
	{
		buffer.put(3, (byte) (buffer.position() - 4 ));
	}
	
	@Override
	public Object getOpcodeName()
	{
		return this;
	}

	@Override
	public String toString()
	{
		return name() +  " (" + opcode + ")";
	}
}
