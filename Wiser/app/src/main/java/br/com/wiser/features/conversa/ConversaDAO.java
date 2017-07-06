package br.com.wiser.features.conversa;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import br.com.wiser.Database;
import br.com.wiser.features.mensagem.MensagemDAO;

/**
 * Created by Jefferson on 16/03/2017.
 */
public class ConversaDAO {

    private Database database;

    public ConversaDAO(Context context) {
        database = Database.getInstance(context);
    }

    public long insert(Conversa conversa) {
        SQLiteDatabase db = database.getWritableDatabase();
        ContentValues valores = new ContentValues();

        long id = 0;

        try {
            valores.put("id", conversa.getId());
            valores.put("id_server", conversa.getIdServer());
            valores.put("destinatario", conversa.getDestinatario());

            id = db.insertOrThrow(database.CONVERSAS, null, valores);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return id;
    }

    public void insert(List<Conversa> listaConversas) {
        for (Conversa conversa : listaConversas) {
            insert(conversa);
        }
    }

    public long getMax() {
        SQLiteDatabase db = database.getReadableDatabase();
        String sql = "SELECT MAX(id) AS id FROM " + database.CONVERSAS;

        long max = 0;

        Cursor c = db.rawQuery(sql, null);
        if (c.moveToNext()) {
            max = c.getLong(c.getColumnIndex("id"));
        }

        return max;
    }

    public List<Conversa> get() throws ParseException {
        SQLiteDatabase db = database.getReadableDatabase();

        String sql =
                "SELECT C.id AS idConversa, C.id_server AS idServerConversa, C.destinatario, M.*" +
                " FROM " + database.CONVERSAS + " AS C" +
                " LEFT JOIN " + database.MENSAGENS + " AS M" +
                " ON M.conversa = C.id";

        List<Conversa> listaConversa = new LinkedList<>();

        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {
            Conversa conversa = null;
            for (Conversa cAux: listaConversa) {
                if (cAux.getId() == c.getLong(c.getColumnIndex("idConversa"))) {
                    conversa = cAux;
                }
            }

            if (conversa == null) {
                conversa = new Conversa();
                conversa.setId(c.getLong(c.getColumnIndex("idConversa")));
                conversa.setIdServer(c.getLong(c.getColumnIndex("idServerConversa")));
                conversa.setDestinatario(c.getLong(c.getColumnIndex("destinatario")));

                listaConversa.add(conversa);
            }

            if (c.getLong(c.getColumnIndex("id")) != 0) {
                conversa.getMensagens().add(MensagemDAO.tratarMensagem(c));
            }
        }

        return listaConversa;
    }
}
