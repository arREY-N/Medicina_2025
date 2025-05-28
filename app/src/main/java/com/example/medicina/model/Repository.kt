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
        var medicinesFromApi: List<Medicine>? = null
        try {
            Log.d("Repository", "getAllMedicines: Attempting to fetch from API.")
            medicinesFromApi = withContext(Dispatchers.IO) {
                // Make sure to use the fully qualified name if there's an ambiguity
                // or ensure the import is specific enough.
                com.example.medicina.functions.
                MedicineFunctions.getAllMedicines()
            }

            if (medicinesFromApi != null) {
                Log.d("Repository", "getAllMedicines: Fetched ${medicinesFromApi.size} medicines from API. Attempting to cache locally.")
                // Save/Update the fetched medicines into the local Room database
                withContext(Dispatchers.IO) { // Perform DB operations on IO dispatcher
                    try {
                        for (apiMedicine in medicinesFromApi) {
                            // Using insertMedicine with OnConflictStrategy.REPLACE acts as an upsert
                            medicineDao.insertMedicine(apiMedicine)
                        }
                        Log.d("Repository", "getAllMedicines: Successfully cached/updated ${medicinesFromApi.size} medicines in local DB.")
                    } catch (dbException: Exception) {
                        Log.e("Repository", "getAllMedicines: Error caching medicines in local DB: ${dbException.message}", dbException)
                        // Decide how to handle this:
                        // - Still emit medicinesFromApi (UI gets fresh data, cache might be stale/incomplete)
                        // - Emit an error or empty list if local caching is critical
                        // For now, we'll still emit API data.
                    }
                }
            } else {
                Log.d("Repository", "getAllMedicines: API returned null or empty list.")
            }

            // Emit the fetched list (or empty if null) from the API
            // The UI will get the fresh data from the API,
            // and the local DB is updated in the background.
            emit(medicinesFromApi ?: emptyList())

        } catch (apiException: Exception) {
            Log.e("Repository", "getAllMedicines: Error fetching medicines from API: ${apiException.message}", apiException)
            // Optionally, try to fetch from local DB as a fallback if API fails
            // For now, emitting empty list on API failure.
            // val localMedicines = medicineDao.getAllMedicines().first() // This would be a blocking call if used here, or emit from it
            // emit(localMedicines)
            emit(emptyList()) // Emit an empty list in case of API error
        }
    }.catch { e -> // Catch exceptions from the flow itself or downstream (less likely here)
        Log.e("Repository", "getAllMedicines: Exception in flow: ${e.message}", e)
        emit(emptyList()) // Fallback to empty list
    }

    suspend fun upsertMedicine(medicineToUpsert: Medicine): Long {
        val serverAssignedId: Long = withContext(Dispatchers.IO) {
            Log.d("Repository", "Attempting to upsert to server: ${medicineToUpsert.brandName}")
            MedicineFunctions.upsertMedicine(medicineToUpsert)
        }

        if (serverAssignedId != -1L) {
            Log.d("Repository", "Server upsert successful. Server ID: $serverAssignedId for ${medicineToUpsert.brandName}")

            // Prepare the medicine object for local Room upsert
            // Use the ID returned by the server, especially for inserts
            val medicineForRoom: Medicine
            if (medicineToUpsert.id == null) { // It was an insert
                medicineForRoom = medicineToUpsert.copy(id = serverAssignedId.toInt())
                Log.d("Repository", "New medicine, using server ID $serverAssignedId for local Room.")
            } else { // It was an update
                // Ensure the ID matches, though serverAssignedId should be the same as medicineToUpsert.id
                medicineForRoom = medicineToUpsert.copy(id = serverAssignedId.toInt())
                Log.d("Repository", "Updated medicine, server ID $serverAssignedId confirmed for local Room.")
            }

            // Now upsert to local Room database
            return try {
                if (medicineDao.getMedicineById(medicineForRoom.id!!) != null) { // Check if exists for update
                    medicineDao.updateMedicine(medicineForRoom)
                    Log.d("Repository", "Local Room: Updated medicine with ID ${medicineForRoom.id}")
                } else {
                    medicineDao.insertMedicine(medicineForRoom) // This might fail if ID already exists and it's not an update
                    Log.d("Repository", "Local Room: Inserted new medicine with ID ${medicineForRoom.id}")
                }
                serverAssignedId // Return the ID from the server
            } catch (e: Exception) {
                Log.e("Repository", "Error upserting to local Room DB after server success: ${e.message}", e)
                -1L // Indicate local DB error, even if server was fine
            }
        } else {
            Log.e("Repository", "Server upsert FAILED for: ${medicineToUpsert.brandName}. Local DB not touched.")
            return -1L // Indicate server failure
        }
    }

    suspend fun deleteMedicine(medicineId: Int): Boolean { // Return boolean for success/failure
        val serverSuccess = withContext(Dispatchers.IO) {
            Log.d("Repository", "Attempting to delete medicine ID: $medicineId from server.")
            MedicineFunctions.deleteMedicineFromServer(medicineId)
        }

        if (serverSuccess) {
            Log.d("Repository", "Server delete successful for ID: $medicineId. Deleting from local Room.")
            try {
                medicineDao.deleteMedicine(medicineId)
                Log.d("Repository", "Local Room: Deleted medicine with ID $medicineId")
                return true // Both server and local delete successful
            } catch (e: Exception) {
                Log.e("Repository", "Error deleting from local Room DB after server success for ID $medicineId: ${e.message}", e)
                // Server delete was successful, but local failed.
                // This leaves the data in an inconsistent state.
                // You might want to handle this case specifically (e.g., log for sync later).
                return false // Indicate local DB error
            }
        } else {
            Log.e("Repository", "Server delete FAILED for ID: $medicineId. Local DB not touched.")
            return false // Indicate server failure
        }
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
