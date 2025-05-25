package dispositivo.awsiotthing;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;
import dispositivo.utils.MySimpleLogger;


public class AWSIoT_TopicHandler extends AWSIotTopic {

	public AWSIoT_TopicHandler(String topic, AWSIotQos qos) {
		super(topic, qos);
	}
	
	@Override
	public void onMessage(AWSIotMessage message) {
		//super.onMessage(message);
		String text = message.getStringPayload();
		MySimpleLogger.info("my-aws-iot-thing" + "-topicHandler", "RECEIVED: " + text);

	}

}

