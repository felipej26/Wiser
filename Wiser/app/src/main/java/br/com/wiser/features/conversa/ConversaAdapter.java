package br.com.wiser.features.conversa;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.wiser.R;
import br.com.wiser.features.usuario.Usuario;
import br.com.wiser.utils.Utils;
import br.com.wiser.utils.UtilsDate;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

/**
 * Created by Jefferson on 23/05/2016.
 */
public class ConversaAdapter extends RealmRecyclerViewAdapter<Conversa, RecyclerView.ViewHolder> {

    public interface ICallback {
        void onClick(long idConversa, Usuario usuario);
    }

    private Context context;
    private RealmResults<Conversa> listaConversas;
    private Map<Long, Usuario> mapUsuarios;
    private ICallback callback;

    public ConversaAdapter(Context context, RealmResults<Conversa> listaConversas, boolean autoUpdate, ICallback callback) {
        super(listaConversas, autoUpdate);
        this.context = context;
        this.listaConversas = listaConversas;
        this.callback = callback;
        this.mapUsuarios = new HashMap<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_mensagens_item, parent, false);

        return new ViewHolder(itemLayoutView, callback);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Conversa conversa = listaConversas.get(position);

        ((ViewHolder) holder).bind(context, conversa, position);

        if (mapUsuarios.containsKey(conversa.getDestinatario())) {
            ((ViewHolder) holder).bindUsuario(mapUsuarios.get(conversa.getDestinatario()));
        }
    }

    @Override
    public int getItemCount() {
        return listaConversas.size();
    }

    public void updateUsuarios(List<Usuario> listaUsuarios) {
        for (Usuario usuario : listaUsuarios) {
            if (!mapUsuarios.containsKey(usuario.getId())) {
                mapUsuarios.put(usuario.getId(), usuario);
            }
        }
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public long idConversa;
        public Usuario usuario;

        public View viewSeparator;

        public ImageView imgPerfil;
        public ProgressBar prgBarra;

        public TextView lblNome;
        public TextView lblDataHora;
        public TextView lblMensagens;
        public TextView lblContMensagens;

        private ICallback callback;
        private int posicao;

        public ViewHolder(View itemLayoutView, ICallback callback) {
            super(itemLayoutView);
            this.callback = callback;
            itemLayoutView.setOnClickListener(this);

            viewSeparator = itemLayoutView.findViewById(R.id.viewSeparator);

            imgPerfil = (ImageView) itemLayoutView.findViewById(R.id.imgPerfil);
            prgBarra = (ProgressBar) itemLayoutView.findViewById(R.id.prgBarra);

            lblNome = (TextView) itemLayoutView.findViewById(R.id.lblNome);
            lblDataHora = (TextView) itemLayoutView.findViewById(R.id.lblDataHora);
            lblMensagens = (TextView) itemLayoutView.findViewById(R.id.lblMensagens);
            lblContMensagens = (TextView) itemLayoutView.findViewById(R.id.lblContMensagens);
        }

        public void bind(Context context, Conversa conversa, int posicao) {
            this.posicao = posicao;
            this.idConversa = conversa.getId();

            viewSeparator.setVisibility(posicao == 0 ? View.INVISIBLE : View.VISIBLE);

            if (conversa.getMensagens().size() > 0) {
                lblDataHora.setText(UtilsDate.formatDate(conversa.getMensagens().last().getData(), UtilsDate.HHMM));
                lblMensagens.setText(Utils.decode(conversa.getMensagens().last().getMensagem()));
            }
            lblContMensagens.setText(conversa.getContMsgNaoLidas() + " " + context.getString(conversa.getContMsgNaoLidas() <= 1 ? R.string.nao_lida : R.string.nao_lidas));
        }

        public void bindUsuario(Usuario usuario) {
            this.usuario = usuario;
            Utils.loadImageInBackground(usuario.getUrlFotoPerfil(), imgPerfil, prgBarra);
            lblNome.setText(usuario.getNome());
        }

        @Override
        public void onClick(View view) {
            if (usuario != null) {
                callback.onClick(idConversa, usuario);
            }
        }
    }
}