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

public class Dispositivo_APIMQTT implements MqttCallback {

    public static final String SYNC_F1_TOPIC = "/dispositivo/sync/funcion/f1/comandos";
    protected MqttClient myClient;
    protected MqttConnectOptions connOpt;
    protected String clientId = null;

    protected IDispositivo dispositivo;
    protected String mqttBroker = null;

    private String loggerId = null;

    public static Dispositivo_APIMQTT build(IDispositivo dispositivo, String brokerURL) {
        Dispositivo_APIMQTT api = new Dispositivo_APIMQTT(dispositivo);
        api.setBroker(brokerURL);
        return api;
    }

    protected Dispositivo_APIMQTT(IDispositivo dev) {
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
        //         $topic_base/dispositivo/funcion/$ID-FUNCION/commamnd
        //   Donde el $topic_base es parametrizable al arrancar el dispositivo
        //   y la $ID-FUNCION es el identificador de la dunción

        String[] topicNiveles = topic.split("/");
        String funcionId = topicNiveles[topicNiveles.length - 2];

        IFuncion f = this.dispositivo.getFuncion(funcionId);
        if (f == null) {
            MySimpleLogger.warn(this.loggerId, "No encontrada funcion " + funcionId);
            return;
        }


        //
        // Definimos una API con mensajes de acciones básicos
        //

        // Ejecutamos acción indicada en campo 'accion' del JSON recibido
        String action = jsonPayload.getString("accion");
        boolean isValidAction = true;
        if (!dispositivo.isHabilitado()) {
            MySimpleLogger.warn(this.loggerId, "Dispositivo no habilitado. No se puede realizar la accion");
            return;
        }
        if (action.equalsIgnoreCase("encender"))
            f.encender();
        else if (action.equalsIgnoreCase("apagar"))
            f.apagar();
        else if (action.equalsIgnoreCase("parpadear"))
            f.parpadear();
        else {
            MySimpleLogger.warn(this.loggerId, "Acción '" + payload + "' no reconocida. Sólo admitidas: encender, apagar o parpadear");
            isValidAction = false;
        }
        if (isValidAction) {
            var status = new JSONObject();
            status.put("estado", f.getStatus());
            var msg = new MqttMessage(status.toString().getBytes());
            String infoTopic = getInfoTopic(f);
            MySimpleLogger.info(this.loggerId, "Publicando estado: " + f.getStatus() + " al topic: " + infoTopic);
            myClient.publish(infoTopic, msg);
        }
    }

    /**
     * runClient
     * The main functionality of this simple example.
     * Create a MQTT client, connect to broker, pub/sub, disconnect.
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
            if (persistence != null)
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

        if (this.myClient == null || !this.myClient.isConnected())
            this.connect();

        if (this.dispositivo == null)
            return;

        for (IFuncion f : this.dispositivo.getFunciones())
            this.subscribe(this.getCommandTopic(f));

        // para el ejercicio 10, no tenemos informacion de los dispositivos activos
        // así que la solucion más sencilla es que todos los dispositivos se suscriban a un topico comun
        // para replicar el estado de la funcion F1 de algun dispositivo objetivo
        this.subscribe(SYNC_F1_TOPIC);
    }


    public void detener() {
        if (this.myClient == null || !this.myClient.isConnected()) return;
        for(IFuncion f : this.dispositivo.getFunciones()) {
            unsubscribe(this.getCommandTopic(f));
        }
        disconnect();
    }


    protected String getCommandTopic(IFuncion f) {
        return Configuracion.TOPIC_BASE + "dispositivo/" + dispositivo.getId() + "/funcion/" + f.getId() + "/comandos";
    }

    protected String getInfoTopic(IFuncion f) {
        return Configuracion.TOPIC_BASE + "dispositivo/" + dispositivo.getId() + "/funcion/" + f.getId() + "/info";
    }
}
