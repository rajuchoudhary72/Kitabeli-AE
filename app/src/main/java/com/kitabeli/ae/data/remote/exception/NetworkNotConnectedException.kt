package com.kitabeli.ae.data.remote.exception

import java.io.IOException

class NetworkNotConnectedException : IOException() {
    // You can send any message whatever you want from here.
    override val message: String
        get() = "No internet found. Check your connection or try again."
}
