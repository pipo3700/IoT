package dispositivo.api.mqtt;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import dispositivo.interfaces.Configuracion;
import dispositivo.interfaces.IDispositivo;
import dispositivo.interfaces.IFuncion;
import dispositivo.utils.MySimpleLogger;
import org.json.JSONObject;

public class ComandoDispositivo_APIMQTT implements MqttCallback {

    protected MqttClient myClient;
    protected MqttConnectOptions connOpt;
    protected String clientId = null;

    protected IDispositivo dispositivo;
    protected String mqttBroker = null;

    private String loggerId = null;

    public static ComandoDispositivo_APIMQTT build(IDispositivo dispositivo, String brokerURL) {
        ComandoDispositivo_APIMQTT api = new ComandoDispositivo_APIMQTT(dispositivo);
        api.setBroker(brokerURL);
        return api;
    }

    protected ComandoDispositivo_APIMQTT(IDispositivo dev) {
        this.dispositivo = dev;
        this.loggerId = dev.getId() + "-apiMQTT";
    }

    protected void setBroker(String mqttBrokerURL) {
        this.mqttBroker = mqttBrokerURL;
    }


    @Override
    public void connectionLost(Throwable t) {
        MySimpleLogger.debug(this.loggerId, "Connection lost!");
        // code to reconnect to the broker would go here if desired
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        //System.out.println("Pub complete" + new String(token.getMessage().getPayload()));
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

        String payload = new String(message.getPayload());
        JSONObject jsonPayload = new JSONObject(payload);
        System.out.println("-------------------------------------------------");
        System.out.println("| Topic:" + topic);
        System.out.println("| Message: " + payload);
        System.out.println("-------------------------------------------------");

        // DO SOME MAGIC HERE!

        //
        // Obtenemos el id de la función
        //   Los topics están organizados de la siguiente manera:
        //         $topic_base/dispositivo/$ID-DISPOSITIVO/commamnd
        //   Donde el $topic_base es parametrizable al arrancar el dispositivo
        //   y la $ID-DISPOSITIVO es el identificador del dispositivo

        String[] topicNiveles = topic.split("/");

        //
        // Definimos una API con mensajes de acciones básicos
        //

        // Ejecutamos acción indicada en campo 'accion' del JSON recibido
        String action = jsonPayload.getString("accion");

        if(action.equalsIgnoreCase("habilitar") ) {
            dispositivo.habilitar();
        } else if ( action.equalsIgnoreCase("deshabilitar") ) {
            dispositivo.deshabilitar();
        }
        else
            MySimpleLogger.warn(this.loggerId, "Acción '" + payload + "' no reconocida. Sólo admitidas: habilitar o deshabilitar");



    }

    /**
     *
     * runClient
     * The main functionality of this simple example.
     * Create a MQTT client, connect to broker, pub/sub, disconnect.
     *
     */
    public void connect() {
        // setup MQTT Client
        String clientID = this.dispositivo.getId() + UUID.randomUUID().toString() + ".subscriber";
        connOpt = new MqttConnectOptions();

        connOpt.setCleanSession(true);
        connOpt.setKeepAliveInterval(30);
//			connOpt.setUserName(M2MIO_USERNAME);
//			connOpt.setPassword(M2MIO_PASSWORD_MD5.toCharArray());

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

            myClient.setCallback(this);
            myClient.connect(connOpt);
        } catch (MqttException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        MySimpleLogger.info(this.loggerId, "Conectado al broker " + this.mqttBroker);

    }


    public void disconnect() {

        // disconnect
        try {
            // wait to ensure subscribed messages are delivered
            Thread.sleep(10000);

            myClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    protected void subscribe(String myTopic) {

        // subscribe to topic
        try {
            int subQoS = 0;
            myClient.subscribe(myTopic, subQoS);
            MySimpleLogger.info(this.loggerId, "Suscrito al topic " + myTopic);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    protected void unsubscribe(String myTopic) {

        // unsubscribe to topic
        try {
            myClient.unsubscribe(myTopic);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void iniciar() {

        if ( this.myClient == null || !this.myClient.isConnected() )
            this.connect();

        if ( this.dispositivo == null )
            return;

        this.subscribe(calculateCommandDevice());
    }



    public void detener() {
        unsubscribe(calculateCommandDevice());
        disconnect();
    }

    protected String calculateCommandDevice() {
        return Configuracion.TOPIC_BASE + "dispositivo/" + dispositivo.getId() + "/comandos";
    }
}
