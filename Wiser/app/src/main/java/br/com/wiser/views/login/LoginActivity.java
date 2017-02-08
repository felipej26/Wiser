package br.com.wiser.views.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import br.com.wiser.Sistema;
import br.com.wiser.R;
import br.com.wiser.presenters.login.LoginPresenter;
import br.com.wiser.views.AbstractActivity;
import br.com.wiser.views.AbstractAppCompatActivity;

public class LoginActivity extends AbstractActivity implements ILoginView {

    private LoginPresenter loginPresenter;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_login);

        loginPresenter = new LoginPresenter();
        loginPresenter.onCreate(this);

        if (getIntent().getBooleanExtra(Sistema.LOGOUT, false)) {
            getIntent().removeExtra(Sistema.LOGOUT);
            loginPresenter.logout();
        }
        else {
            //loginPresenter.checkLogin();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loginPresenter.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginPresenter.setCallbackManager(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == Sistema.PERMISSION_ALL) {
            loginPresenter.tratarPermissioes(grantResults);
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onInitView() {
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginPresenter.setOnClickBtnLogin();
            }
        });
    }
}
