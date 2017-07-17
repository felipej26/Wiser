package br.com.wiser.presenters.principal;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.LinkedList;

import br.com.wiser.features.conversa.ConversaFragment;
import br.com.wiser.features.contato.ContatosFragment;
import br.com.wiser.views.forum.ForumFragment;

/**
 * Created by Jefferson on 21/05/2016.
 */
public class PrincipalTabs extends FragmentPagerAdapter {
    private LinkedList<Fragment> abasFragmentos;

    public PrincipalTabs(FragmentManager fm) {
        super(fm);

        abasFragmentos = new LinkedList<>();
        abasFragmentos.add(0, ContatosFragment.newInstance());
        abasFragmentos.add(1, ConversaFragment.newInstance());
        abasFragmentos.add(2, ForumFragment.newInstance());
    }

    @Override
    public int getCount() {
        return abasFragmentos.size();
    }

    @Override
    public Fragment getItem(int position) {
        return abasFragmentos.get(position);
    }
}