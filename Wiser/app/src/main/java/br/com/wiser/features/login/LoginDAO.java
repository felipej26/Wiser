package br.com.wiser.features.login;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;

import br.com.wiser.Sistema;
import br.com.wiser.utils.UtilsDate;

/**
 * Created by Jefferson on 27/02/2017.
 */
public class LoginDAO extends SQLiteOpenHelper {

    private final static String TABELA = "Usuario_Logado";

    public LoginDAO(Context context) {
        super(context, TABELA, null, 5);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql =
                "CREATE TABLE " + TABELA + " (" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "    usuario BIGINT NOT NULL," +
                "    data DATETIME NOT NULL," +
                "    logado INTEGER NOT NULL" +
                ");";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABELA + ";";
        db.execSQL(sql);
        onCreate(db);

        /*
        switch (oldVersion) {
            case 5:

        }
        */
    }

    public void logarUsuario() {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues valores = new ContentValues();
            valores.put("logado", 0);

            db.update(TABELA, valores, "logado = ?", new String[]{"1"});

            valores = new ContentValues();
            valores.put("usuario", Sistema.getUsuario().getUserID());
            valores.put("data", UtilsDate.formatDate(new Date(), UtilsDate.YYYYMMDD_HHMMSS));
            valores.put("logado", 1);

            db.insertOrThrow(TABELA, null, valores);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deslogarUsuario() {
        SQLiteDatabase db = getWritableDatabase();

        String sql = "UPDATE " + TABELA + " SET logado = 0 WHERE logado = 1;";
        db.execSQL(sql);
    }

    public void getDados() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + TABELA;

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

        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + TABELA + " WHERE logado = 1";

        long usuario = 0;

        Cursor c = db.rawQuery(sql, null);
        if (c.moveToNext()) {
            usuario = c.getLong(c.getColumnIndex("usuario"));
        }
        c.close();

        return usuario;
    }
}