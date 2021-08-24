package br.com.fiap.config;

public class PropertiesLoaderImpl {
    private static PropertiesLoader loader = new PropertiesLoader();

    /**
     * Retorna o valor das propriadades
     * @param key Armazena o nome da propriedade a ser retornada
     * @return
     */
    public static String getValue(String key){
        return (String)loader.getValue(key);
    }
}
