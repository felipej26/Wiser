package br.com.wiser.features.mensagem;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import br.com.wiser.Database;
import br.com.wiser.utils.UtilsDate;

/**
 * Created by Jefferson on 16/03/2017.
 */
public class MensagemDAO {

    private Database database;

    public MensagemDAO() {
        database = Database.getInstance();
    }

    public void insert(Mensagem mensagem) {
        SQLiteDatabase sql = database.getWritableDatabase();
        ContentValues valores = new ContentValues();

        try {
            valores.put("id", mensagem.getId());
            valores.put("conversa", mensagem.getConversa());
            valores.put("estado", mensagem.getEstado().getEstado());
            valores.put("usuario", mensagem.getUsuario());
            valores.put("data", UtilsDate.formatDate(mensagem.getData(), UtilsDate.YYYYMMDD_HHMMSS));
            valores.put("mensagem", mensagem.getMensagem());
            valores.put("lida", mensagem.isLida());

            sql.insertOrThrow(database.MENSAGENS, null, valores);

            EventBus.getDefault().post(mensagem);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void insert(Map<Long, List<Mensagem>> mapMensagens) {
        for (long conversa : mapMensagens.keySet()) {
            for (Mensagem mensagem : mapMensagens.get(conversa)) {
                insert(mensagem);
            }
        }
    }

    public void update(Mensagem mensagem) {
        SQLiteDatabase sql = database.getWritableDatabase();
        ContentValues valores = new ContentValues();

        valores.put("estato", mensagem.getEstado().getEstado());
        valores.put("mensagem", mensagem.getMensagem());
        valores.put("lida", mensagem.isLida());

        sql.update(database.MENSAGENS, valores, "id = ?", new String[] {String.valueOf(mensagem.getId())});
    }

    public List<Mensagem> get(long conversa) throws ParseException {
        SQLiteDatabase db = database.getReadableDatabase();

        String sql =
                "SELECT * FROM " + database.MENSAGENS +
                " WHERE conversa = " + conversa;

        List<Mensagem> listaMensagens = new LinkedList<>();

        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {
            listaMensagens.add(tratarMensagem(c));
        }

        return listaMensagens;
    }

    public static Mensagem tratarMensagem(Cursor c) throws ParseException {
        Mensagem mensagem = new Mensagem();
        mensagem.setId(c.getLong(c.getColumnIndex("id")));
        mensagem.setEstado(c.getInt(c.getColumnIndex("estado")));
        mensagem.setUsuario(c.getLong(c.getColumnIndex("usuario")));
        mensagem.setData(UtilsDate.parseDate(c.getString(c.getColumnIndex("data")), "yyyy-mm-dd HH:mm:ss"));
        mensagem.setMensagem(c.getString(c.getColumnIndex("mensagem")));
        mensagem.setLida(c.getInt(c.getColumnIndex("lida")) == 1);

        return mensagem;
    }

    public long getMaxId() {
        SQLiteDatabase db = database.getReadableDatabase();
        String sql = "SELECT MAX(id) AS id FROM " + database.MENSAGENS;

        long max = 0;

        Cursor c = db.rawQuery(sql, null);
        if (c.moveToNext()) {
            max = c.getLong(c.getColumnIndex("id"));
        }

        return max;
    }

    public long getMaxId(long conversa) {
        SQLiteDatabase db = database.getReadableDatabase();
        String sql =
                "SELECT MAX(id) AS id FROM " + database.MENSAGENS +
                " WHERE conversa = " + conversa;

        long max = 0;

        Cursor c = db.rawQuery(sql, null);
        if (c.moveToNext()) {
            max = c.getLong(c.getColumnIndex("id"));
        }

        return max;
    }
}