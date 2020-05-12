package br.com.polenflorestal.siftrade;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

import static br.com.polenflorestal.siftrade.Constants.LOGOS_ROTATE_TIME;
import static br.com.polenflorestal.siftrade.Constants.empresas_logos;

public class MainActivity extends AppCompatActivity {
    private int empresa_index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((TextView) findViewById(R.id.btn_user_logout)).setText(Html.fromHtml("<u>Sair</u>"));

        empresa_index = new Random().nextInt(empresas_logos.length);
        if (empresa_index >= empresas_logos.length)
            empresa_index = 0;
        if( empresas_logos.length > 0 )
            ((ImageView) findViewById(R.id.empresas_logos)).setImageResource(empresas_logos[empresa_index]);
        empresa_index += 1;
        logosRotativas();
    }

    private void logosRotativas() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (empresa_index >= empresas_logos.length)
                    empresa_index = 0;

                if( empresas_logos.length > 0 )
                    ((ImageView) findViewById(R.id.empresas_logos)).setImageResource(empresas_logos[empresa_index]);

                empresa_index += 1;

                logosRotativas();
            }
        }, LOGOS_ROTATE_TIME);
    }

    public void btnTabelasSIF(View view) {
        Intent intent = new Intent(this, TabelasSIFActivity.class);
        startActivity(intent);
        //finish(); // nao precisa fechar essa activity. deixar voltar pra ela caso queiram
    }

    @Override
    protected void onStart() {
        super.onStart();

        verificaLogin();
    }

    private void verificaLogin(){
        if( UserUtil.isLogged() ){
            ((TextView) findViewById(R.id.btn_user_name)).setText("Olá "+UserUtil.getCurrentUser().getDisplayName()+"!     ");

            findViewById(R.id.view_user_logado).setVisibility(View.VISIBLE);
        }
        else {
            findViewById(R.id.view_user_logado).setVisibility(View.GONE);
        }
    }

    public void userLogout(View view) {
        UserUtil.logOut();

        verificaLogin();
    }
}
