package com.diloopstudios.main;

import com.diloopstudios.debug.DebugAPIS;
import com.diloopstudios.model.IndicesAndKeywords;
import com.diloopstudios.model.RecipientsThresholdsAndInterval;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;

import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.compiler.PackageBuilder;
import org.drools.core.RuleBase;
import org.drools.core.RuleBaseFactory;
import org.drools.core.WorkingMemory;


public class DroolsTest {

	
	public static void main(String[] args) throws DroolsParserException, IOException {
		
		Timer t = new Timer();
		DRLServerTimerTask drlServerTimerTask = new DRLServerTimerTask();
		t.scheduleAtFixedRate(drlServerTimerTask, 0, 30000);
		
		
		
//PREVIOUS Approach		
//		BufferedReader csvReader = new BufferedReader(new FileReader(args[0]));
//		String row;
//		while ((row = csvReader.readLine()) != null) {
//		    String[] ruleNameWithInterval = row.split(",");
//			Timer t = new Timer();
//			RuleTimerTask mTask = new RuleTimerTask(ruleNameWithInterval[0]);
//			t.scheduleAtFixedRate(mTask, 0, Long.parseLong(ruleNameWithInterval[1].trim()));
//		}
//		csvReader.close(); 
        
	}

	public void executeDrools(String option, HashMap<String, Integer> RecipientsAndThresholds) throws DroolsParserException, IOException {
		PackageBuilder packageBuilder = new PackageBuilder();
		
//		String ruleFile = "/com/rule/backup/Rules.drl";
//		InputStream resourceAsStream = getClass().getResourceAsStream(ruleFile);
		
		String ruleFile = "c:/temp/Rules.drl";
		InputStream drlFileAsStream = new FileInputStream(new File(ruleFile));
		Reader reader = new InputStreamReader(drlFileAsStream);
		packageBuilder.addPackageFromDrl(reader);
		org.drools.core.rule.Package rulesPackage = packageBuilder.getPackage();
		RuleBase ruleBase = RuleBaseFactory.newRuleBase();
		ruleBase.addPackage(rulesPackage);
		WorkingMemory workingMemory = ruleBase.newStatefulSession();
		
		//Fact class
		IndicesAndKeywords indicesAndKeywords = new IndicesAndKeywords();
		indicesAndKeywords.setOption(option);

		workingMemory.insert(indicesAndKeywords);
		workingMemory.fireAllRules();

		//System.out.println("DEBUG: The source indices are: " + Arrays.toString(indicesAndKeywords.getSourceIndices()));
		if(indicesAndKeywords.getTargetIndex() != null)
		{
			ElasticsearchAPI esAPI = new ElasticsearchAPI();
			//System.out.print(esAPI.generateMatchObjects(indicesAndKeywords));
			//System.out.print("DEBUG Query: "+ esAPI.generateQueryString(indicesAndKeywords.getTargetIndex(), indicesAndKeywords.getSourceIndices(), indicesAndKeywords.getKeywords(), indicesAndKeywords));
			esAPI.initiateESReindexing(indicesAndKeywords, RecipientsAndThresholds, option);
			//System.out.print(
			//		esAPI.generateQueryString(
			//				indicesAndKeywords.getTargetIndex(), indicesAndKeywords.getSourceIndices(), indicesAndKeywords.getKeywords()));
		}
	}

}