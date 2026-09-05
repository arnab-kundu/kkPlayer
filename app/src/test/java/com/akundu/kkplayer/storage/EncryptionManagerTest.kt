package com.akundu.kkplayer.storage

import android.content.Context
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FilterInputStream
import java.io.FilterOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import javax.crypto.BadPaddingException

/** The key material [AppFileManager] hands to the cipher. */
private const val KEY = "keyLength16digit"
private const val SPEC = "keySizeMustBe16-"

private const val AES_BLOCK_SIZE = 16

class EncryptionManagerTest {
    /**
     * [EncryptionManager] exposes its cipher primitives to subclasses only, so the tests reach
     * them through a subclass rather than through [AppFileManager]'s file handling.
     */
    private class TestEncryptionManager : EncryptionManager() {
        override fun encryptFile(
            context: Context,
            srcFilePath: String,
            encryptedFileName: String,
        ): File? = null

        override fun decryptFile(
            context: Context,
            encryptedFilePath: String,
            outputFileName: String,
        ): File? = null

        fun encrypt(
            input: InputStream,
            output: OutputStream,
            keyStr: String = KEY,
            specStr: String = SPEC,
        ) = encryptToFile(keyStr, specStr, input, output)

        fun decrypt(
            input: InputStream,
            output: OutputStream,
            keyStr: String = KEY,
            specStr: String = SPEC,
        ) = decryptToFile(keyStr, specStr, input, output)
    }

    private lateinit var encryptionManager: TestEncryptionManager

    @Before
    fun setUp() {
        encryptionManager = TestEncryptionManager()
    }

    private fun encrypt(
        plainText: ByteArray,
        keyStr: String = KEY,
        specStr: String = SPEC,
    ): ByteArray =
        ByteArrayOutputStream()
            .also { encryptionManager.encrypt(ByteArrayInputStream(plainText), it, keyStr, specStr) }
            .toByteArray()

    private fun decrypt(
        cipherText: ByteArray,
        keyStr: String = KEY,
        specStr: String = SPEC,
    ): ByteArray =
        ByteArrayOutputStream()
            .also { encryptionManager.decrypt(ByteArrayInputStream(cipherText), it, keyStr, specStr) }
            .toByteArray()

    @Test
    fun `encrypting then decrypting returns the original content`() {
        val plainText = "Tu hi meri sab hay".toByteArray()

        assertArrayEquals(plainText, decrypt(encrypt(plainText)))
    }

    @Test
    fun `encrypting hides the original content`() {
        val plainText = "Tu hi meri sab hay".toByteArray()

        val cipherText = encrypt(plainText)

        assertFalse(String(cipherText, Charsets.ISO_8859_1).contains("Tu hi meri sab hay"))
    }

    @Test
    fun `round trips content larger than the read buffer`() {
        // Three full buffers plus a partial one, so the streaming loop runs more than once and
        // ends on a short read.
        val plainText = ByteArray(encryptionManager.READ_WRITE_BLOCK_BUFFER * 3 + 17) { (it % 251).toByte() }

        assertArrayEquals(plainText, decrypt(encrypt(plainText)))
    }

    @Test
    fun `round trips an empty stream`() {
        val cipherText = encrypt(ByteArray(0))

        // An empty input still produces one block: PKCS5 pads it out to the cipher block size.
        assertEquals(AES_BLOCK_SIZE, cipherText.size)
        assertArrayEquals(ByteArray(0), decrypt(cipherText))
    }

    @Test
    fun `pads the ciphertext out to whole cipher blocks`() {
        val cipherText = encrypt("five!".toByteArray())

        assertEquals(AES_BLOCK_SIZE, cipherText.size)
        assertEquals(0, cipherText.size % AES_BLOCK_SIZE)
    }

    @Test
    fun `the fixed initialisation vector makes encryption deterministic`() {
        val plainText = "Tu hi meri sab hay".toByteArray()

        assertArrayEquals(encrypt(plainText), encrypt(plainText))
    }

    @Test(expected = BadPaddingException::class)
    fun `decrypting with a different key is rejected`() {
        val cipherText = encrypt("Tu hi meri sab hay".toByteArray())

        decrypt(cipherText, keyStr = "anotherKey16dig!")
    }

    @Test
    fun `decrypting with a different initialisation vector silently corrupts the first block`() {
        val plainText = ByteArray(64) { it.toByte() }
        val cipherText = encrypt(plainText)

        // CBC mixes the initialisation vector into the first block only, so a wrong one decrypts
        // without complaint and hands back damaged leading bytes instead of an error.
        val decrypted = decrypt(cipherText, specStr = "anotherSpec16di!")

        assertEquals(plainText.size, decrypted.size)
        assertNotEquals(plainText.take(AES_BLOCK_SIZE), decrypted.take(AES_BLOCK_SIZE))
        assertEquals(plainText.drop(AES_BLOCK_SIZE), decrypted.drop(AES_BLOCK_SIZE))
    }

    @Test(expected = InvalidKeyException::class)
    fun `a key that is not sixteen bytes is rejected`() {
        encrypt("Tu hi meri sab hay".toByteArray(), keyStr = "tooShort")
    }

    @Test(expected = InvalidAlgorithmParameterException::class)
    fun `an initialisation vector that is not sixteen bytes is rejected`() {
        encrypt("Tu hi meri sab hay".toByteArray(), specStr = "tooShort")
    }

    @Test
    fun `encrypting closes both streams`() {
        val input = ClosingTrackingInputStream(ByteArrayInputStream("Tu hi meri sab hay".toByteArray()))
        val output = ClosingTrackingOutputStream(ByteArrayOutputStream())

        encryptionManager.encrypt(input, output)

        assertTrue(input.isClosed)
        assertTrue(output.isClosed)
    }

    @Test
    fun `decrypting closes both streams`() {
        val input = ClosingTrackingInputStream(ByteArrayInputStream(encrypt("Tu hi meri sab hay".toByteArray())))
        val output = ClosingTrackingOutputStream(ByteArrayOutputStream())

        encryptionManager.decrypt(input, output)

        assertTrue(input.isClosed)
        assertTrue(output.isClosed)
    }

    @Test
    fun `uses AES in CBC mode with PKCS5 padding`() {
        assertEquals("AES/CBC/PKCS5Padding", encryptionManager.ALGO_IMAGE_ENCRYPTOR)
        assertEquals("AES", encryptionManager.ALGO_SECRET_KEY)
        assertEquals(1024, encryptionManager.READ_WRITE_BLOCK_BUFFER)
    }

    private class ClosingTrackingInputStream(
        delegate: InputStream,
    ) : FilterInputStream(delegate) {
        var isClosed = false
            private set

        override fun close() {
            isClosed = true
            super.close()
        }
    }

    private class ClosingTrackingOutputStream(
        delegate: OutputStream,
    ) : FilterOutputStream(delegate) {
        var isClosed = false
            private set

        override fun close() {
            isClosed = true
            super.close()
        }
    }
}
