package utils;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class ApiSmartParking {

    private static final String ENDPOINT_RESERVA = "http://localhost:8080/plazas/reserve";
    private static final String ENDPOINT_OCCUPY = "http://localhost:8080/plazas/occupy";
    private static final String ENDPOINT_PLAZASDISPONIBLES = "http://localhost:8080/plazas/R5s1";

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

    public static String ocuparPlaza(String spotId, String smartCarId) {
        try {
            URL url = new URL(ENDPOINT_OCCUPY);
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

    public static String verplazasdisponibles() {
        try {
            URL url = new URL(ENDPOINT_PLAZASDISPONIBLES);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            int status = conn.getResponseCode();
            InputStream is = (status >= 200 && status < 300) ? conn.getInputStream() : conn.getErrorStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));

            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                responseBuilder.append(line.trim());
            }

            conn.disconnect();

            // Parsear la respuesta como JSON
            JSONObject jsonResponse = new JSONObject(responseBuilder.toString());

            // Imprimir el JSON formateado
            System.out.println("Plazas disponibles (formato JSON):\n" + jsonResponse.toString(4));

            return jsonResponse.toString(4);

        } catch (Exception e) {
            return "Error en verplazasdisponibles: " + e.getMessage();
        }
    }

}
