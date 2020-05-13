package br.com.polenflorestal.siftrade.utils;

import java.util.HashMap;
import java.util.Map;

import br.com.polenflorestal.siftrade.R;

public class Constants {
    // Nome da empresa que vai usar o APP
    public static final String EMPRESA_NOME = "POLEN";

    // sharedPreferences nome
    public static final String SP_NOME = "siftradepolen_prefs";
    public static final String SP_KEY_VERSION_CODE = "version_code";

    // constantes relacionadas ao banco
    public static final String DB_COLLECTION_UF = "UF";
    public static final String DB_COLLECTION_REGIOES = "REGIONS";
    public static final String DB_COLLECTION_PRODUTOS = "PRODUCTS";
    public static final String DB_COLLECTION_PRECOS = "PRICES";
    public static final String DB_COLLECTION_USUARIOS = "USERS";
    public static final String DB_DOCUMENT_PRECO_FIELD_DATA = "date";
    public static final String DB_DOCUMENT_PRECO_FIELD_PRECO = "price";
    public static final String DB_DOCUMENT_USUARIO_FIELD_ADMIN = "admin";
    public static final String DB_DOCUMENT_USUARIO_FIELD_EMPRESA = "company";

    // tempo de exibicao dos logos das empresas
    public static final int LOGOS_ROTATE_TIME = 2000;

    // defaults values (quando nao encontra o valor pedido)
    public static final String DEFAULT_STRING_VALUE = "-1";
    public static final long DEFAULT_LONG_VALUE = 0;
    public static final int DEFAULT_INT_VALUE = 0;

    // logos das empresas associadas
    public static int[] empresasLogos = {R.drawable.aperam, R.drawable.arcelor, R.drawable.bracell, R.drawable.def, R.drawable.duratex, R.drawable.gerdau, R.drawable.suzano, R.drawable.ufv, R.drawable.veracel, R.drawable.westrock};

    // unidade de preco dos produtos
    public static Map<String, String> prodUnidades = new HashMap<String, String>() {
        {
            put("Carvão", "R$/mdc (Reais por metro de carvão)");
        }
    };
}
