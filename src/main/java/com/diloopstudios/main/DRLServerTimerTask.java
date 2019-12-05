package com.diloopstudios.main;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.drools.compiler.compiler.DroolsParserException;

import com.diloopstudios.model.RecipientsThresholdsAndInterval;
import com.diloopstudios.model.RuleTaskObjects;

public class DRLServerTimerTask extends TimerTask{
	 
	HashMap<String, RecipientsThresholdsAndInterval> RuleNamesAndInterval;
	HashMap<String, RuleTaskObjects> RuleTasks;
	 
	 public HashMap<String, RecipientsThresholdsAndInterval> returnChangedOrNewRules() {
		 HashMap<String, RecipientsThresholdsAndInterval> latestRuleNamesAndInterval =  new GenerateDRLConfig().generateDRLConfigAndRetrieveRuleNames();
		 HashMap<String, RecipientsThresholdsAndInterval> changedOrNewRules = new HashMap<String, RecipientsThresholdsAndInterval>();
		 
		 Iterator latestRuleNamesAndIntervalIterator = latestRuleNamesAndInterval.entrySet().iterator();
		 while (latestRuleNamesAndIntervalIterator.hasNext()) { 
	            Map.Entry latestRuleNamesAndIntervalElement = (Map.Entry) latestRuleNamesAndIntervalIterator.next();
	            RecipientsThresholdsAndInterval rtai = (RecipientsThresholdsAndInterval)latestRuleNamesAndIntervalElement.getValue();
	            long taskInterval = ((long)rtai.TaskInterval); //+10000; 
	            //System.out.println(latestRuleNamesAndIntervalElement.getKey() + " : " + taskInterval); 
				
	            if(!RuleNamesAndInterval.containsKey(latestRuleNamesAndIntervalElement.getKey().toString()))
	            {	
	            	changedOrNewRules.put(latestRuleNamesAndIntervalElement.getKey().toString(), rtai);
	            	RuleNamesAndInterval.put(latestRuleNamesAndIntervalElement.getKey().toString(), rtai);
	            }
	            else
	            {
	            	if(!rtai.RecipientsAndThresholds.equals(RuleNamesAndInterval.get(latestRuleNamesAndIntervalElement.getKey().toString()).RecipientsAndThresholds)
	            		|| (long)rtai.TaskInterval != (long)RuleNamesAndInterval.get(latestRuleNamesAndIntervalElement.getKey().toString()).TaskInterval)
	            	{            	
		            	changedOrNewRules.put(latestRuleNamesAndIntervalElement.getKey().toString(), rtai);
	            		RuleNamesAndInterval.put(latestRuleNamesAndIntervalElement.getKey().toString(), rtai);
	            	}
	            }
	     }
		return changedOrNewRules;
	 }
	 
	 @Override
	 public void run() {
		 HashMap<String, RecipientsThresholdsAndInterval> tempRuleNamesAndInterval = new HashMap<String, RecipientsThresholdsAndInterval>();
		 
		 	if(RuleNamesAndInterval == null)
		 	{
		 		RuleNamesAndInterval = new GenerateDRLConfig().generateDRLConfigAndRetrieveRuleNames();
		 		tempRuleNamesAndInterval.putAll(RuleNamesAndInterval);
		 		
		 		RuleTasks = new HashMap<String, RuleTaskObjects>();
			}
		 	else
		 	{
		 		tempRuleNamesAndInterval = returnChangedOrNewRules();
		 	}

		 	try {
	 			Iterator ruleNameAndIntervalIterator = tempRuleNamesAndInterval.entrySet().iterator();	
	 			
	 			while (ruleNameAndIntervalIterator.hasNext()) { 
	 				Map.Entry RuleNamesAndIntervalElement = (Map.Entry) ruleNameAndIntervalIterator.next();
	 				RecipientsThresholdsAndInterval rtai = (RecipientsThresholdsAndInterval)RuleNamesAndIntervalElement.getValue();
	 				long taskInterval = ((long)rtai.TaskInterval)+100000; 
	 				//System.out.println(RuleNamesAndIntervalElement.getKey() + " : " + taskInterval + " "); 
	 				
	 				if(RuleTasks.containsKey(RuleNamesAndIntervalElement.getKey().toString()))
	 				{
	 					RuleTasks.get(RuleNamesAndIntervalElement.getKey().toString()).timerTaskObj.cancel();
	 					RuleTasks.get(RuleNamesAndIntervalElement.getKey().toString()).timerObj.cancel();
	 				}
	 				
	 				System.out.println("Adding task: " + RuleNamesAndIntervalElement.getKey().toString());
	 				Timer timerTask = new Timer();
	 				RuleTimerTask rtTask = new RuleTimerTask(RuleNamesAndIntervalElement.getKey().toString(), rtai.RecipientsAndThresholds);
	 				timerTask.scheduleAtFixedRate(rtTask, 0, taskInterval);
	 				
	 				RuleTaskObjects ruleTaskObjects = new RuleTaskObjects(timerTask, rtTask);
	 				
	 				RuleTasks.put(RuleNamesAndIntervalElement.getKey().toString(), ruleTaskObjects);
	 				
	 			}
	 		} catch (Exception e) {
	 			// TODO Auto-generated catch block
	 			e.printStackTrace();
	 		}
	 }

}

