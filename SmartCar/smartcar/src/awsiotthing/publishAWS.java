package awsiotthing;

import com.amazonaws.services.iot.client.*;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil.KeyStorePasswordPair;
import utils.MySimpleLogger;

import java.util.UUID;

public class publishAWS {

    protected static final String clientEndpoint = "a10q4l8jmbuqc1-ats.iot.us-east-1.amazonaws.com";
    protected static final String clientId = "IoTDeviceClient-SmartCar-" + UUID.randomUUID();
    protected static final String certificateFile = "smartcar/src/awscert/certificate.pem.crt";
    protected static final String privateKeyFile = "smartcar/src/awscert/private.pem.key";
    protected static final AWSIotQos qos = AWSIotQos.QOS0;

    private static AWSIotMqttClient client = null;
    private static boolean connected = false;

    // Inicializar el cliente una sola vez y conectarlo si no lo está
    private static AWSIotMqttClient getClient() {
        if (client == null) {
            KeyStorePasswordPair pair = SampleUtil.getKeyStorePasswordPair(certificateFile, privateKeyFile);
            client = new AWSIotMqttClient(clientEndpoint, clientId, pair.keyStore, pair.keyPassword);
        }

        if (!connected) {
            try {
                client.connect();
                connected = true;
                MySimpleLogger.info("my-aws-iot-thing", "Client Connected to AWS IoT MQTT");
            } catch (AWSIotException e) {
                e.printStackTrace();
            }
        }

        return client;
    }

    public publishAWS(String spotId, String smartCarId, String action) {
        String topic;
        String payload = String.format("{ \"spotId\": \"%s\", \"vehicleId\": \"%s\" }", spotId, smartCarId);

        if (action.equals("free")) {
            topic = "smartbollard/" + spotId + "/freed";
        } else if (action.equals("occupy")) {
            topic = "smartbollard/" + spotId + "/occupied";
        } else {
            return; // acción no válida
        }

        publish(getClient(), topic, payload, qos);
    }

    public static void publish(AWSIotMqttClient client, String topic, String payload, AWSIotQos qos) {
        try {
            AWSIotMessage message = new AWSIotMessage(topic, qos, payload);
            client.publish(message);
            MySimpleLogger.info("my-aws-iot-thing", "... PUBLISHED message " + payload + " to TOPIC: " + topic);
        } catch (AWSIotException e) {
            e.printStackTrace();
        }
    }
}
