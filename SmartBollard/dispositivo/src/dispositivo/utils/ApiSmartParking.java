package dispositivo.utils;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class ApiSmartParking {

    private static final String ENDPOINT_DARALTABOLARDO = "http://localhost:8080/plazas";

    public static String darAltaBolardo(String roadSegment, String IdBolardo) {
        try {
            URL url = new URL(ENDPOINT_DARALTABOLARDO);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = "{"
                    + "\"roadSegment\": \"" + roadSegment + "\", "
                    + "\"id\": \"" + IdBolardo + "\""
                    + "}";

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

            System.out.println("Bolardo dado de alta (formato JSON):\n" + responseJson.toString(4));
            return responseJson.toString(4);

        } catch (Exception e) {
            return "Error en darAltaBolardo: " + e.getMessage();
        }
    }
}
