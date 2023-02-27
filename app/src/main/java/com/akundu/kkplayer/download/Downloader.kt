package com.akundu.kkplayer.download

interface Downloader {
    fun downloadFile(url: String, fileName: String): Long
}