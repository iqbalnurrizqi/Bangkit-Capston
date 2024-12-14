package com.example.capstoneproject4.ui.main.routine

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.capstoneproject4.data.model.Routine
import com.example.capstoneproject4.data.model.RoutineRequest

class SharedViewModel : ViewModel() {

    private val _routines = MutableLiveData<List<RoutineRequest>>() // Sesuaikan tipe data
    val routines: LiveData<List<RoutineRequest>> get() = _routines

    fun setRoutines(routines: List<RoutineRequest>) {
        _routines.value = routines
    }
}
