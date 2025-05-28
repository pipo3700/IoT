package dispositivo.componentes;


import dispositivo.api.mqtt.Bollard_APIMQTT;
import dispositivo.interfaces.IBollard;
import dispositivo.interfaces.IFuncion;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Bollard implements IBollard{
    protected String deviceId = null;
    protected String deviceName = null;
    protected Bollard_APIMQTT subscriber = null;
    protected Map<String, IFuncion> functions = null;

    public static Bollard build(String deviceId, String ip, int port, String mqttBrokerURL, String deviceName) {
        Bollard bollard = new Bollard(deviceId, deviceName);
        bollard.subscriber = Bollard_APIMQTT.build(bollard, deviceId, ip, mqttBrokerURL);
        return bollard;
    }
    protected Bollard(String deviceId, String deviceName) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
    }
    protected Map<String, IFuncion> getFunctions() {
        return this.functions;
    }
    protected void setFunctions(Map<String, IFuncion> fs) {
        this.functions = fs;
    }

    public String getId() {
        return deviceName;
    }
    @Override
    public IBollard iniciarBollard() {
        for(IFuncion f : this.getFunciones()) {
            f.iniciar();
        }
        this.subscriber.subscribeTraffic();;
        return this;
    }

    @Override
    public IBollard addFuncion(IFuncion f) {
        if ( this.getFunctions() == null )
            this.setFunctions(new HashMap<String, IFuncion>());
        this.getFunctions().put(f.getId(), f);
        return this;
    }

    @Override
    public Collection<IFuncion> getFunciones() {
        if ( this.getFunctions() == null )
            return null;
        return this.getFunctions().values();
    }
    @Override
    public IFuncion getFuncion(String funcionId) {
        if ( this.getFunctions() == null )
            return null;
        return this.getFunctions().get(funcionId);
    }

}
