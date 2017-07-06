package br.com.wiser;

import android.content.Context;
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
            "    id_server BIGINT NOT NULL," +
            "    destinatario BIGINT NOT NULL" +
            ")";

    public static final String MENSAGENS = "Mensagens";
    private static final String CREATE_MENSAGENS =
            "CREATE TABLE " + MENSAGENS + " (" +
            "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    id_server BIGINT NOT NULL," +
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

    private static Database instance;

    public static synchronized Database getInstance(Context context) {
        if (instance == null)
            instance = new Database(context);
        return  instance;
    }

    private Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CONVERSAS);
        db.execSQL(CREATE_MENSAGENS);
        db.execSQL(CREATE_LOGIN);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
