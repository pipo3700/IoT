package awsiotthing;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil.KeyStorePasswordPair;
import org.json.JSONObject;
import utils.MySimpleLogger;

import java.util.UUID;

public class AWSIoTThingStarter {
    // valores por defecto de los parámetros de inicio
    protected static String clientEndpoint = "a10q4l8jmbuqc1-ats.iot.us-east-1.amazonaws.com";       // replace <prefix> and <region> with your own
    protected static String clientId = "IoTDeviceClient-SmartCar" + UUID.randomUUID().toString();                  // replace with your own client ID. Use unique client IDs for concurrent connections.

    protected static String certificateFile = "smartcar/src/awscert/certificate.pem.crt";               // X.509 based certificate file
    protected static String privateKeyFile = "smartcar/src/awscert//private.pem.key";

    protected static boolean publisher = true;
    protected static String topic = "smartbollard/";
    protected static String payload = "{\"plaza\": \"liberada\"}";

    public AWSIoTThingStarter(){
        AWSIotMqttClient client = initClient();
        // CONNECT CLIENT TO AWS IOT MQTT
        // optional parameters can be set before connect()
        AWSIotQos qos = AWSIotQos.QOS0;
        try {
            client.connect();
            MySimpleLogger.info("my-aws-iot-thing", "Client Connected to AWS IoT MQTT");

        } catch (AWSIotException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // PUBLISH a message in a TOPIC
        if ( publisher ) {
            publish(client, topic, payload, qos);
        }
    }

    public static AWSIotMqttClient initClient() {

        // SampleUtil.java and its dependency PrivateKeyReader.java can be copied from the sample source code.
        // Alternatively, you could load key store directly from a file - see the example included in this README.
        KeyStorePasswordPair pair = SampleUtil.getKeyStorePasswordPair(certificateFile, privateKeyFile);
        AWSIotMqttClient client = new AWSIotMqttClient(clientEndpoint, clientId, pair.keyStore, pair.keyPassword);

        return client;
    }
    public static void publish(AWSIotMqttClient client, String topic, String payload, AWSIotQos qos) {

        // optional parameters can be set before connect()
        try {
            AWSIotMessage message = new AWSIotMessage(topic, qos, payload);
            client.publish(message);
            MySimpleLogger.info("my-aws-iot-thing", "... PUBLISHED message " + payload + " to TOPIC: " + topic);
        } catch (AWSIotException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
