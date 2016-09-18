package br.com.wiser.services;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.LinkedList;

import br.com.wiser.business.chat.conversas.ConversasDAO;

/**
 * Created by Jefferson on 18/09/2016.
 */
public class CarregarConversasService extends Service {

    ConversasDAO objConversas;
    LinkedList<ConversasDAO> listaConversas;

    @Override
    public void onCreate() {
        super.onCreate();

        objConversas = new ConversasDAO();
        listaConversas = new LinkedList<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    objConversas.carregarGeral(CarregarConversasService.this, listaConversas);

                    EventBus.getDefault().post(listaConversas);

                    try {
                        Thread.sleep(3000);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
