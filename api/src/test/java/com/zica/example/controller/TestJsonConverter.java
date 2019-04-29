package com.zica.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TestJsonConverter {
    public static <T> List<T> jsonToList(String json, Class<T> listMemberClass)
            throws IOException, ClassNotFoundException {
        ObjectMapper mapper = new ObjectMapper();
        Class<T[]> arrayClass = (Class<T[]>) Class.forName("[L" + listMemberClass.getName() + ";");
        T[] objects = mapper.readValue(json, arrayClass);
        return Arrays.asList(objects);
    }

    public static <T> T jsonToObject(String json, Class<T> classOf) throws IOException {
        var mapper = new ObjectMapper();
        return mapper.readValue(json, classOf);
    }

    public static String objectToJson(Object o) throws IOException {
        var mapper = new ObjectMapper();
        return mapper.writeValueAsString(o);
    }
}
