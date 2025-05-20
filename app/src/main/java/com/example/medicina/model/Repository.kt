package com.example.medicina.model

import android.widget.Toast
import com.example.medicina.functions.MedicineFunctions
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter

object Repository {
    private val _medicines = MutableStateFlow(TestData.MedicineRepository.toList())
    val medicines: StateFlow<List<Medicine>> = _medicines

    fun getAllMedicines(): StateFlow<List<Medicine>> = medicines
    fun getMedicinesByName(name: String) : List<Medicine> {
        return medicines.value.filter{
            it.brandName.contains(name, ignoreCase = true)
        }
    }

    fun updateMedicine(updatedMedicine: Medicine): Int{
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

    fun getMedicineById(id: Int): Medicine? = TestData.MedicineRepository.find { it.id == id }

    fun deleteMedicine(medicineId: Int){
        TestData.MedicineRepository.removeIf { it.id == medicineId }
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

    fun upsertCategory(updatedCategory: Category){
        val index = TestData.CategoryRepository.indexOfFirst { it.id == updatedCategory.id }

        if(index == -1){
            val id = TestData.CategoryRepository.size + 1
            val saveCategory = updatedCategory.copy(id = id)
            TestData.CategoryRepository.add(saveCategory)
        } else {
            TestData.CategoryRepository[index] = updatedCategory
        }
    }

    fun deleteCategory(categoryId: Int){
        TestData.CategoryRepository.removeIf { it.id == categoryId }
    }

    fun getAccountById(id: Int): Account? = TestData.AccountRepository.find { it.id == id }
    fun getAllAccounts(): List<Account> = TestData.AccountRepository

    fun updateAccount(updatedAccount: Account){
        val index = TestData.AccountRepository.indexOfFirst { it.id == updatedAccount.id }

        if(index == -1){
            val id = TestData.MedicineRepository.size + 1
            val saveAccount = updatedAccount.copy(id = id)
            TestData.AccountRepository.add(saveAccount)
        } else {
            TestData.AccountRepository[index] = updatedAccount
        }
    }

    fun getNotificationById(id: Int): Notification? = TestData.NotificationRepository.find { it.id == id }
    fun getAllNotifications(): List<Notification> = TestData.NotificationRepository

    fun getCategoryById(id: Int): Category? = TestData.CategoryRepository.find { it.id == id }
    fun getAllCategories(): List<Category> = TestData.CategoryRepository

    fun getRegulationById(id: Int): Regulation? = TestData.RegulationRepository.find { it.id == id }
    fun getAllRegulations(): List<Regulation> = TestData.RegulationRepository

    fun getSupplierById(id: Int): Supplier? = TestData.SupplierRepository.find { it.id == id }
    fun getAllSuppliers(): List<Supplier> = TestData.SupplierRepository

    fun updateSupplier(updatedSupplier: Supplier){
        val index = TestData.SupplierRepository.indexOfFirst { it.id == updatedSupplier.id }

        if(index == -1){
            val id = TestData.SupplierRepository.size + 1
            val saveMedicine = updatedSupplier.copy(id = id)
            TestData.SupplierRepository.add(saveMedicine)
        } else {
            TestData.SupplierRepository[index] = updatedSupplier
        }
    }

    fun deleteSupplier(supplierId: Int){
        TestData.SupplierRepository.removeIf { it.id == supplierId }
    }

    fun getOrderById(id: Int): Order? = TestData.OrderRepository.find { it.id == id }
    fun getAllOrders(): List<Order> = TestData.OrderRepository

    fun deleteOrder(orderId: Int){
        TestData.OrderRepository.removeIf { it.id == orderId }
    }

    fun updateOrder(updatedOrder: Order){
        val index = TestData.OrderRepository.indexOfFirst { it.id == updatedOrder.id }

        if(index == -1){
            val id = TestData.OrderRepository.size + 1
            val saveOrder = updatedOrder.copy(id = id)
            TestData.OrderRepository.add(saveOrder)
        } else {
            TestData.OrderRepository[index] = updatedOrder
        }
    }

    fun getDesignationById(id: Int): Designation? = TestData.DesignationRepository.find { it.id == id }
    fun getAllDesignation(): List<Designation> = TestData.DesignationRepository
}
