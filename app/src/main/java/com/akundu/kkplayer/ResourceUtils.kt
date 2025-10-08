package com.akundu.kkplayer

import kotlin.random.Random

/**
 * GetDrawable provides the movie avatar.
 * @param movie String
 */
fun getDrawable(movie: String): Int {
    val listOfAvatar =
        listOf(
            R.drawable.ic_music_album_avatar,
            R.drawable.ic_music_album_avatar1,
        )
    val randomIndex = Random.nextInt(listOfAvatar.size)
    val randomAvatar = listOfAvatar[randomIndex]
    return when (movie) {
        // "Bajrangi Bhaijaan" -> R.drawable.bajrangi_bhaijaan
        // "Bhool Bhulaiyaa" -> R.drawable.bhool_bhulaiyaa
        // "Crook" -> R.drawable.crook
        // "EK THA TIGER" -> R.drawable.ek_tha_tiger
        // "Force" -> R.drawable.force
        // "G" -> R.drawable.g
        // "Gangster" -> R.drawable.gangster
        // "Golmaal Returns" -> R.drawable.golmaal_returns
        // "Jannat" -> R.drawable.jannat
        // "Jism" -> R.drawable.jism
        // "Kabir Singh (2019)" -> R.drawable.kabir_singh
        // "Kites" -> R.drawable.kites
        // "Laali Ki Shaadi" -> R.drawable.laali_ki_shaadi
        // "Musafir" -> R.drawable.musafir
        // "New York" -> R.drawable.new_york
        // "Om Shanti Om" -> R.drawable.om_shanti_om
        // "Raaz Reboot" -> R.drawable.raaz_reboot
        // "Race" -> R.drawable.race
        // "Raees" -> R.drawable.raees
        // "raqeeb" -> R.drawable.raqeeb
        // "Raaz - TMC" -> R.drawable.razz
        // "Razz" -> R.drawable.razz
        // "Saathiya" -> R.drawable.saathiya
        // "The Killer" -> R.drawable.the_killer
        // "Life...Metro" -> R.drawable.life_metro
        // "Live-The Train" -> R.drawable.the_train
        // "Woh Lamhe" -> R.drawable.woh_lamhe
        // "Zeher" -> R.drawable.zeher
        else -> randomAvatar
    }
}
