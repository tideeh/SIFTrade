package br.com.polenflorestal.siftrade;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.Random;

import static br.com.polenflorestal.siftrade.Constants.LOGOS_ROTATE_TIME;
import static br.com.polenflorestal.siftrade.Constants.empresas_logos;

public class RegisterActivity extends AppCompatActivity {
    private EditText input_email;
    private EditText input_senha;
    private EditText input_senha_repetir;
    private ProgressBar progressBar;

    private int empresa_index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        input_email = findViewById(R.id.register_input_email);
        input_senha = findViewById(R.id.register_input_senha);
        input_senha_repetir = findViewById(R.id.register_input_senha_repetir);

        progressBar = findViewById(R.id.progress_bar);

        empresa_index = new Random().nextInt(empresas_logos.length);
        if (empresa_index >= empresas_logos.length)
            empresa_index = 0;
        if( empresas_logos.length > 0 )
            ((ImageView) findViewById(R.id.empresas_logos)).setImageResource(empresas_logos[empresa_index]);
        empresa_index += 1;
        logosRotativas();
    }

    public void btnRegister(View view){
        if (!validaDados()) {
            return;
        }

        String email = input_email.getText().toString();
        String senha = input_senha.getText().toString();

        UserUtil.createUserWithEmailAndPassword(this, email, senha, progressBar);
    }

    private boolean validaDados() {
        boolean valido = true;

        String email = input_email.getText().toString();
        if (TextUtils.isEmpty(email)) {
            input_email.setError("Campo necessário.");
            valido = false;
        } else {
            input_email.setError(null);
        }

        String senha = input_senha.getText().toString();
        if (TextUtils.isEmpty(senha)) {
            input_senha.setError("Campo necessário.");
            valido = false;
        } else if (senha.length() < 6) {
            input_senha.setError("Necessário pelo menos 6 caracteres.");
            valido = false;
        } else {
            input_senha.setError(null);
        }

        String senhaRepetir = input_senha_repetir.getText().toString();
        if (!senha.equals(senhaRepetir)) {
            input_senha_repetir.setError("Senhas diferentes.");
            valido = false;
        } else {
            input_senha_repetir.setError(null);
        }

        return valido;
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
}
