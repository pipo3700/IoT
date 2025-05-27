package utils;

import awsiotthing.liberarplazaAWS;
import componentes.SmartCar;

import java.util.Objects;

import static spark.Spark.*;

public class SmartCarApiServer {

    public static void startApi(SmartCar car) {
        port(9091); // Puerto
        get("/smartcar/plazasdisponibles", (req, res) -> {
            res.type("application/json");
            return ApiSmartParking.verplazasdisponibles();
        });

        post("/smartcar/reserve/:spotId", (req, res) -> {
            res.type("application/json");

            String spotId = req.params(":spotId");
            String smartCarId = car.getSmartCarID(); // Puedes recibirlo por body o tenerlo fijo

            return ApiSmartParking.reservaPlaza(spotId, smartCarId);
        });

        post("/smartcar/free/:spotId", (req, res) -> {
            res.type("application/json");
            String spotId = req.params(":spotId");
            String smartCarId = car.getSmartCarID(); // Puedes recibirlo por body o tenerlo fijo
            new liberarplazaAWS(spotId, smartCarId);
            car.publishVehicleOut();
            return "Plaza liberada";
        });

        post("/smartcar/occupy/:spotId", (req, res) -> {
            res.type("application/json");

            String spotId = req.params(":spotId");
            String smartCarId = car.getSmartCarID(); // Puedes recibirlo por body o tenerlo fijo

            return ApiSmartParking.ocuparPlaza(spotId, smartCarId);
        });

        post("/smartcar/move/:roadSegment", (req, res) -> {
            String roadSegment = req.params(":roadSegment");
            if (Objects.equals(car.getCurrentPlace().getRoad(), roadSegment)){
                return "Ya estas en el road segment:" + roadSegment;
            } else {
                car.publishVehicleOut();
                // Esperar unos segundos
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                car.getIntoRoad(roadSegment, 100);  // Cada uno en un km distinto
                car.publishVehicleIn();
                System.out.println(car.getSmartCarID() + " ha entrado en el tramo " + roadSegment);
                return "Coche movido al tramo " + roadSegment;
            }
        });

        System.out.println("API REST activa en http://localhost:9091");
    }
}
