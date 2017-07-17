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

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import br.com.wiser.APIClient;
import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.features.conversa.Conversa;
import br.com.wiser.features.conversa.ConversaDAO;
import br.com.wiser.features.conversa.IConversaService;
import br.com.wiser.features.mensagem.Mensagem;
import br.com.wiser.features.mensagem.MensagemDAO;
import br.com.wiser.features.splashscreen.SplashScreenActivity;
import br.com.wiser.features.usuario.Usuario;
import br.com.wiser.features.usuario.UsuarioDAO;
import br.com.wiser.features.usuario.UsuarioPresenter;
import br.com.wiser.interfaces.ICallbackFinish;
import br.com.wiser.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jefferson on 18/09/2016.
 */
public class CarregarConversasService extends Service implements Observer {

    private IConversaService service;
    private Map<Long, List<String>> listaMensagensNaoEnviadas;

    private long idUltimaMensagem;
    private boolean lock;

    @Override
    public void onCreate() {
        super.onCreate();

        service = APIClient.getClient().create(IConversaService.class);
        listaMensagensNaoEnviadas = new HashMap<>();

        EventBus.builder().logNoSubscriberMessages(false).build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final MensagemDAO mensagensDAO = new MensagemDAO();

        new Thread(new Runnable() {
            @Override
            public void run() {
                idUltimaMensagem = mensagensDAO.getMaxId();

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

    private void processar(final ICallbackFinish callback) {
        final ConversaDAO conversasDAO = new ConversaDAO();
        final MensagemDAO mensagensDAO = new MensagemDAO();

        try {
            Call<LinkedList<Conversa>> call = service.carregarConversas(Sistema.getUsuario().getId(), idUltimaMensagem);
            call.enqueue(new Callback<LinkedList<Conversa>>() {
                @Override
                public void onResponse(Call<LinkedList<Conversa>> call, Response<LinkedList<Conversa>> response) {
                    final Map<Long, List<Mensagem>> mapMensagens = new HashMap<>();

                    if (response.isSuccessful()) {
                        for (final Conversa conversa : response.body()) {
                            if (conversa.getMensagens().size() <= 0) {
                                continue;
                            }

                            mapMensagens.put(conversa.getId(), conversa.getMensagens());

                            verificarUsuario(conversa.getIdDestinatario(), new ICallbackFinish() {
                                @Override
                                public void onFinish() {
                                    conversasDAO.insert(conversa);
                                    for (Mensagem mensagem : conversa.getMensagens()) {
                                        mensagensDAO.insert(mensagem);
                                    }

                                    Utils.vibrar(CarregarConversasService.this, 150);
                                }
                            });
                        }

                        if (mapMensagens.size() > 0) {
                            idUltimaMensagem = getMaxIdMensagem(mapMensagens);
                        }
                    }
                    else {
                        callback.onFinish();
                    }
                }

                @Override
                public void onFailure(Call<LinkedList<Conversa>> call, Throwable t) {
                    Log.e("Serviço", "Erro ao Carregar Conversa", t);
                    callback.onFinish();
                }
            });
        }
        catch (Exception e) {
            Log.e("Serviço", "Erro", e);
            callback.onFinish();
        }
    }

    private void verificarUsuario(long idDestinatario, final ICallbackFinish callback) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        UsuarioPresenter usuarioPresenter = new UsuarioPresenter();

        if (!usuarioDAO.exist(idDestinatario)) {
            usuarioPresenter.getInServer(idDestinatario, new UsuarioPresenter.ICallback() {
                @Override
                public void onFinished(List<Usuario> usuarios) {
                    callback.onFinish();
                }
            });
        }
        else {
            callback.onFinish();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void update(Observable observable, Object data) {
        /*
        if (observable instanceof Usuario) {
            if (listaMensagensNaoEnviadas.containsKey(((Usuario) observable).getId())) {
                notificar(listaMensagensNaoEnviadas.get(((Usuario) observable).getId()));
            }
        }
        */
    }

    private long getMaxIdMensagem(Map<Long, List<Mensagem>> mapMensagens) {
        long max = 0;

        for(List<Mensagem> lista : mapMensagens.values()) {
            for (Mensagem m : lista) {
                if (m.getId() > max) {
                    max = m.getId();
                }
            }
        }

        return max;
    }

    private void notificar(List<String> listaMensagensNaoLidas) {

        final int NOTIFICATION_ID = 1;

        if (listaMensagensNaoLidas.size() == 0) {
            return;
        }

        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo_wiser_notificacao)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Chegaram " + listaMensagensNaoLidas.size() + " novas mensagens!");

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(getString(R.string.app_name));

        for (String mensagem : listaMensagensNaoLidas) {
            System.out.println(mensagem);
            inboxStyle.addLine(mensagem);
        }

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