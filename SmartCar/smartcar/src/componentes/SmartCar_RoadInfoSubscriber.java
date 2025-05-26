package componentes;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;
import utils.MySimpleLogger;

public class SmartCar_RoadInfoSubscriber extends MyMqttClient {

    protected SmartCar smartcar;

    public SmartCar_RoadInfoSubscriber(String clientId, SmartCar smartcar, String MQTTBrokerURL) {
        super(clientId, smartcar, MQTTBrokerURL);
        this.smartcar = smartcar;
    }

    @Override
    public void connect() {
        super.connect();

        String segment = this.smartcar.getCurrentPlace().getRoad();

        String trafficTopic = "es/upv/pros/tatami/smartcities/traffic/PTPaterna/road/" + segment + "/traffic";
        String infoTopic = "es/upv/pros/tatami/smartcities/traffic/PTPaterna/road/" + segment + "/info";

        this.subscribe(trafficTopic);
        this.subscribe(infoTopic);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

        super.messageArrived(topic, message); // Muestra el mensaje recibido (puedes comentarlo si no lo quieres ver siempre)

        String payload = new String(message.getPayload());

        try {
            JSONObject json = new JSONObject(payload);

            if (json.has("action") && json.getString("action").equals("SPEED_LIMIT")) {

                int newLimit = json.getInt("speed-limit");
                int currentSpeed = smartcar.getRoadCurrentSpeed();

                int appliedSpeed = Math.min(newLimit, currentSpeed);

                System.out.println("Recibido nuevo límite de velocidad:");
                System.out.println("Límite propuesto por señal: " + newLimit + " km/h");
                System.out.println("Velocidad actual en la vía: " + currentSpeed + " km/h");
                System.out.println("Ajustando velocidad del vehículo a: " + appliedSpeed + " km/h\n");
            }

            else if (json.has("action") && json.getString("action").equals("ACCIDENT")) {
                System.out.println("Accidente detectado en el tramo " 
                    + smartcar.getCurrentPlace().getRoad() + "");
            }

        } catch (Exception e) {
            System.err.println("Error procesando mensaje recibido:");
            e.printStackTrace();
        }
    }
}

