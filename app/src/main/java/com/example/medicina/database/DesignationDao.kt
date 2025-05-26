package com.example.medicina.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.medicina.model.Designation
import kotlinx.coroutines.flow.Flow
import androidx.room.OnConflictStrategy
import androidx.room.Update

@Dao
interface DesignationDao {

    @Query("Select * FROM designations")
    fun getAllDesignations(): Flow<List<Designation>>

    @Query("Select * FROM designations WHERE id = :id")
    suspend fun getDesignationById(id: Int): Designation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDesignation(designation: Designation): Long

    @Update
    suspend fun updateDesignation(designation: Designation)

    @Query("DELETE FROM designations WHERE id = :id")
    suspend fun deleteDesigantion(id: Int)

}