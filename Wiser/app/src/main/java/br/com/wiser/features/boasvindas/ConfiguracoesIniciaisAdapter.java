package br.com.wiser.features.boasvindas;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.LinkedList;

import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.interfaces.ICallbackFinish;
import br.com.wiser.utils.ComboBoxItem;
import br.com.wiser.utils.FiltrosManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Jefferson on 26/09/2017.
 */
public class ConfiguracoesIniciaisAdapter extends FragmentPagerAdapter {

    public interface ItemListener{
        void onChoosed(int idFiltro);
    }

    private LinkedList<Fragment> abasFragmentos;

    private int languageChoosed = 0;
    private boolean isLanguageChoosed = false;

    private int fluencyChoosed = 0;
    private boolean isFluencyChoosed = false;

    public ConfiguracoesIniciaisAdapter(FragmentManager fm, final ICallbackFinish callback) {
        super(fm);

        abasFragmentos = new LinkedList<>();
        abasFragmentos.add(0, WiserFragment.newInstance());
        abasFragmentos.add(1, LanguageFragment.newInstance());
        abasFragmentos.add(2, FluencyFragment.newInstance());

        ((LanguageFragment)abasFragmentos.get(1)).setListener(new ItemListener() {
            @Override
            public void onChoosed(int idFiltro) {
                languageChoosed = idFiltro;
                isLanguageChoosed = true;

                if (itemsChoosed()) {
                    callback.onFinish();
                }
            }
        });

        ((FluencyFragment)abasFragmentos.get(2)).setListener(new ItemListener() {
            @Override
            public void onChoosed(int idFiltro) {
                fluencyChoosed = idFiltro;
                isFluencyChoosed = true;

                if (itemsChoosed()) {
                    callback.onFinish();
                }
            }
        });
    }

    @Override
    public int getCount() {
        return abasFragmentos.size();
    }

    @Override
    public Fragment getItem(int position) {
        return abasFragmentos.get(position);
    }

    private boolean itemsChoosed() {
        return isLanguageChoosed && isFluencyChoosed;
    }

    public int getLanguageChoosed() {
        return languageChoosed;
    }

    public int getFluencyChoosed() {
        return fluencyChoosed;
    }

    public static class WiserFragment extends Fragment {

        public static WiserFragment newInstance() {
            return new WiserFragment();
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.frame_welcome_wiser, container, false);

            return view;
        }
    }

    public static class LanguageFragment extends Fragment {

        @BindView(R.id.btnIdioma) Button btnIdioma;
        private FiltrosManager fm;

        private ItemListener listener;

        public static LanguageFragment newInstance() {
            return new LanguageFragment();
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.frame_welcome_language, container, false);

            ButterKnife.bind(this, view);

            fm = new FiltrosManager();
            for (ComboBoxItem item : Sistema.getListaIdiomas()) {
                fm.addFiltro(item.getId(), item.getDescricao());
            }

            return view;
        }

        public void setListener(ItemListener listener) {
            this.listener = listener;
        }

        @OnClick(R.id.btnIdioma)
        public void onLanguageClicked() {
            fm.selecionarItem(getContext(), new FiltrosManager.OnClickItemListener() {
                @Override
                public void onClick(int idFiltro, String descricao) {
                    btnIdioma.setText(descricao);
                    listener.onChoosed(idFiltro);
                }
            });
        }
    }

    public static class FluencyFragment extends Fragment {

        @BindView(R.id.btnFluencia) Button btnFluencia;
        private FiltrosManager fm;

        private ItemListener listener;

        public static FluencyFragment newInstance() {
            return new FluencyFragment();
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.frame_welcome_fluency, container, false);

            ButterKnife.bind(this, view);

            fm = new FiltrosManager();
            for (ComboBoxItem item : Sistema.getListaFluencias()) {
                fm.addFiltro(item.getId(), item.getDescricao());
            }

            return view;
        }

        public void setListener(ItemListener listener) {
            this.listener = listener;
        }

        @OnClick(R.id.btnFluencia)
        public void onFluencyClicked() {
            fm.selecionarItem(getContext(), new FiltrosManager.OnClickItemListener() {
                @Override
                public void onClick(int idFiltro, String descricao) {
                    btnFluencia.setText(descricao);
                    listener.onChoosed(idFiltro);
                }
            });
        }
    }
}
