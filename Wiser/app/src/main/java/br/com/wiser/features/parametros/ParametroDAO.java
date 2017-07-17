package br.com.wiser.features.parametros;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import br.com.wiser.Database;

/**
 * Created by Jefferson on 16/07/2017.
 */
public class ParametroDAO {

    private Database database;

    public ParametroDAO() {
        database = Database.getInstance();
    }

    public String get(CodigoParametro id) {
        SQLiteDatabase db = database.getReadableDatabase();
        String parametro = "";

        String sql = "SELECT parametro FROM " + database.PARAMETROS +
                " WHERE id = " + id.getValor();

        Cursor c = db.rawQuery(sql, null);
        if (c.moveToNext()) {
            parametro = c.getString(c.getColumnIndex("parametro"));
        }

        return parametro;
    }

    public void update(CodigoParametro codigo, String novoParametro) {
        SQLiteDatabase db = database.getWritableDatabase();

        String sql = "UPDATE " + database.PARAMETROS + " SET" +
                " parametro = '" + novoParametro + "'" +
                " WHERE id = " + codigo.getValor();

        db.execSQL(sql);
    }

    public void insert(Parametro parametro) {
        SQLiteDatabase db = database.getWritableDatabase();

        String sql = "INSERT INTO " + database.PARAMETROS + " VALUES (" +
                parametro.getId().getValor() + ", '" + parametro.getDescricao() + "', " +
                "'" + parametro.getParametro() + "'" +
                ")";

        db.execSQL(sql);
    }

    public boolean exist(CodigoParametro codigo) {
        SQLiteDatabase db = database.getReadableDatabase();

        String sql = "SELECT * FROM " + database.PARAMETROS +
                " WHERE ID = " + codigo.getValor();

        Cursor c = db.rawQuery(sql, null);
        return c.moveToNext();
    }
}
