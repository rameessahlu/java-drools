package com.diloopstudios.model;

public class IndicesAndKeywords {
	
	private String option;
	private String[] sourceIndices;
	private String targetIndex;
	private String[] Keywords;
	
	private String hostOSFamily;
	private String userName;
	private String eventType;
	private String hostOSName;
	private String agentType;
	private String hostOSPlatform;
	
	
	public String getHostOSFamily() {
		return hostOSFamily;
	}
	public void setHostOSFamily(String hostOSFamily) {
		this.hostOSFamily = hostOSFamily;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public String getHostOSName() {
		return hostOSName;
	}
	public void setHostOSName(String hostOSName) {
		this.hostOSName = hostOSName;
	}
	public String getAgentType() {
		return agentType;
	}
	public void setAgentType(String agentType) {
		this.agentType = agentType;
	}
	public String getHostOSPlatform() {
		return hostOSPlatform;
	}
	public void setHostOSPlatform(String hostOSPlatform) {
		this.hostOSPlatform = hostOSPlatform;
	}
	public String getOption() {
		return option;
	}
	public void setOption(String option) {
		this.option = option;
	}
	public String[] getSourceIndices() {
		return sourceIndices;
	}
	public void setSourceIndices(String[] sourceIndices) {
		this.sourceIndices = sourceIndices;
	}
	public String getTargetIndex() {
		return targetIndex;
	}
	public void setTargetIndex(String targetIndex) {
		this.targetIndex = targetIndex;
	}
	public String[] getKeywords() {
		return Keywords;
	}
	public void setKeywords(String[] keywords) {
		Keywords = keywords;
	}
}
