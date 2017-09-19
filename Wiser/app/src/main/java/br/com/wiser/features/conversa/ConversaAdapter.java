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
import br.com.wiser.WiserApplication;
import br.com.wiser.features.usuario.Usuario;
import br.com.wiser.interfaces.IClickListener;
import br.com.wiser.utils.Utils;
import br.com.wiser.utils.UtilsDate;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

/**
 * Created by Jefferson on 23/05/2016.
 */
public class ConversaAdapter extends RealmRecyclerViewAdapter<Conversa, RecyclerView.ViewHolder> {

    public interface ICallback {
        void onClick(long idConversa, Usuario usuario);
    }

    private RealmResults<Conversa> listaConversas;
    private Map<Long, Usuario> mapUsuarios;
    private ICallback callback;
    private IClickListener onPerfilClickListener;

    public ConversaAdapter(RealmResults<Conversa> listaConversas, boolean autoUpdate, ICallback callback) {
        super(listaConversas, autoUpdate);
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

        ((ViewHolder) holder).bind(conversa, position);

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

    public Usuario getUsuario(long userID) {
        return mapUsuarios.get(userID);
    }

    public void setOnPerfilClickListener(IClickListener onPerfilClickListener) {
        this.onPerfilClickListener = onPerfilClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public long idConversa;
        public Usuario usuario;

        @BindView(R.id.viewSeparator) View viewSeparator;

        @BindView(R.id.imgPerfil) ImageView imgPerfil;
        @BindView(R.id.prgBarra) ProgressBar prgBarra;

        @BindView(R.id.lblNome) TextView lblNome;
        @BindView(R.id.lblDataHora) TextView lblDataHora;
        @BindView(R.id.lblMensagens) TextView lblMensagens;
        @BindView(R.id.lblContMensagens) TextView lblContMensagens;

        private ICallback callback;
        private int posicao;

        public ViewHolder(View view, ICallback callback) {
            super(view);
            this.callback = callback;
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        public void bind(Conversa conversa, int posicao) {
            this.posicao = posicao;
            this.idConversa = conversa.getId();

            Context context = WiserApplication.getAppContext();

            viewSeparator.setVisibility(posicao == 0 ? View.GONE : View.VISIBLE);

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

        @OnClick(R.id.imgPerfil)
        public void onImgPerfilClicked() {
            if (onPerfilClickListener != null) {
                onPerfilClickListener.itemClicked(imgPerfil, posicao);
            }
        }
    }
}