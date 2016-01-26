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

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.util.Log;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import br.com.charlessilva.biblioteca.SQLiteHandler;
import br.com.charlessilva.biblioteca.SessionManager;
import br.com.charlessilva.fragmentos.OneFragment;
import br.com.charlessilva.fragmentos.ThreeFragment;
import br.com.charlessilva.fragmentos.TwoFragment;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // TAG Log - Logcat
    private static final String TAG = MainActivity.class.getSimpleName();

    // Elementos de Design
    private TextView txtNome;
    private TextView txtEmail;

    // SQLite
    private SQLiteHandler db;
    private SessionManager session;

    // Google Maps
    private GoogleMap googleMap;
    public static LocationManager locationManager;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private static final int CHECK_PERMISSION_REQUEST_WRITE_STORAGE = 51;

    // Evento de Criaçao
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Criando Visualização das Paginas
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

       // Criando Layouts da Tabs
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        try {
            // Carrega mapa
            inicializaMapa();
            Log.d(TAG,"Inicializando Google Maps");

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Logs de Evento Criado
        Log.d(TAG, "Layout iniciado MainActivity!");

        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }

        HashMap<String, String> user = db.getUserDetails();
        String nome = user.get("name");
        String email = user.get("email");
        Log.d(TAG, "Recebido dados do BD");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // Adicioando Nome e Email na Header
        View header = LayoutInflater.from(this).inflate(R.layout.nav_header_main, null);
        navigationView.addHeaderView(header);
        txtNome = (TextView) header.findViewById(R.id.nome);
        txtEmail = (TextView) header.findViewById(R.id.email);
        txtNome.setText("Olá: " + nome);
        txtEmail.setText("Conectado: " + email);
        Log.d(TAG, "Layout da Gaveta de navegação iniciado MainActivity ");

        /*// Manipulando Data e Hora
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
        }
        Log.d(TAG, "Exibindo Hora e Saudações");*/

    }

    /**********
     * Criando metodos VisualizarPaginas
     * *********/
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OneFragment(), "GPS");
        adapter.addFragment(new TwoFragment(), "Mapas");
        adapter.addFragment(new ThreeFragment(), "Lista");
        viewPager.setAdapter(adapter);
    }

    /************
     * Criando adaptação das Paginas
     * ***********/
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
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
     * Função camera zoom automatico com localização atual
     * */
    private void setUpMap() {

        checkPermissionWriteExtStorage(CHECK_PERMISSION_REQUEST_WRITE_STORAGE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)

            return;
        }
        googleMap.setMyLocationEnabled(true);
        LocationManager locationManager = MainActivity.locationManager;
        Criteria criteria = new Criteria();

        final Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude())) // Define o centro do mapa para localização do usuário
                    .zoom(17)                   // Define o zoom
                    .bearing(90)                // Define a orientação da câmara para leste
                    .tilt(40)                   // Define a inclinação da câmara para 30 graus
                    .build();                   // Cria uma CameraPosition para o construtor
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }

    }

    /**
     * Função carregar mapa. Se o mapa não é criado ele irá cria-lo para você
     * */
    private void inicializaMapa() {
        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.getUiSettings().setRotateGesturesEnabled(true);
            googleMap.getUiSettings().setAllGesturesEnabled(true);

            // Checando se o map está criado ou não com sucesso
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Desculpe! incapaz de criar mapas", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    /**
     * Ao fazer logout o usuário vai setar isLoggedIn para false
     * e desconectar, deletando usuario do SQLite
     */
    private void logoutUser() {
        session.setLogin(false);
        db.deleteUsers();
        // Inicia a tela de Login
        Log.d(TAG, "Tela de Login lançada");
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.acao_settings) {

            Log.d(TAG, "Ação configurações clicada");
            return true;

        } else if (id == R.id.acao_busca) {

            Toast.makeText(getApplicationContext(), "Ação clicada", Toast.LENGTH_LONG).show();
            return true;

        } else if (id == R.id.acao_sobre){

            Log.d(TAG, "Ação Sobre clicada");

            return true;
        } else if (id == R.id.acao_sair){

            logoutUser();
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
        } else if (id == R.id.nav_local) {

         //setUpMap();

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

                    Log.d(TAG,"Permissão negada, vazia! Funções inativas");

            }
                return;
            }

            // outras linhas  alterar para verificar se há outras
            // permissões para este APP
        }
    }
}
