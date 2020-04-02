package com.rafael.med.common;

import java.nio.ByteBuffer;

public interface MessageBuilder
{
	void buildMessage(ByteBuffer buffer,Object... params);
	Object getOpcodeName();
}
