package com.diloopstudios.model;

import java.util.HashMap;
import java.util.Timer;

import com.diloopstudios.main.RuleTimerTask;

public class RuleTaskObjects {

	public Timer timerObj;
	public RuleTimerTask timerTaskObj;
	
	public RuleTaskObjects(Timer timerObj, RuleTimerTask timerTaskObj)
	{
		this.timerObj = timerObj;
		this.timerTaskObj = timerTaskObj; 
	}
}
