package br.com.wiser.features.conversa;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import br.com.wiser.Database;
import br.com.wiser.features.mensagem.MensagemDAO;
import br.com.wiser.features.usuario.UsuarioDAO;
import br.com.wiser.features.usuario.UsuarioPresenter;

/**
 * Created by Jefferson on 16/03/2017.
 */
public class ConversaDAO {

    private Database database;

    public ConversaDAO() {
        database = Database.getInstance();
    }

    public void insert(Conversa conversa) {
        SQLiteDatabase db = database.getWritableDatabase();
        ContentValues valores = new ContentValues();

        try {
            valores.put("id", conversa.getId());
            valores.put("destinatario", conversa.getIdDestinatario());

            db.insertOrThrow(database.CONVERSAS, null, valores);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
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
        final UsuarioDAO usuarioDAO = new UsuarioDAO();
        UsuarioPresenter usuarioPresenter = new UsuarioPresenter();

        String sql =
                "SELECT C.id AS idConversa, C.destinatario, M.*" +
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
                conversa.setIdDestinatario(c.getLong(c.getColumnIndex("destinatario")));
                conversa.setUsuario(usuarioDAO.getById(c.getLong(c.getColumnIndex("destinatario"))));

                listaConversa.add(conversa);
            }

            if (c.getLong(c.getColumnIndex("id")) != 0) {
                conversa.getMensagens().add(MensagemDAO.tratarMensagem(c));
            }
        }

        return listaConversa;
    }
}
