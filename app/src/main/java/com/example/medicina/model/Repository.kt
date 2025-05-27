package com.example.medicina.model

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.medicina.database.*
import com.example.medicina.functions.MedicineFunctions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

object Repository {

    private lateinit var data: Data
    lateinit var medicineDao: MedicineDao
    lateinit var categoryDao: CategoryDao
    lateinit var medicineCategoryDao: MedicineCategoryDao
    lateinit var brandedGenericDao: BrandedGenericDao
    lateinit var genericDao: GenericDao
    lateinit var regulationDao: RegulationDao
    lateinit var supplierDao: SupplierDao
    lateinit var orderDao: OrderDao
    lateinit var notificationDao: NotificationDao
    lateinit var accountDao: AccountDao
    lateinit var designationDao: DesignationDao

    fun initialize(context: Context){
        data = Data(context)
        medicineDao = data.db.medicineDao()
        categoryDao = data.db.categoryDao()
        medicineCategoryDao = data.db.medicineCategoryDao()
        brandedGenericDao = data.db.brandedGenericDao()
        genericDao = data.db.genericDao()
        regulationDao = data.db.regulationDao()
        supplierDao = data.db.supplierDao()
        orderDao = data.db.orderDao()
        notificationDao = data.db.notificationDao()
        accountDao = data.db.accountDao()
        designationDao = data.db.designationDao()
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
        if(designationDao.getAllDesignations().first().isEmpty()){
            designationDao.insertDesignation(
                Designation(
                    designation = "Super Admin"
                )
            )
            designationDao.insertDesignation(
                Designation(
                    designation = "Admin"
                )
            )
            designationDao.insertDesignation(
                Designation(
                    designation = "User"
                )
            )
        }
    }

    fun getAllMedicines(): Flow<List<Medicine>> = flow {
        try {
            // MedicineFunctions.getAllMedicines() is a blocking network call,
            // so it must be run on a background thread.
            val medicinesFromApi = withContext(Dispatchers.IO) {
                // Make sure to use the fully qualified name if there's an ambiguity
                // or ensure the import is specific enough.
                com.example.medicina.functions.
                MedicineFunctions.getAllMedicines()
            }
            emit(medicinesFromApi ?: emptyList())
        // Emit the fetched list (or empty if null)
        } catch (e: Exception) {
            Log.e("Repository", "Error fetching medicines from API in Repository: ${e.message}", e)
            emit(emptyList())
        // Emit an empty list in case of an error
        }
    }.catch { e -> // Catch exceptions from the flow itself or downstream
        Log.e("Repository", "Exception in getAllMedicines flow: ${e.message}", e)
        emit(emptyList()) // Fallback to empty list
    }
    suspend fun getMedicineById(id: Int): Medicine? = medicineDao.getMedicineById(id)

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

    // ORDERS

    fun getAllOrders(): Flow<List<Order>> = orderDao.getAlOrders()

    suspend fun getOrderById(id: Int): Order? = orderDao.getOrderById(id)

    suspend fun upsertOrder(upsertOrder: Order): Long {
        return if (upsertOrder.id == null){
            orderDao.insertOrder(upsertOrder)
        } else {
            orderDao.updateOrder(upsertOrder)
            upsertOrder.id.toLong()
        }
    }

    suspend fun deleteOrder(orderId: Int){
        orderDao.deleteOrder(orderId)
    }


    // NOTIFICATIONS

    fun getAllNotifications(): Flow<List<Notification>> = notificationDao.getAllNotifications()

    suspend fun getNotificationById(id: Int): Notification? = notificationDao.getNotificationById(id)


    // ACCOUNTS


    fun getAllAccounts(): Flow<List<Account>> = accountDao.getAllAccounts()

    @JvmStatic
    fun getAccountsAtOnce(): List<Account> = runBlocking {
        accountDao.getAllAccounts().first()
    }

    suspend fun getAccountById(id: Int): Account? = accountDao.getAccountById(id)

    suspend fun upsertAccount(updatedAccount: Account): Long {
        return if(updatedAccount.id == null) {
            accountDao.insertAccount(updatedAccount)
        } else {
            accountDao.updateAccount(updatedAccount)
            updatedAccount.id.toLong()
        }
    }

    suspend fun deleteAccount(id: Int){
        accountDao.deleteAccount(id)
    }


    // DESIGNATION


    fun getAllDesignations(): Flow<List<Designation>> = designationDao.getAllDesignations()
    suspend fun getDesignationById(id: Int): Designation? = designationDao.getDesignationById(id)
}
