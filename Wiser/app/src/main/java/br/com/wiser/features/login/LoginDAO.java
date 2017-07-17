package br.com.wiser.features.login;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Date;

import br.com.wiser.Database;
import br.com.wiser.Sistema;
import br.com.wiser.utils.UtilsDate;

/**
 * Created by Jefferson on 27/02/2017.
 */
public class LoginDAO {

    private Database database;

    public LoginDAO() {
        database = Database.getInstance();
    }

    public void logarUsuario() {
        SQLiteDatabase db = database.getWritableDatabase();

        try {
            ContentValues valores = new ContentValues();
            valores.put("logado", 0);

            db.update(database.LOGIN, valores, "logado = ?", new String[]{"1"});

            valores = new ContentValues();
            valores.put("usuario", Sistema.getUsuario().getId());
            valores.put("data", UtilsDate.formatDate(new Date(), UtilsDate.YYYYMMDD_HHMMSS));
            valores.put("logado", 1);

            db.insertOrThrow(database.LOGIN, null, valores);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deslogarUsuario() {
        SQLiteDatabase db = database.getWritableDatabase();

        String sql = "UPDATE " + database.LOGIN + " SET logado = 0 WHERE logado = 1;";
        db.execSQL(sql);
    }

    public void getDados() {
        SQLiteDatabase db = database.getReadableDatabase();
        String sql = "SELECT * FROM " + database.LOGIN;

        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {
            Log.i("Dados", "id: " + c.getInt(c.getColumnIndex("id")) +
                    ", usuario: " + c.getLong(c.getColumnIndex("usuario")) +
                    ", data: " + c.getString(c.getColumnIndex("data")) +
                    ", logado: " + c.getInt(c.getColumnIndex("logado")));
        }
        c.close();
    }

    public Long getLogado() {
        getDados();

        SQLiteDatabase db = database.getReadableDatabase();
        String sql = "SELECT * FROM " + database.LOGIN + " WHERE logado = 1";

        long usuario = 0;

        Cursor c = db.rawQuery(sql, null);
        if (c.moveToNext()) {
            usuario = c.getLong(c.getColumnIndex("usuario"));
        }
        c.close();

        return usuario;
    }
}