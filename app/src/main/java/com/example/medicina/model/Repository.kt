package com.example.medicina.model

import android.content.Context
import android.widget.Toast
import com.example.medicina.database.*
import com.example.medicina.functions.MedicineFunctions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.map

object Repository {

    private lateinit var data: Data
    lateinit var medicineDao: MedicineDao
    lateinit var categoryDao: CategoryDao
    lateinit var medicineCategoryDao: MedicineCategoryDao

    fun initialize(context: Context){
        data = Data(context)
        medicineDao = data.db.medicineDao()
        categoryDao = data.db.categoryDao()
        medicineCategoryDao = data.db.medicineCategoryDao()
    }

    suspend fun clearAllData(){
        withContext(Dispatchers.IO){
            data.db.clearAllTables()
        }
    }

    suspend fun initializeSampleData(){

    }

    fun getAllMedicines(): Flow<List<Medicine>> = medicineDao.getAllMedicines()

    suspend fun upsertMedicine(updatedMedicine: Medicine): Long {
        return if (updatedMedicine.id == null) {
            medicineDao.insertMedicine(updatedMedicine)
        } else {
            medicineDao.updateMedicine(updatedMedicine)
            updatedMedicine.id.toLong()
        }
    }

    suspend fun deleteMedicine(medicineId: Int){
        medicineDao.deleteMedicine(medicineId)
    }

    fun getAllMedicineCategory(): Flow<List<MedicineCategory>> = medicineCategoryDao.getAllMedicineCategories()

    suspend fun upsertMedicineCategory(medicineId: Int, updatedMedicineCategory: List<MedicineCategory>) {
        medicineCategoryDao.replaceCategoriesForMedicine(medicineId, updatedMedicineCategory)
    }

    // CATEGORIES

    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()

    fun getCategoryById(id: Int): Category? = TestData.CategoryRepository.find { it.id == id }

    suspend fun upsertCategory(updatedCategory: Category): Long {
        return if (updatedCategory.id == null) {
            categoryDao.insertCategory(updatedCategory)
        } else {
            categoryDao.updateCategory(updatedCategory)
            updatedCategory.id.toLong()
        }
    }

    fun getAllMedicineCategories(): Flow<List<MedicineCategory>> = medicineCategoryDao.getAllMedicineCategories()

//        val index = TestData.CategoryRepository.indexOfFirst { it.id == updatedCategory.id }
//
//        if(index == -1){
//            val id = TestData.CategoryRepository.size + 1
//            val saveCategory = updatedCategory.copy(id = id)
//            TestData.CategoryRepository.add(saveCategory)
//            _categories.value = TestData.CategoryRepository.toList()
//            return id
//        } else {
//            TestData.CategoryRepository[index] = updatedCategory
//            _categories.value = TestData.CategoryRepository.toList()
//        }
//        return index
//    }

    suspend fun deleteCategory(categoryId: Int){
        categoryDao.deleteCategory(categoryId)
    }


    // ACCOUNTS

    private val _accounts = MutableStateFlow(TestData.AccountRepository.toList())
    val accounts: StateFlow<List<Account>> = _accounts

    init {
        println("Repository initialized with accounts size: ${_accounts.value.size}")
    }

    fun getAllAccounts(): StateFlow<List<Account>> = accounts

    fun getAccountById(id: Int): Account? = TestData.AccountRepository.find { it.id == id }

    fun upsertAccount(updatedAccount: Account): Account{
        val index = TestData.AccountRepository.indexOfFirst { it.id == updatedAccount.id }

        if(index == -1){
            val id = TestData.AccountRepository.size + 1
            val saveAccount = updatedAccount.copy(id = id)
            TestData.AccountRepository.add(saveAccount)
            _accounts.value = TestData.AccountRepository.toList()
            return saveAccount
        } else {
            TestData.AccountRepository[index] = updatedAccount
            _accounts.value = TestData.AccountRepository.toList()
        }
        return updatedAccount
    }

    // fun deleteAccount()


    // SUPPLIERS


    private val _suppliers = MutableStateFlow(TestData.SupplierRepository.toList())
    val suppliers: StateFlow<List<Supplier>> = _suppliers
    fun getAllSuppliers(): StateFlow<List<Supplier>> = suppliers

    fun getSupplierById(id: Int): Supplier? = TestData.SupplierRepository.find { it.id == id }

    fun upsertSupplier(upsertSupplier: Supplier){
        val index = TestData.SupplierRepository.indexOfFirst { it.id == upsertSupplier.id }

        if(index == -1){
            val id = TestData.SupplierRepository.size + 1
            val saveMedicine = upsertSupplier.copy(id = id)
            TestData.SupplierRepository.add(saveMedicine)
            _suppliers.value = TestData.SupplierRepository.toList()
        } else {
            TestData.SupplierRepository[index] = upsertSupplier
            _suppliers.value = TestData.SupplierRepository.toList()
        }
    }

    fun deleteSupplier(supplierId: Int){
        TestData.SupplierRepository.removeIf { it.id == supplierId }
    }

    // ORDERS

    private val _orders = MutableStateFlow(TestData.OrderRepository.toList())
    val orders: StateFlow<List<Order>> = _orders
    fun getAllOrders(): StateFlow<List<Order>> = orders

    fun getOrderById(id: Int): Order? = TestData.OrderRepository.find { it.id == id }

    fun upsertOrder(upsertOrder: Order){
        val index = TestData.OrderRepository.indexOfFirst { it.id == upsertOrder.id }

        if(index == -1){
            val id = TestData.OrderRepository.size + 1
            val saveOrder = upsertOrder.copy(id = id)
            TestData.OrderRepository.add(saveOrder)
            _orders.value = TestData.OrderRepository.toList()
        } else {
            TestData.OrderRepository[index] = upsertOrder
            _orders.value = TestData.OrderRepository.toList()
        }
    }

    fun deleteOrder(orderId: Int){
        TestData.OrderRepository.removeIf { it.id == orderId }
        _orders.value = TestData.OrderRepository.toList()
    }

    private val _generics = MutableStateFlow(TestData.GenericRepository.toList())
    val generics: StateFlow<List<Generic>> = _generics
    fun getAllGenerics(): StateFlow<List<Generic>> = generics

    fun getGenericById(id: Int): Generic? = TestData.GenericRepository.find { it.id == id }


    fun getDesignationById(id: Int): Designation? = TestData.DesignationRepository.find { it.id == id }
    fun getAllDesignation(): List<Designation> = TestData.DesignationRepository

    fun getNotificationById(id: Int): Notification? = TestData.NotificationRepository.find { it.id == id }
    fun getAllNotifications(): List<Notification> = TestData.NotificationRepository

    fun getRegulationById(id: Int): Regulation? = TestData.RegulationRepository.find { it.id == id }
    fun getAllRegulations(): List<Regulation> = TestData.RegulationRepository

    private val _brandedGenerics = MutableStateFlow(TestData.BrandedGenericRepository.toList())
    val brandedGenerics: StateFlow<List<BrandedGeneric>> = _brandedGenerics
    fun getAllBrandedGenerics(): StateFlow<List<BrandedGeneric>> = brandedGenerics

    fun updateBrandedGenericsForMedicine(medicineId: Int, newEntries: List<BrandedGeneric>) {
        _brandedGenerics.update { currentList ->
            // Remove all old entries for this medicine
            val withoutOld = currentList.filterNot { it.medicineId == medicineId }

            // Add new entries for this medicine
            withoutOld + newEntries
        }

        println("Updated brandedGenerics: ${_brandedGenerics.value}")
    }
}
