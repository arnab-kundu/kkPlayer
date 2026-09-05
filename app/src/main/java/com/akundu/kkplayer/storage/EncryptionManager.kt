package com.akundu.kkplayer.storage

import android.content.Context
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

abstract class EncryptionManager {
    val READ_WRITE_BLOCK_BUFFER = 1024
    val ALGO_IMAGE_ENCRYPTOR = "AES/CBC/PKCS5Padding"
    val ALGO_SECRET_KEY = "AES"

    /**
     * **Encrypt File**
     *
     * @param context Context
     * @param srcFilePath String
     * @param encryptedFileName String
     * @return encryptedFile - File?
     */
    abstract fun encryptFile(
        context: Context,
        srcFilePath: String,
        encryptedFileName: String,
    ): File?

    /**
     * **Decrypt File**
     *
     * @param filePath String
     * @param rule String
     * @return decryptedFile - File
     */
    abstract fun decryptFile(
        context: Context,
        encryptedFilePath: String,
        outputFileName: String,
    ): File?

    /**
     * Encrypt file
     *
     * @param keyStr            Key is a String of length 16            (i.e. 128 bit)
     * @param specStr           SpecStr is also a String of length 16.  (i.e. 128 bit)
     * @param inputStream       InputStream of the file to be encrypted (Input File)
     * @param outputStream      OutputStream of the encrypted file.     (Output File)
     */
    @Throws(
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        InvalidAlgorithmParameterException::class,
        InvalidKeyException::class,
        IOException::class,
    )
    protected fun encryptToFile(
        keyStr: String,
        specStr: String,
        inputStream: InputStream,
        outputStream: OutputStream,
    ) = transform(Cipher.ENCRYPT_MODE, keyStr, specStr, inputStream, outputStream)

    /**
     * Decrypt file
     *
     * @param keyStr            Key is a String of length 16            (i.e. 128 bit)
     * @param specStr           SpecStr is also a String of length 16.  (i.e. 128 bit)
     * @param inputStream       InputStream of the file to be decrypted (Input File)
     * @param outputStream      OutputStream of the decrypted file.     (Output File)
     */
    @Throws(
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        InvalidAlgorithmParameterException::class,
        InvalidKeyException::class,
        IOException::class,
    )
    protected fun decryptToFile(
        keyStr: String,
        specStr: String,
        inputStream: InputStream,
        outputStream: OutputStream,
    ) = transform(Cipher.DECRYPT_MODE, keyStr, specStr, inputStream, outputStream)

    /**
     * Runs the cipher over the whole stream. [Cipher.doFinal] is called explicitly rather than
     * relying on [CipherOutputStream], which swallows the padding error that identifies input
     * the key cannot decrypt.
     */
    private fun transform(
        mode: Int,
        keyStr: String,
        specStr: String,
        inputStream: InputStream,
        outputStream: OutputStream,
    ) {
        val iv = IvParameterSpec(specStr.toByteArray(charset("UTF-8")))
        val keySpec = SecretKeySpec(keyStr.toByteArray(charset("UTF-8")), ALGO_SECRET_KEY)
        val cipher = Cipher.getInstance(ALGO_IMAGE_ENCRYPTOR)
        cipher.init(mode, keySpec, iv)

        inputStream.use { input ->
            outputStream.use { out ->
                var count: Int
                val buffer = ByteArray(READ_WRITE_BLOCK_BUFFER)
                while (input.read(buffer).also { count = it } > 0) {
                    cipher.update(buffer, 0, count)?.let { out.write(it) }
                }
                out.write(cipher.doFinal())
            }
        }
    }
}
