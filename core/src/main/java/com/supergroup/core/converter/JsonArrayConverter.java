package com.supergroup.core.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.json.JSONArray;
import org.json.JSONException;

@Converter
public class JsonArrayConverter implements AttributeConverter<JSONArray, String> {
    @Override
    public String convertToDatabaseColumn(JSONArray jsonArray) {
        try{
            return jsonArray.toString();
        }catch (NullPointerException e){
            return "";
        }

    }


    @Override
    public JSONArray convertToEntityAttribute(String jsonString) {
        try {
            return jsonString == null ? new JSONArray() : new JSONArray(jsonString);
        } catch (JSONException e) {
            return null;
        }
    }


}
