package br.com.charlessilva;

/**
 *     / )|                                   |( \
 *    / / |         Charles Silva             | \ \
 *  _( (_ |  _  [www.charlessilva.com.br]  _  | _) )_
 * (((\ \)|_/ )___________________________( \_|(/ /)))
 * (\\\\ \_/ /                             \ \_/ ////)
 *  \       /                               \       /
 *   \    _/                                 \_    /
 *   /   /                                     \   \
 * Autor   : Charles Silva (suporte@charlessilva.com.br)
 * Linguagem : Java (SDK Android) ATD: Android Studio
 * URL: www.charlessilva.com.br
 * twitter: http://twitter.com/charlessilva_
 * GitHub: https://github.com/silvacharles
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import br.com.charlessilva.biblioteca.AnalyticsTrackers;
import br.com.charlessilva.biblioteca.AppController;
import br.com.charlessilva.biblioteca.AppConfig;
import br.com.charlessilva.biblioteca.SQLiteHandler;
import br.com.charlessilva.biblioteca.SessionManager;
import br.com.charlessilva.biblioteca.Funcoes;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends Activity {

    // Declarando privates
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnLogin;
    private Button btnLinkToRegister;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private CoordinatorLayout coordinatorLayout;
    private AdView mAdView;
    private WifiManager AdministraWifi;
    private static LoginActivity mInstance;

    // Chmando Funçoes na Package Biblioteca
    public Funcoes Funcoes = new Funcoes(this);


    //Classe LogCat
    public static final String PREFS_NAME = "MinhasPref";

    // Evento para Verificar se está Online


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Google Analytics
        mInstance = this;
        AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);



    // Criando contexo para Habilitar o WiFi
        AdministraWifi = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
         // Criando Laytou para Cordenada do Snackbar
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);

       // Propaganadas do Google (Monetização)
        AdView mAdView = (AdView) findViewById(R.id.adView);
        // AdRequest adRequest = new AdRequest.Builder().build();
        // Para Testes
        //AdRequest.Builder.addTestDevice("ABCDEF012345");
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

            //Criando objetos
            inputEmail = (EditText) findViewById(R.id.email);
            inputPassword = (EditText) findViewById(R.id.password);
            btnLogin = (Button) findViewById(R.id.btnLogin);
            btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);

        if (!Funcoes.isExternalStorageWritable()){
            Log.d(TAG,"Aceita Gravaçao");
        }

        //Restaura as preferencias gravadas
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        inputEmail.setText(settings.getString("PrefUsuario", ""));
        inputPassword.setText(settings.getString("PrefSenha",""));

        Log.d(TAG, "Restaurando SharedPreferences");

            // Exibe na tela progresso em forma de Dialogo
            pDialog = new ProgressDialog(this);
            pDialog.setCancelable(false);

            // Database em SQLite e gerenciando sessão
            db = new SQLiteHandler(getApplicationContext());
            session = new SessionManager(getApplicationContext());
            // Checando se o usuario está logado
            if (session.isLoggedIn()) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                Log.d(TAG,"Verificado se está logado, chamou o MainActivity");
            }
        // Verifica se está conectado
        if(Funcoes.isOnline()) {
            Log.d(TAG,"Online, passou para fase de verificação ");
        } else {
            Conexao();
        }

    // Botão de evento
    btnLogin.setOnClickListener(new View.OnClickListener() {

        public void onClick(View view) {
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            // Verifica se esta em branco e loga usuario
            if (!email.isEmpty() && !password.isEmpty()) {
               checkLogin(email, password);
                Log.d(TAG,"Checando Login");

            } else {
                    Toast.makeText(getApplicationContext(),
                        R.string.camposnulos, Toast.LENGTH_LONG)
                        .show();
                Log.d(TAG,"Campos nulos");
            }
        }

    });


    // Link para registrar
    btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

        public void onClick(View view) {
            Intent i = new Intent(getApplicationContext(),
                    RegisterActivity.class);
            startActivity(i);
           finish();
        }
    });

}

    public static synchronized LoginActivity getInstance() {
        return mInstance;
    }

    public synchronized Tracker getGoogleAnalyticsTracker() {
        AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
        return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
    }

    public void trackScreenView(String screenName) {
        Tracker t = getGoogleAnalyticsTracker();

        // Set screen name.
        t.setScreenName(screenName);

        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());

        GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }


    public void trackException(Exception e) {
        if (e != null) {
            Tracker t = getGoogleAnalyticsTracker();

            t.send(new HitBuilders.ExceptionBuilder()
                            .setDescription(
                                    new StandardExceptionParser(this, null)
                                            .getDescription(Thread.currentThread().getName(), e))
                            .setFatal(false)
                            .build()
            );
        }
    }


    public void trackEvent(String category, String action, String label) {
        Tracker t = getGoogleAnalyticsTracker();

        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
    }

    //Função para verificar login e detalhes no mysql
    private void checkLogin(final String email, final String password) {

        if(Funcoes.isOnline()) {

        String tag_string_req = "req_login";
        pDialog = new ProgressDialog(LoginActivity.this);

        pDialog.setMessage(getString(R.string.entrando));
        showDialog();
        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Resposta de Login: " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                           session.setLogin(true);
                        String uid = jObj.getString("uid");
                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String created_at = user
                                .getString("created_at");

                        db.addUser(name, email, uid, created_at);
                        Intent intent = new Intent(LoginActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Erro JSON: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Erro no Login: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
     else
          {

         Toast.makeText(getApplicationContext(), R.string.semconexao, Toast.LENGTH_LONG).show();
    }
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    protected void HabilitaWifi(){
      AdministraWifi.setWifiEnabled(!AdministraWifi.isWifiEnabled());
    }

    protected void Conexao(){

        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, R.string.semconexao, Snackbar.LENGTH_LONG)
                .setAction(R.string.conectar, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HabilitaWifi();
                    }
                });
        snackbar.setActionTextColor(Color.RED);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }

    @Override
    protected void onStop(){
        super.onStop();

        //Caso o checkbox esteja marcado gravamos o usuário
        CheckBox chkSalvar = (CheckBox)findViewById(R.id.chkSalvar);
        if (chkSalvar.isChecked()){
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("PrefUsuario", inputEmail.getText().toString());
            editor.putString("PrefSenha", inputPassword.getText().toString());
            //Confirma a gravação dos dados
            editor.commit();
        }
    }

    protected void onDestroy(){
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    protected void onPause(){
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onPause();
    }

    protected void onResume(){
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onResume();
    }
}
