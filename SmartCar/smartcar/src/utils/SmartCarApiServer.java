package utils;

import awsiotthing.liberarplazaAWS;
import componentes.SmartCar;

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

        post("/smartcar/move/:roadSegment", (req, res) -> {
            String roadSegment = req.params(":roadSegment");
            car.getIntoRoad(roadSegment, 100);  // Cada uno en un km distinto
            car.publishVehicleIn();
            System.out.println(car.getSmartCarID() + " ha entrado en el tramo " + roadSegment);
            return "Coche movido";
        });

        System.out.println("API REST activa en http://localhost:9091");
    }
}
