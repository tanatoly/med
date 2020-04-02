package com.rafael.med.spect;

public final class NodeOpcode 
{
	private final int nodeId;
	private final int opcode;
	
	public NodeOpcode(int nodeId, int opcode)
	{
		this.nodeId = nodeId;
		this.opcode = opcode;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + nodeId;
		result = prime * result + opcode;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodeOpcode other = (NodeOpcode) obj;
		if (nodeId != other.nodeId)
			return false;
		if (opcode != other.opcode)
			return false;
		return true;
	}
	
	
}
