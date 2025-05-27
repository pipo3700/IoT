package dispositivo.iniciador;

import dispositivo.awsiotthing.AWSIoTThingStarter;
import dispositivo.componentes.Bollard;
import dispositivo.componentes.Dispositivo;
import dispositivo.componentes.Funcion;
import dispositivo.interfaces.FuncionStatus;
import dispositivo.interfaces.IBollard;
import dispositivo.interfaces.IDispositivo;
import dispositivo.interfaces.IFuncion;
import dispositivo.utils.ApiSmartParking;


public class DispositivoIniciador {

	public static void main(String[] args) {
		
		if ( args.length < 4 ) {
			System.out.println("Usage: java -jar dispositivo.jar device deviceIP rest-port mqttBroker");
			System.out.println("Example: java -jar dispositivo.jar ttmi050 ttmi050.iot.upv.es 8182 tcp://ttmi008.iot.upv.es:1883");
			return;
		}



		String deviceId = args[0];
		String deviceIP = args[1];
		String port = args[2];
		String mqttBroker = args[3];
		ApiSmartParking.darAltaBolardo("R5s1", "Bolardo1");

		// Añadimos funciones al dispositivo
		IFuncion f1 = Funcion.build("f1", FuncionStatus.OFF);

		IFuncion f2 = Funcion.build("f2", FuncionStatus.OFF);

		IFuncion f3 = Funcion.build("f3", FuncionStatus.ON);

		Bollard bollard = Bollard.build(deviceId, deviceIP, Integer.valueOf(port), mqttBroker);
		bollard.addFuncion(f1); //luz roja
		bollard.addFuncion(f2); //luz amarilla
		bollard.addFuncion(f3); //luz verde
		bollard.iniciarBollard();

		new AWSIoTThingStarter(bollard);

	}

}
