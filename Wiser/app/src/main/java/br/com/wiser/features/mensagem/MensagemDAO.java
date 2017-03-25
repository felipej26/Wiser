package br.com.wiser.features.mensagem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.greenrobot.eventbus.EventBus;

import java.sql.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import br.com.wiser.utils.UtilsDate;

/**
 * Created by Jefferson on 16/03/2017.
 */

public class MensagemDAO extends SQLiteOpenHelper {

    public final static String TABELA = "Mensagens";

    public MensagemDAO(Context context) {
        super(context, TABELA, null, 2);
        // realiza uma busca para que a Tabela seja criada
        getMaxId();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql =
                "CREATE TABLE " + TABELA + " (" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    id_server BIGINT NOT NULL," +
                "    conversa BIGINT NOT NULL," +
                "    estado INTEGER NOT NULL," +
                "    usuario BIGINT NOT NULL," +
                "    data DATETIME NOT NULL," +
                "    mensagem TEXT NOT NULL," +
                "    lida INTEGER NOT NULL" +
                ")";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TABELA);
        onCreate(db);
    }

    /***
     * @param mensagem objeto Mensagem
     * @return id da Mensagem
     */
    public long insert(Mensagem mensagem) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues valores = new ContentValues();

        long id = 0;

        try {
            valores.put("id_server", mensagem.getId());
            valores.put("conversa", mensagem.getConversa());
            valores.put("estado", mensagem.getEstado().getEstado());
            valores.put("usuario", mensagem.getUsuario());
            valores.put("data", UtilsDate.formatDate(mensagem.getData(), UtilsDate.YYYYMMDD_HHMMSS));
            valores.put("mensagem", mensagem.getMensagem());
            valores.put("lida", mensagem.isLida());

            id = db.insertOrThrow(TABELA, null, valores);

            EventBus.getDefault().post(mensagem);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return id;
    }

    public void insert(Map<Long, List<Mensagem>> mapMensagens) {
        for (long conversa : mapMensagens.keySet()) {
            for (Mensagem mensagem : mapMensagens.get(conversa)) {
                insert(mensagem);
            }
        }
    }

    public void update(Mensagem mensagem) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues valores = new ContentValues();

        valores.put("id_server", mensagem.getIdServer());
        valores.put("estato", mensagem.getEstado().getEstado());
        valores.put("mensagem", mensagem.getMensagem());
        valores.put("lida", mensagem.isLida());

        db.update(TABELA, valores, "id = ?", new String[] {String.valueOf(mensagem.getId())});
    }

    public List<Mensagem> get(long conversa) {
        SQLiteDatabase db = getReadableDatabase();
        String sql =
                "SELECT * FROM " + TABELA +
                " WHERE conversa = " + conversa;

        List<Mensagem> listaMensagens = new LinkedList<>();

        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {
            listaMensagens.add(tratarMensagem(c));
        }

        return listaMensagens;
    }

    public Mensagem tratarMensagem(Cursor c) {
        Mensagem mensagem = new Mensagem();
        mensagem.setId(c.getLong(c.getColumnIndex("id")));
        mensagem.setEstado(c.getInt(c.getColumnIndex("estado")));
        mensagem.setUsuario(c.getLong(c.getColumnIndex("usuario")));
        mensagem.setData(Date.valueOf(c.getString(c.getColumnIndex("data"))));
        mensagem.setMensagem(c.getString(c.getColumnIndex("mensagem")));
        mensagem.setLida(c.getInt(c.getColumnIndex("lida")) == 1);

        return mensagem;
    }

    public long getMaxId() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT MAX(id) AS id FROM " + TABELA;

        long max = 0;

        Cursor c = db.rawQuery(sql, null);
        if (c.moveToNext()) {
            max = c.getLong(c.getColumnIndex("id"));
        }

        return max;
    }

    public long getMaxIdServer() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT MAX(id_server) AS id FROM " + TABELA;

        long max = 0;

        Cursor c = db.rawQuery(sql, null);
        if (c.moveToNext()) {
            max = c.getLong(c.getColumnIndex("id"));
        }

        return max;
    }

    public long getMaxIdServer(long conversa) {
        SQLiteDatabase db = getReadableDatabase();
        String sql =
                "SELECT MAX(id_server) AS id FROM " + TABELA +
                " WHERE conversa = " + conversa;

        long max = 0;

        Cursor c = db.rawQuery(sql, null);
        if (c.moveToNext()) {
            max = c.getLong(c.getColumnIndex("id"));
        }

        return max;
    }
}
