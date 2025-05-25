package com.example.medicina.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.medicina.model.BrandedGeneric
import kotlinx.coroutines.flow.Flow
import androidx.room.OnConflictStrategy
import androidx.room.Transaction

@Dao
interface BrandedGenericDao {

    @Query("Select * FROM brandedGenerics")
    fun getAllBrandedGenerics(): Flow<List<BrandedGeneric>>

    @Query("Select * FROM brandedGenerics WHERE medicineId = :id")
    suspend fun getGenericsByMedicine(id: Int): BrandedGeneric?

    @Query("Select * FROM brandedGenerics WHERE genericId = :id")
    suspend fun getMedicinesByGeneric(id: Int): BrandedGeneric?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(brandedGenerics: List<BrandedGeneric>): List<Long>

    @Query("DELETE FROM brandedGenerics WHERE medicineId = :id")
    suspend fun deleteBrandedGenerics(id: Int)

    @Transaction
    suspend fun replaceCategoriesForMedicine(medicineId: Int, newGenerics: List<BrandedGeneric>){
        deleteBrandedGenerics(medicineId)
        insertAll(newGenerics)
    }

}