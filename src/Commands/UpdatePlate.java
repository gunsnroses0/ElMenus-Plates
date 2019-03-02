package Commands;

import Model.Plate;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;

public class UpdatePlate extends Command {

	@Override
	protected void execute() {
		
		// TODO Auto-generated method stub
		HashMap<String, Object> props = parameters;
		Channel channel = (Channel) props.get("channel");
		JSONParser parser = new JSONParser();

		try {

		
			JSONObject messageBody = (JSONObject) parser.parse((String) props.get("body"));
			System.out.println( ((JSONObject) parser.parse((String) props.get("body"))).get("uri").toString());
//			HashMap<String, Object> paramsHashMap = jsonToMap((JSONObject) messageBody.get("uri"));
			String url = ((JSONObject) parser.parse((String) props.get("body"))).get("uri").toString();
//			System.out.println(paramsHashMap.toString());
			HashMap<String, Object> requestBodyHash = jsonToMap((JSONObject) messageBody.get("body"));
			url = url.substring(1);
			System.out.println(Arrays.toString(url.split("/")));
			
			String[] parametersArray = url.split("/");
			AMQP.BasicProperties properties = (AMQP.BasicProperties) props.get("properties");
			AMQP.BasicProperties replyProps = (AMQP.BasicProperties) props.get("replyProps");
			Envelope envelope = (Envelope) props.get("envelope");
			HashMap<String, Object> updatedMessage = Plate.update(parametersArray[1], requestBodyHash);
			JSONObject response = jsonFromMap(updatedMessage);
			// handle http codes
			channel.basicPublish("", properties.getReplyTo(), replyProps, response.toString().getBytes("UTF-8"));
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
