package com.rafael.med.spect;

public enum ManetType 
{
	U("Manet U", 56, 140),
	L("Manet L", 57, 224),
	;
	
	public final String name;
	public final  int opcode;
	public final  int channelSize;

	private ManetType(String name, int opcode, int channelSize)
	{
		this.name   		= name;
		this.opcode 		= opcode;
		this.channelSize 	= channelSize;
	}
	
	@Override
	public String toString() 
	{
		return name + " , opcode = " + opcode + ", size = " + channelSize;
	}
}
