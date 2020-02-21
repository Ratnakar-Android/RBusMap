package com.redbus.map.ui

import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


open class BaseActivity : AppCompatActivity() {
    var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun displayErrorToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun displayErrorDialog() {

    }

    fun displayNetworkErrorDialog() {
        try {
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setPositiveButton("Ok") { dialoginterface: DialogInterface?, which: Int ->

            }
            alertDialog = alertDialogBuilder.create()
            alertDialog?.setMessage("Internet not available, Please check your internet connection and try again.")
            alertDialog?.show()
        } catch (e: Exception) {
        }
    }

    override fun onStop() {
        super.onStop()

        if(alertDialog?.isShowing ?: false) alertDialog?.dismiss()
    }
}