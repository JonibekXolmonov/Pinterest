package com.example.pinterest

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.TextView
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.ktx.Firebase

object DeepLink {

    fun createLongLink(partnerId: String) {
        val dynamicLink = Firebase.dynamicLinks.dynamicLink {
            link = Uri.parse("https://www.pinterest.com/jonibekxolmonov/")
            domainUriPrefix = "https://myphotoapp.page.link"
            // Open links with this app on Android
            androidParameters("com.example.pinterest") {
                minimumVersion = 100
            }
            // Open links with com.example.ios on iOS
            iosParameters("com.example.ios") {
                appStoreId = "123456789"
                minimumVersion = "1.0.1"
            }
        }

        val longLink = dynamicLink.uri
        Log.d("DeepLink ", longLink.toString())
    }

    fun createShortLink(partnerId: String) {
        Firebase.dynamicLinks.shortLinkAsync {
            link = Uri.parse(" https://www.pinterest.com/jonibekxolmonov/")
            domainUriPrefix = "https://myphotoapp.page.link"
            // Open links with this app on Android
            androidParameters("com.example.pinterest") {
                minimumVersion = 100
            }
            // Open links with com.example.ios on iOS
            iosParameters("com.example.ios") {
                appStoreId = "123456789"
                minimumVersion = "1.0.1"
            }
        }.addOnSuccessListener { (shortLink, _) ->
            Log.d("DeepLink ", shortLink.toString())
        }.addOnFailureListener {
            Log.d("DeepLink", it.toString())
        }
    }

    fun retrieveLink(intent: Intent) {
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener {
                var deepLink: Uri? = null
                if (it != null) {
                    deepLink = it.link
                }
                if (deepLink != null) {
                    val uri = Uri.parse(it.link.toString())
                    Log.d("TAG", "retrieveLink: $uri")
                    val productID =
                        uri.getQueryParameter("partnerId") // productID will be 61 as from the URL
                    Log.d("TAG", "retrieveLink: $productID")
                } else {
                    Log.d("DeepLink", "no link")
                    Log.d("TAG", "retrieveLink: no link")
                }
            }
            .addOnFailureListener {
                Log.d("DeepLink", it.toString())
            }
    }

}