package com.rafael.med.common.bnet;

import de.jensd.fx.glyphs.GlyphIcons;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;

public enum ChannelPoint 
{
	RTP(FontAwesomeIcon.HOURGLASS_END), NC(FontAwesomeIcon.HOURGLASS_START) , ALC(FontAwesomeIcon.SPOON); 
	
	public final GlyphIcons label;

	
	private ChannelPoint(GlyphIcons label)
	{
		this.label = label;
	}
}
