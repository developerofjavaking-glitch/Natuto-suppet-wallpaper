package com.example.data

import androidx.compose.ui.graphics.Color
import com.example.R
import com.example.ui.theme.ChakraGold
import com.example.ui.theme.ChidoriPurple
import com.example.ui.theme.GlowCyan
import com.example.ui.theme.RasenganBlue
import com.example.ui.theme.SharinganRed

enum class JutsuSoundType {
    RASENGAN,
    CHIDORI,
    SHARINGAN,
    RAIKIRI,
    FLYING_RAIJIN,
    CHAKRA_SURGE
}

data class NarutoCharacter(
    val id: String,
    val name: String,
    val title: String,
    val japaneseName: String,
    val clan: String,
    val chakraNature: String,
    val signatureJutsu: String,
    val quote: String,
    val drawableRes: Int,
    val primaryColor: Color,
    val secondaryColor: Color,
    val glowColor: Color,
    val sealKanji: String,
    val soundType: JutsuSoundType,
    val chargingSpeedTitle: String = "Ultra Fast Charging"
)

object CharacterRepository {
    val characters: List<NarutoCharacter> = listOf(
        NarutoCharacter(
            id = "naruto_sixpaths",
            name = "Naruto Uzumaki",
            title = "Six Paths Sage & Kurama Mode",
            japaneseName = "うずまき ナルト",
            clan = "Uzumaki Clan • Nine-Tails Jinchuriki",
            chakraNature = "Wind • Yang • Sage Chakra",
            signatureJutsu = "Sage Art: Super Tailed Beast Rasenshuriken",
            quote = "I never go back on my word... That's my ninja way!",
            drawableRes = R.drawable.img_naruto_kurama,
            primaryColor = ChakraGold,
            secondaryColor = RasenganBlue,
            glowColor = Color(0xFFFFD54F),
            sealKanji = "封", // Eight Trigrams Seal
            soundType = JutsuSoundType.RASENGAN,
            chargingSpeedTitle = "Kurama Chakra Warp • 120W"
        ),
        NarutoCharacter(
            id = "sasuke_rinnegan",
            name = "Sasuke Uchiha",
            title = "Eternal Mangekyo & Rinnegan",
            japaneseName = "うちは サスケ",
            clan = "Uchiha Clan • Indra Reincarnation",
            chakraNature = "Lightning • Fire • Blaze Release",
            signatureJutsu = "Chidori • Indra's Arrow & Susanoo",
            quote = "I have long since closed my eyes... My only goal is in the dark.",
            drawableRes = R.drawable.img_sasuke_susanoo,
            primaryColor = ChidoriPurple,
            secondaryColor = RasenganBlue,
            glowColor = Color(0xFFD8B4FE),
            sealKanji = "雷", // Lightning
            soundType = JutsuSoundType.CHIDORI,
            chargingSpeedTitle = "Chidori Voltage Supercharge • 100W"
        ),
        NarutoCharacter(
            id = "itachi_mangekyo",
            name = "Itachi Uchiha",
            title = "Tsukuyomi & Black Amaterasu",
            japaneseName = "うちは イタチ",
            clan = "Uchiha Clan • Akatsuki",
            chakraNature = "Fire • Wind • Water • Yin Release",
            signatureJutsu = "Tsukuyomi • Amaterasu Black Flames",
            quote = "Forgive me, Sasuke... This is the last time.",
            drawableRes = R.drawable.img_itachi_amaterasu,
            primaryColor = SharinganRed,
            secondaryColor = Color(0xFF1E1E1E),
            glowColor = Color(0xFFFF5252),
            sealKanji = "炎", // Fire
            soundType = JutsuSoundType.SHARINGAN,
            chargingSpeedTitle = "Amaterasu Infinite Power • 80W"
        ),
        NarutoCharacter(
            id = "kakashi_raikiri",
            name = "Kakashi Hatake",
            title = "The Copy Ninja & Sixth Hokage",
            japaneseName = "はたけ カカシ",
            clan = "Hatake Clan • Team 7 Sensei",
            chakraNature = "Lightning • Earth • Water • Fire",
            signatureJutsu = "Lightning Blade (Raikiri) • Kamui Warp",
            quote = "Those who break the rules are scum, but those who abandon their friends are worse.",
            drawableRes = R.drawable.img_kakashi_kamui,
            primaryColor = GlowCyan,
            secondaryColor = SharinganRed,
            glowColor = Color(0xFF80DEEA),
            sealKanji = "神", // Kamui / Divine
            soundType = JutsuSoundType.RAIKIRI,
            chargingSpeedTitle = "Raikiri Lightning Pulse • 67W"
        )
    )

    fun getCharacterById(id: String): NarutoCharacter {
        return characters.find { it.id == id } ?: characters.first()
    }
}
