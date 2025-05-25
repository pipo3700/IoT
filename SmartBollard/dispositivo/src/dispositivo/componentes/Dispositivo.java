package dispositivo.componentes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import dispositivo.api.iot.infraestructure.Dispositivo_RegistradorMQTT;
import dispositivo.api.mqtt.ComandoDispositivo_APIMQTT;
import dispositivo.api.mqtt.Dispositivo_APIMQTT;
import dispositivo.api.rest.Dispositivo_APIREST;
import dispositivo.interfaces.IDispositivo;
import dispositivo.interfaces.IFuncion;

public class Dispositivo implements IDispositivo {
	
	protected String deviceId = null;
	protected boolean habilitado = true;
	protected Map<String, IFuncion> functions = null;
	protected Dispositivo_RegistradorMQTT registrador = null;
	protected Dispositivo_APIMQTT apiFuncionesMQTT = null;
	protected ComandoDispositivo_APIMQTT apiComandoDispositivoMQTT = null;
	protected Dispositivo_APIREST apiFuncionesREST = null;
	
	
	public static Dispositivo build(String deviceId, String ip, String mqttBrokerURL) {
		Dispositivo dispositivo = new Dispositivo(deviceId);
		dispositivo.registrador = Dispositivo_RegistradorMQTT.build(deviceId, ip, mqttBrokerURL);
		dispositivo.apiFuncionesMQTT = Dispositivo_APIMQTT.build(dispositivo, mqttBrokerURL);
		dispositivo.apiComandoDispositivoMQTT = ComandoDispositivo_APIMQTT.build(dispositivo, mqttBrokerURL);
		dispositivo.apiFuncionesREST = Dispositivo_APIREST.build(dispositivo);
		return dispositivo;
	}

	public static Dispositivo build(String deviceId, String ip, int port, String mqttBrokerURL) {
		Dispositivo dispositivo = new Dispositivo(deviceId);
		dispositivo.registrador = Dispositivo_RegistradorMQTT.build(deviceId, ip, mqttBrokerURL);
		dispositivo.apiFuncionesMQTT = Dispositivo_APIMQTT.build(dispositivo, mqttBrokerURL);
		dispositivo.apiComandoDispositivoMQTT = ComandoDispositivo_APIMQTT.build(dispositivo, mqttBrokerURL);
		dispositivo.apiFuncionesREST = Dispositivo_APIREST.build(dispositivo, port);
		return dispositivo;
	}

	protected Dispositivo(String deviceId) {
		this.deviceId = deviceId;
	}
	
	@Override
	public String getId() {
		return this.deviceId;
	}

	protected Map<String, IFuncion> getFunctions() {
		return this.functions;
	}
	
	protected void setFunctions(Map<String, IFuncion> fs) {
		if (!this.habilitado) return;
		this.functions = fs;
	}
	
	@Override
	public Collection<IFuncion> getFunciones() {
		if ( this.getFunctions() == null )
			return null;
		return this.getFunctions().values();
	}

	@Override
	public IDispositivo habilitar() {
		this.habilitado = true;
		return this;
	}

	@Override
	public IDispositivo deshabilitar() {
		this.habilitado = false;
		return this;
	}

	@Override
	public boolean isHabilitado() {
		return this.habilitado;
	}

	@Override
	public IDispositivo addFuncion(IFuncion f) {
		if (!this.habilitado) return this;
		if ( this.getFunctions() == null )
			this.setFunctions(new HashMap<String, IFuncion>());
		this.getFunctions().put(f.getId(), f);
		return this;
	}
	
	
	@Override
	public IFuncion getFuncion(String funcionId) {
		if ( this.getFunctions() == null )
			return null;
		return this.getFunctions().get(funcionId);
	}
	
		
	@Override
	public IDispositivo iniciar() {
		for(IFuncion f : this.getFunciones()) {
			f.iniciar();
		}

		this.registrador.registrar();
		this.apiFuncionesMQTT.iniciar();
		this.apiComandoDispositivoMQTT.iniciar();
		this.apiFuncionesREST.iniciar();
		return this;
	}

	@Override
	public IDispositivo detener() {
		this.registrador.desregistrar();
		this.apiFuncionesMQTT.detener();
		this.apiComandoDispositivoMQTT.detener();
		this.apiFuncionesREST.detener();
		for(IFuncion f : this.getFunciones()) {
			f.detener();
		}
		return this;
	}
	
	
	
}
