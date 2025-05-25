package dispositivo.awsiotthing;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;
import dispositivo.componentes.Bollard;
import dispositivo.interfaces.IFuncion;
import dispositivo.utils.MySimpleLogger;


public class AWSIoT_TopicHandler extends AWSIotTopic {
	private Bollard bollard;  // 💡 Guardamos la instancia del bollard

	public AWSIoT_TopicHandler(String topic, AWSIotQos qos, Bollard bollard) {
		super(topic, qos);
		this.bollard = bollard;
	}
	
	@Override
	public void onMessage(AWSIotMessage message) {
		//super.onMessage(message);
		String text = message.getStringPayload();
		MySimpleLogger.info("my-aws-iot-thing" + "-topicHandler", "RECEIVED: " + text);

		if (text.equals("plaza: reservada")) {
				System.out.println("Un coche ha reservado la plaza, por lo que subimos el pilón, luz amarilla");
				IFuncion f1 = bollard.getFuncion("f1");
				IFuncion f2 = bollard.getFuncion("f2");
				IFuncion f3 = bollard.getFuncion("f3");
				f1.apagar();
				f2.encender();
				f3.apagar();
		}
		else if (text.equals("plaza: liberada")) {
			System.out.println("Un coche ha liberado la plaza, por lo que el pilón sigue abajo pero la plaza está libre, luz verde");
			IFuncion f1 = bollard.getFuncion("f1");
			IFuncion f2 = bollard.getFuncion("f2");
			IFuncion f3 = bollard.getFuncion("f3");
			f1.apagar();
			f2.apagar();
			f3.encender();
		}
	}
}

