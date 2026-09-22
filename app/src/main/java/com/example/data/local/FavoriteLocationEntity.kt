package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.LocationItem

@Entity(tableName = "favorite_locations")
data class FavoriteLocationEntity(
    @PrimaryKey val id: String,
    val name: String,
    val country: String,
    val countryCode: String,
    val admin1: String? = null,
    val latitude: Double,
    val longitude: Double,
    val timezone: String? = null,
    val orderIndex: Int = 0,
    val isDefault: Boolean = false,
    val addedAt: Long = System.currentTimeMillis()
) {
    fun toLocationItem(): LocationItem {
        return LocationItem(
            id = id,
            name = name,
            country = country,
            countryCode = countryCode,
            admin1 = admin1,
            latitude = latitude,
            longitude = longitude,
            timezone = timezone,
            isFavorite = true
        )
    }

    companion object {
        fun fromLocationItem(item: LocationItem, orderIndex: Int = 0, isDefault: Boolean = false): FavoriteLocationEntity {
            return FavoriteLocationEntity(
                id = item.id,
                name = item.name,
                country = item.country,
                countryCode = item.countryCode,
                admin1 = item.admin1,
                latitude = item.latitude,
                longitude = item.longitude,
                timezone = item.timezone,
                orderIndex = orderIndex,
                isDefault = isDefault
            )
        }
    }
}
