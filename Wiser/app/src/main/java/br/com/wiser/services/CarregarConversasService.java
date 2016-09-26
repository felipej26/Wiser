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

import br.com.wiser.R;
import br.com.wiser.activity.app.splashscreen.AppSplashScreenActivity;
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

                    if (objConversas.carregarGeral(getApplicationContext(), listaConversas)) {
                        notificar();
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

    private void notificar() {
        int notificacaoID = 1;

        String[] events = new String[2];

        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo_wiser_notificacao)
                .setContentTitle("Wiser")
                .setContentText("Chegou uma nova mensagem!");

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle("Wiser");

        events[0] = "Atchim";
        events[1] = "Saude";

        for (int i=0; i < events.length; i++) {
            inboxStyle.addLine(events[i]);
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
