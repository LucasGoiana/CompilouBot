package br.com.fiap.config;

import com.google.common.io.Resources;

import java.io.*;
import java.util.Properties;

public class PropertiesLoader {

    private Properties props;
    private String NAME_FILE_PROPERTIES = "application.properties";

    /**
     * Carregar os arquivo de Properties
     */
    protected PropertiesLoader() {
        props = new Properties();

        try{
            InputStream in = Resources.getResource(NAME_FILE_PROPERTIES).openStream();
            props.load(in);
            in.close();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retorna o valor das propriadades
     * @param key Armazena o nome da propriedade a ser retornada
     * @return
     */
    protected String getValue(String key){
        return (String)props.getProperty(key);
    }
}

