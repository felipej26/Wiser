package br.com.wiser.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by Jefferson on 29/01/2017.
 */
public class ComboDeserializer implements JsonDeserializer<ComboBoxItem> {

    @Override
    public ComboBoxItem deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject json = jsonElement.getAsJsonObject();
        ComboBoxItem combo = new ComboBoxItem();

        if (json.has("cod_idioma")) {
            combo.setId(Integer.parseInt(json.get("cod_idioma").toString()));
        }
        else {
            combo.setId(Integer.parseInt(json.get("nivel").toString()));
        }

        combo.setDescricao(json.get("descricao").toString().replace("\"", ""));

        return combo;
    }
}
