package com.rafael.med.common.bnet;

import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import de.jensd.fx.glyphs.GlyphIcons;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

public enum ChannelType 
{
	
	
	LEGACY_RX_RTP	(2,"LEGACY" ,Color.ROYALBLUE.brighter(), 			"127.0.0.1", 30006,  true, ChannelPoint.RTP),
	LEGACY_TX_RTP	(1,"LEGACY" ,Color.ROYALBLUE,  						"127.0.0.1", 30004, false, ChannelPoint.RTP),
	
	MANET_RX_RTP	(4,"MANET" ,Color.GREEN.brighter(),					"10.0.0.1",  20050, true, ChannelPoint.RTP),
	MANET_TX_RTP	(3,"MANET" ,Color.GREEN,							"0.0.0.0", 	 20050, false, ChannelPoint.RTP),
	
	SATCOM_RX_RTP	(6,"SATCOM" ,Color.YELLOW.brighter(),				"10.0.0.1",  20052, true, ChannelPoint.RTP),
	SATCOM_TX_RTP	(5,"SATCOM" ,Color.YELLOW,							"0.0.0.0", 	 20052, false, ChannelPoint.RTP),
	
	GUARD_RX_RTP	(8,"GUARD" ,Color.PALEVIOLETRED.brighter(),  		"127.0.0.1", 50058, true, ChannelPoint.RTP),
	GUARD_TX_RTP	(7,"GUARD" ,Color.PALEVIOLETRED, 					"127.0.0.1", 50064, false, ChannelPoint.RTP),
	
	;

	
	public final Color  color;
	public final String sourceIp;
	public final int dstPort;
	public final boolean isRx;
	public final ChannelPoint point;
	public final Background background;
	public final String name;
	public final GlyphIcons rxtxLabel;
	public final String rxtxString;
	public final int index;
	public final String type;
	
	private static final TreeSet<ChannelType> treeset = new TreeSet<ChannelType>((o1, o2) -> o1.index - o2.index);
	
	
	
	private ChannelType(int index, String type, Color color, String sourceIp, int dstPort, boolean isRx, ChannelPoint point)
	{
		this.index = index;
		this.type = type;
		this.color = color;
		this.sourceIp = sourceIp;
		this.dstPort = dstPort;
		this.isRx = isRx;
		this.point = point;
		this.background = new Background(new BackgroundFill(color, null,null));
		this.rxtxLabel  = (isRx) ? FontAwesomeIcon.CHEVRON_CIRCLE_DOWN : FontAwesomeIcon.CHEVRON_CIRCLE_UP;
		this.rxtxString  = (isRx) ? "RX" : "TX";
		this.name = StringUtils.substringBefore(this.name(), "_");
		
	}
	
	public static TreeSet<ChannelType> getTreeset()
	{
		if(treeset.isEmpty())
		{
			for (ChannelType channelType : ChannelType.values())
			{
				treeset.add(channelType);
			}
		}
		return treeset;
	}
}