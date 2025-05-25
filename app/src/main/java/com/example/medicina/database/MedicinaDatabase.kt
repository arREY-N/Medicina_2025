package com.example.medicina.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.medicina.model.*

@Database(
    entities = [
        Medicine::class,
        Category::class,
        MedicineCategory::class,
        BrandedGeneric::class,
        Generic::class,
        Regulation::class,
        Supplier::class
    ],
    version = 8)

abstract class MedicinaDatabase: RoomDatabase() {

    abstract fun medicineDao(): MedicineDao
    abstract fun categoryDao(): CategoryDao
    abstract fun medicineCategoryDao(): MedicineCategoryDao
    abstract fun brandedGenericDao(): BrandedGenericDao
    abstract fun genericDao(): GenericDao
    abstract fun regulationDao(): RegulationDao
    abstract fun supplierDao(): SupplierDao

}