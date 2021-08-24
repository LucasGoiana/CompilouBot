package br.com.fiap.clients;

/**
 * @author  Guilherme Cristiano
 * <a href='https://openweathermap.org/api'>Link Documentação</a>
 */

import br.com.fiap.config.PropertiesLoaderImpl;
import br.com.fiap.domains.Weather;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class ApiClientWeather {

    /**
     * Metódo responsável por o clima de uma cidade.
     * @param message Cidade enviado pelo usuário
     * @param weather
     * @return Retorna uma string para Telegram com os dados do clima.
     * @throws IOException
     */
    public static String getWeather(String message, Weather weather) throws IOException {
        URL url = new URL(PropertiesLoaderImpl.getValue("api.weather.url") + "?q=" + message + "&lang=" + PropertiesLoaderImpl.getValue("api.weather.lang") + "&units=metric&appid=" + PropertiesLoaderImpl.getValue("api.weather.appid"));

        Scanner in = new Scanner((InputStream) url.getContent());
        String result = "";

        while (in.hasNext()) {
            result += in.nextLine();
        }

        JSONObject object = new JSONObject(result);
        weather.setName(object.getString("name"));

        JSONObject main = object.getJSONObject("main");
        weather.setTemperature(main.getDouble("temp"));
        weather.setHumidity(main.getDouble("humidity"));

        JSONArray getArray = object.getJSONArray("weather");
        for(int i = 0; i < getArray.length(); i++) {
            JSONObject obj = getArray.getJSONObject(i);
            weather.setMain((String) obj.get("description"));
        }

        return "Localização: " + weather.getName() + ", Brazil" + "\n" +
                "Temperatura: " + weather.getTemperature() + " °C" + "\n" +
                "Humidade: " + weather.getHumidity() + "%" + "\n" +
                "Clima: " + weather.getMain();
    }

}