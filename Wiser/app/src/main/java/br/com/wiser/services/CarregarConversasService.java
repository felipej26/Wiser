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
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import br.com.wiser.APIClient;
import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.features.conversa.Conversa;
import br.com.wiser.features.conversa.IConversaService;
import br.com.wiser.features.splashscreen.SplashScreenActivity;
import br.com.wiser.interfaces.ICallbackFinish;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jefferson on 18/09/2016.
 */
public class CarregarConversasService extends Service {

    private IConversaService service;
    private CarregarConversasPresenter carregarConversasPresenter;

    private boolean lock;

    @Override
    public void onCreate() {
        super.onCreate();

        service = APIClient.getClient().create(IConversaService.class);
        carregarConversasPresenter = new CarregarConversasPresenter();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    if (Sistema.getUsuario() == null) {
                        stopSelf();
                        return;
                    }

                    lock = true;
                    processar(new ICallbackFinish() {
                        @Override
                        public void onFinish() {
                            lock = false;
                        }
                    });

                    while (lock);

                    try {
                        Thread.sleep(3000);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
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

    private void processar(final ICallbackFinish callback) {
        try {
            carregarConversasPresenter.getIdUltimaMensagem(new CarregarConversasPresenter.ICallbackIdUltimaMensagem() {
                @Override
                public void onSuccess(long idUltimaMensagem) {
                    carregarMensagens(idUltimaMensagem, callback);
                }
            });
        }
        catch (Exception e) {
            Log.e("Serviço", "Erro", e);
            callback.onFinish();
        }
    }

    private void carregarMensagens(long idUltimaMensagem, final ICallbackFinish callback) {
        Call<LinkedList<Conversa>> call = service.carregarConversas(Sistema.getUsuario().getId(), idUltimaMensagem);
        call.enqueue(new Callback<LinkedList<Conversa>>() {
            @Override
            public void onResponse(Call<LinkedList<Conversa>> call, Response<LinkedList<Conversa>> response) {
                if (response.isSuccessful()) {
                    List<Conversa> listaConversas = response.body();

                    for (Conversa conversa : listaConversas) {
                        if (conversa.getMensagens().size() > 0) {
                            carregarConversasPresenter.adicionarConversa(conversa);
                        }
                    }
                }

                callback.onFinish();
            }

            @Override
            public void onFailure(Call<LinkedList<Conversa>> call, Throwable t) {
                Log.e("Serviço", "Erro ao Carregar Conversa", t);
                callback.onFinish();
            }
        });
    }

    private void notificar(String mensagemNova) {

        final int NOTIFICATION_ID = 1;

        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo_wiser_notificacao)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.novas_mensagem));

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(getString(R.string.app_name));
        inboxStyle.addLine(mensagemNova);

        builder.setStyle(inboxStyle);

        Intent resultIntent = new Intent(this, SplashScreenActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(SplashScreenActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}