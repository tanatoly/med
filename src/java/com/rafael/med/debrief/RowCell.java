package com.rafael.med.debrief;

public class RowCell
{
	public boolean isLegacyRx;
	public boolean isLegacyTx;
	public boolean isGuardRx;
	public boolean isGuardTx;
	public boolean isManetRx;
	public boolean isManetTx;
	public boolean isSatcomRx;
	public boolean isSatcomTx;
	
	
	public void clear()
	{
		isLegacyRx	= false;
		isLegacyTx	= false; 
		isGuardRx	= false;  
		isGuardTx	= false;  
		isManetRx	= false;  
		isManetTx	= false;  
		isSatcomRx	= false; 
		isSatcomTx	= false; 
	}
	
	public boolean isExist()
	{
		return isLegacyRx || isLegacyTx || isGuardRx || isGuardTx || isManetRx || isManetTx || isSatcomRx || isSatcomTx;
	}
}
