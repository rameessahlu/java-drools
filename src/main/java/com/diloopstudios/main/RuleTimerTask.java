package com.diloopstudios.main;

import java.io.IOException;
import java.util.HashMap;
import java.util.TimerTask;

import org.drools.compiler.compiler.DroolsParserException;

public class RuleTimerTask extends TimerTask{
	 String option;
	 HashMap<String, Integer> RecipientsAndThresholds = new HashMap<String, Integer>();
	 
	 public RuleTimerTask(String option, HashMap<String, Integer> RecipientsAndThresholds){
		 this.option = option;
		 this.RecipientsAndThresholds.putAll(RecipientsAndThresholds);
	 }
	 
	 @Override
	 public void run() {
			DroolsTest droolsTest = new DroolsTest();
			try {
				System.out.println("Running rule: " + this.option);
				droolsTest.executeDrools(this.option, this.RecipientsAndThresholds);
			} catch (DroolsParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	 }

}
