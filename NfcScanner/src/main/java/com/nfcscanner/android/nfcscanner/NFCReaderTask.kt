package com.nfcscanner.android.nfcscanner

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.os.Build
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nfcscanner.android.databinding.BottomSheetNfcBinding

class NFCReaderTask(private val activity: FragmentActivity, private val adapter: NfcAdapter) {

    private var completeListener: ((NFCData) -> Unit)? = null
    private var failureListener: ((Exception) -> Unit)? = null
    private val bottomSheetDialog = BottomSheetDialog(activity)


    init {
        val binding = BottomSheetNfcBinding.inflate(bottomSheetDialog.layoutInflater)

        bottomSheetDialog.setContentView(binding.root)
        binding.btnCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
            failureListener?.invoke(Exception("Scanning Canceled"))
            stopForegroundDispatch()
        }
    }

    fun setOnCompleteListener(listener: (NFCData) -> Unit) = apply {
        this.completeListener = listener
    }

    fun setOnFailureListener(listener: (Exception) -> Unit) = apply {
        this.failureListener = listener
    }

    fun start() {
        setupForegroundDispatch()
        bottomSheetDialog.show()
    }


    @SuppressLint("WrongConstant")
    private fun setupForegroundDispatch() {
        val intent = Intent(activity, NFCDataReceiver::class.java)
        val pendingIntent = if (Build.VERSION.SDK_INT >= 31) {
            PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else PendingIntent.getBroadcast(
            activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val intentFilters = arrayOf(
            IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
            IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED),
        )
        NFCDataReceiver.onDataReceived {
            bottomSheetDialog.dismiss()
            completeListener?.invoke(it)
            stopForegroundDispatch()
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, intentFilters, null)
    }

    private fun stopForegroundDispatch() {
        adapter.disableForegroundDispatch(activity)
    }

}