package com.rafael.med;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.rafael.med.common.Utilities;

public class MedData 
{
	public final Map<Integer, Device>		allDevices		= new TreeMap<>();
	public final Map<String, Department> 	departments 	= new TreeMap<>();
	public final Map<Integer, Bed>		 	allBeds			= new TreeMap<>();
	public final List<Bed> 					emergencyBeds 	= new ArrayList<>(42);

	public int excelPeriodInMinutes;
	
	public MedData () throws Exception
	{
		Document document = Utilities.xmlReadResource("med-config.xml");
		updateData(document);
	}
	
	private void updateData(Document document)
	{
		departments.clear();
		allBeds.clear();
		
		Element rootElement = document.getDocumentElement();
		excelPeriodInMinutes = Integer.parseInt(rootElement.getAttribute("excelPeriodInMinutes"));
		
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
						//String serial = deviceElement.getAttribute("serial");
						
						Device device = new Device(type, name);
						allDevices.put(device.type, device);
						
						
						NodeList paramList = deviceElement.getChildNodes();
						for (int j = 0; j < paramList.getLength(); j++)
						{
							Node paramNode = paramList.item(j);
							if (paramNode instanceof Element)
							{
								String nodeName = paramNode.getNodeName();
								Element paramElement = (Element) paramNode;
								if(nodeName.equals("param"))
								{
									String paramId = paramElement.getAttribute("id");
									String paramName = paramElement.getAttribute("name");
									String paramType = paramElement.getAttribute("type");
									String presision = paramElement.getAttribute("presision");
									String units = paramElement.getAttribute("units");
								
									String min = paramElement.getAttribute("min");
									String max = paramElement.getAttribute("max");
								
									String regular = paramElement.getAttribute("regular");
									String alarm = paramElement.getAttribute("alarm");
								
									Param param = new Param(paramId, paramName, paramType, presision, units, min, max , regular, alarm);
									device.addParam(param);
								}
								else if(nodeName.equals("range"))
								{
									String rangeId 	= paramElement.getAttribute("id");
									String paramId 	= paramElement.getAttribute("paramId");
									String isMin 	= paramElement.getAttribute("isMin");
									
									
									device.addDinamicRange(rangeId, paramId, isMin);
									
								}
								else if(nodeName.equals("default"))
								{
									String defaultId 	= paramElement.getAttribute("id");
									String paramId 		= paramElement.getAttribute("paramId");
									
									device.addDefault(defaultId, paramId);
									
								}
								else if(nodeName.equals("mfl"))
								{
									String mflId 	= paramElement.getAttribute("id");
									String mflName = paramElement.getAttribute("name");
									String isError 	= paramElement.getAttribute("isError");
									device.addMfl(mflId,mflName, isError);
								}
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
