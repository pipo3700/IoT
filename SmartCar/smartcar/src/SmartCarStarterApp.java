import awsiotthing.liberarplazaAWS;
import componentes.SmartCar;
import utils.SmartCarApiServer;

public class SmartCarStarterApp {
    public static void main(String[] args) throws Exception {

        if (args.length < 2) {
            System.out.println("Usage: SmartCarStarterApp <CarId> <brokerURL>");
            System.exit(1);
        }

        String SmartCarId = args[0];       // Número de vehículos
        String brokerURL = args[1];                    // Broker MQTT

        // Crear e iniciar el vehículo
        SmartCar car = new SmartCar(SmartCarId, brokerURL);
        // Llamada para arrancar la API REST
        SmartCarApiServer.startApi(car);

    }
}
