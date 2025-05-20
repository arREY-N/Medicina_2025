package com.example.medicina.model

import android.widget.Toast
import com.example.medicina.functions.MedicineFunctions
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter

object Repository {
    // get all medicines from repository
    private val _medicines = MutableStateFlow(TestData.MedicineRepository.toList())
    val medicines: StateFlow<List<Medicine>> = _medicines
    fun getAllMedicines(): StateFlow<List<Medicine>> = medicines

    fun getMedicineById(id: Int): Medicine? = TestData.MedicineRepository.find { it.id == id }
    fun getMedicinesByName(name: String) : List<Medicine> {
        return medicines.value.filter{
            it.brandName.contains(name, ignoreCase = true)
        }
    }

    fun upsertMedicine(updatedMedicine: Medicine): Int{
        val index = TestData.MedicineRepository.indexOfFirst { it.id == updatedMedicine.id }

        if(index == -1){
            val id = TestData.MedicineRepository.size
            val saveMedicine = updatedMedicine.copy(id = id)
            TestData.MedicineRepository.add(saveMedicine)
            _medicines.value = TestData.MedicineRepository.toList()
            return id
        }

        TestData.MedicineRepository[index] = updatedMedicine
        _medicines.value = TestData.MedicineRepository.toList()
        return index
    }

    fun deleteMedicine(medicineId: Int){
        TestData.MedicineRepository.removeIf { it.id == medicineId }
        _medicines.value = TestData.MedicineRepository.toList()
    }

    fun getMedicinesByCategory(categoryId: Int) : List<Medicine> {
        val medicines = getAllMedicines()
        val categoryMedicine: MutableList<Medicine> = mutableListOf()
        medicines.value.forEach { medicine ->
            if(medicine.categoryId == categoryId){
                categoryMedicine.add(medicine)
            }
        }
        return categoryMedicine
    }


    // CATEGORIES


    private val _categories = MutableStateFlow(TestData.CategoryRepository.toList())
    val categories: StateFlow<List<Category>> = _categories
    fun getAllCategories(): StateFlow<List<Category>> = categories

    fun getCategoryById(id: Int): Category? = TestData.CategoryRepository.find { it.id == id }

    fun upsertCategory(updatedCategory: Category): Int{
        val index = TestData.CategoryRepository.indexOfFirst { it.id == updatedCategory.id }

        if(index == -1){
            val id = TestData.CategoryRepository.size + 1
            val saveCategory = updatedCategory.copy(id = id)
            TestData.CategoryRepository.add(saveCategory)
            _categories.value = TestData.CategoryRepository.toList()
            return id
        } else {
            TestData.CategoryRepository[index] = updatedCategory
            _categories.value = TestData.CategoryRepository.toList()
        }
        return index
    }

    fun deleteCategory(categoryId: Int){
        TestData.CategoryRepository.removeIf { it.id == categoryId }
        _categories.value = TestData.CategoryRepository.toList()
    }


    // ACCOUNTS

    private val _accounts = MutableStateFlow(TestData.AccountRepository.toList())
    val accounts: StateFlow<List<Account>> = _accounts
    fun getAllAccounts(): StateFlow<List<Account>> = accounts

    fun getAccountById(id: Int): Account? = TestData.AccountRepository.find { it.id == id }

    fun upsertAccount(updatedAccount: Account){
        val index = TestData.AccountRepository.indexOfFirst { it.id == updatedAccount.id }

        if(index == -1){
            val id = TestData.MedicineRepository.size + 1
            val saveAccount = updatedAccount.copy(id = id)
            TestData.AccountRepository.add(saveAccount)
            _accounts.value = TestData.AccountRepository.toList()
        } else {
            TestData.AccountRepository[index] = updatedAccount
            _accounts.value = TestData.AccountRepository.toList()
        }
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


}
