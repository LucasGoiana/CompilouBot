package br.com.fiap.domains;

/**
 * @author  Guilherme Cristiano
 * Classe responsável por representar um clima
 */

public class Weather {


    private String name;
    private Double temperature;
    private Double humidity;
    private String main;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

}
