package com.example.medicina.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicina.functions.AccountFunctions
import com.example.medicina.model.Account
import com.example.medicina.model.Designation
import com.example.medicina.model.Medicine
import com.example.medicina.model.Repository
import com.example.medicina.model.UserSession
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AccountViewModel : ViewModel() {
    private val repository = Repository

    val accounts = repository.getAllAccounts().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    private val _userDesignation = MutableStateFlow(Designation())
    val userDesignation: StateFlow<Designation> = _userDesignation

    fun setDesignation(id: Int){
        viewModelScope.launch {
            val currentAccount = accounts.value.find { it.id == id }?.designationID ?: 2

            _userDesignation.value = repository.getDesignationById(currentAccount) ?: Designation()
        }
    }

    private val _account = MutableStateFlow(Account())
    val account: StateFlow<Account> = _account

    private val _editAccount = MutableStateFlow(Account())
    val editAccount: StateFlow<Account> = _editAccount

    private val _accountDesignation = MutableStateFlow(Designation())
    val accountDesignation: StateFlow<Designation> = _accountDesignation

    val designations = repository.getAllDesignations().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    val designationsMap: StateFlow<Map<Int?, Designation>> = designations
        .map { list -> list.associateBy { designation -> designation.id } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyMap()
        )


    fun getAccountById(accountId: Int) {
        viewModelScope.launch {
            val account = repository.getAccountById(accountId)
            account?.let {
                _account.value = it
                _editAccount.value = it.copy()
            }
        }
    }

    fun saveAccount() {
        viewModelScope.launch {
            if(_editAccount.value.designationID == null){
                updateData { it.copy(designationID = 3) }
            }

            val id = repository.upsertAccount(_editAccount.value)

            _account.value = editAccount.value

            UserSession.accountID = id.toInt()
            UserSession.designationID = _account.value.designationID
        }
    }

    fun updateData(transform: (Account) -> Account) {
        _editAccount.value = transform(_editAccount.value)
    }

}