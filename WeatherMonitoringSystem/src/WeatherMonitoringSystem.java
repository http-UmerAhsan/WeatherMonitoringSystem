import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

// This class represents a geographical location with a city and country
class Location {
    private String city;
    private String country;

    public Location(String city, String country) {
        this.city = city;
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Location location = (Location) o;
        return city.equals(location.city) && country.equals(location.country);
    }

    @Override
    public int hashCode() {
        return city.hashCode() + country.hashCode();
    }

    @Override
    public String toString() {
        return city + ", " + country;
    }
}

//This class encapsulates the weather data for a specific location.
class WeatherData {
    private double temperature;
    private double humidity;
    private double windSpeed;
    private Location location;

    public WeatherData(double temperature, double humidity, double windSpeed, Location location) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.location = location;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "Location: " + location.getCity() + ", " + location.getCountry() +
                "\nTemperature: " + temperature + "°C" +
                "\nHumidity: " + humidity + "%" +
                "\nWind Speed: " + windSpeed + " m/s";
    }
}

// This class handles the communication with the OpenWeatherMap API to fetch weather data.
class WeatherApiClient {
    private static final String API_KEY = "YOUR_API_KEY";//Enter Your API key from OpenWeather Here.
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    public WeatherData fetchWeatherData(Location location) throws Exception {
        String urlString = String.format("%s?q=%s,%s&units=metric&appid=%s",
                BASE_URL, location.getCity(), location.getCountry(), API_KEY);
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
            double temperature = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
            double humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsDouble();
            double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();

            return new WeatherData(temperature, humidity, windSpeed, location);
        } else {
            throw new RuntimeException("Failed to get weather data: " + responseCode);
        }
    }
}

//This class handles the user interface, allowing the user to interact with the weather monitoring system.
class UserInterface {
    private WeatherApiClient weatherApiClient;
    private Scanner scanner;
    private static final int TERMINAL_WIDTH = 145;

    public UserInterface() {
        this.weatherApiClient = new WeatherApiClient();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        displayWelcomeMessage();
        while (true) {
            System.out.println("1. View Current Weather");
            System.out.println("2. Exit");
            System.out.print("Enter Choice: ");
            String input = scanner.nextLine();
            int choice = -1;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice, Try again!");
                continue;
            }

            switch (choice) {
                case 1:
                    viewCurrentWeather();
                    break;
                case 2:
                    displayGoodbyeMessage();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice, try again.");
            }
        }
    }

    private void viewCurrentWeather() {
        System.out.print("\nEnter city name: ");
        String city = scanner.nextLine();
        System.out.print("Enter country name: ");
        String country = scanner.nextLine();
        Location location = new Location(city, country);
        try {
            WeatherData data = weatherApiClient.fetchWeatherData(location);
            System.out.println("=================================");
            System.out.println("Current Weather:\n" + data);
            System.out.println("=================================");
        } catch (Exception e) {
            System.out.println("Error fetching weather data: " + e.getMessage());
        }
    }

    private void displayWelcomeMessage() {
        String separator = "==========================================================================================================================================";
        String welcomeMessage = "Welcome to Weather Monitoring System created by http-UmerAhsan";
        int paddingSize = (TERMINAL_WIDTH - welcomeMessage.length()) / 2;
        String padding = " ".repeat(Math.max(0, paddingSize));
        System.out.println(separator);
        System.out.println(padding + welcomeMessage);
        System.out.println(separator);
    }

    private void displayGoodbyeMessage() {
        String separator = "==========================================================================================================================================";
        String goodbyeMessage = "Thank you for using our weather monitoring system! Stay weather aware!";
        int paddingSize = (TERMINAL_WIDTH - goodbyeMessage.length()) / 2;
        String padding = " ".repeat(Math.max(0, paddingSize));
        System.out.println(separator);
        System.out.println(padding + goodbyeMessage);
        System.out.println(separator);
    }
}
//This is the entry point of the application 
public class WeatherMonitoringSystem {
    public static void main(String[] args) {
        UserInterface ui = new UserInterface();
        ui.start();
    }
}
