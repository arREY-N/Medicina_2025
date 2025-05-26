package com.example.medicina.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.medicina.model.Account
import kotlinx.coroutines.flow.Flow
import androidx.room.OnConflictStrategy
import androidx.room.Update

@Dao
interface AccountDao {

    @Query("Select * FROM accounts")
    fun getAllAccounts(): Flow<List<Account>>

    @Query("Select * FROM accounts WHERE id = :id")
    suspend fun getAccountById(id: Int): Account?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: Account): Long

    @Update
    suspend fun updateAccount(account: Account)

    @Query("DELETE FROM accounts WHERE id = :id")
    suspend fun deleteAccount(id: Int)

}