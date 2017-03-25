package br.com.wiser.features.conversa;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.LinkedList;
import java.util.List;

import br.com.wiser.features.mensagem.MensagemDAO;

/**
 * Created by Jefferson on 16/03/2017.
 */
public class ConversaDAO extends SQLiteOpenHelper {
    private final static String TABELA = "Conversa";
    private MensagemDAO mensagemDAO;

    public ConversaDAO(Context context) {
        super(context, TABELA, null, 1);
        mensagemDAO = new MensagemDAO(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql =
                "CREATE TABLE " + TABELA + " (" +
                "    id INTEGER PRIMARY KEY NOT NULL," +
                "    id_server BIGINT NOT NULL," +
                "    destinatario BIGINT NOT NULL" +
                ")";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long insert(Conversa conversa) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues valores = new ContentValues();

        long id = 0;

        try {
            valores.put("id", conversa.getId());
            valores.put("destinatario", conversa.getDestinatario());

            id = db.insertOrThrow(TABELA, null, valores);
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
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT MAX(id) AS id FROM " + TABELA;

        long max = 0;

        Cursor c = db.rawQuery(sql, null);
        if (c.moveToNext()) {
            max = c.getLong(c.getColumnIndex("id"));
        }

        return max;
    }

    public List<Conversa> get() {
        SQLiteDatabase db = getReadableDatabase();
        String sql =
                "SELECT C.id AS idConversa, C.id_server AS idServerConversa, C.destinatario, M.*" +
                " FROM " + TABELA + " AS C" +
                " LEFT JOIN " + mensagemDAO.TABELA + " AS M" +
                " ON M.conversa = C.conversa";

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
                conversa.getMensagens().add(mensagemDAO.tratarMensagem(c));
            }
        }

        return listaConversa;
    }
}
