package dispositivo.api.mqtt;

import dispositivo.api.iot.infraestructure.Dispositivo_RegistradorMQTT;
import dispositivo.interfaces.Configuracion;
import dispositivo.interfaces.IBollard;
import dispositivo.interfaces.IFuncion;
import dispositivo.utils.MySimpleLogger;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.json.JSONObject;

import java.util.UUID;

public class Bollard_APIMQTT implements MqttCallback{
    protected MqttClient myClient;
    protected MqttConnectOptions connOpt;

    protected String dispositivoId = null;
    protected String dispositivoIP = null;
    protected String mqttBroker = null;
    protected IBollard bollard;

    private String loggerId = null;

    public static Bollard_APIMQTT build(IBollard bollard, String dispositivoId, String dispositivoIP, String mqttBroker) {
        Bollard_APIMQTT reg = new Bollard_APIMQTT(bollard);
        reg.setDispositivoID(dispositivoId);
        reg.setDispositivoIP(dispositivoIP);
        reg.setBrokerURL(mqttBroker);
        return reg;
    }

    protected Bollard_APIMQTT(IBollard bol) {
        this.bollard = bol;
        this.loggerId = String.valueOf(bol.getFuncion("f2"));
    }

    protected void setDispositivoID(String dispositivoID) {
        this.dispositivoId = dispositivoID;
        this.loggerId = dispositivoID + "-RegisterService";
    }

    protected void setDispositivoIP(String dispositivoIP) {
        this.dispositivoIP = dispositivoIP;
    }

    protected void setBrokerURL(String brokerURL) {
        this.mqttBroker = brokerURL;
    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

        String payload = new String(message.getPayload());
        JSONObject jsonPayload = new JSONObject(payload);
        String roadsegment = jsonPayload.getString("road-segment");
        String action = jsonPayload.getString("action");
        if(roadsegment.equalsIgnoreCase("R5s1") && action.equalsIgnoreCase("VEHICLE_IN") ) {
            System.out.println("-------------------------------------------------");
            System.out.println("| Topic:" + topic);
            System.out.println("| Message: " + payload);
            System.out.println("-------------------------------------------------");

            System.out.println("El coche está en el roadsegment "+roadsegment+" del SmartParking, y se dispone a aparcarlo, por lo que bajamos el pilón, luz roja");
            IFuncion f1 = this.bollard.getFuncion("f1");
            IFuncion f2 = this.bollard.getFuncion("f2");
            IFuncion f3 = this.bollard.getFuncion("f3");
            f1.encender();
            f2.apagar();
            f3.apagar();
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    public void subscribeTraffic() {
        if ( this.myClient == null || !this.myClient.isConnected() ) {
            this.connect();
        }

        // subscribe to topic
        try {
            int subQoS = 0;
            myClient.subscribe(Configuracion.TOPIC_TRAFFIC, subQoS);
            MySimpleLogger.info(this.loggerId, "Suscrito al topic " + Configuracion.TOPIC_TRAFFIC);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void connect() {

        // setup MQTT Client
        String clientID = this.dispositivoId + UUID.randomUUID().toString();
        connOpt = new MqttConnectOptions();

        connOpt.setCleanSession(true);
        connOpt.setKeepAliveInterval(30);

        // Connect to Broker
        try {

            MqttDefaultFilePersistence persistence = null;
            try {
                persistence = new MqttDefaultFilePersistence("/tmp");
            } catch (Exception e) {
            }
            if ( persistence != null )
                myClient = new MqttClient(this.mqttBroker, clientID, persistence);
            else
                myClient = new MqttClient(this.mqttBroker, clientID);

            myClient.setCallback((MqttCallback) this);
            myClient.connect(connOpt);
        } catch (MqttException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        MySimpleLogger.info(this.loggerId, "Conectado al broker " + this.mqttBroker);

    }
}
