package utils;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class ApiSmartParking {

    private static final String ENDPOINT_CONSULTA = "http://localhost:8080/plazas";
    private static final String ENDPOINT_RESERVA = "http://localhost:8080/plazas/reserve";

    public static void consultaPlazas(String roadSegment, String smartCarId) {
        try {
            URL url = new URL(ENDPOINT_CONSULTA);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Configurar la solicitud POST
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Construir el JSON de consulta
            String jsonInputString = "{\"roadSegment\": \"" + roadSegment + "\"}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int status = conn.getResponseCode();
            InputStream is = (status >= 200 && status < 300) ? conn.getInputStream() : conn.getErrorStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));

            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                responseBuilder.append(line.trim());
            }

            conn.disconnect();

            JSONObject responseJson = new JSONObject(responseBuilder.toString());
            JSONObject spot = responseJson.getJSONObject("spot");

            String state = spot.getString("state");
            boolean free = spot.getBoolean("free");
            String spotId = spot.getString("id");

            System.out.println("Respuesta JSON formateada:\n" + responseJson.toString(4));

            if ("FREE".equalsIgnoreCase(state) && free) {
                System.out.println("La plaza está libre. Procediendo a reservar...");
                String reservaResponse = reservaPlaza(spotId, smartCarId);
                System.out.println(reservaResponse);
            } else {
                System.out.println("La plaza no está disponible para reservar.");
            }

        } catch (Exception e) {
            System.out.println("Error en consultaPlazas: " + e.getMessage());
        }
    }

    public static String reservaPlaza(String spotId, String smartCarId) {
        try {
            URL url = new URL(ENDPOINT_RESERVA);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = String.format(
                    "{\"spotId\": \"%s\", \"vehicleId\": \"%s\"}",
                    spotId, smartCarId
            );

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int status = conn.getResponseCode();
            InputStream is = (status >= 200 && status < 300) ? conn.getInputStream() : conn.getErrorStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));

            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                responseBuilder.append(line.trim());
            }

            conn.disconnect();

            JSONObject json = new JSONObject(responseBuilder.toString());
            return "Reserva realizada. Respuesta JSON:\n" + json.toString(4);

        } catch (Exception e) {
            return "Error en reservaPlaza: " + e.getMessage();
        }
    }
}
