package projekt;

import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.File;
import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.ClientProtocolException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;


class Main {
    public static void main(String[] args) {
        String jsonFilePath = "src/main/resources/miasta.json";
        String chosenCity;
        Map<String, Coordinates> cityCoordinatesMap = CityCoordinatesLoader.loadCityCoordinates(jsonFilePath);
        List<String> miasta = new ArrayList<>(cityCoordinatesMap.keySet());
        // W przyszłości być może obsługa wyjątków związanych z odczytem

        Set<String> formatyZapisu = new HashSet<>(Arrays.asList("PDF", "JSON", "XML"));
        Scanner scan = new Scanner(System.in);

        while (true) {
            System.out.println("P-Podaj miasto, Z-Zakończ");
            String opcja1 = scan.nextLine();

            switch (opcja1) {
                case "P":
                    chosenCity =  wybierzMiastoIWyswietlDane(scan, miasta);
                    WeatherService weatherService = new WeatherService();
                    Coordinates coords = cityCoordinatesMap.get(chosenCity);
                    String xmlString = weatherService.getWeatherData(String.valueOf(coords.getLat()), String.valueOf(coords.getLon()));
                    try {
                        WeatherData weatherData = XmlParser.parseWeatherData(xmlString);
                        System.out.println("Temperatura: " + weatherData.getTemperature().getValue());
                        System.out.println("Ciśnienie: " + weatherData.getPressure().getValue());
                        System.out.println("Wilgotność: " + weatherData.getHumidity().getValue());
                    } catch (JAXBException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Dane pogodowe dla " + chosenCity + ": " + xmlString);
                    break;
                case "Z":
                    if (wybierzFormatIZapisz(scan, formatyZapisu)) {
                        return;
                    }
                    break;
                default:
                    System.out.println("Podano niedostępną opcję!"); // Można tutaj rzucić wyjątek, jeśli to konieczne
                    break;
            }
        }
    }

    private static String wybierzMiastoIWyswietlDane(Scanner scan, List<String> miasta) {
        String miasto;
        while (true) {
            System.out.println("Dostępne miasta: " + String.join(", ", miasta));
            miasto = scan.nextLine();
            if (miasta.contains(miasto)) {
                break;
            } else {
                System.out.println("Nie można wyświetlić danych dla podanego miasta.");
            }
        }
        return miasto;
    }

    private static boolean wybierzFormatIZapisz(Scanner scan, Set<String> formatyZapisu) {
        while (true) {
            System.out.print("Podaj format zapisu [P-PDF J-JSON X-XML]: ");
            String opcja2 = scan.nextLine();
            if (formatyZapisu.contains(opcja2)) {
                System.out.println("Wykonywanie instrukcji zapisu w formacie " + opcja2);
                // Zapisz dane w wybranym formacie
                return true;
            } else {
                System.out.println("Podano niewłaściwy format!");
            }
        }
    }
}

