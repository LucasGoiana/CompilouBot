package br.com.fiap.custom;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateDeserializer implements JsonDeserializer<LocalDate> {


    @Override
    /**
     * Metódo para tratar as informações enviadas no total DATE, transformando o Json para Localdate
     */
    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if(json.isJsonNull() || json.getAsString().equals("") ) return null;
        return LocalDate.parse(json.getAsString(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
