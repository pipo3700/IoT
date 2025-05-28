package dispositivo.awsiotthing;

import java.util.UUID;

import dispositivo.componentes.Bollard;
import dispositivo.utils.MySimpleLogger;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil.KeyStorePasswordPair;

public class AWSIoTThingStarter {
    // valores por defecto de los parámetros de inicio
    protected static String clientEndpoint = "a10q4l8jmbuqc1-ats.iot.us-east-1.amazonaws.com";       // replace <prefix> and <region> with your own
    protected static String clientId = "IoTDeviceClient-01112" + UUID.randomUUID().toString();                  // replace with your own client ID. Use unique client IDs for concurrent connections.

    protected static String certificateFile = "dispositivo/src/dispositivo/awscertificates/certificate.pem.crt";               // X.509 based certificate file
    protected static String privateKeyFile = "dispositivo/src/dispositivo/awscertificates/private.pem.key";

    protected static boolean subscriber = true;
    protected static String topicreserva;
    protected static String topicfree;

    public AWSIoTThingStarter(Bollard bollard){
        topicreserva = "smartbollard/"+bollard.getId()+"/command/reserve";
        topicfree = "smartbollard/"+bollard.getId()+"/freed";
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
        // SUBSCRIBE to a TOPIC
        if ( subscriber ) {
            subscribe(client, topicreserva, qos, bollard);
            subscribe2(client, topicfree, qos, bollard);
        }
    }

    public static AWSIotMqttClient initClient() {

        // SampleUtil.java and its dependency PrivateKeyReader.java can be copied from the sample source code.
        // Alternatively, you could load key store directly from a file - see the example included in this README.
        KeyStorePasswordPair pair = SampleUtil.getKeyStorePasswordPair(certificateFile, privateKeyFile);
        AWSIotMqttClient client = new AWSIotMqttClient(clientEndpoint, clientId, pair.keyStore, pair.keyPassword);

        return client;
    }

    public static void subscribe(AWSIotMqttClient client, String topic, AWSIotQos qos, Bollard bollard) {
        AWSIoT_TopicHandler_reserva theTopic = new AWSIoT_TopicHandler_reserva(topic, qos, bollard);
        try {
            client.subscribe(theTopic);
            MySimpleLogger.info("my-aws-iot-thing", "... SUBSCRIBED to TOPIC: " + topic);
        } catch (AWSIotException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    public static void subscribe2(AWSIotMqttClient client, String topic, AWSIotQos qos, Bollard bollard) {
        AWSIoT_TopicHandler_free theTopic = new AWSIoT_TopicHandler_free(topic, qos, bollard);
        try {
            client.subscribe(theTopic);
            MySimpleLogger.info("my-aws-iot-thing", "... SUBSCRIBED to TOPIC: " + topic);
        } catch (AWSIotException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
