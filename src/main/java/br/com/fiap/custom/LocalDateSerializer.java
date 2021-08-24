package br.com.fiap.custom;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateSerializer implements JsonSerializer<LocalDate> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    /**
     * Metódo para transformar Localdate para Json
     */
    public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
        return null;
    }
}
