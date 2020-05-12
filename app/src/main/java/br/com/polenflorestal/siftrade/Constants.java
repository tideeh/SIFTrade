package br.com.polenflorestal.siftrade;

public class Constants {
    // Nome da empresa que vai usar o APP
    public static final String EMPRESA_NOME = "POLEN";

    // sharedPreferences nome
    public static final String SP_NOME = "siftradepolen_prefs";
    public static final String SP_KEY_VERSION_CODE = "version_code";

    // logos das empresas associadas
    public static int[] empresas_logos = {R.drawable.aperam, R.drawable.arcelor, R.drawable.bracell, R.drawable.def, R.drawable.duratex, R.drawable.gerdau, R.drawable.suzano, R.drawable.ufv, R.drawable.veracel, R.drawable.westrock};

    // tempo de exibicao dos logos das empresas
    public static final int LOGOS_ROTATE_TIME = 2000;
    // defaults values (quando nao encontra o valor pedido)
    public static final String DEFAULT_STRING_VALUE = "-1";
    public static final long DEFAULT_LONG_VALUE = 0;
    public static final int DEFAULT_INT_VALUE = 0;
}
