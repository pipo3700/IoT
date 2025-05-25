package controlador.iniciador;

import controlador.interfaces.FuncionStatus;
import controlador.utils.MySimpleLogger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;
import org.restlet.resource.ClientResource;

public class ControladorIniciador {

	public static final String TOPIC_SINCRONIZACION_F1 = "/dispositivo/sync/funcion/f1/comandos";

	public static void main(String[] args) {
		
		if ( args.length < 3 ) {
			System.out.println("Usage: java -jar controlador.jar deviceToCopyIp rest-port mqttBroker");
			System.out.println("Example: java -jar controlador.jar http://ttmi05X.iot.upv.es 8182 tcp://ttmi008.iot.upv.es:1883");
			return;
		}


		String deviceToCopyIp = args[0];
		String port = args[1];
		String mqttBroker = args[2];

		try {
			// URL del recurso al que se quiere acceder
			String pathF1 = "/dispositivo/funcion/f1";
			String url = deviceToCopyIp + ":" + port + pathF1;

			// Utilizar ClientResource para gestionar la solicitud
			ClientResource clientResource = new ClientResource(url);

			// Realizar una solicitud GET
			String resp = clientResource.get().getText();

			JSONObject respuesta = new JSONObject(resp);

			String estado = respuesta.getString("estado");

			MqttClient mqttClient = new MqttClient(mqttBroker, "controladorDispositivos");
			mqttClient.connect();

			JSONObject payload = new JSONObject();
			FuncionStatus estadoFuncion = FuncionStatus.getStatus(estado);
			payload.put("accion", estadoFuncion.getNombre());
			MqttMessage message = new MqttMessage(payload.toString().getBytes());
			mqttClient.publish(TOPIC_SINCRONIZACION_F1, message);

			MySimpleLogger.info("", "Resultado: " + respuesta);

		} catch (Exception e) {
			e.printStackTrace();
		}

}

}


