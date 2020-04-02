package com.rafael.med.common;

import java.nio.ByteBuffer;

public interface MessageHandler
{
	void handle(ByteBuffer buffer,Object source) throws Exception;

}
