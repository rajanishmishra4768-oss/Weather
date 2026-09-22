package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("SELECT * FROM favorite_locations ORDER BY orderIndex ASC, addedAt ASC")
    fun getAllFavorites(): Flow<List<FavoriteLocationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(location: FavoriteLocationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorites(locations: List<FavoriteLocationEntity>)

    @Query("DELETE FROM favorite_locations WHERE id = :id")
    suspend fun deleteFavoriteById(id: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_locations WHERE id = :id)")
    fun isFavorite(id: String): Flow<Boolean>

    @Query("SELECT COUNT(*) FROM favorite_locations")
    suspend fun getFavoritesCount(): Int
}
