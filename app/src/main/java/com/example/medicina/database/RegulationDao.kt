package com.example.medicina.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.medicina.model.Regulation
import kotlinx.coroutines.flow.Flow
import androidx.room.OnConflictStrategy
import androidx.room.Update

@Dao
interface RegulationDao {

    @Query("Select * FROM regulations")
    fun getAllRegulations(): Flow<List<Regulation>>

    @Query("Select * FROM regulations WHERE id = :id")
    suspend fun getRegulationById(id: Int): Regulation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegulation(regulation: Regulation): Long

    @Update
    suspend fun updateRegulation(regulation: Regulation)

    @Query("DELETE FROM regulations WHERE id = :id")
    suspend fun deleteRegulation(id: Int)

}