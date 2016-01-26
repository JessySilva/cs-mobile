package br.com.charlessilva.biblioteca;

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

/* * TODO: Adicionar nas Classe para chamar Funcoes.class
   * public Funcoes Funcoes = new Funcoes(this);
   */

/* TODO: Ativando WiFi para Debugar (Android Monitor)
 * =====FEITO NO ANDROID STUDIO=====
 * adb tcpip 5555
 * adb connect 192.168.200.10:5555
 * adb shell
 * setprop service.adb.tcp.port 5555
 */


 /* TODO: Clonagem de Biblioteca no repositório do Github
 * // Repositorio no Github
 * git clone https://android.googlesource.com/platform/frameworks/volley
 * git clone https://github.com/googlesamples/cardboard-java.git
 * // Comandos do Android Studio
 * android update project -p .
 * // Apache Ant
 * ant jar
 */

/* TODO: Gerando chaves e arquivo Keystore
 * keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
 * keytool -genkey -v -keystore my-release-key.keystore -alias alias_name -keyalg RSA -keysize 2048 -validity 10000
 * keytool -v -keystore my-release-key.keystore -list
 * Debug Key =  DF:A0:A4:AA:6D:4F:B2:AA:42:50:20:F6:7A:7C:8C:58:DA:91:BE:20
 *
 * */

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.util.Log;


public class Funcoes {

    private Context context;
    private static final String TAG = Funcoes.class.getSimpleName();

    public Funcoes(Context context) {
        this.context = context;
    }

    // Verifica se está Online
    public boolean isOnline() {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return manager.getActiveNetworkInfo() != null &&
                manager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    // Verifica se está conectado á Internet com relatório de Erro
    public boolean isConexaoInternet() {
        boolean connected = false;
        try {
            ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conn.getActiveNetworkInfo() != null
                    && conn.getActiveNetworkInfo().isAvailable()
                    && conn.getActiveNetworkInfo().isConnected()) {
                connected = true;
            }
        } catch (Exception e) {
            Log.d(TAG, "Erro na Conexão " + e + " Leia Mensagem " + e.getMessage());
        }
        return connected;
    }

    // Verifica se de armazenamento externo está disponível para leitura e gravação
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    // Verifica se de armazenamento externo está disponível para ler
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

}
