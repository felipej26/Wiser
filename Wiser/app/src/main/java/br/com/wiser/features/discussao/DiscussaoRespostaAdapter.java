package br.com.wiser.features.discussao;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import br.com.wiser.R;
import br.com.wiser.utils.Utils;
import br.com.wiser.utils.UtilsDate;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jefferson on 20/05/2016.
 */
public class DiscussaoRespostaAdapter extends RecyclerView.Adapter<DiscussaoRespostaAdapter.ViewHolder> {

    private LinkedList<Resposta> listaRespostas;

    public DiscussaoRespostaAdapter() {
        listaRespostas = new LinkedList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.forum_discussao_resposta, parent, false);

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Resposta r = listaRespostas.get(position);

        holder.viewSeparator.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);

        Utils.loadImageInBackground(r.getUsuario().getUrlFotoPerfil(), holder.imgPerfil, holder.prgBarra);
        holder.lblIDResposta.setText("#" + (position + 1));
        holder.lblAutor.setText(r.getUsuario().getPrimeiroNome());
        holder.lblDataHora.setText(UtilsDate.formatDate(r.getData(), UtilsDate.DDMMYYYY_HHMMSS));
        holder.lblResposta.setText(Utils.decode(r.getResposta()));
    }

    @Override
    public int getItemCount() {
        return listaRespostas.size();
    }

    public void addItem(Resposta resposta) {
        listaRespostas.add(resposta);
        notifyItemInserted(listaRespostas.size());
    }

    public void addItems(List<Resposta> respostas) {
        listaRespostas.addAll(respostas);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.viewSeparator) View viewSeparator;
        @BindView(R.id.imgPerfil) ImageView imgPerfil;
        @BindView(R.id.prgBarra) ProgressBar prgBarra;
        @BindView(R.id.lblIDResposta) TextView lblIDResposta;
        @BindView(R.id.lblAutor) TextView lblAutor;
        @BindView(R.id.lblDataHora) TextView lblDataHora;
        @BindView(R.id.lblResposta) TextView lblResposta;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}