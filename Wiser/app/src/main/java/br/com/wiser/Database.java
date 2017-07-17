package br.com.wiser;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jefferson on 05/07/2017.
 */
public class Database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Wiser";
    private static final int DATABASE_VERSION = 1;

    public static final String CONVERSAS = "Conversas";
    private static final String CREATE_CONVERSAS =
            "CREATE TABLE " + CONVERSAS + " (" +
            "    id INTEGER PRIMARY KEY NOT NULL," +
            "    destinatario BIGINT NOT NULL" +
            ")";

    public static final String MENSAGENS = "Mensagens";
    private static final String CREATE_MENSAGENS =
            "CREATE TABLE " + MENSAGENS + " (" +
            "    id INTEGER PRIMARY KEY," +
            "    conversa BIGINT NOT NULL," +
            "    estado INTEGER NOT NULL," +
            "    usuario BIGINT NOT NULL," +
            "    data DATETIME NOT NULL," +
            "    mensagem TEXT NOT NULL," +
            "    lida INTEGER NOT NULL" +
            ")";

    public static final String LOGIN = "Login";
    private static final String CREATE_LOGIN =
            "CREATE TABLE " + LOGIN + " (" +
            "    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            "    usuario BIGINT NOT NULL," +
            "    data DATETIME NOT NULL," +
            "    logado INTEGER NOT NULL" +
            ");";

    public static final String USUARIOS = "Usuarios";
    private static final String CREATE_USUARIO =
            "CREATE TABLE " + USUARIOS + " (" +
            "    id INTEGER PRIMARY KEY NOT NULL," +
            "    nome TEXT NOT NULL," +
            "    primeiroNome TEXT NOT NULL," +
            "    dataNascimento DATE NOT NULL," +
            "    urlFotoPerfil TEXT NOT NULL DEFAULT('')," +
            "    facebookID TEXT NOT NULL," +
            "    accessToken TEXT NOT NULL," +
            "    dataUltimoAcesso DATETIME NOT NULL," +
            "    contaAtiva BIT NOT NULL," +
            "    latitude REAL NOT NULL," +
            "    longitude REAL NOT NULL," +
            "    idioma INTEGER NOT NULL," +
            "    fluencia INTEGER NOT NULL," +
            "    status TEXT NOT NULL" +
            ")";

    public static final String CONTATOS = "Contatos";
    public static final String CREATE_CONTATOS =
            "CREATE TABLE " + CONTATOS + " (" +
            "    id INTEGER PRIMARY KEY NOT NULL," +
            "    usuario INTEGER NOT NULL" +
            ")";

    public static final String PARAMETROS = "Parametros";
    public static final String CREATE_PARAMETROS =
            "CREATE TABLE " + PARAMETROS + " (" +
            "    id INTEGER PRIMARY KEY NOT NULL," +
            "    descricao TEXT NOT NULL," +
            "    parametro TEXT NOT NULL" +
            ")";

    private static Database instance;

    public static synchronized Database getInstance() {
        if (instance == null)
            instance = new Database();
        return instance;
    }

    private Database() {
        super(WiserApplication.getAppContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CONVERSAS);
        db.execSQL(CREATE_MENSAGENS);
        db.execSQL(CREATE_LOGIN);
        db.execSQL(CREATE_USUARIO);
        db.execSQL(CREATE_CONTATOS);
        db.execSQL(CREATE_PARAMETROS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
