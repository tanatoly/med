package com.rafael.med.common;

import java.nio.ByteBuffer;

import org.apache.commons.lang3.StringUtils;

import com.rafael.med.common.ILBC.Mode;

public interface Codec
{

	static final String TYPE_G711A 	= "G711A";
	static final String TYPE_ILBC_20 	= "ILBC-20";
	static final String TYPE_ILBC_30 	= "ILBC-30";
	static final String TYPE_SPEEX 	= "SPEEX";
	static final String TYPE_G729 	= "G729";

	static final String NONE 	= "NONE";

	public void decode(ByteBuffer input, ByteBuffer output);
	public void encode(ByteBuffer input,ByteBuffer output);
	public int getCompressedSize();

	public static Codec getByName(String codecType)
	{
		if(StringUtils.isNoneBlank(codecType))
		{
			if(codecType.equalsIgnoreCase(TYPE_G711A))
			{
				return G711A.INSTANCE;
			}
			else if(codecType.equalsIgnoreCase(TYPE_ILBC_20))
			{
				return new ILBC(Mode.MODE_20);
			}
			else if(codecType.equalsIgnoreCase(TYPE_ILBC_30))
			{
				return new ILBC(Mode.MODE_30);
			}
		}
		return null;
	}
}
