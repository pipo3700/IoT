import awsiotthing.AWSIoTThingStarter;
import componentes.SmartCar;
import utils.ApiSmartParking;

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
        car.getIntoRoad("R5s1", 100);  // Cada uno en un km distinto
        car.publishVehicleIn();
        System.out.println(SmartCarId + " ha entrado en el tramo.");

        // Esperar unos segundos
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(" Dando de alta un spot, si está libre (que lo va a estar), lo reservo");
        ApiSmartParking.consultaPlazas("R5s1", SmartCarId);
        // Esperar unos segundos
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //PETICIÓN A LA API DEL COCHE DE SALIR DE LA PLAZA
        //Cuando se le haga una petición a la api del coche de liberar plaza:
        car.publishVehicleOut();
        System.out.println(car.getSmartCarID() + " ha salido del tramo.");
        new AWSIoTThingStarter(); //En este mensaje publicado al topic smartbollard/id de "plaza: liberada" (también en el json)
    }
}
