package com.diloopstudios.debug;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DebugAPIS {
	final Gson gsonBuilder = new GsonBuilder().create();
	final JsonParser jsonParser = new JsonParser();
	
	public void generateDRL()
	{
		String dbJSON = "{\"rules\":[{\"id\":\"4\",\"agent_hostname\":\"\",\"agent_version\":\"\",\"agent_type\":\"winlogbeat\",\"event_type\":\"\",\"host_arch\":\"\",\"host_hostname\":\"EC2AMAZ-4PJ0E5T\",\"host_name\":\"EC2AMAZ-4PJ0E5T\",\"host_os_family\":\"windows\",\"host_os_name\":\"\",\"host_os_plat\":\"windows\",\"host_os_ver\":\"\",\"message\":\"SQL Server is terminating because of a system shutdown.\",\"uname\":\"\",\"rule_name\":\"SQLServerTerminating\",\"source_index\":\"winlogbeat-*\",\"rule_interval\":\"0\",\"description\":\"\"},{\"id\":\"5\",\"agent_hostname\":\"\",\"agent_version\":\"Test\",\"agent_type\":\"\",\"event_type\":\"\",\"host_arch\":\"\",\"host_hostname\":\"\",\"host_name\":\"\",\"host_os_family\":\"Test\",\"host_os_name\":\"\",\"host_os_plat\":\"\",\"host_os_ver\":\"\",\"message\":\"\",\"uname\":\"\",\"rule_name\":\"Test\",\"source_index\":\"Test\",\"rule_interval\":\"0\",\"description\":\"\"},{\"id\":\"6\",\"agent_hostname\":\"\",\"agent_version\":\"\",\"agent_type\":\"\",\"event_type\":\"\",\"host_arch\":\"\",\"host_hostname\":\"\",\"host_name\":\"\",\"host_os_family\":\"\",\"host_os_name\":\"\",\"host_os_plat\":\"\",\"host_os_ver\":\"\",\"message\":\"\",\"uname\":\"\",\"rule_name\":\"Rule5\",\"source_index\":\"drools_rule_engine\",\"rule_interval\":\"0\",\"description\":\"\"},{\"id\":\"7\",\"agent_hostname\":\"\",\"agent_version\":\"\",\"agent_type\":\"\",\"event_type\":\"\",\"host_arch\":\"\",\"host_hostname\":\"\",\"host_name\":\"\",\"host_os_family\":\"\",\"host_os_name\":\"\",\"host_os_plat\":\"\",\"host_os_ver\":\"\",\"message\":\"\",\"uname\":\"\",\"rule_name\":\"Rule7\",\"source_index\":\"kie workbench\",\"rule_interval\":\"0\",\"description\":\"\"},{\"id\":\"8\",\"agent_hostname\":\"\",\"agent_version\":\"\",\"agent_type\":\"\",\"event_type\":\"\",\"host_arch\":\"\",\"host_hostname\":\"\",\"host_name\":\"\",\"host_os_family\":\"\",\"host_os_name\":\"\",\"host_os_plat\":\"\",\"host_os_ver\":\"\",\"message\":\"\",\"uname\":\"\",\"rule_name\":\"Rule8\",\"source_index\":\"Filebeat rule\",\"rule_interval\":\"0\",\"description\":\"\"}]}";
        
		JsonArray dbRuleObjs = new JsonArray();
		dbRuleObjs = (JsonArray)jsonParser.parse(dbJSON.toString()).getAsJsonObject().get("rules");
        //System.out.println("Created: " + Integer.parseInt(responseJSON.get("created").toString()));

		String drlConfigFormat =  "rule %s\r\n" + 
				"	when \r\n" + 
				"		indicesAndKeywordsObject: IndicesAndKeywords(option==%s)\r\n" + 
				"	then\r\n" + 
				"		String[] sourceIndices = new String[]{%s};\r\n" + 
				"		String[] keywords = new String[]{\"%s\"};\r\n" + 
				"		\r\n" + 
				"		indicesAndKeywordsObject.setSourceIndices(sourceIndices);\r\n" + 
				"		indicesAndKeywordsObject.setKeywords(keywords);\r\n" + 
				"		indicesAndKeywordsObject.setTargetIndex(\"thinkpad1\");\r\n" + 
				"		\r\n" + 
				"		indicesAndKeywordsObject.setHostOSFamily(%s);\r\n" + 
				"		indicesAndKeywordsObject.setUserName(%s);\r\n" + 
				"		indicesAndKeywordsObject.setEventType(%s);\r\n" + 
				"		indicesAndKeywordsObject.setHostOSName(%s);\r\n" + 
				"		indicesAndKeywordsObject.setAgentType(%s);\r\n" + 
				"		indicesAndKeywordsObject.setHostOSPlatform(%s);\r\n" + 
				"	end\n";
		String drlConfig = "import com.virtusa.model.IndicesAndKeywords;\r\n";
        for(Object dbro : dbRuleObjs)
        {
        	JsonObject dbRuleObj = (JsonObject)dbro;
        	String ruleKeyword = "";
        	if(!dbRuleObj.get("message").toString().equals("\"\""))
        		ruleKeyword = "**" + dbRuleObj.get("message").toString().substring(1,dbRuleObj.get("message").toString().length()-1) + "**";
        	drlConfig = drlConfig + String.format(drlConfigFormat, dbRuleObj.get("rule_name"), dbRuleObj.get("rule_name"), 
        			dbRuleObj.get("source_index"), ruleKeyword, dbRuleObj.get("host_os_family"),
        			dbRuleObj.get("uname"), dbRuleObj.get("event_type"), dbRuleObj.get("host_os_name"),
        			dbRuleObj.get("agent_type"), dbRuleObj.get("host_os_plat")
        			);
//        	Set<Entry<String, JsonElement>> entrySet = dbRuleObj.entrySet();
//        	for(Map.Entry<String,JsonElement> entry : entrySet){
//        	      System.out.prnt(entry.getKey() + dbRuleObj.get(entry.getKey()));
//        	}
        }
        System.out.print(drlConfig);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("cool.drl", false));
            writer.write(drlConfig);
             
            writer.close();
        }catch(IOException ioe)
        {
        	
        }
	}
	
	public void debugJSON() {
        
		String jon = "{\"took\":14,\"timed_out\":false,\"total\":24,\"updated\":24,\"created\":0,\"deleted\":0,\"batches\":1,\"version_conflicts\":0,\"noops\":0,\"retries\":{\"bulk\":0,\"search\":0},\"throttled_millis\":0,\"requests_per_second\":-1.0,\"throttled_until_millis\":0,\"failures\":[]}\r\n";
        
        
        JsonObject responseJSON = new JsonObject();
        responseJSON = jsonParser.parse(jon.toString()).getAsJsonObject();
        System.out.println("Created: " + Integer.parseInt(responseJSON.get("created").toString()));

        String msgBody = "";
        Set<Map.Entry<String, JsonElement>> entrySet = responseJSON.entrySet();
        for(Map.Entry<String,JsonElement> entry : entrySet){
        	if (entry.getValue() instanceof JsonObject )
        		msgBody = msgBody + entry.getKey() +": " + gsonBuilder.toJson(responseJSON.get(entry.getKey())) + "\n";
        	else if(entry.getValue() instanceof JsonArray)
        		msgBody = msgBody + entry.getKey() +": " + gsonBuilder.toJson(responseJSON.get(entry.getKey())) + "\n";
        	else
        		msgBody = msgBody + entry.getKey() +": " + responseJSON.get(entry.getKey()).getAsString() + "\n";
        }
        System.out.println(msgBody);
	}
}
