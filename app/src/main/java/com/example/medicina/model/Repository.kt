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
    lateinit var brandedGenericDao: BrandedGenericDao
    lateinit var genericDao: GenericDao
    lateinit var regulationDao: RegulationDao
    lateinit var supplierDao: SupplierDao

    fun initialize(context: Context){
        data = Data(context)
        medicineDao = data.db.medicineDao()
        categoryDao = data.db.categoryDao()
        medicineCategoryDao = data.db.medicineCategoryDao()
        brandedGenericDao = data.db.brandedGenericDao()
        genericDao = data.db.genericDao()
        regulationDao = data.db.regulationDao()
        supplierDao = data.db.supplierDao()
    }

    suspend fun clearAllData(){
        withContext(Dispatchers.IO){
            data.db.clearAllTables()
        }
    }

    suspend fun initializeSampleData(){
        if(regulationDao.getAllRegulations().first().isEmpty()){
            regulationDao.insertRegulation(
                Regulation(
                    regulation = "OTC"
                )
            )
            regulationDao.insertRegulation(
                Regulation(
                    regulation = "Prescription"
                )
            )
        }
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

    // medicine category
    fun getAllMedicineCategory(): Flow<List<MedicineCategory>> = medicineCategoryDao.getAllMedicineCategories()

    suspend fun upsertMedicineCategory(medicineId: Int, updatedMedicineCategory: List<MedicineCategory>) {
        medicineCategoryDao.replaceCategoriesForMedicine(medicineId, updatedMedicineCategory)
    }

    // medicine generics
    fun getAllBrandedGenerics(): Flow<List<BrandedGeneric>> = brandedGenericDao.getAllBrandedGenerics()

    suspend fun upsertBrandedGenerics(medicineId: Int, newEntries: List<BrandedGeneric>) {
        brandedGenericDao.replaceCategoriesForMedicine(medicineId, newEntries)
    }


    // GENERICS

    fun getAllGenerics(): Flow<List<Generic>> = genericDao.getAllGenerics()

    suspend fun getGenericById(id: Int): Generic? = genericDao.getGenericById(id)

    suspend fun upsertGeneric(generic: Generic): Long{
        return if (generic.id == null) {
            genericDao.insertGeneric(generic)
        } else {
            genericDao.updateGeneric(generic)
            generic.id.toLong()
        }
    }

    suspend fun deleteGeneric(generic: Generic){
        generic.id?.let{
            genericDao.deleteGeneric(generic.id)
        }
    }

    // CATEGORIES

    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()

    suspend fun getCategoryById(id: Int): Category? = categoryDao.getCategoryById(id)

    suspend fun upsertCategory(updatedCategory: Category): Long {
        return if (updatedCategory.id == null) {
            categoryDao.insertCategory(updatedCategory)
        } else {
            categoryDao.updateCategory(updatedCategory)
            updatedCategory.id.toLong()
        }
    }

    suspend fun deleteCategory(categoryId: Int){
        categoryDao.deleteCategory(categoryId)
    }


    // REGULATIONS

    fun getAllRegulations(): Flow<List<Regulation>> = regulationDao.getAllRegulations()

    suspend fun getRegulationById(id: Int): Regulation? = regulationDao.getRegulationById(id)


    // SUPPLIERS

    fun getAllSuppliers(): Flow<List<Supplier>> = supplierDao.getAllSuppliers()

    suspend fun getSupplierById(id: Int): Supplier? = supplierDao.getSupplierById(id)

    suspend fun upsertSupplier(updatedSupplier: Supplier): Long {
        return if (updatedSupplier.id == null) {
            supplierDao.insertSupplier(updatedSupplier)
        } else {
            supplierDao.updateSupplier(updatedSupplier)
            updatedSupplier.id.toLong()
        }
    }

    suspend fun deleteSupplier(supplierId: Int){
        supplierDao.deleteSupplier(supplierId)
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




    fun getDesignationById(id: Int): Designation? = TestData.DesignationRepository.find { it.id == id }
    fun getAllDesignation(): List<Designation> = TestData.DesignationRepository

    fun getNotificationById(id: Int): Notification? = TestData.NotificationRepository.find { it.id == id }
    fun getAllNotifications(): List<Notification> = TestData.NotificationRepository
}
