package Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;

import Commands.Command;

public class Plate {

	private static final String COLLECTION_NAME = "plates";
	private static int DbPoolCount = 4;
	public static int getDbPoolCount() {
		return DbPoolCount;
	}
	public static void setDbPoolCount(int dbPoolCount) {
		DbPoolCount = dbPoolCount;
	}
	private static MongoCollection<Document> collection = null;

	public static HashMap<String, Object> create(HashMap<String, Object> atrributes) throws ParseException {
		
		MongoClientOptions.Builder options = MongoClientOptions.builder()
	            .connectionsPerHost(DbPoolCount);
		MongoClientURI uri = new MongoClientURI(
				"mongodb://localhost",options);
		MongoClient mongoClient = new MongoClient(uri);
		MongoDatabase database = mongoClient.getDatabase("El-Menus");
//    	Method method =   Class.forName("PlatesService").getMethod("getDB", null);
//    	MongoDatabase database = (MongoDatabase) method.invoke(null, null);

		// Retrieving a collection
		MongoCollection<Document> collection = database.getCollection("plates");
		Document newPlate = new Document();

		for (String key : atrributes.keySet()) {
			newPlate.append(key, atrributes.get(key));
		}
		collection.insertOne(newPlate);
		
		System.out.println("NewPlate" + newPlate.toString());
		JSONParser parser = new JSONParser();
		HashMap<String, Object> returnValue = Command.jsonToMap((JSONObject) parser.parse(newPlate.toJson()));


		return returnValue;
	}

	public static HashMap<String, Object> update(String id, HashMap<String, Object> atrributes) {
		MongoClientOptions.Builder options = MongoClientOptions.builder()
	            .connectionsPerHost(DbPoolCount);
		MongoClientURI uri = new MongoClientURI(
				"mongodb://localhost",options);
		MongoClient mongoClient = new MongoClient(uri);
		MongoDatabase database = mongoClient.getDatabase("El-Menus");
//    	Method method =   Class.forName("PlatesService").getMethod("getDB", null);
//    	MongoDatabase database = (MongoDatabase) method.invoke(null, null);

		// Retrieving a collection
		MongoCollection<Document> collection = database.getCollection("plates");
		Document updatedPlate = new Document();
		Bson filter = new Document("_id", new ObjectId(id));
		System.out.println(filter.toString());
		for (String key : atrributes.keySet()) {
			updatedPlate.append(key, atrributes.get(key));
		}

		Bson updateOperationDocument = new Document("$set", updatedPlate);
		collection.updateMany(filter, updateOperationDocument);

		return atrributes;
	}

	public static HashMap<String, Object> get(String messageId) {

		MongoClientOptions.Builder options = MongoClientOptions.builder()
	            .connectionsPerHost(DbPoolCount);
		MongoClientURI uri = new MongoClientURI(
				"mongodb://localhost",options);
		MongoClient mongoClient = new MongoClient(uri);
		MongoDatabase database = mongoClient.getDatabase("El-Menus");
//    	Method method =   Class.forName("PlatesService").getMethod("getDB", null);
//    	MongoDatabase database = (MongoDatabase) method.invoke(null, null);

		// Retrieving a collection
		MongoCollection<Document> collection = database.getCollection("plates");
		System.out.println("Inside Get");
		BasicDBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(messageId));

		System.out.println(query.toString());
		HashMap<String, Object> message = null;
		Document doc = collection.find(query).first();
		JSONParser parser = new JSONParser(); 
		try {
			JSONObject json = (JSONObject) parser.parse(doc.toJson());
		
			message = Command.jsonToMap(json);
			
			System.out.println(message.toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return message;
	}
}
