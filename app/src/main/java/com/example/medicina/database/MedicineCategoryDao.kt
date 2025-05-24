package com.example.medicina.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.medicina.model.MedicineCategory
import kotlinx.coroutines.flow.Flow
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.example.medicina.model.Category
import com.example.medicina.model.Medicine

@Dao
interface MedicineCategoryDao {

    @Query("SELECT * FROM medicineCategories")
    fun getAllMedicineCategories(): Flow<List<MedicineCategory>>

    @Query("Select * FROM medicineCategories WHERE categoryId = :id")
    fun getAllMedicineByCategory(id: Int): Flow<List<MedicineCategory>>

    @Query("Select * FROM medicineCategories WHERE medicineId = :id")
    fun getAllCategoryByMedicine(id: Int): Flow<List<MedicineCategory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(medicineCategories: List<MedicineCategory>): List<Long>

    @Query("DELETE FROM medicineCategories WHERE medicineId = :id")
    suspend fun deleteMedicineCategory(id: Int)

    @Query("DELETE FROM medicineCategories WHERE categoryId = :id")
    suspend fun deleteCategoryMedicine(id: Int)

    @Transaction
    suspend fun replaceCategoriesForMedicine(medicineId: Int, newCategories: List<MedicineCategory>){
        deleteMedicineCategory(medicineId)
        insertAll(newCategories)
    }
}