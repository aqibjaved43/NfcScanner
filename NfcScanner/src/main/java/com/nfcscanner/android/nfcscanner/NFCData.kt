package com.nfcscanner.android.nfcscanner

import android.nfc.tech.TagTechnology
import java.io.Serializable

class NFCData : Serializable {
    var tagType = ""
    var technologies = emptyList<String>()
    var serialNumber = ""

    //NfcA data
    var sak = ""
    var atqa = ""

    //NfcV data
    var dsfId= ""


}