package com.example.medicina.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.medicina.model.Generic
import kotlinx.coroutines.flow.Flow
import androidx.room.OnConflictStrategy
import androidx.room.Update

@Dao
interface GenericDao {

    @Query("Select * FROM generics")
    fun getAllGenerics(): Flow<List<Generic>>

    @Query("Select * FROM generics WHERE id = :id")
    suspend fun getGenericById(id: Int): Generic?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGeneric(generic: Generic): Long

    @Update
    suspend fun updateGeneric(generic: Generic)

    @Query("DELETE FROM generics WHERE id = :id")
    suspend fun deleteGeneric(id: Int)

}