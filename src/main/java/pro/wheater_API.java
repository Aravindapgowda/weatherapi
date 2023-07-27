package pro;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class wheater_API {

    private static final String API_URL = "https://samples.openweathermap.org/data/2.5/forecast/hourly?q=London,us&appid=b6907d289e10d714a6e88b30761fae22";

    public static void main(String[] args) {
        wheater_API weatherApp = new wheater_API();
        weatherApp.run();
    }

    private void run() {
        Scanner scanner = new Scanner(System.in);
        String weatherData = getWeatherData();

        if (weatherData == null) 
        {
            return;
        }

        while (true) {
            System.out.println("1. Get weather");
            System.out.println("2. Get Wind Speed");
            System.out.println("3. Get Pressure");
            System.out.println("0. Exit");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter the date (YYYY-MM-DD): ");
                    String targetDate = scanner.next();
                    double temperature = getTemperature(weatherData, targetDate);
                    if (Double.isNaN(temperature)) {
                        System.out.println("Data not available for the input date.\n");
                    } else {
                        System.out.printf("The temperature on %s is %.2f Â°K\n\n", targetDate, temperature);
                    }
                    break;

                case 2:
                    System.out.print("Enter the date (YYYY-MM-DD): ");
                    targetDate = scanner.next();
                    double windSpeed = getWindSpeed(weatherData, targetDate);
                    if (Double.isNaN(windSpeed)) {
                        System.out.println("Data not available for the input date.\n");
                    } else {
                        System.out.printf("The wind speed on %s is %.2f m/s\n\n", targetDate, windSpeed);
                    }
                    break;

                case 3:
                    System.out.print("Enter the date (YYYY-MM-DD): ");
                    targetDate = scanner.next();
                    double pressure = getPressure(weatherData, targetDate);
                    if (Double.isNaN(pressure)) {
                        System.out.println("Data not available for the input date.\n");
                    } else {
                        System.out.printf("The pressure on %s is %.2f hPa\n\n", targetDate, pressure);
                    }
                    break;

                case 0:
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.\n");
            }
        }
    }

    private String getWeatherData() {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                connection.disconnect();
                return response.toString();
            } else {
                System.out.println("Error fetching weather data.Response code:"+connection.getResponseCode());
                return null;
            }
        } catch (IOException e) {
            System.out.println("Error fetching weather data: " + e.getMessage());
            return null;
        }
    }

    private double getTemperature(String weatherData, String targetDate) {
    	 JSONObject json = new JSONObject(weatherData);
         JSONArray list = json.getJSONArray("list");

         for (int i = 0; i < list.length(); i++) {
             JSONObject forecast = list.getJSONObject(i);
             String date = forecast.getString("dt_txt").split(" ")[0];

             if (date.equals(targetDate)) {
                 JSONObject main = forecast.getJSONObject("main");
                 return main.getDouble("temp");
             }
         }
 
        return Double.NaN;
    }

    private double getWindSpeed(String weatherData, String targetDate) {
    	JSONObject json = new JSONObject(weatherData);
        JSONArray list = json.getJSONArray("list");

        for (int i = 0; i < list.length(); i++) {
            JSONObject forecast = list.getJSONObject(i);
            String date = forecast.getString("dt_txt").split(" ")[0];

            if (date.equals(targetDate)) {
                JSONObject wind = forecast.getJSONObject("wind");
                return wind.getDouble("speed");
            }
        }
      
        return Double.NaN;
    }

    private double getPressure(String weatherData, String targetDate) {
    	JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(weatherData);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonArray list = jsonObject.getAsJsonArray("list");

        for (JsonElement element : list) {
            JsonObject forecast = element.getAsJsonObject();
            String date = forecast.get("dt_txt").getAsString().split(" ")[0];

            if (date.equals(targetDate)) {
                JsonObject main = forecast.getAsJsonObject("main");
                return main.get("pressure").getAsDouble();
            }
        }

       
        return Double.NaN;
    }
}
