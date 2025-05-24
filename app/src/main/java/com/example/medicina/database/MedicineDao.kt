package com.example.medicina.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.medicina.model.Medicine
import kotlinx.coroutines.flow.Flow
import androidx.room.OnConflictStrategy
import androidx.room.Update

@Dao
interface MedicineDao {

    @Query("DELETE FROM sqlite_sequence WHERE name = :tableName")
    suspend fun resetAutoIncrement(tableName: String)

    @Query("DELETE FROM medicines WHERE id = :id")
    suspend fun deleteMedicine(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicine(medicine: Medicine): Long

    @Query("Select * FROM medicines WHERE id = :id")
    suspend fun getMedicineById(id: Int): Medicine?

    @Update
    suspend fun updateMedicine(medicing: Medicine)

    @Query("Select * FROM medicines")
    fun getAllMedicines(): Flow<List<Medicine>>
}