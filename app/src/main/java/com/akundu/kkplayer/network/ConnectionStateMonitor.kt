package com.akundu.kkplayer.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.NetworkRequest.Builder
import android.os.Handler
import android.util.Log
import com.akundu.kkplayer.MainActivity


@Suppress("JoinDeclarationAndAssignment")
class ConnectionStateMonitor : NetworkCallback() {

    private val networkRequest: NetworkRequest
    private lateinit var context: Context

    init {
        networkRequest = Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
    }

    fun enable(context: Context) {
        this.context = context
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerNetworkCallback(networkRequest, this)
    }

    // Likewise, you can have a disable method that simply calls ConnectivityManager.unregisterNetworkCallback(NetworkCallback) too.
    override fun onAvailable(network: Network) {
        Log.i(TAG, "onAvailable:")

        // TODO Do what you need to do here
        val mainHandler: Handler = Handler(context.mainLooper)

        val runnable: Runnable = Runnable {
            MainActivity().internetAvailable(context)
        }
        mainHandler.post(runnable)
    }

    override fun onUnavailable() {
        Log.w(TAG, "onUnavailable: No getting called onUnavailable() os issue.", )
        super.onUnavailable()
        val mainHandler: Handler = Handler(context.mainLooper)

        val runnable: Runnable = Runnable {
            MainActivity().noInternet(context)
        }
        mainHandler.post(runnable)
    }

    companion object {
        private const val TAG = "ConnectionStateMonitor"
    }
}