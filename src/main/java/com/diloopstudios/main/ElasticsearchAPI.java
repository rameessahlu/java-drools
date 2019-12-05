package com.diloopstudios.main;

import com.diloopstudios.model.IndicesAndKeywords;
import com.diloopstudios.model.RecipientsThresholdsAndInterval;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;


public class ElasticsearchAPI {
		
	public void initiateESReindexing(IndicesAndKeywords indicesAndKeywords, HashMap<String, Integer> RecipientsAndThresholds, String option) {		
        JsonParser jsonParser = new JsonParser();
        Gson gsonBuilder = new GsonBuilder().create();
        EmailAPI emailAPI = new EmailAPI();
        
		String usernameColonPassword = "elastic:Test@123";
        String basicAuthPayload = "Basic " + Base64.getEncoder().encodeToString(usernameColonPassword.getBytes());
        String queryString = generateQueryString(indicesAndKeywords.getTargetIndex(), indicesAndKeywords.getSourceIndices(), indicesAndKeywords.getKeywords(), indicesAndKeywords);
        BufferedReader httpResponseReader = null;
        try {
            URL serverUrl = new URL("http://34.223.32.184:9200/_reindex");
            HttpURLConnection urlConnection = (HttpURLConnection) serverUrl.openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.addRequestProperty("Authorization", basicAuthPayload);
            urlConnection.addRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);
            
            
            DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
            wr.writeBytes(queryString);
            System.out.println("DEBUG: Reindexing Respone code: " + urlConnection.getResponseCode() +
            " and the query String is - " + queryString);
            wr.flush();
            wr.close();
            
            
            httpResponseReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String lineRead = "";
            StringBuilder wholeResponse = new StringBuilder();
            while((lineRead = httpResponseReader.readLine()) != null) {
            	wholeResponse = wholeResponse.append(lineRead);
            }
            System.out.println("DEBUG: Reindexing Response Message -  " + wholeResponse.toString());
            
            JsonObject responseJSON = new JsonObject();
            responseJSON = jsonParser.parse(wholeResponse.toString()).getAsJsonObject();
            System.out.println(Integer.parseInt(responseJSON.get("created").toString()) 
            		+ " entries reindexed in " + option);
            
            
            
//MSG Body Generation
//            String msgBody = "";
//            Set<Map.Entry<String, JsonElement>> entrySet = responseJSON.entrySet();
//            for(Map.Entry<String,JsonElement> entry : entrySet){
//            	if (entry.getValue() instanceof JsonObject )
//            		msgBody = msgBody + entry.getKey() +": " + gsonBuilder.toJson(responseJSON.get(entry.getKey())) + "\n";
//            	else if(entry.getValue() instanceof JsonArray)
//            		msgBody = msgBody + entry.getKey() +": " + gsonBuilder.toJson(responseJSON.get(entry.getKey())) + "\n";
//            	else
//            		msgBody = msgBody + entry.getKey() +": " + responseJSON.get(entry.getKey()).getAsString() + "\n";
//            
//            System.out.println("DEBUG-MSG_BODY: " + msgBody);
 
            
            
            Iterator recipientsAndThresholds = RecipientsAndThresholds.entrySet().iterator();	
    		
            while (recipientsAndThresholds.hasNext()) { 
                Map.Entry recipientsAndThresholdEntry = (Map.Entry)recipientsAndThresholds.next();
                int emailThreshold = Integer.parseInt(recipientsAndThresholdEntry.getValue().toString());
    			String emailAddress = recipientsAndThresholdEntry.getKey().toString();
    		
    			if(Integer.parseInt(responseJSON.get("created").toString()) >= emailThreshold)
                {
                	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");  
                	LocalDateTime now = LocalDateTime.now();  
                	String msgBody = String.format(indicesAndKeywords.getKeywords()[0]+"\nThere are %s alert logs generated on '%s' and"
                			+ " the total number of docs in the index raised to %s", responseJSON.get("created").toString(), dtf.format(now), responseJSON.get("total").toString());
                	
                	emailAPI.sendmail("HIGH ALERT: " + indicesAndKeywords.getOption(), msgBody, emailAddress);
                }
            }
            
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        } finally {

            if (httpResponseReader != null) {
                try {
                    httpResponseReader.close();
                } catch (IOException ioe) {
                }
            }
        }

    }
	
	public String  generateQueryString(String destIndex, String[] sourceIndices, String[] keywords, IndicesAndKeywords indicesAndKeywords) {
		JsonParser jsonParser = new JsonParser();
		Gson gsonBuilder = new GsonBuilder().create();
		
		//destination object
		JsonObject destObject = new JsonObject();
		destObject.addProperty("index", destIndex);
		
		//source object
		JsonObject sourceObject = new JsonObject();
		String sourceIndiceList = "[";
		for(String sourceIndice : sourceIndices)
		{
			sourceIndiceList = sourceIndiceList + "\"" + sourceIndice + "\", ";
		}

		sourceIndiceList = sourceIndiceList.substring(0, sourceIndiceList.length() - 2) + "]";
		sourceObject.add("index", jsonParser.parse(sourceIndiceList).getAsJsonArray());
		//queryObject.add("bool", value);
		//queryObject.add("must", value);
		sourceObject.add("query", generateMatchObjects(indicesAndKeywords));
		
		JsonObject wholeQueryObject = new JsonObject();
		wholeQueryObject.add("dest", destObject);
		wholeQueryObject.add("source", sourceObject);
		//System.out.println(gsonBuilder.toJson(wholeQueryObject));
		return gsonBuilder.toJson(wholeQueryObject);
	}
	
	public JsonObject generateMatchObjects(IndicesAndKeywords indicesAndKeywords) {
		JsonParser jsonParser = new JsonParser();
		try {
			Gson gsonBuilder = new GsonBuilder().create();
			JsonObject matchObj = new JsonObject();
			String mustObj = "[";
			
			HashMap<String, String> DRLParams = new HashMap<String, String>() {
				{
					put("host.os.family", indicesAndKeywords.getHostOSFamily());
					put("user.name", indicesAndKeywords.getUserName());
					put("event.type", indicesAndKeywords.getEventType());
					put("agent.type", indicesAndKeywords.getAgentType());
					put("host.os.name", indicesAndKeywords.getHostOSName());
					put("host.os.platform", indicesAndKeywords.getHostOSPlatform());
				}
			};
			
			Iterator DRLParamIterator = DRLParams.entrySet().iterator();	
 			
 			while (DRLParamIterator.hasNext()) { 
 				Map.Entry DRLParamElement = (Map.Entry) DRLParamIterator.next();
 				
 				if(!DRLParamElement.getValue().toString().isEmpty())
 				{
 					JsonObject tempJSONObj = new JsonObject();
 					JsonObject matchObject = new JsonObject();
 					tempJSONObj.addProperty(DRLParamElement.getKey().toString(), DRLParamElement.getValue().toString());
 					matchObject.add("match", tempJSONObj);
 					mustObj = mustObj + gsonBuilder.toJson(matchObject) + ", ";
 				}
 			}
			
 			JsonObject queryStringObj = new JsonObject();
 			if(!indicesAndKeywords.getKeywords()[0].isEmpty())
 			{
 				queryStringObj.addProperty("default_field", "message");
 				String keywordsQuery = "";
 				for(String keyword : indicesAndKeywords.getKeywords())
 				{
 					keywordsQuery = keywordsQuery + "\"" + keyword + "\" OR ";
 				}
 				queryStringObj.addProperty("query", keywordsQuery.substring(0, keywordsQuery.length() - 4));
 				JsonObject queryObject = new JsonObject();
 				queryObject.add("query_string", queryStringObj);
 				mustObj = mustObj + gsonBuilder.toJson(queryObject) + ",";
 			}
 			mustObj = mustObj.substring(0, mustObj.length() - 1) + "]";
 			JsonObject mustObject = new JsonObject();
 			mustObject.add("must", jsonParser.parse(mustObj).getAsJsonArray());
		
 			JsonObject boolObj = new JsonObject();
 			boolObj.add("bool", mustObject);
		
 			return boolObj;
		}catch(Exception e) {
			return new JsonObject();
		}
	}
}
