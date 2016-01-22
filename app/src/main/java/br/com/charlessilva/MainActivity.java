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

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import br.com.charlessilva.biblioteca.SQLiteHandler;
import br.com.charlessilva.biblioteca.SessionManager;


import static com.google.android.gms.location.LocationServices.FusedLocationApi;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // TAG Log - Logcat
    private static final String TAG = MainActivity.class.getSimpleName();

    // Elementos de Design
    private TextView txtName;
    private TextView txtEmail;

    // SQLite
    private SQLiteHandler db;
    private SessionManager session;

    private static final int CHECK_PERMISSION_REQUEST_WRITE_STORAGE=51;

    // Evento de Criaçao
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Logs de Evento Criado
        Log.d(TAG, "Layout iniciado MainActivity!");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // txtName = (TextView) findViewById(R.id.name);
        // txtEmail = (TextView) findViewById(R.id.email);

        Log.d(TAG, "Layout da Gaveta de navegação iniciado MainActivity ");

        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }

        HashMap<String, String> user = db.getUserDetails();
        String name = user.get("name");
        String email = user.get("email");

        Log.d(TAG, "Recebido dados do BD");

        /*Log.d(TAG, "Exibindo Hora e Saudações");

        // Manipulando Data e Hora
        SimpleDateFormat dateFormat_hora = new SimpleDateFormat("HH:mm:ss");
        Date data = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        Date data_atual = cal.getTime();
        String hora_atual = dateFormat_hora.format(data_atual);
        Log.i(TAG, data_atual.toString());
        Log.i(TAG, hora_atual);

        int hora = cal.get(Calendar.HOUR_OF_DAY);
        if (hora > 6 && hora < 12) {
            txtName.setText(getString(R.string.bomdia)+" " + name);
            txtEmail.setText("Conectado: " + email);
        } else if (hora > 12 && hora < 18) {
            txtName.setText("Boa tarde, " + name);
            txtEmail.setText("Conectado: " + email);
        } else {
            txtName.setText("Boa Noite, " + name);
            txtEmail.setText("Conectado: " + email);
        }*/

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Inicia a verificação da permissão
        checkPermissionWriteExtStorage(CHECK_PERMISSION_REQUEST_WRITE_STORAGE);
              }

   // Chamado quando retornar à atividade
    @Override
    protected void onResume() {
        super.onResume();
     }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
     }

    @Override
    public void onDestroy() {
         super.onDestroy();
    }

    /**
     * Ao fazer logout o usuário vai setar isLoggedIn para false
     * e desconectar, deletando usuario do SQLite
     */
    private void logoutUser() {
        session.setLogin(false);
        db.deleteUsers();
        // Inicia a tela de Login
        Log.d(TAG,"Tela de Login lançada");
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.acao_settings) {

            Log.d(TAG, "Ação configurações clicada");

        } else if (id == R.id.acao_busca) {
            Toast.makeText(getApplicationContext(), "Ação clicada", Toast.LENGTH_LONG).show();
        } else if (id == R.id.acao_sobre){

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_sair) {
            logoutUser();
        } else if (id == R.id.nav_salvar) {
            Log.d(TAG,"Ação salvar cliclada");
        } else if (id == R.id.nav_cafe){
            Log.d(TAG,"Abrindo navegador nativo no Android");
            // Redireciona para o PagSeugro
            Uri uri = Uri.parse("http://www.charlessilva.com.br/pagseguro.php");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

     // Método para checar permissão Gravação memória externa
      private  boolean checkPermissionWriteExtStorage(int requestCode){
        boolean res=true;

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                res=false;
                requestPermissions( new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},requestCode);

            }
        }
        return res;

    }
    // Método para obter o resultado se a permisão foi concedida
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CHECK_PERMISSION_REQUEST_WRITE_STORAGE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                 File sd = Environment.getExternalStorageDirectory();
                    boolean isWritable=sd.canWrite();

                    Log.d(TAG, "Gravação de armazenamento concedida. Armazenamento Gravável="+ isWritable);


                } else {

                    // permissão negada, vazia! Desative as
                    // funcionalidades que depende dessa permissão.

            }
                return;
            }

            // outras linhas  alterar para verificar se há outras
            // permissões para este APP
        }
    }
}
