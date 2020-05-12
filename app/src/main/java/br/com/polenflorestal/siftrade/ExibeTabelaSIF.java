package br.com.polenflorestal.siftrade;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static br.com.polenflorestal.siftrade.Constants.LOGOS_ROTATE_TIME;
import static br.com.polenflorestal.siftrade.Constants.empresasLogos;
import static br.com.polenflorestal.siftrade.Constants.prodUnidades;

public class ExibeTabelaSIF extends AppCompatActivity {
    private static final int RC_FAZER_LOGIN = 9003;
    private int empresaIndex;
    private boolean activeLogos;
    private TableLayout tableLayout;
    private LinearLayout adminView;
    private boolean isAdmin = false;
    private EditText inputData;
    private EditText inputPreco;
    private String uf;
    private String regiao;
    private String produto;
    private int rowIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exibe_tabela_s_i_f);

        TextView txtUF = findViewById(R.id.exibe_tabela_uf);
        TextView txtRegiao = findViewById(R.id.exibe_tabela_regiao);
        TextView txtUnidade = findViewById(R.id.exibe_tabela_unidade);
        tableLayout = findViewById(R.id.exibe_tabela_tableLayout);
        adminView = findViewById(R.id.admin_add_preco);
        inputData = findViewById(R.id.exibe_tabela_input_data);
        inputPreco = findViewById(R.id.exibe_tabela_input_preco);

        Intent intent = getIntent();
        uf = intent.getStringExtra("UF");
        regiao = intent.getStringExtra("Região");
        produto = intent.getStringExtra("Produto");

        String sUF = getString(R.string.UF_dois_pontos) + uf;
        String sRegiao = getString(R.string.Regiao_dois_pontos) + regiao;
        txtUF.setText(sUF);
        txtRegiao.setText(sRegiao);
        String sUnidade = getString(R.string.Unidade_dois_pontos);
        if( prodUnidades.containsKey(produto) )
            sUnidade += prodUnidades.get(produto);
        txtUnidade.setText(sUnidade);

        DataBaseUtil.getInstance().getCollectionReference("/UF/" + uf + "/Regioes/" + regiao + "/Produtos/" + produto + "/Preços", "data", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("MY_DATABASE", "Listen failed.", e);
                            return;
                        }
                        if( queryDocumentSnapshots != null ){
                            limpaTabelaPrecos();
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                if ( doc.get("data") != null && doc.get("preço") != null ) {
                                    addTableRow((Timestamp) doc.get("data"), (double) doc.get("preço"));
                                }
                            }
                        }
                    }
                });
                /*
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot documents = task.getResult();
                            if (documents != null) {
                                int i = 0;
                                for (QueryDocumentSnapshot document : documents) {
                                    //Log.d("MY_DATABASE", document.getId() + " => " + document.getData());
                                    Timestamp data = (Timestamp) document.get("data");
                                    double preco = (double) document.get("preço");

                                    TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);

                                    TableRow tableRow = new TableRow(ExibeTabelaSIF.this);
                                    tableRow.setLayoutParams(tableParams);// TableLayout is the parent view
                                    if (i % 2 == 0)
                                        tableRow.setBackgroundColor(Color.parseColor("#cfcfcf"));
                                    else
                                        tableRow.setBackgroundColor(Color.parseColor("#ededed"));

                                    TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1);

                                    TextView txtData = new TextView(ExibeTabelaSIF.this);
                                    txtData.setLayoutParams(rowParams);// TableRow is the parent view
                                    String sData = new SimpleDateFormat("MM/yyyy").format(data != null ? data.toDate() : "-");
                                    txtData.setText(sData);
                                    txtData.setGravity(Gravity.CENTER_HORIZONTAL);

                                    TextView txtPreco = new TextView(ExibeTabelaSIF.this);
                                    txtPreco.setLayoutParams(rowParams);// TableRow is the parent view
                                    txtPreco.setText(preco + "");
                                    txtPreco.setGravity(Gravity.CENTER_HORIZONTAL);

                                    tableLayout.addView(tableRow);
                                    tableRow.addView(txtData);
                                    tableRow.addView(txtPreco);

                                    i += 1;
                                }
                            }
                        } else {
                            Log.w("MY_DATABASE", "Error getting documents.", task.getException());
                        }
                    }
                });

                 */

        // primeira logo
        empresaIndex = new Random().nextInt(empresasLogos.length);
        if (empresaIndex >= empresasLogos.length)
            empresaIndex = 0;
        if (empresasLogos.length > 0)
            ((ImageView) findViewById(R.id.empresas_logos)).setImageResource(empresasLogos[empresaIndex]);
        empresaIndex += 1;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!UserUtil.isLogged()) {
            startActivityForResult(new Intent(this, LoginActivity.class), RC_FAZER_LOGIN);
            //finish();
        }

        // exibe ou esconde a caixa de cadastrar preco
        adminView.setVisibility(View.GONE);
        DataBaseUtil.getInstance().getDocument("Usuarios", UserUtil.getCurrentUser().getEmail())
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                if (document.exists()) {
                                    //Log.d("MY_DATABASE", "DocumentSnapshot data: " + document.getData());
                                    if (document.contains("admin")) {
                                        isAdmin = (boolean) document.get("admin");
                                        if (isAdmin) {
                                            adminView.setVisibility(View.VISIBLE);
                                        }
                                    }
                                } else {
                                    Log.d("MY_DATABASE", "No such document");
                                }
                            }
                        }
                    }
                });

        // ativa a rotacao dos logos
        activeLogos = true;
        logosRotativas();
    }

    @Override
    protected void onStop() {
        super.onStop();

        activeLogos = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_FAZER_LOGIN) {
            if (resultCode != RESULT_OK) {
                ToastUtil.show(this, "Falha ao realizar login!", Toast.LENGTH_LONG);
                finish();
            }
        }

    }

    private void logosRotativas() {
        if (activeLogos) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (empresaIndex >= empresasLogos.length)
                        empresaIndex = 0;

                    if (empresasLogos.length > 0)
                        ((ImageView) findViewById(R.id.empresas_logos)).setImageResource(empresasLogos[empresaIndex]);

                    empresaIndex += 1;

                    logosRotativas();
                }
            }, LOGOS_ROTATE_TIME);
        }
    }

    public void btnCadastrarPreco(View view) {
        if (!validaDados()) {
            return;
        }

        String sData = inputData.getText().toString();
        double preco = Double.parseDouble(inputPreco.getText().toString());

        Map<String, Object> precoData = new HashMap<>();
        precoData.put("preço", preco);

        Date date = null;
        try {
            date = new SimpleDateFormat("MM/yyyy").parse(sData);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if( date != null )
            precoData.put("data", new Timestamp(date));

        DataBaseUtil.getInstance().insertDocument("/UF/" + uf + "/Regioes/" + regiao + "/Produtos/" + produto + "/Preços", precoData);

        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        inputData.setText("");
        inputPreco.setText("");
    }

    private boolean validaDados() {
        boolean valido = true;

        String sData = inputData.getText().toString();
        if (TextUtils.isEmpty(sData)) {
            inputData.setError("Campo necessário.");
            valido = false;
        } else if (sData.length() != 7) {
            inputData.setError("Utilize o formato: MM/AAAA (ex: 01/2009)");
            valido = false;
        } else {
            inputData.setError(null);
        }

        String sPreco = inputPreco.getText().toString();
        if (TextUtils.isEmpty(sPreco) || sPreco.equals(".") || sPreco.equals(",") || sPreco.equals("-")) {
            inputPreco.setError("Campo necessário.");
            valido = false;
        }
        else {
            double preco = Double.parseDouble(sPreco);
            if (preco < 0) {
                inputPreco.setError("Digite um valor positivo.");
                valido = false;
            }
            else {
                inputPreco.setError(null);
            }
        }

        return valido;
    }

    private void limpaTabelaPrecos(){
        ((TableLayout)findViewById(R.id.exibe_tabela_tableLayout)).removeAllViews();
        rowIndex = 0;
    }

    private void addTableRow(Timestamp data, double preco){
        TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);

        TableRow tableRow = new TableRow(ExibeTabelaSIF.this);
        tableRow.setLayoutParams(tableParams);// TableLayout is the parent view
        if (rowIndex % 2 == 0)
            tableRow.setBackgroundColor(Color.parseColor("#cfcfcf"));
        else
            tableRow.setBackgroundColor(Color.parseColor("#ededed"));

        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1);

        TextView txtData = new TextView(ExibeTabelaSIF.this);
        txtData.setLayoutParams(rowParams);// TableRow is the parent view
        String sData = new SimpleDateFormat("MM/yyyy").format(data != null ? data.toDate() : "-");
        txtData.setText(sData);
        txtData.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView txtPreco = new TextView(ExibeTabelaSIF.this);
        txtPreco.setLayoutParams(rowParams);// TableRow is the parent view
        txtPreco.setText(preco + "");
        txtPreco.setGravity(Gravity.CENTER_HORIZONTAL);

        tableLayout.addView(tableRow);
        tableRow.addView(txtData);
        tableRow.addView(txtPreco);

        rowIndex += 1;
    }
}
