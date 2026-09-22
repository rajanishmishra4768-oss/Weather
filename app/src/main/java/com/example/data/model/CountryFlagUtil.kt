package com.example.data.model

object CountryFlagUtil {
    fun getFlagEmoji(countryCode: String?): String {
        if (countryCode.isNullOrBlank() || countryCode.length != 2) {
            return "📍"
        }
        val upper = countryCode.uppercase()
        val firstChar = upper[0]
        val secondChar = upper[1]

        if (firstChar !in 'A'..'Z' || secondChar !in 'A'..'Z') {
            return "📍"
        }

        val firstCodePoint = 0x1F1E6 + (firstChar - 'A')
        val secondCodePoint = 0x1F1E6 + (secondChar - 'A')

        return String(Character.toChars(firstCodePoint)) + String(Character.toChars(secondCodePoint))
    }
}
