package mx.edu.utez.uiii_t7.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import mx.edu.utez.uiii_t7.data.db.Trip
import mx.edu.utez.uiii_t7.data.repository.TripRepository

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TripRepository(application)
    val completedTrips: StateFlow<List<Trip>> =
        repository.getAllCompletedTrips()
            .stateIn(
                viewModelScope,

                SharingStarted.WhileSubscribed(5000),

                emptyList()

            )
}