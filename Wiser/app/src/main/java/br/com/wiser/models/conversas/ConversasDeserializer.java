package br.com.wiser.models.conversas;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by Jefferson on 26/01/2017.
 */
public class ConversasDeserializer implements JsonDeserializer<Conversas> {

    @Override
    public Conversas deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonElement content = json.getAsJsonObject().get("");
        return new Gson().fromJson(content, Conversas.class);
    }
}
