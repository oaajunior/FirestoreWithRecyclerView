package livroandroid.com.recyclerviewexample.util

import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast

object Util {

    //Verifica se o device está conectado a internet
    fun isDeviceConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }

    //Função que recebe uma string e exibe na tela do usuário em forma de mensagem
    fun showMessage(context: Context, msg: String?) {

        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }
}