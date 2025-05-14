package be.foodflow.foodflow_101.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import be.foodflow.foodflow_101.data.model.LogHistory
import be.foodflow.foodflow_101.data.repository.LogRepository

class HistoryViewModel(
    private val logRepository: LogRepository
) : ViewModel() {

    // Get all log history entries
    val logHistory: LiveData<List<LogHistory>> = logRepository.getAllLogHistory()
}

// Factory for creating HistoryViewModel with dependencies
class HistoryViewModelFactory(
    private val logRepository: LogRepository
) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoryViewModel(logRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 