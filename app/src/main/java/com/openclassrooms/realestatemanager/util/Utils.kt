package com.openclassrooms.realestatemanager.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

/**
 * Created by Philippe on 21/02/2018.
 */
object Utils {
    /**
     * Conversion d'un prix d'un bien immobilier (Dollars vers Euros)
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     *
     * @param dollars value to convert
     * @return converted value of money
     */
    @JvmStatic
    fun convertDollarToEuro(dollars: Int): Int {
        return (dollars * 0.812).roundToInt()
    }

    /**
     * Conversion d'un prix d'un bien immobilier (Euros vers Dollars)
     *
     * @param euros value to convert
     * @return converted value of money
     */
    @JvmStatic
    fun convertEuroToDollar(euros: Int): Int {
        return (euros * 1.137).roundToInt()
    }

    /**
     * Conversion de la date d'aujourd'hui en un format plus approprié
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     *
     * @return data format in english version
     */
    @JvmStatic
    val todayDate: String
        get() {
            val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            return dateFormat.format(Date())
        }

    @JvmStatic
    fun getUSFormatOfDate(date: Date): String {
        val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        return dateFormat.format(date)
    }

    /**
     * Verification de la connexion WI-FI
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     *
     * @return boolean true or false about question if is internet available
     */
    //@SuppressWarnings("deprecation")
    @JvmStatic
    fun isInternetAvailable(context: Context): Boolean {
        val cm =
            context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nI = cm.activeNetworkInfo
        val activeNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        val isModileActive: Boolean = activeNetwork?.isConnected == true
        //return nI != null && nI.state == NetworkInfo.State.CONNECTED
        return nI != null && isModileActive
    }

    /**
     * Alternative solution
     *
    fun isInternetConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }
    */
}