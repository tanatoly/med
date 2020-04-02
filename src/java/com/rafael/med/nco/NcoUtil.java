package com.rafael.med.nco;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;

import com.rafael.med.common.BitByteUtils;
import com.rafael.med.common.Constants;

public class NcoUtil 
{
	public static double resolution(double msbValue, int msbIndex)
	{
		return msbValue/Math.pow(2.0, msbIndex - 1);
	}

	public static long toDiscrete64(double value, double msbValue, int msbIndex)
	{
		return Math.round((value) /resolution(msbValue, msbIndex));
	}

	public static int toDiscrete32(double value, double msbValue, int msbIndex)
	{
		return (int) toDiscrete64(value,msbValue,msbIndex);
	}

	public static short toDiscrete16(double value, double msbValue, int msbIndex)
	{
		return (short) toDiscrete64(value,msbValue,msbIndex);
	}

	public static double toScale64(long value, double msbValue, int msbIndex)
	{
		return (value * resolution(msbValue,msbIndex));
	}

	public static double toScale32(int value, double msbValue, int msbIndex)
	{
		return toScale64(value,msbValue,msbIndex);
	}

	public static double toScale16(short value, double msbValue, int msbIndex)
	{
		return toScale64(value,msbValue,msbIndex);
	}
	 
	 
	
	
	public static short time2mcdate(double timeInSeconds)
	{
		long timeInMs = (long) (timeInSeconds * 1000);	
		LocalDateTime time = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeInMs), Constants.ZONE_OFFSET);
		byte year 	= (byte) (time.get(ChronoField.YEAR) - 2000);
		byte month	= (byte) time.get(ChronoField.MONTH_OF_YEAR);
		byte day 	= (byte) time.get(ChronoField.DAY_OF_MONTH);
		short date = 0;
		date = (short) (date | (year << 9));
		date = (short) (date | (month << 5));
		date = (short) (date | (day));
	    return date;
	}
	
	public static byte[] time2mctime(double timeInSeconds)
	{
		long timeInSec = (long)timeInSeconds;
		double decimal = timeInSeconds - timeInSec;
		LocalDateTime time = LocalDateTime.ofInstant(Instant.ofEpochSecond(timeInSec), Constants.ZONE_OFFSET);
		byte[] result = new byte[6];
		
		
		byte hour	= (byte) time.get(ChronoField.HOUR_OF_DAY);
		byte min	= (byte) time.get(ChronoField.MINUTE_OF_HOUR);
		byte sec	= (byte) time.get(ChronoField.SECOND_OF_MINUTE);
		int ms		= (int)  (decimal/9.54e-7);
		ms = (ms << 8);
		result[0] = hour;
		result[1] = min;
		result[2] = sec;
		result[3] = BitByteUtils.int3(ms);
		result[4] = BitByteUtils.int2(ms);
		result[5] = BitByteUtils.int1(ms);
	
	    return result;
	}

	public static int swap(int value)
	{
		short tmp = (short) (value & 0xFFFF);
		int result = (value & 0xFFFF0000) >> 16;
		result = (tmp << 16) | result;
		return  result;
	}
}
