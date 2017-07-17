package br.com.wiser.features.usuario;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;

import br.com.wiser.Database;
import br.com.wiser.utils.UtilsDate;

/**
 * Created by Jefferson on 07/07/2017.
 */

public class UsuarioDAO {

    private Database database;

    public UsuarioDAO() {
        database = Database.getInstance();
    }

    public void insert(Usuario usuario) {
        SQLiteDatabase db = database.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("id", usuario.getId());
        values.put("nome", usuario.getNome());
        values.put("primeiroNome", usuario.getPrimeiroNome());
        values.put("dataNascimento", UtilsDate.formatDate(usuario.getDataNascimento(), UtilsDate.YYYYMMDD_HHMMSS));
        values.put("urlFotoPerfil", usuario.getUrlFotoPerfil());
        values.put("facebookID", usuario.getFacebookID());
        values.put("accessToken", usuario.getAccessToken());
        values.put("dataUltimoAcesso", UtilsDate.formatDate(usuario.getDataUltimoAcesso(), UtilsDate.YYYYMMDD_HHMMSS));
        values.put("contaAtiva", usuario.isContaAtiva());
        values.put("latitude", usuario.getLatitude());
        values.put("longitude", usuario.getLongitude());
        values.put("idioma", usuario.getIdioma());
        values.put("fluencia", usuario.getFluencia());
        values.put("status", usuario.getStatus());

        db.insertOrThrow(database.USUARIOS, null, values);
    }

    public Usuario getById(long id) {
        SQLiteDatabase db = database.getReadableDatabase();
        Usuario usuario = null;

        String sql =
                "SELECT * FROM " + database.USUARIOS +
                " WHERE id = " + id;

        Cursor c = db.rawQuery(sql, null);
        if (c.moveToNext()) {
            usuario = getUsuarioOfCursor(c);
        }

        return usuario;
    }

    /*
    public void getById(Set<Long> idsUsuarios, final ICallback callback) throws ParseException {
        SQLiteDatabase db = database.getReadableDatabase();
        Set<Usuario> usuarios = new HashSet<>();
        Set<Long> usuariosNaoCarregados = new HashSet<>();

        usuariosNaoCarregados.addAll(idsUsuarios);

        if (usuarios.size() > 0) {
            String sql =
                    "SELECT * FROM " + database.USUARIOS +
                    " WHERE id IN (" + idsUsuarios.toString() + ")";

            Cursor c = db.rawQuery(sql, null);
            while (c.moveToNext()) {
                usuarios.add(getUsuarioOfCursor(c));
                usuariosNaoCarregados.remove(c.getLong(c.getColumnIndex("id")));
            }
        }

        if (usuariosNaoCarregados.size() > 0) {
            getInServer(usuariosNaoCarregados, new ICallback() {
                @Override
                public void onFinished(Set<Usuario> usuarios) {
                    callback.onFinished(usuarios);
                }
            });
        }
        else {
            callback.onFinished(usuarios);
        }
    }
    */

    private static Usuario getUsuarioOfCursor(Cursor c) {
        Usuario usuario = new Usuario();

        usuario.setId(c.getLong(c.getColumnIndex("id")));
        usuario.setNome(c.getString(c.getColumnIndex("nome")));
        usuario.setPrimeiroNome(c.getString(c.getColumnIndex("primeiroNome")));
        usuario.setUrlFotoPerfil(c.getString(c.getColumnIndex("urlFotoPerfil")));
        usuario.setFacebookID(c.getString(c.getColumnIndex("facebookID")));
        usuario.setAccessToken(c.getString(c.getColumnIndex("accessToken")));
        usuario.setContaAtiva(c.getInt(c.getColumnIndex("contaAtiva")) == 1);
        usuario.setLatitude(c.getFloat(c.getColumnIndex("latitude")));
        usuario.setLongitude(c.getFloat(c.getColumnIndex("longitude")));
        usuario.setIdioma(c.getInt(c.getColumnIndex("idioma")));
        usuario.setFluencia(c.getInt(c.getColumnIndex("fluencia")));
        usuario.setStatus(c.getString(c.getColumnIndex("status")));

        try {
            usuario.setDataNascimento(UtilsDate.parseDate(c.getString(c.getColumnIndex("dataNascimento")), "yyyy-mm-dd HH:mm:ss"));
            usuario.setDataUltimoAcesso(UtilsDate.parseDate(c.getString(c.getColumnIndex("dataUltimoAcesso")), "yyyy-mm-dd HH:mm:ss"));
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        return usuario;
    }

    public boolean exist(long id) {
        SQLiteDatabase db = database.getReadableDatabase();

        String sql = "SELECT * FROM " + database.USUARIOS +
                " WHERE id = " + id;

        Cursor c = db.rawQuery(sql, null);
        return c.moveToNext();
    }
}
