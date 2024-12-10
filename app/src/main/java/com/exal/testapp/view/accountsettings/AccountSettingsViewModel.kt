package com.exal.testapp.view.accountsettings

import androidx.lifecycle.ViewModel
import com.exal.testapp.data.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccountSettingsViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

}