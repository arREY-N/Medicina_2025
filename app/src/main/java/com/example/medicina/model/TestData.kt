package com.example.medicina.model

import androidx.compose.runtime.mutableStateListOf
import com.example.medicina.ui.theme.CustomGreen
import com.example.medicina.ui.theme.CustomRed
import java.time.LocalDate

class TestData {
    companion object {
        val AccountRepository = mutableListOf(
            Account(
                0,
                "Super",
                "Min",
                "Ad",
                0,
                "superAdmin",
                "superPassword"
            ),
            Account(
                1,
                "Admin",
                "One",
                "Ad",
                1,
                "adminOne",
                "adminPassword"
            ),
            Account(
                2,
                "Clerk",
                "One",
                "Cl",
                2,
                "clerkOne",
                "clerkPassword"
            )
        )

        val MedicineRepository = mutableStateListOf(
            Medicine(
                0,
                "Brand 1",
                "Generic 1",
                0,
                1,
                10f,
                "Description 1",
                1000
            ),
            Medicine(
                1,
                "Brand 2",
                "Generic 2",
                1,
                1,
                5f,
                "Description 2",
                500
            ),
            Medicine(
                2,
                "Brand 3",
                "Generic 3",
                2,
                1,
                5f,
                "Description 3",
                500
            ),
            Medicine(
                3,
                "Brand 4",
                "Generic 4",
                3,
                2,
                5f,
                "Description 4",
                500
            ),
            Medicine(
                4,
                "Brand 5",
                "Generic 5",
                1,
                2,
                5f,
                "Description 5",
                500
            ),
            Medicine(
                5,
                "Brand 6",
                "Generic 6",
                2,
                2,
                5f,
                "Description 6",
                500
            ),
            Medicine(
                6,
                "Brand 7",
                "Generic 7",
                3,
                2,
                5f,
                "Description 7",
                500
            )
        )

        val NotificationRepository = mutableListOf(
            Notification(
                0,
                "Banner",
                "Message",
                "Overview",
                LocalDate.of(2025, 5,7),
                CustomRed
            ),
            Notification(
                1,
                "Banner 2",
                "Message 2",
                "Overview 2",
                LocalDate.of(2025, 5,6),
                CustomGreen
            ),
            Notification(
                2,
                "Banner 3",
                "Message 3",
                "Overview 3",
                LocalDate.of(2025, 5,5),
                CustomGreen
            ),
            Notification(
                3,
                "Banner 4",
                "Message 4",
                "Overview 4",
                LocalDate.of(2025, 5,5),
                CustomGreen
            )
        )

        val CategoryRepository = mutableListOf(
            Category(
                0,
                categoryName = "Antibiotic",
                description = "Antibiotic Description"
            ),
            Category(
                1,
                categoryName = "Antipyretic",
                description = "Antipyretic Description"
            ),
            Category(
                2,
                categoryName = "Antacids",
                description = "Antacids Description"
            ),
            Category(
                3,
                categoryName = "Diuretics",
                description = "Diuretics Description"
            )
        )

        val OrderRepository = mutableListOf(
            Order(
                0,
                1,
                0,
                10,
                10f,
                LocalDate.of(2025, 5, 7),
                LocalDate.of(2026, 5, 7)
            ),
            Order(
                1,
                2,
                1,
                20,
                20f,
                LocalDate.of(2025, 5, 8),
                LocalDate.of(2025, 5, 8)
            ),
            Order(
                2,
                3,
                2,
                30,
                30f,
                LocalDate.of(2025, 5, 9),
                LocalDate.of(2025, 5, 9)
            ),
            Order(
                3,
                2,
                1,
                30,
                30f,
                LocalDate.of(2025, 5, 9),
                LocalDate.of(2025, 5, 9)
            ),
            Order(
                4,
                3,
                2,
                30,
                30f,
                LocalDate.of(2025, 5, 9),
                LocalDate.of(2025, 5, 9)
            )
        )
        val RegulationRepository = mutableListOf(
            Regulation(
                0,
                "OTC"
            ),
            Regulation(
                1,
                "Prescription"
            )
        )
        val SupplierRepository = mutableListOf(
            Supplier(
                0,
                "Supplier A"
            ),
            Supplier(
                1,
                "Supplier B"
            ),
            Supplier(
                2,
                "Supplier C"
            )
        )
        val DesignationRepository = mutableListOf(
            Designation(
                0,
                "Super Admin"
            ),
            Designation(
                1,
                "Admin"
            ),
            Designation(
                2,
                "Clerk"
            )
        )
    }
}