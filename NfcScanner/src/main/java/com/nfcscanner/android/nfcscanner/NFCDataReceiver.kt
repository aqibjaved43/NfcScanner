package com.nfcscanner.android.nfcscanner

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.nfc.tech.NfcA
import android.nfc.tech.NfcB
import android.nfc.tech.NfcBarcode
import android.nfc.tech.NfcF
import android.nfc.tech.NfcV
import com.nfc.extensions.parcelable

class NFCDataReceiver : BroadcastReceiver() {
    companion object {
        private var onDataReceived: ((NFCData) -> Unit)? = null

        fun onDataReceived(onDataReceived: (NFCData) -> Unit) {
            Companion.onDataReceived = onDataReceived
        }
    }

    override fun onReceive(p0: Context?, p1: Intent?) {
        val nfcData = NFCData()

        val tag = p1?.parcelable<Tag>(NfcAdapter.EXTRA_TAG)
        val techList = tag?.techList ?: arrayOf()

        nfcData.tagType = determineTagType(techList.toList())
        nfcData.serialNumber = determineSerialNumber(tag)
        nfcData.technologies = determineTechnologiesAvailable(techList.toList())

        if (nfcData.technologies.contains(NfcA::class.java.simpleName)) handleNfcA(tag, nfcData)
        if (nfcData.technologies.contains(NfcV::class.java.simpleName)) handleNfcV(tag, nfcData)

        onDataReceived?.invoke(nfcData)


    }


    private fun determineSerialNumber(tag: Tag?) =
        tag?.id?.joinToString(":") { byte -> "%02x".format(byte and 0xFF) } ?: ""

    private fun determineTagType(techList: List<String>) = when {
        techList.contains(NfcA::class.java.name) -> "ISO 14443-3A"
        techList.contains(NfcB::class.java.name) -> "ISO 14443-3B"
        techList.contains(NfcF::class.java.name) -> "JIS 6319-4"
        techList.contains(NfcV::class.java.name) -> "ISO 15693"
        techList.contains(IsoDep::class.java.name) -> "ISO 1444-4"
        else -> "Unknown"
    }

    private fun determineTechnologiesAvailable(techList: List<String>): List<String> {

        0 and 1
        val technologies = arrayListOf<String>()

        if (techList.contains(IsoDep::class.java.name)) technologies.add(IsoDep::class.java.simpleName)
        if (techList.contains(MifareClassic::class.java.name)) technologies.add(MifareClassic::class.java.simpleName)
        if (techList.contains(MifareUltralight::class.java.name)) technologies.add(MifareUltralight::class.java.simpleName)
        if (techList.contains(Ndef::class.java.name)) technologies.add(Ndef::class.java.simpleName)
        if (techList.contains(NdefFormatable::class.java.name)) technologies.add(NdefFormatable::class.java.simpleName)
        if (techList.contains(NfcA::class.java.name)) technologies.add(NfcA::class.java.simpleName)
        if (techList.contains(NfcB::class.java.name)) technologies.add(NfcB::class.java.simpleName)
        if (techList.contains(NfcBarcode::class.java.name)) technologies.add(NfcBarcode::class.java.simpleName)
        if (techList.contains(NfcF::class.java.name)) technologies.add(NfcF::class.java.simpleName)
        if (techList.contains(NfcV::class.java.name)) technologies.add(NfcV::class.java.simpleName)

        return technologies
    }

    private fun handleNfcA(tag: Tag?, nfcData: NFCData): NFCData {

        val nfcA = NfcA.get(tag)
        if (nfcA != null) {
            try {
                nfcA.connect()
                nfcData.sak = nfcA.sak.toShortString()
                nfcData.atqa = nfcA.atqa.toHexString()
                nfcA.connect()
            } catch (_: Exception) {
            }

        }

        return nfcData
    }

    private fun handleNfcV(tag: Tag?, nfcData: NFCData): NFCData {

        val nfcV = NfcV.get(tag)
        if (nfcV != null) {
            try {
                nfcV.connect()
                nfcData.dsfId = nfcV.dsfId.toShort().toShortString()
                nfcV.connect()
            } catch (_: Exception) {
            }

        }

        return nfcData
    }

    private fun ByteArray.toHexString() = "0x${joinToString("") { "%02x".format(it and 0xFF) }}"
    private fun Short.toShortString() = "0x${toString(16)}"

}

private infix fun Byte.and(mask: Int): Int = toInt() and mask

/*
*   var tagFromIntent: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)?.also { rawMessages ->
                    val messages: List<NdefMessage> = rawMessages.map { it as NdefMessage }
                    // Process the messages array.
                    messages[0].records[0].payload[0]
                    binding.tvText.text = TextUtils.join(" ", messages)
                }*/
