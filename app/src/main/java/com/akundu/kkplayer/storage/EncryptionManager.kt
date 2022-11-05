package com.akundu.kkplayer.storage;

import java.io.File;

interface EncryptionManager {

    fun encryptFile(filePath: String, encryptionRule: String): File

    fun decryptFile(filePath: String, rule: String): File

}
