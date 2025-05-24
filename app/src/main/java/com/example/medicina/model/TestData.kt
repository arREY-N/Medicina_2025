package com.example.medicina.model

import androidx.compose.runtime.mutableStateListOf
import com.example.medicina.ui.theme.CustomGreen
import com.example.medicina.ui.theme.CustomRed
import java.time.LocalDate

class TestData {
    companion object {
        val AccountRepository = mutableStateListOf(
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
            ),
            Account(
                3,
                "reyn",
                "reyn",
                "reyn",
                2,
                "a",
                "a"
            )
        )
        val GenericRepository = mutableStateListOf(
            Generic(0, "Paracetamol"),
            Generic(1, "Ibuprofen"),
            Generic(2, "Amoxicillin"),
            Generic(3, "Azithromycin"),
            Generic(4, "Salbutamol"),
            Generic(5, "Cetirizine"),
            Generic(6, "Fluoxetine"),
            Generic(7, "Risperidone"),
            Generic(8, "Ciprofloxacin"),
            Generic(9, "Doxycycline")
        )

        val MedicineRepository = mutableStateListOf(
            Medicine(0, "Biogesic", 0 , 10f, "For headache and minor pains"),
            Medicine(1, "Advil", 1, 12f, "Anti-inflammatory and pain relief"),
            Medicine(2, "Calpol", 0, 11.5f, "For pain and fever in children"),
            Medicine(3, "Medicol Advance", 1, 14.75f, "Pain reliever for adults"),
            Medicine(4, "Tempra Forte", 0,  13.25f, "Paracetamol for adults"),
            Medicine(5, "Motrin", 1, 15f, "NSAID pain reliever"),
            Medicine(6, "Feverall", 0,  9f, "Fever reducer"),
            Medicine(7, "Nurofen", 1,  13f, "Relieves pain and reduces fever"),
            Medicine(8, "Panadol", 0,  10.75f, "Pain and fever relief"),
            Medicine(9, "Tylenol", 0,  12.5f, "Used to treat mild to moderate pain and fever"),
            Medicine(10, "Dolan", 1,  10.25f, "Ibuprofen-based fever reducer"),
            Medicine(11, "Paracetamol Med", 0,  8.75f, "Fever and pain"),
            Medicine(12, "Amoxil", 2,  18f, "For bacterial infections"),
            Medicine(13, "Zithromax", 3,  20f, "Antibiotic for respiratory infections"),
            Medicine(14, "Ciprobay", 8,  22f, "Broad-spectrum antibiotic"),
            Medicine(15, "Doxycin", 9,  19.5f, "Used for acne and respiratory infections"),
            Medicine(16, "Moxillin", 2,  16.5f, "For bacterial throat and ear infections"),
            Medicine(17, "Cifran", 8,  21f, "Used for urinary and respiratory infections"),
            Medicine(18, "Ventolin", 4,  25f, "Asthma reliever"),
            Medicine(19, "Allerkid", 5,  14f, "Allergy medicine"),
            Medicine(20, "Salbutamol GPO", 4,  23.5f, "Bronchodilator"),
            Medicine(21, "Virlix", 5,  13.25f, "For allergy and respiratory symptoms"),
            Medicine(22, "Asmalin", 4,  22f, "Respiratory bronchodilator"),
            Medicine(23, "Zyrtec", 5,  15f, "Allergy relief"),
            Medicine(24, "Prozac", 6,  35f, "Antidepressant"),
            Medicine(25, "Risperdal", 7,  40f, "Antipsychotic medication"),
            Medicine(26, "Fluoxx", 6,  33f, "Used to treat depression and anxiety"),
            Medicine(27, "Rivastigmine", 7,  45f, "Used for schizophrenia"),
            Medicine(28, "Zoloft", 6,  38f, "Antidepressant SSRI"),
            Medicine(29, "Quetiapine", 7,  42f, "Bipolar disorder and schizophrenia")
        )



        val NotificationRepository = mutableStateListOf(
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

        val CategoryRepository = mutableStateListOf(
            Category(
                0,
                categoryName = "Analgesic",
                description = "Analgesics Description",
                hexColor = "#91b53e"
            ),
            Category(
                1,
                categoryName = "Antipyretic",
                description = "Antipyretics Description",
                hexColor = "#cc4125"
            ),
            Category(
                2,
                categoryName = "Antibiotic",
                description = "Antibiotics Description",
                hexColor = "#56e2c0"
            ),
            Category(
                3,
                categoryName = "Respiratory Drugs",
                description = "Respiratory Drugs Description",
                hexColor = "#4739b7"
            ),
            Category(
                4,
                categoryName = "Psychiatric Medication",
                description = "Psychiatric Medication Description",
                hexColor = "#cf7330"
            )
        )

        val OrderRepository = mutableStateListOf(
            Order(
                id = 0,
                medicineId = 22,
                supplierId = 4,
                quantity = 140,
                price = 12.5f,
                orderDate = LocalDate.of(2025, 1, 18),
                expirationDate = LocalDate.of(2028, 8, 5),
                remainingQuantity = 140
            ),
            Order(
                id = 1,
                medicineId = 1,
                supplierId = 1,
                quantity = 100,
                price = 10f,
                orderDate = LocalDate.of(2025, 1, 1),
                expirationDate = LocalDate.of(2026, 5, 14),
                remainingQuantity = 100
            ),
            Order(
                id = 2,
                medicineId = 1,
                supplierId = 2,
                quantity = 100,
                price = 13f,
                orderDate = LocalDate.of(2025, 2, 2),
                expirationDate = LocalDate.of(2026, 6, 14),
                remainingQuantity = 100
            ),
            Order(
                id = 3,
                medicineId = 2,
                supplierId = 4,
                quantity = 150,
                price = 12f,
                orderDate = LocalDate.of(2025, 2, 5),
                expirationDate = LocalDate.of(2026, 3, 18),
                remainingQuantity = 150
            ),
            Order(
                id = 4,
                medicineId = 2,
                supplierId = 3,
                quantity = 150,
                price = 15f,
                orderDate = LocalDate.of(2025, 3, 1),
                expirationDate = LocalDate.of(2027, 7, 7),
                remainingQuantity = 150
            ),
            Order(
                id = 5,
                medicineId = 11,
                supplierId = 1,
                quantity = 60,
                price = 11.25f,
                orderDate = LocalDate.of(2025, 4, 20),
                expirationDate = LocalDate.of(2027, 8, 8),
                remainingQuantity = 60
            ),
            Order(
                id = 6,
                medicineId = 11,
                supplierId = 3,
                quantity = 60,
                price = 10.25f,
                orderDate = LocalDate.of(2025, 9, 13),
                expirationDate = LocalDate.of(2027, 1, 30),
                remainingQuantity = 60
            ),
            Order(
                id = 7,
                medicineId = 19,
                supplierId = 4,
                quantity = 120,
                price = 25f,
                orderDate = LocalDate.of(2025, 2, 10),
                expirationDate = LocalDate.of(2026, 2, 14),
                remainingQuantity = 120
            ),
            Order(
                id = 8,
                medicineId = 19,
                supplierId = 3,
                quantity = 120,
                price = 30f,
                orderDate = LocalDate.of(2025, 3, 10),
                expirationDate = LocalDate.of(2026, 3, 19),
                remainingQuantity = 120
            ),
            Order(
                id = 9,
                medicineId = 22,
                supplierId = 1,
                quantity = 140,
                price = 14.5f,
                orderDate = LocalDate.of(2025, 11, 15),
                expirationDate = LocalDate.of(2028, 2, 28),
                remainingQuantity = 140
            )
        )

        val RegulationRepository = mutableStateListOf(
            Regulation(
                1,
                "OTC"
            ),
            Regulation(
                2,
                "Prescription"
            )
        )
        val SupplierRepository = mutableStateListOf(
            Supplier(
                0,
                "Mercury Pharma"
            ),
            Supplier(
                1,
                "Unilab"
            ),
            Supplier(
                2,
                "Pfizer"
            ),
            Supplier(
                3,
                "GSK"
            ),
            Supplier(
                4,
                "Sanofi"
            )
        )
        val DesignationRepository = mutableStateListOf(
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

        val BrandedGenericRepository = mutableStateListOf(
            BrandedGeneric(0, 0, 0),
            BrandedGeneric(1, 1, 0),
            BrandedGeneric(2, 1, 1),
            BrandedGeneric(3, 0, 2),
            BrandedGeneric(4, 1, 2),
            BrandedGeneric(5, 1, 3),
            BrandedGeneric(6, 0, 4),
            BrandedGeneric(7, 1, 5),
            BrandedGeneric(8, 0, 6),
            BrandedGeneric(9, 1, 7),
            BrandedGeneric(10, 0, 7),
            BrandedGeneric(11, 0, 8),
            BrandedGeneric(12, 5, 8),
            BrandedGeneric(13, 0, 9),
            BrandedGeneric(14, 1, 10),
            BrandedGeneric(15, 0, 11),
            BrandedGeneric(16, 2, 12),
            BrandedGeneric(17, 3, 13),
            BrandedGeneric(18, 8, 14),
            BrandedGeneric(19, 9, 15),
            BrandedGeneric(20, 2, 16),
            BrandedGeneric(21, 9, 16),
            BrandedGeneric(22, 8, 17),
            BrandedGeneric(23, 4, 18),
            BrandedGeneric(24, 5, 19),
            BrandedGeneric(25, 4, 20),
            BrandedGeneric(26, 5, 21),
            BrandedGeneric(27, 4, 22),
            BrandedGeneric(28, 5, 23),
            BrandedGeneric(29, 6, 23),
            BrandedGeneric(30, 6, 24),
            BrandedGeneric(31, 7, 25),
            BrandedGeneric(32, 6, 26),
            BrandedGeneric(33, 7, 27),
            BrandedGeneric(34, 6, 27),
            BrandedGeneric(35, 6, 28),
            BrandedGeneric(36, 5, 28),
            BrandedGeneric(37, 7, 29),
            BrandedGeneric(38, 6, 29)
        )
    }
}