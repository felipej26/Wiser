package br.com.wiser.services;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.LinkedList;
import java.util.List;

import br.com.wiser.R;
import br.com.wiser.activity.app.splashscreen.AppSplashScreenActivity;
import br.com.wiser.business.chat.conversas.ConversasDAO;
import br.com.wiser.utils.Utils;

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
                List<String> listaNovasMensagens;

                while (true) {

                    listaNovasMensagens = objConversas.carregarGeral(getApplicationContext(), listaConversas);

                    if (listaNovasMensagens.size() > 0) {
                        notificar(listaNovasMensagens);
                        Utils.vibrar(CarregarConversasService.this, 150);
                    }

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

    private void notificar(List<String> listaNovasMensagens) {

        int notificacaoID = 1;

        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo_wiser_notificacao)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Chegou uma nova mensagem!");

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(getString(R.string.app_name));

        for (String mensagem : listaNovasMensagens) {
            System.out.println(mensagem);
            inboxStyle.addLine(mensagem);
        }

        builder.setStyle(inboxStyle);

        Intent resultIntent = new Intent(this, AppSplashScreenActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(AppSplashScreenActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(notificacaoID, builder.build());
    }
}
