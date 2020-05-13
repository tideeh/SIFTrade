package br.com.polenflorestal.siftrade.utils;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static br.com.polenflorestal.siftrade.utils.Constants.DB_COLLECTION_USUARIOS;
import static br.com.polenflorestal.siftrade.utils.Constants.DB_DOCUMENT_USUARIO_FIELD_ADMIN;
import static br.com.polenflorestal.siftrade.utils.Constants.DB_DOCUMENT_USUARIO_FIELD_EMPRESA;

public class UserUtil {
    private static FirebaseAuth mAuth;

    // retorna o usuario logado atual (sera null caso nao esteja logado)
    public static FirebaseUser getCurrentUser() {
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }
        return mAuth.getCurrentUser();
    }

    // retorna true caso esteja logado, e false caso nao esteja logado
    public static boolean isLogged() {
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }

        return mAuth.getCurrentUser() != null;
    }

    public static void loginWithGoogle(final AppCompatActivity ctx, GoogleSignInAccount acct, final ProgressBar progressBar) {
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }

        progressBar.setVisibility(View.VISIBLE);

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(ctx, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // se for o primeiro login 'registra' no banco esse usuario
                            if( Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getAdditionalUserInfo()).isNewUser() ){
                                // insere no banco tambem, pra ter controle de admin, empresa...
                                Map<String, Object> userData = new HashMap<String, Object>() {
                                    {
                                        put(DB_DOCUMENT_USUARIO_FIELD_ADMIN, false);
                                        put(DB_DOCUMENT_USUARIO_FIELD_EMPRESA, "");
                                    }
                                };
                                DataBaseUtil.getInstance().insertDocument(DB_COLLECTION_USUARIOS, getCurrentUser().getEmail(), userData);
                            }

                            ctx.setResult(Activity.RESULT_OK);
                            ctx.finish();
                        } else {
                            ToastUtil.show(ctx, "Autenticação falhou: " + task.getException(), Toast.LENGTH_SHORT);
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    public static void loginWithFacebook(final AppCompatActivity ctx, AccessToken token, final ProgressBar progressBar) {
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }

        progressBar.setVisibility(View.VISIBLE);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(ctx, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // se for o primeiro login 'registra' no banco esse usuario
                            if( Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getAdditionalUserInfo()).isNewUser() ){
                                // insere no banco tambem, pra ter controle de admin, empresa...
                                Map<String, Object> userData = new HashMap<String, Object>() {
                                    {
                                        put(DB_DOCUMENT_USUARIO_FIELD_ADMIN, false);
                                        put(DB_DOCUMENT_USUARIO_FIELD_EMPRESA, "");
                                    }
                                };
                                DataBaseUtil.getInstance().insertDocument(DB_COLLECTION_USUARIOS, getCurrentUser().getEmail(), userData);
                            }

                            ctx.setResult(Activity.RESULT_OK);
                            ctx.finish();
                        } else {
                            ToastUtil.show(ctx, "Autenticação falhou: " + task.getException(), Toast.LENGTH_SHORT);
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    public static void loginWithEmailAndPassword(final AppCompatActivity ctx, String email, String senha, final ProgressBar progressBar) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(senha)) {
            ToastUtil.show(ctx, "Os campos Email e Senha não podem ser vazios!", Toast.LENGTH_SHORT);
            return;
        }

        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(ctx, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // login realizado com sucesso, vai para a tela selecionar fazenda
                    //ctx.startActivity(new Intent(ctx, SelecionarFazenda.class));
                    ctx.setResult(Activity.RESULT_OK);
                    ctx.finish();
                } else {
                    ToastUtil.show(ctx, "Autenticação falhou: " + task.getException(), Toast.LENGTH_SHORT);
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public static void createUserWithEmailAndPassword(final AppCompatActivity ctx, String email, String senha, final ProgressBar progressBar, final String nome) {

        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(ctx, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // registrado com sucesso, vai para a tela selecionar fazenda
                            //ctx.startActivity(new Intent(ctx, SelecionarFazenda.class));

                            // adiciona o nome
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(nome)
                                    .build();
                            getCurrentUser().updateProfile(profileUpdates);

                            // insere no banco tambem, pra ter controle de admin, empresa...
                            Map<String, Object> userData = new HashMap<String, Object>() {
                                {
                                    put(DB_DOCUMENT_USUARIO_FIELD_ADMIN, false);
                                    put(DB_DOCUMENT_USUARIO_FIELD_EMPRESA, "");
                                }
                            };
                            DataBaseUtil.getInstance().insertDocument(DB_COLLECTION_USUARIOS, getCurrentUser().getEmail(), userData);

                            ctx.setResult(Activity.RESULT_OK);
                            ctx.finish();
                        } else {
                            // registro falhou
                            ToastUtil.show(ctx, "Registro falhou: " + task.getException(), Toast.LENGTH_SHORT);
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    public static void logOut() {
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }

        mAuth.signOut();
        LoginManager.getInstance().logOut();
    }
}
