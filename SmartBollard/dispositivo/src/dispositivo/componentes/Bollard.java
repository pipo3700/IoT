package dispositivo.componentes;


import dispositivo.api.mqtt.Bollard_APIMQTT;
import dispositivo.interfaces.IBollard;


public class Bollard implements IBollard{
    protected String deviceId = null;
    protected Bollard_APIMQTT subscriber = null;

    public static Bollard build(String deviceId, String ip, int port, String mqttBrokerURL) {
        Bollard bollard = new Bollard(deviceId);
        bollard.subscriber = Bollard_APIMQTT.build(deviceId, ip, mqttBrokerURL);
        return bollard;
    }
    protected Bollard(String deviceId) {
        this.deviceId = deviceId;
    }


    @Override
    public IBollard iniciarBollard() {
        this.subscriber.subscribeTraffic();;
        return this;
    }
}
