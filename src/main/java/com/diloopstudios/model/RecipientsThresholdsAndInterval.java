package com.diloopstudios.model;

import java.util.HashMap;

public class RecipientsThresholdsAndInterval {

	public HashMap<String, Integer> RecipientsAndThresholds = new HashMap<String, Integer>();
	public Long TaskInterval;
	
	public RecipientsThresholdsAndInterval(HashMap<String, Integer> RecipientsAndThresholds, Long TaskInterval)
	{
		this.RecipientsAndThresholds.putAll(RecipientsAndThresholds);
		this.TaskInterval = TaskInterval; 
	}
}