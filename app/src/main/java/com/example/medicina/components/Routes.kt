package com.example.medicina.components

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Search : Screen("search")
    object MainMenu : Screen("mainMenu")

    object Accounts : Screen("accounts")
    object Categories : Screen("categories")
    object Inventory : Screen("inventory")
    object Notifications : Screen("notifications")
    object Orders : Screen("orders")
    object Suppliers : Screen("suppliers")


    object ViewCategory : Screen("medicineCategory?categoryID={categoryID}"){
        fun createRoute(categoryID: Int) = "medicineCategory?categoryID=$categoryID"
    }
    object ViewMedicine : Screen("medicine?medicineID={medicineID}"){
        fun createRoute(medicineID: Int) = "medicine?medicineID=$medicineID"
    }
    object ViewNotification : Screen("notification?notificationID={notificationID}"){
        fun createRoute(notificationID: Int?) = "notification?notificationID=$notificationID"
    }
    object ViewAccount : Screen("account?accountID={accountID}"){
        fun createRoute(accountID: Int?) = "account?accountID=$accountID"
    }
    object ViewSupplier : Screen("supplier?supplierID={supplierID}"){
        fun createRoute(supplierID: Int?) = "supplier?supplierID=$supplierID"
    }


    object UpsertCategory : Screen("upsertCategory?categoryID={categoryID}"){
        fun createRoute(categoryID: Int) = "upsertCategory?categoryID=$categoryID"
    }
    object UpsertMedicine : Screen("upsertMedicine?medicineID={medicineID}"){
        fun createRoute(medicineID: Int) = "upsertMedicine?medicineID=$medicineID"
    }
    object UpsertOrder : Screen("upsertOrder?orderID={orderID}"){
        fun createRoute(orderID: Int) = "upsertOrder?orderID=$orderID"
    }
    object UpsertSupplier : Screen("upsertSupplier?supplierID={supplierID}"){
        fun createRoute(supplierID: Int) = "upsertSupplier?supplierID=$supplierID"
    }
}
