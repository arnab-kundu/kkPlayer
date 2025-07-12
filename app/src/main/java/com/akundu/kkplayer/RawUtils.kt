package com.akundu.kkplayer

import android.util.Log
import java.lang.reflect.Field


fun listRaw() {
    val fields: Array<Field> = R.raw::class.java.fields
    for (count in fields.indices) {
        Log.i("Raw Asset: ", fields[count].name)
    }
}

/**
 * Get the Raw files ResourceID using the filename.
 * Throws RuntimeException if filename is not exist in RAW folder.
 * @param fileName
 * @return ResourceID - For example: R.raw.i_dont_wanna_live_forever_fifty_shades_darker
 */
fun getRawFileResourceID(fileName: String): Int {
    val fileNameWithoutExtension: String = fileName.substringBeforeLast('.')

    val fields: Array<Field> = R.raw::class.java.fields
    for (count in fields.indices) {
        Logg.i("Raw Asset: ${fields[count].name}")

        if (fields[count].name == fileNameWithoutExtension) {
            return fields[count].getInt(fields[count])
        }
    }
    throw RuntimeException("ResourceID not found")
}