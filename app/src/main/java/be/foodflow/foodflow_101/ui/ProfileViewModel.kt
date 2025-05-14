package be.foodflow.foodflow_101.ui

import androidx.lifecycle.*
import be.foodflow.foodflow_101.data.model.NutritionGoals
import be.foodflow.foodflow_101.data.model.UserProfile
import be.foodflow.foodflow_101.data.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {
    
    // LiveData for user profile
    val userProfile: LiveData<UserProfile?> = userRepository.userProfile
    
    // LiveData for nutrition goals
    val nutritionGoals: LiveData<NutritionGoals?> = userRepository.nutritionGoals
    
    // Save user profile
    fun saveUserProfile(userProfile: UserProfile) {
        viewModelScope.launch {
            userRepository.saveUserProfile(userProfile)
        }
    }
    
    // Save nutrition goals
    fun saveNutritionGoals(userProfile: UserProfile, nutritionGoals: NutritionGoals) {
        viewModelScope.launch {
            userRepository.saveNutritionGoals(userProfile, nutritionGoals)
        }
    }
}

// Factory for creating ProfileViewModel with dependencies
class ProfileViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 