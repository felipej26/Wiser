package br.com.wiser.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.lang.reflect.Type;

import br.com.wiser.R;
import br.com.wiser.WiserApplication;

/**
 * Created by Jefferson on 13/09/2017.
 */

public class ArmazenarCache {

    private Type typeParameter;

    public ArmazenarCache() {

    }

    public ArmazenarCache(Type typeParameter) {
        this.typeParameter = typeParameter;
    }

    public void salvarObjeto(Object object, int preferencesKey) {
        Context context = WiserApplication.getAppContext();

        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preferences_file_key), Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(preferencesKey), new Gson().toJson(object));
        editor.commit();
    }

    public Object getObjeto(int preferencesKey) {
        Context context = WiserApplication.getAppContext();

        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preferences_file_key), Context.MODE_PRIVATE
        );

        return new Gson().fromJson(sharedPreferences.getString(context.getString(preferencesKey), null), typeParameter);
    }
}
