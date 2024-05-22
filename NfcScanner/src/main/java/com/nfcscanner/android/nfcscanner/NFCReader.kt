package com.nfcscanner.android.nfcscanner

import android.content.Context
import android.content.Intent
import android.nfc.NfcManager
import android.provider.Settings
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class NFCReader private constructor(private val activity: FragmentActivity) {

    private val nfcManager = activity.getSystemService(Context.NFC_SERVICE) as NfcManager
    private val nfcAdapter = nfcManager.defaultAdapter
    private val readerTask = NFCReaderTask(activity, nfcAdapter)

    companion object {
        const val TAG = "MyNFCReader"
        fun create(activity: FragmentActivity) = NFCReader(activity)
    }


    private fun isNFCReaderAvailable(): Boolean {
        return nfcAdapter != null
    }

    private fun isNFCReaderEnabled(): Boolean {
        return nfcAdapter.isEnabled
    }

    fun startScanning(): NFCReaderTask {
        return readerTask.apply {
            if (isNFCReaderAvailable().not()) {
                showNoSupportDialog()
            } else if (isNFCReaderEnabled().not()) {
                showEnableDialog()
            } else readerTask.start()
        }
    }

    private fun showNoSupportDialog() {
        MaterialAlertDialogBuilder(activity).setTitle("Sorry")
                .setMessage("Your device do not have NFC support!")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }.show()


    }

    private fun showEnableDialog() {
        MaterialAlertDialogBuilder(activity).setTitle("NFC Disabled")
                .setMessage("Please turn on your NFC form settings")
                .setPositiveButton("Turn on") { dialog, _ ->
                    activity.startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
                    dialog.dismiss()
                }.setNegativeButton("OK") { dialog, _ -> dialog.dismiss() }.show()


    }


}