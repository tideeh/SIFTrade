package br.com.polenflorestal.siftrade;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.Random;

import static br.com.polenflorestal.siftrade.Constants.LOGOS_ROTATE_TIME;
import static br.com.polenflorestal.siftrade.Constants.empresasLogos;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private int empresa_index;
    private boolean activeLogos;
    private static final int RC_SIGN_IN_GOOGLE = 9001;
    private static final int RC_REGISTER_ACCOUNT = 9002;

    private ProgressBar progressBar;

    private EditText inputEmail;
    private EditText inputPassword;

    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = findViewById(R.id.progress_bar);

        // Configura Google Login
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Configura Facebook Login
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton btnLoginWithFacebook = findViewById(R.id.btn_login_com_facebook);
        btnLoginWithFacebook.setPermissions("email", "public_profile");
        btnLoginWithFacebook.setOnClickListener(this);
        btnLoginWithFacebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // sucesso, agora autentica com o firebase
                //Toast.makeText(getApplicationContext(), "Facebook onSuccess", Toast.LENGTH_SHORT).show();
                UserUtil.loginWithFacebook(LoginActivity.this, loginResult.getAccessToken(), progressBar);
            }

            @Override
            public void onCancel() {
                //Toast.makeText(getApplicationContext(), "Facebook onCancel", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(FacebookException error) {
                ToastUtil.show(getApplicationContext(), "Facebook onError: " + error.toString(), Toast.LENGTH_SHORT);
                progressBar.setVisibility(View.GONE);
            }
        });

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        inputEmail = findViewById(R.id.login_input_email);
        inputPassword = findViewById(R.id.login_input_senha);

        SignInButton btnLoginWithGoogle = findViewById(R.id.btn_login_com_google);
        setGooglePlusButtonText(btnLoginWithGoogle, getString(R.string.fazer_login_com_google));
        btnLoginWithGoogle.setOnClickListener(this);

        Button btnLoginWithPassword = findViewById(R.id.btn_login_com_senha);
        btnLoginWithPassword.setOnClickListener(this);

        TextView btnRegister = findViewById(R.id.btn_registrar);
        btnRegister.setOnClickListener(this);

        // primeiro logo
        empresa_index = new Random().nextInt(empresasLogos.length);
        if (empresa_index >= empresasLogos.length)
            empresa_index = 0;
        if( empresasLogos.length > 0 )
            ((ImageView) findViewById(R.id.empresas_logos)).setImageResource(empresasLogos[empresa_index]);
        empresa_index += 1;
    }

    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_login_com_senha:
                // login com email e senha
                String email = inputEmail.getText().toString();
                String senha = inputPassword.getText().toString();

                UserUtil.loginWithEmailAndPassword(this, email, senha, progressBar);
                break;

            case R.id.btn_login_com_google:
                // login com Google
                progressBar.setVisibility(View.VISIBLE);
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
                break;

            case R.id.btn_login_com_facebook:
                progressBar.setVisibility(View.VISIBLE);
                break;

            case R.id.btn_registrar:
                startActivityForResult(new Intent(getApplicationContext(), RegisterActivity.class), RC_REGISTER_ACCOUNT);
                break;

            default:
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN_GOOGLE) { // resposta do login com Google
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                UserUtil.loginWithGoogle(this, account, progressBar);
            } catch (ApiException e) {
                // Google Sign In failed
                ToastUtil.show(getApplicationContext(), "Autenticação falhou: " + e.toString(), Toast.LENGTH_SHORT);
                progressBar.setVisibility(View.GONE);
            }
        }
        else if( requestCode == RC_REGISTER_ACCOUNT ){
            if( resultCode == RESULT_OK ){
                setResult(RESULT_OK);
                finish();
            }
            else {
                ToastUtil.show(this, "Falha ao registrar uma conta!", Toast.LENGTH_LONG);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // ativa a rotacao dos logos
        activeLogos = true;
        logosRotativas();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // desativa a rotacao dos logos
        activeLogos = false;
    }

    private void logosRotativas() {
        if( activeLogos ) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (empresa_index >= empresasLogos.length)
                        empresa_index = 0;

                    if (empresasLogos.length > 0)
                        ((ImageView) findViewById(R.id.empresas_logos)).setImageResource(empresasLogos[empresa_index]);

                    empresa_index += 1;

                    logosRotativas();
                }
            }, LOGOS_ROTATE_TIME);
        }
    }

}
