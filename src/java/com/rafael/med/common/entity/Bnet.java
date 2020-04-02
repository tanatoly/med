package com.rafael.med.common.entity;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Bnet 
{
	private StringProperty 	id 					= new SimpleStringProperty();
	

	private StringProperty  ipAddress			= new SimpleStringProperty();
	private StringProperty  lanAddress			= new SimpleStringProperty();
	
	private String masterVersion;
	private String dpuVersion;
	private String rf1Version;
	private String rf2Version;
	

	private String iomVersion;
	private String cpldVersion;
	private String imageVersion;
	
	
	
	
	private BooleanProperty isChecked 			= new SimpleBooleanProperty(true);
	
	public DoubleProperty progress 			= new SimpleDoubleProperty();
	
	public Bnet(String id, String ipAddress)
	{
		this.id.set(id);
		this.ipAddress.set(ipAddress);
	}
	
	public String getId()
	{
		return id.get();
	}

	public String getIpAddress() 
	{
		return ipAddress.get();
	}
	
	public BooleanProperty checkedProperty()
	{
		return isChecked;
	}
	
	public DoubleProperty progressProperty()
	{
		return progress;
	}
	
	public String getMasterVersion() 
	{
		return masterVersion;
	}

	public String getDpuVersion() 
	{
		return dpuVersion;
	}

	public String getRf1Version()
	{
		return rf1Version;
	}

	public String getRf2Version()
	{
		return rf2Version;
	}

	public String getIomVersion()
	{
		return iomVersion;
	}

	public String getCpldVersion()
	{
		return cpldVersion;
	}

	public String getImageVersion()
	{
		return imageVersion;
	}
}
