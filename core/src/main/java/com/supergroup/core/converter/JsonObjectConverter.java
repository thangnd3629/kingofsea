package com.supergroup.core.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.json.JSONException;
import org.json.JSONObject;

@Converter
public class JsonObjectConverter implements AttributeConverter<JSONObject, String> {
    @Override
    public String convertToDatabaseColumn(JSONObject jsonData) {
        String json;
        try{
            json = jsonData.toString();
        }
        catch (NullPointerException ex)
        {
            json = "";
        }
        return json;
    }

    @Override
    public JSONObject convertToEntityAttribute(String jsonDataAsJson) {
        JSONObject jsonData;
        try {
            jsonData = new JSONObject(jsonDataAsJson);
        } catch (JSONException ex) {
            jsonData = null;
        }
        return jsonData;
    }
}