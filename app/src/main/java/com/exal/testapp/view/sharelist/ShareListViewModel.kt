package com.exal.testapp.view.sharelist

import androidx.lifecycle.ViewModel
import com.exal.testapp.data.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ShareListViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

}