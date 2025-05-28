package com.example.medicina.model

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.medicina.database.*
import com.example.medicina.functions.AccountFunctions
import com.example.medicina.functions.CategoryFunctions
import com.example.medicina.functions.MedicineFunctions
import com.example.medicina.functions.OrderFunctions
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

    suspend fun getCategoryById(id: Int): Category? = categoryDao.getCategoryById(id)


    fun getAllCategories(): Flow<List<Category>> = flow {
        try {
            Log.d("Repository", "getAllCategories: Fetching from API")
            val categoriesFromApi = withContext(Dispatchers.IO) {
                CategoryFunctions.getAllCategories()
            }
            categoriesFromApi?.let { apiList ->
                Log.d("Repository", "getAllCategories: Fetched ${apiList.size} from API. Caching to Room.")
                withContext(Dispatchers.IO) {
                    // Assuming categoryDao.insertCategory has OnConflictStrategy.REPLACE
                    apiList.forEach { categoryDao.insertCategory(it) }
                }
            }
            emit(categoriesFromApi ?: emptyList())
        } catch (e: Exception) {
            Log.e("Repository", "Error fetching categories from API: ${e.message}", e)
            emit(emptyList())
        }
    }.catch { e ->
        Log.e("Repository", "Exception in getAllCategories flow: ${e.message}", e)
        emit(emptyList())
    }

    suspend fun upsertCategory(categoryToUpsert: Category): Long {
        val serverAssignedId = withContext(Dispatchers.IO) {
            Log.d("Repository", "upsertCategory: Attempting API upsert for ${categoryToUpsert.categoryName}")
            CategoryFunctions.upsertCategory(categoryToUpsert)
        }

        if (serverAssignedId != -1L) {
            Log.d("Repository", "upsertCategory: API success, server ID $serverAssignedId for ${categoryToUpsert.categoryName}")
            val categoryForRoom: Category = categoryToUpsert.copy(id = serverAssignedId.toInt())

            return try {
                // Assuming categoryDao.insertCategory uses OnConflictStrategy.REPLACE
                categoryDao.insertCategory(categoryForRoom)
                Log.d("Repository", "upsertCategory: Room upsert successful for ID $serverAssignedId")
                serverAssignedId
            } catch (e: Exception) {
                Log.e("Repository", "upsertCategory: Room upsert failed for ID $serverAssignedId after API success: ${e.message}", e)
                -1L
            }
        } else {
            Log.e("Repository", "upsertCategory: API upsert FAILED for ${categoryToUpsert.categoryName}")
            return -1L
        }
    }

    suspend fun deleteCategory(categoryId: Int): Boolean {
        val serverSuccess = withContext(Dispatchers.IO) {
            Log.d("Repository", "deleteCategory: Attempting API delete for ID $categoryId")
            CategoryFunctions.deleteCategory(categoryId)
        }

        if (serverSuccess) {
            Log.d("Repository", "deleteCategory: API delete successful for ID $categoryId. Deleting from Room.")
            return try {
                categoryDao.deleteCategory(categoryId)
                Log.d("Repository", "deleteCategory: Room delete successful for ID $categoryId")
                true
            } catch (e: Exception) {
                Log.e("Repository", "deleteCategory: Room delete failed for ID $categoryId after API success: ${e.message}", e)
                false
            }
        } else {
            Log.e("Repository", "deleteCategory: API delete FAILED for ID $categoryId.")
            return false
        }
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

    fun getAllOrders(): Flow<List<Order>> = flow {
        try {
            Log.d("Repository", "getAllOrders: Fetching from API")
            val ordersFromApi = withContext(Dispatchers.IO) {
                OrderFunctions.getAllOrders()
            }
            ordersFromApi?.let { apiList ->
                Log.d("Repository", "getAllOrders: Fetched ${apiList.size} from API. Caching to Room.")
                withContext(Dispatchers.IO) {
                    // Assuming orderDao.insertOrder has OnConflictStrategy.REPLACE
                    apiList.forEach { orderDao.insertOrder(it) }
                }
            }
            emit(ordersFromApi ?: emptyList())
        } catch (e: Exception) {
            Log.e("Repository", "Error fetching orders from API: ${e.message}", e)
            emit(emptyList())
        }
    }.catch { e ->
        Log.e("Repository", "Exception in getAllOrders flow: ${e.message}", e)
        emit(emptyList())
    }
    suspend fun getOrderById(id: Int): Order? = orderDao.getOrderById(id)

    suspend fun upsertOrder(orderToUpsert: Order): Long {
        val serverAssignedId = withContext(Dispatchers.IO) {
            Log.d("Repository", "upsertOrder: Attempting API upsert for order ID: ${orderToUpsert.id ?: "NEW"}")
            OrderFunctions.upsertOrder(orderToUpsert)
        }

        if (serverAssignedId != -1L) {
            Log.d("Repository", "upsertOrder: API success, server ID $serverAssignedId")
            val orderForRoom: Order = orderToUpsert.copy(id = serverAssignedId.toInt())

            return try {
                // Assuming orderDao.insertOrder uses OnConflictStrategy.REPLACE
                orderDao.insertOrder(orderForRoom)
                Log.d("Repository", "upsertOrder: Room upsert successful for ID $serverAssignedId")
                serverAssignedId
            } catch (e: Exception) {
                Log.e("Repository", "upsertOrder: Room upsert failed for ID $serverAssignedId after API success: ${e.message}", e)
                -1L
            }
        } else {
            Log.e("Repository", "upsertOrder: API upsert FAILED for order ID: ${orderToUpsert.id ?: "NEW"}")
            return -1L
        }
    }

    suspend fun deleteOrder(orderId: Int): Boolean {
        val serverSuccess = withContext(Dispatchers.IO) {
            Log.d("Repository", "deleteOrder: Attempting API delete for ID $orderId")
            OrderFunctions.deleteOrder(orderId)
        }

        if (serverSuccess) {
            Log.d("Repository", "deleteOrder: API delete successful for ID $orderId. Deleting from Room.")
            return try {
                orderDao.deleteOrder(orderId) // Make sure this DAO method exists
                Log.d("Repository", "deleteOrder: Room delete successful for ID $orderId")
                true
            } catch (e: Exception) {
                Log.e("Repository", "deleteOrder: Room delete failed for ID $orderId after API success: ${e.message}", e)
                false
            }
        } else {
            Log.e("Repository", "deleteOrder: API delete FAILED for ID $orderId.")
            return false
        }
    }


    // NOTIFICATIONS

    fun getAllNotifications(): Flow<List<Notification>> = notificationDao.getAllNotifications()

    suspend fun getNotificationById(id: Int): Notification? = notificationDao.getNotificationById(id)


    // ACCOUNTS



    @JvmStatic
    fun getAccountsAtOnce(): List<Account> = runBlocking {
        accountDao.getAllAccounts().first()
    }

    suspend fun getAccountById(id: Int): Account? = accountDao.getAccountById(id)

    suspend fun upsertAccount(accountToUpsert: Account): Long {
        val serverAssignedId = withContext(Dispatchers.IO) {
            Log.d("Repository", "upsertAccount: Attempting API upsert for ${accountToUpsert.username}")
            AccountFunctions.upsertAccount(accountToUpsert) // accountToUpsert is your new Account.kt type
        }

        if (serverAssignedId != -1L) {
            Log.d("Repository", "upsertAccount: API success, server ID $serverAssignedId for ${accountToUpsert.username}")

            // Create accountForRoom using the data from accountToUpsert and the serverAssignedId
            // Since Account is a data class, .copy() is the cleanest way.
            val accountForRoom: Account = accountToUpsert.copy(id = serverAssignedId.toInt())

            Log.d("Repository", "upsertAccount: Preparing to upsert to local Room: ID ${accountForRoom.id} - User: ${accountForRoom.username}")

            return try {
                // Assuming accountDao.insertAccount uses OnConflictStrategy.REPLACE for upsert behavior
                accountDao.insertAccount(accountForRoom)
                Log.d("Repository", "upsertAccount: Room upsert successful for ID $serverAssignedId")
                serverAssignedId // Return the ID from the server
            } catch (e: Exception) {
                Log.e("Repository", "upsertAccount: Room upsert failed for ID $serverAssignedId after API success: ${e.message}", e)
                -1L // Indicate local DB error
            }
        } else {
            Log.e("Repository", "upsertAccount: API upsert FAILED for ${accountToUpsert.username}")
            return -1L // Indicate server failure
        }
    }

    fun getAllAccounts(): Flow<List<Account>> = flow {
        try {
            Log.d("Repository", "getAllAccounts: Fetching from API")
            val accountsFromApi = withContext(Dispatchers.IO) {
                AccountFunctions.getAllAccounts()
            }
            accountsFromApi?.let { apiList ->
                Log.d("Repository", "getAllAccounts: Fetched ${apiList.size} from API. Caching to Room.")
                withContext(Dispatchers.IO) {
                    // Assuming accountDao.insertAccount has OnConflictStrategy.REPLACE
                    apiList.forEach { accountDao.insertAccount(it) }
                }
            }
            emit(accountsFromApi ?: emptyList())
        } catch (e: Exception) {
            Log.e("Repository", "Error fetching accounts from API: ${e.message}", e)
            emit(emptyList()) // Fallback or emit from local DB if preferred
        }
    }.catch { e ->
        Log.e("Repository", "Exception in getAllAccounts flow: ${e.message}", e)
        emit(emptyList())
    }




    suspend fun deleteAccount(id: Int): Boolean {
        val serverSuccess = withContext(Dispatchers.IO) {
            Log.d("Repository", "deleteAccount: Attempting API delete for ID $id")
            AccountFunctions.deleteAccount(id)
        }

        if (serverSuccess) {
            Log.d("Repository", "deleteAccount: API delete successful for ID $id. Deleting from Room.")
            return try {
                accountDao.deleteAccount(id)
                Log.d("Repository", "deleteAccount: Room delete successful for ID $id")
                true
            } catch (e: Exception) {
                Log.e("Repository", "deleteAccount: Room delete failed for ID $id after API success: ${e.message}", e)
                false // Local DB error
            }
        } else {
            Log.e("Repository", "deleteAccount: API delete FAILED for ID $id.")
            return false // Server failure
        }
    }



    // DESIGNATION


    fun getAllDesignations(): Flow<List<Designation>> = designationDao.getAllDesignations()
    suspend fun getDesignationById(id: Int): Designation? = designationDao.getDesignationById(id)
}
