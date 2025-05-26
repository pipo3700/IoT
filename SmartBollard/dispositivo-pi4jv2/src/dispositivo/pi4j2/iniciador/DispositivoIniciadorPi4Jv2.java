package dispositivo.pi4j2.iniciador;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;

import dispositivo.awsiotthing.AWSIoTThingStarter;
import dispositivo.componentes.Bollard;
import dispositivo.componentes.Dispositivo;
import dispositivo.componentes.pi4j2.FuncionPi4Jv2;
import dispositivo.interfaces.FuncionStatus;
import dispositivo.interfaces.IDispositivo;

public class DispositivoIniciadorPi4Jv2 {

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
		

		// Configuramos el contexto/plataforma del GPIO de la Raspberry
		Context pi4jContext =  Pi4J.newAutoContext();
		//Platforms platforms = pi4jContext.platforms();

		// Añadimos funciones al dispositivo
		// f1 - GPIO_17 luz roja
		FuncionPi4Jv2 f1 = FuncionPi4Jv2.build("f1", 17, FuncionStatus.OFF, pi4jContext);
		
		// f2 - GPIO_13 luz amarilla
		FuncionPi4Jv2 f2 = FuncionPi4Jv2.build("f2", 13, FuncionStatus.OFF, pi4jContext);

		// f3 - GPIO_13 luz verde
		FuncionPi4Jv2 f3 = FuncionPi4Jv2.build("f3", 15, FuncionStatus.ON, pi4jContext);

		Bollard bollard = Bollard.build(deviceId, deviceIP, Integer.valueOf(port), mqttBroker);
		bollard.addFuncion(f1); //luz roja
		bollard.addFuncion(f2); //luz amarilla
		bollard.addFuncion(f3); //luz verde
		bollard.iniciarBollard();

		new AWSIoTThingStarter(bollard);
		
	}

}
