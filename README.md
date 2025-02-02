# Weather Monitoring System
## Introduction
The Weather Monitoring System is a Java-based application that allows users to fetch and display current weather information for any city and country. This application uses the OpenWeatherMap API to get real-time weather data, making it a reliable tool for checking weather conditions.

## Objectives
- To provide real-time weather information for any specified location.
- To create an intuitive user interface for easy interaction.
- To demonstrate the use of various Java libraries and API integration.

## Scope
The Weather Monitoring System can be used by anyone who needs to quickly check the weather conditions for a specific city and country. It is designed to be user-friendly, providing essential weather information such as temperature, humidity, and wind speed.

## Working
There are a total of 5 classes in this program and the function of each class is explained below:

### 1. Location
- **Attributes**: `city`, `country`
- **Methods**:
  - `getCity()`: Returns the city name.
  - `getCountry()`: Returns the country name.
  - `equals(Object o)`: Checks if two locations are equal.
  - `hashCode()`: Generates a hash code for the location.
  - `toString()`: Returns a string representation of the location.

```java
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
```
- **Description**: This class represents a geographical location with a city and country.

### 2. WeatherData
- **Attributes**: `temperature`, `humidity`, `windSpeed`, `location`
- **Methods**:
  - `getTemperature()`: Returns the temperature.
  - `getHumidity()`: Returns the humidity.
  - `getWindSpeed()`: Returns the wind speed.
  - `getLocation()`: Returns the location.
  - `toString()`: Returns a string representation of the weather data.
```java
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
```
- **Description**: This class encapsulates the weather data for a specific location.

### 3. WeatherApiClient
- **Attributes**: `API_KEY`, `BASE_URL`
- **Methods**:
  - `fetchWeatherData(Location location)`: Fetches weather data for the given location from the OpenWeatherMap API.
- **Libraries Used**:
  - `java.net.HttpURLConnection`
  - `java.net.URL`
  - `java.io.BufferedReader`
  - `java.io.InputStreamReader`
  - `com.google.gson.JsonObject`
  - `com.google.gson.JsonParser`
```java
class WeatherApiClient {
    private static final String API_KEY = "89c7d050a502a21d982b9d621f542214";
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
```
- **Description**: This class handles the communication with the OpenWeatherMap API to fetch weather data.

### 4. UserInterface
- **Attributes**: `weatherApiClient`, `scanner`, `TERMINAL_WIDTH`
- **Methods**:
  - `start()`: Starts the user interface loop.
  - `viewCurrentWeather()`: Prompts the user to enter a location and displays the current weather data.
  - `displayWelcomeMessage()`: Displays a welcome message.
  - `displayGoodbyeMessage()`: Displays a goodbye message.
- **Libraries Used**:
  - `java.util.Scanner`
```java
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
```
- **Description**: This class handles the user interface, allowing the user to interact with the weather monitoring system.

### 5. WeatherMonitoringSystem
- **Methods**:
  - `main(String[] args)`: The main method that initializes and starts the user interface.
```java
public class WeatherMonitoringSystem {
    public static void main(String[] args) {
        UserInterface ui = new UserInterface();
        ui.start();
    }
}
```
- **Description**: This is the entry point of the application.

## How to Run This Code on Visual Studio Code

### Step 1: Install Java Development Kit (JDK)
1. Download and install the latest JDK from the [Oracle website](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) or [OpenJDK](https://jdk.java.net/).
2. After installation, set the `JAVA_HOME` environment variable to the JDK installation path and add the `bin` directory to your system's `PATH`.

### Step 2: Install Visual Studio Code
1. Download and install Visual Studio Code from the [official website](https://code.visualstudio.com/).

### Step 3: Install Java Extension Pack in VS Code
1. Open VS Code.
2. Go to the Extensions view by clicking on the Extensions icon in the Activity Bar on the side of the window or by pressing `Ctrl+Shift+X`.
3. Search for "Java Extension Pack" and install it. This extension pack includes essential extensions for Java development, such as:
   - Language Support for Java(TM) by Red Hat
   - Debugger for Java
   - Java Test Runner
   - Maven for Java
   - Visual Studio IntelliCode

### Step 4: Set Up Your Project
1. Create a new folder for your project.
2. Open the folder in VS Code by going to `File` > `Open Folder...` and selecting your project folder.
3. Inside the project folder, create the following file structure:
```
weather-monitoring-system/
├── src/
│ └── WeatherMonitoringSystem.java
├── lib/
└── README.md
```
4. Place your Java code in `src/WeatherMonitoringSystem.java`.

### Step 5: Add External Libraries
1. Download the Gson library (used in your code) from [Maven Central](https://repo1.maven.org/maven2/com/google/code/gson/gson/2.8.6/gson-2.8.6.jar) and place it in the `lib` folder.
2. Update your Java code to include the Gson library by specifying the classpath during compilation and runtime.

### Step 6: Configure and Run Your Project
1. Create a `launch.json` file for debugging configurations.
2. Go to the Debug view by clicking on the Debug icon in the Activity Bar or by pressing `Ctrl+Shift+D`.
3. Click on the gear icon to open the `launch.json` file.
4. Add the following configuration to `launch.json`:
```json
{
 "version": "0.2.0",
 "configurations": [
   {
     "type": "java",
     "name": "Launch WeatherMonitoringSystem",
     "request": "launch",
     "mainClass": "WeatherMonitoringSystem",
     "classPaths": [
       "src",
       "lib/gson-2.8.6.jar"
     ]
   }
 ]
}
```

### Step 7: Run Your Program
1. Go to the Terminal view by clicking on the Terminal icon in the Activity Bar or by pressing `Ctrl+``.
2. Compile your Java program using the following command:
```sh
javac -cp ".;lib/gson-2.8.6.jar" -d bin src/*.java
```
3. Run your Java program using the following command:
```sh
java -cp ".;lib/gson-2.8.6.jar;bin" WeatherMonitoringSystem
```
Alternatively, you can run and debug your program using the Debug view:
1. Go to the Debug view.
2. Select `Launch WeatherMonitoringSystem` configuration.
3. Click on the green play button to start debugging.

## Conclusion
The Weather Monitoring System provides an easy and efficient way to check current weather conditions for any city and country. By leveraging the OpenWeatherMap API and implementing a user-friendly interface, this application serves as a practical example of integrating external APIs into a Java application. We can further improve this program by creating a graphical user interface.

**Note**: The information provided by the program may not be accurate since we are using a free API.

Here is my YouTube video link for the output of this program: [Weather Monitoring System Output](https://www.youtube.com/watch?v=h3c5NCAsJgY&ab_channel=UmerAhsan)
