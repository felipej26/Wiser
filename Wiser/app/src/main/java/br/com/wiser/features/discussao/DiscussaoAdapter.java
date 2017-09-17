package br.com.wiser.features.discussao;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.WiserApplication;
import br.com.wiser.features.usuario.Usuario;
import br.com.wiser.utils.Utils;
import br.com.wiser.utils.UtilsDate;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Jefferson on 16/05/2016.
 */
public class DiscussaoAdapter extends RecyclerView.Adapter<DiscussaoAdapter.ViewHolder> {

    private IDiscussao iDiscussao;
    private List<Discussao> listaDiscussoes;

    public DiscussaoAdapter(IDiscussao discussao) {
        this.iDiscussao = discussao;
        listaDiscussoes = new LinkedList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.forum_discussao_resultados, parent, false);

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Discussao discussao = listaDiscussoes.get(position);
        Usuario usuario = discussao.getUsuario();

        Context context = WiserApplication.getAppContext();

        Utils.loadImageInBackground(usuario.getUrlFotoPerfil(), viewHolder.imgPerfil, viewHolder.prgBarra);
        viewHolder.lblIDDiscussao.setText("#" + discussao.getId());
        viewHolder.lblAutorDiscussao.setText(usuario.getPrimeiroNome());
        viewHolder.lblTituloDiscussao.setText(Utils.decode(discussao.getTitulo()));
        viewHolder.lblDescricaoDiscussao.setText(Utils.decode(discussao.getDescricao()));
        viewHolder.lblContRespostas.setText(
                context.getString(discussao.getListaRespostas().size() == 1 ? R.string.resposta : R.string.respostas,
                        discussao.getListaRespostas().size()));
        viewHolder.lblDataHora.setText(UtilsDate.formatDate(discussao.getData(), UtilsDate.DDMMYYYY_HHMMSS));
        viewHolder.btnDesativar.setText(discussao.isAtiva() ? context.getString(R.string.desativar) : context.getString(R.string.ativar));

        if (usuario.getId() != Sistema.getUsuario().getId()) {
            viewHolder.btnDesativar.setVisibility(View.INVISIBLE);
        }
        else {
            viewHolder.btnDesativar.setVisibility(View.VISIBLE);
        }

        viewHolder.setPosicao(position);
    }

    @Override
    public int getItemCount() {
        if (listaDiscussoes == null) return 0;
        return listaDiscussoes.size();
    }

    public void addItem(Discussao discussao) {
        listaDiscussoes.add(discussao);
        notifyItemChanged(listaDiscussoes.size());
    }

    public void addItems(List<Discussao> discussoes) {
        listaDiscussoes.addAll(discussoes);
        notifyDataSetChanged();
    }

    public void updateItem(int position) {
        notifyItemChanged(position);
    }

    public void limparLista() {
        listaDiscussoes = new LinkedList<>();
    }

    public Discussao getItem(int position) {
        return listaDiscussoes.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private int posicao;

        private View itemView;
        @BindView(R.id.lblIDDiscussao) TextView lblIDDiscussao;
        @BindView(R.id.imgPerfil) ImageView imgPerfil;
        @BindView(R.id.prgBarra) ProgressBar prgBarra;
        @BindView(R.id.lblAutorDiscussao) TextView lblAutorDiscussao;
        @BindView(R.id.lblTituloDiscussao) TextView lblTituloDiscussao;
        @BindView(R.id.lblDescricaoDiscussao) TextView lblDescricaoDiscussao;
        @BindView(R.id.lblContRespostas) TextView lblContRespostas;
        @BindView(R.id.lblDataHora) TextView lblDataHora;
        @BindView(R.id.btnDesativar) Button btnDesativar;
        @BindView(R.id.btnCompartilhar) Button btnCompartilhar;

        public ViewHolder(final View view) {
            super(view);
            this.itemView = view;

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DiscussaoAdapter.this.iDiscussao.onDiscussaoClicked(posicao);
                }
            });
        }

        public void setPosicao(int posicao) {
            this.posicao = posicao;
        }

        @OnClick(R.id.imgPerfil)
        public void onImgPerfilClicked() {
            iDiscussao.onPerfilClicked(posicao);
        }

        @OnClick(R.id.btnDesativar)
        public void onDesativarClicked() {
            iDiscussao.onDesativarCliked(posicao);
        }

        @OnClick(R.id.btnCompartilhar)
        public void onCompartilharClicked() {
            iDiscussao.onCompartilharClicked(itemView);
        }
    }
}