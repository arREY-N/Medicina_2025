package com.example.medicina.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicina.model.Account
import com.example.medicina.model.Designation
import com.example.medicina.model.Repository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AccountViewModel : ViewModel() {
    private val repository = Repository

    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts: MutableStateFlow<List<Account>> = _accounts

    private val _account = MutableStateFlow(Account())
    val account: StateFlow<Account> = _account

    private val _editAccount = MutableStateFlow(Account())
    val editAccount: StateFlow<Account> = _editAccount

    private val _accountDesignation = MutableStateFlow(Designation())
    val accountDesignation: StateFlow<Designation> = _accountDesignation

    private val _designations = MutableStateFlow<List<Designation>>(emptyList())
    val designations: MutableStateFlow<List<Designation>> = _designations

    val designationsMap: StateFlow<Map<Int, Designation>> = _designations
        .map { list -> list.associateBy { it.id } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyMap()
        )

    fun getAccountById(accountId: Int) {
        val account = repository.getAccountById(accountId)
        account?.let {
            _account.value = it
            _editAccount.value = it.copy()
        }
    }

    fun saveAccount() {
        repository.upsertAccount(_editAccount.value)
        _account.value = editAccount.value
    }

    fun updateData(transform: (Account) -> Account) {
        _editAccount.value = transform(_editAccount.value)
    }


    init{
        _designations.value = repository.getAllDesignation()
    }
}