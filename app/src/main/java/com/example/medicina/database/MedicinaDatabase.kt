package com.example.medicina.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.medicina.model.*

@Database(
    entities = [
        Medicine::class,
        Category::class,
        MedicineCategory::class,
        BrandedGeneric::class,
        Generic::class,
        Regulation::class,
        Supplier::class,
        Order::class,
        Notification::class,
        Account::class,
        Designation::class
    ],
    version = 12)

@TypeConverters(Converters::class)
abstract class MedicinaDatabase: RoomDatabase() {

    abstract fun medicineDao(): MedicineDao
    abstract fun categoryDao(): CategoryDao
    abstract fun medicineCategoryDao(): MedicineCategoryDao
    abstract fun brandedGenericDao(): BrandedGenericDao
    abstract fun genericDao(): GenericDao
    abstract fun regulationDao(): RegulationDao
    abstract fun supplierDao(): SupplierDao
    abstract fun orderDao(): OrderDao
    abstract fun notificationDao(): NotificationDao
    abstract fun accountDao(): AccountDao
    abstract fun designationDao(): DesignationDao

}