package br.com.wiser.features.contato;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import br.com.wiser.Database;

/**
 * Created by Jefferson on 12/07/2017.
 */

public class ContatoDAO {

    private Database database;

    public ContatoDAO() {
        database = Database.getInstance();
    }

    public boolean isContato(long usuario) {
        SQLiteDatabase db = database.getReadableDatabase();
        String sql = "SELECT * FROM " + database.CONTATOS +
                " WHERE usuario = " + usuario;

        Cursor c = db.rawQuery(sql, null);
        return c.moveToNext();
    }
}
