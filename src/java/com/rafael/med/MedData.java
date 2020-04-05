package com.rafael.med;

import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.rafael.med.common.Utilities;

public class MedData 
{
	public static class Param
	{
		private static final int TYPE_FLOAT 	= 1;
		private static final int TYPE_INT 		= 2;
		private static final int TYPE_STRING 	= 3;
		
		
		public int id;
		public String name;
		public int valueType;
		public float valueFloat;
		public int valueInt;
		public String valueString;
		public double minValue;
		public double maxValue;
		public String units;
		public int presision;
		
		public Param(String id, String name, String valueType, String presision, String units, String min, String max)
		{
			this.id 			= Integer.parseInt(id);
			this.name 			= name;
			this.valueType		= Integer.parseInt(valueType);
			this.presision		= Integer.parseInt(presision);
			this.units			= units;
			if(StringUtils.isNumeric(min))
			{
				minValue = Double.parseDouble(min);
			}
			if(StringUtils.isNumeric(max))
			{
				maxValue = Double.parseDouble(max);
			}
		}

		public String getValue()
		{
			return "value";
		}

		public void handleMessage(ByteBuffer buffer) 
		{
			int valueType = buffer.get();
			if(valueType == TYPE_FLOAT)
			{
				valueFloat = buffer.getFloat();
			}
			else if(valueType == TYPE_INT)
			{
				valueInt = buffer.getInt();
			}
			else if(valueType == TYPE_STRING)
			{
				int stringLength 	= buffer.get();
				byte[] bytes 		= new byte[stringLength];
				buffer.get(bytes);
				valueString = new String(bytes);
			}
		}
	}
	
	public static class Device 
	{
		public int type;
		public String name;
		public String serial;
		public String error;
		public boolean isWorking;
		
		
		public final Map<Integer, Param> params = new LinkedHashMap<>();




		public Device(String type, String name, String serial) 
		{
			this.type 	= Integer.parseInt(type);
			this.name 	= name;
			this.serial	= serial;
		}


		public void handleMessage(int deviceType, ByteBuffer buffer) throws Exception
		{
			if(type != deviceType)
			{
				throw new Exception("UNEXPECTED DEVICE TYPE " + deviceType + " (SERIAL  = " + serial + " , TYPE = " + type);
			}
			int paramsSize = buffer.get();
			for (int i = 0; i < paramsSize; i++)
			{
				int paramKey = buffer.getShort();
				Param param = params.get(paramKey);
				if(param == null)
				{
					throw new Exception("NOT FOUND PARAM FOR KEY = " + paramKey);
				}
				param.handleMessage(buffer);
			}
		}


		public void addParam(Param param)
		{
			params.put(param.id, param);
			
		}
	}
	
	public static final class Bed
	{
		
		public final Map<String,Device> devices = new HashMap<>();
		
		public String number;
		public String patientName;
		public String patientId;
		public final int id;
		private Map<String, Device> allDevices;

		public String room;
		
		public Bed(String id, Map<String, Device> allDevices)
		{
			this.id = Integer.parseInt(id);
			this.allDevices = allDevices;
		}
		public void handleMessage(ByteBuffer buffer) throws Exception
		{
			int deviceType 		= buffer.get();
			int serialLength	= buffer.get();
			byte[] serialBytes	= new byte[serialLength];
			buffer.get(serialBytes);
			String serial		= new String(serialBytes);
			
			Device device = devices.get(serial);
			if(device == null)
			{
				device = allDevices.get(serial);
				if(device == null)
				{
					throw new Exception("NOT FOUND DEVICE WITH SERIAL = " + serial);
				}
				
				devices.put(serial, device);
			}
			device.handleMessage(deviceType,buffer);
		}
		public void addDevice(Device device)
		{
			devices.put(device.serial, device);
			
		}
	}
	
	public static final class Room
	{
		public final String id;
		public final List<Bed> beds = new ArrayList<>();
		public Room(String id) 
		{
			this.id = id;
		}
		
		public void addBed(Bed bed)
		{
			beds.add(bed);
		}
	}
	
	public static final class Department
	{
		public final String id;
		public final List<Room> rooms = new ArrayList<>();
		public Department(String id)
		{
			this.id = id;
		}
		public void addRoom(Room room)
		{
			rooms.add(room);
		}
	}
	
	public final Map<String, Device>		allDevices			= new TreeMap<>();
	public final Map<String, Department> 	departments 	= new TreeMap<>();
	public final Map<Integer, Bed>		 	allBeds			= new TreeMap<>();
	
	
	public final List<Bed> 					emergencyBeds 	= new ArrayList<>(42);
	
	
	
	
	public MedData () throws Exception
	{
		Document document = Utilities.xmlReadResource("med-config.xml");
		updateData(document);
	}
	
	private void updateData(Document document)
	{
		departments.clear();
		allBeds.clear();
		emergencyBeds.clear();
		
		Element rootElement = document.getDocumentElement();
		
		NodeList devicesByTagName = rootElement.getElementsByTagName("devices");
		if(devicesByTagName != null)
		{
			Element devicesElement = (Element) devicesByTagName.item(0);
			if(devicesElement != null)
			{
				NodeList deviceList = devicesElement.getChildNodes();
				for (int i = 0; i < deviceList.getLength(); i++)
				{
					Node deviceNode = deviceList.item(i);
					if (deviceNode instanceof Element)
					{
						Element deviceElement = (Element) deviceNode;
						
						String type = deviceElement.getAttribute("type");
						String name = deviceElement.getAttribute("name");
						String serial = deviceElement.getAttribute("serial");
						
						Device device = new Device(type, name, serial);
						allDevices.put(device.serial, device);
						
						
						NodeList paramList = deviceElement.getChildNodes();
						for (int j = 0; j < paramList.getLength(); j++)
						{
							Node paramNode = paramList.item(j);
							if (paramNode instanceof Element)
							{
								Element paramElement = (Element) paramNode;
								
								String paramId = paramElement.getAttribute("id");
								String paramName = paramElement.getAttribute("name");
								String paramType = paramElement.getAttribute("type");
								String presision = paramElement.getAttribute("presision");
								String units = paramElement.getAttribute("units");
								
								String min = paramElement.getAttribute("min");
								String max = paramElement.getAttribute("max");
								
								Param param = new Param(paramId, paramName, paramType, presision, units, min, max);
								device.addParam(param);
							}
						}
					}
				}
			}
		}
		
		
		
		NodeList bedsByTagName = rootElement.getElementsByTagName("beds");
		if(bedsByTagName != null)
		{
			Element bedsElement = (Element) bedsByTagName.item(0);
			if(bedsElement != null)
			{
				NodeList departmentList = bedsElement.getChildNodes();
				for (int i = 0; i < departmentList.getLength(); i++)
				{
					Node deppartmentNode = departmentList.item(i);
					if (deppartmentNode instanceof Element)
					{
						Element departmentElement = (Element) deppartmentNode;
						String departmentId = departmentElement.getAttribute("id");
						Department department = new Department(departmentId);
						departments.put(departmentId, department);
						
						NodeList roomList = departmentElement.getElementsByTagName("room");
						for (int j = 0; j < roomList.getLength(); j++) 
						{
							Node roomNode = roomList.item(j);
							if (roomNode instanceof Element)
							{
								Element roomElement = (Element) roomNode;
								String roomId = roomElement.getAttribute("id");
								Room room = new Room(roomId);
								department.addRoom(room);
								
								NodeList bedList = roomElement.getElementsByTagName("bed");
								for (int k = 0; k < bedList.getLength(); k++) 
								{
									Node bedNode = bedList.item(k);
									if (bedNode instanceof Element)
									{
										Element bedElement = (Element) bedNode;
										
										String bedId 		= bedElement.getAttribute("id");
										Bed bed 			= new Bed(bedId,allDevices);
										bed.number 			= bedElement.getAttribute("number");
										bed.patientName 	= bedElement.getAttribute("patientName");
										bed.patientId 		= bedElement.getAttribute("patientId");
										bed.room			= roomId;
										
										allBeds.put(bed.id, bed);
										room.addBed(bed);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	
	public void handleMessage(ByteBuffer buffer, Integer bedId) throws Exception
	{
		Bed bed = allBeds.get(bedId);
		if(bed == null)
		{
			throw new Exception("NOT FOUND BED FOR NUMBER = " + bedId);
		}
		bed.handleMessage(buffer);
	}
}
