package be.foodflow.foodflow_101.ui

import androidx.lifecycle.*
import be.foodflow.foodflow_101.data.model.DailyLog
import be.foodflow.foodflow_101.data.model.Food
import be.foodflow.foodflow_101.data.model.FoodLog
import be.foodflow.foodflow_101.data.model.NutritionGoals
import be.foodflow.foodflow_101.data.repository.FoodRepository
import be.foodflow.foodflow_101.data.repository.LogRepository
import be.foodflow.foodflow_101.data.repository.UserRepository
import androidx.navigation.fragment.findNavController
import androidx.lifecycle.switchMap
import kotlinx.coroutines.launch
import java.time.LocalDate

class DashboardViewModel(
    private val logRepository: LogRepository,
    private val userRepository: UserRepository,
    private val foodRepository: FoodRepository
) : ViewModel() {

    // Today's log
    private val _selectedDate = MutableLiveData<LocalDate>().apply {
        value = LocalDate.now()
    }

    val selectedDate: LiveData<LocalDate> = _selectedDate

    val todayLog: LiveData<DailyLog?> = _selectedDate.switchMap { date ->
        logRepository.getDailyLogByDate(date)
    }

    // Food logs for today
    val foodLogs: LiveData<List<FoodLog>> = _selectedDate.switchMap { date ->
        logRepository.getFoodLogsByDate(date)
    }

    // Search results
    private val _searchQuery = MutableLiveData<String>()
    val searchResults: LiveData<List<Food>> = _searchQuery.switchMap { query ->
        foodRepository.searchFoods(query)
    }

    // Nutrition goals
    val nutritionGoals: LiveData<NutritionGoals?> = userRepository.nutritionGoals

    // Weekly logs for chart
    val weeklyLogs: LiveData<List<DailyLog>> = logRepository.getRecentDailyLogs(7)

    // Calculate remaining macros
    val remainingMacros: LiveData<Map<String, Float>> = MediatorLiveData<Map<String, Float>>().apply {
        val mediator = this

        // Initial value
        mediator.value = mapOf(
            "kcal" to 0f,
            "protein" to 0f,
            "fat" to 0f,
            "carbs" to 0f
        )

        // Update when either goals or consumption changes
        fun updateRemaining() {
            val goals = nutritionGoals.value
            val consumed = todayLog.value

            if (goals != null) {
                mediator.value = mapOf(
                    "kcal" to (goals.dailyKcal - (consumed?.totalKcal ?: 0f)),
                    "protein" to (goals.dailyProtein - (consumed?.totalProtein ?: 0f)),
                    "fat" to (goals.dailyFat - (consumed?.totalFat ?: 0f)),
                    "carbs" to (goals.dailyCarbs - (consumed?.totalCarbs ?: 0f))
                )
            }
        }

        addSource(nutritionGoals) { updateRemaining() }
        addSource(todayLog) { updateRemaining() }
    }

    // Calculate macro percentages of goals
    val macroPercentages: LiveData<Map<String, Int>> = MediatorLiveData<Map<String, Int>>().apply {
        val mediator = this

        // Initial value
        mediator.value = mapOf(
            "kcal" to 0,
            "protein" to 0,
            "fat" to 0,
            "carbs" to 0
        )

        // Update when either goals or consumption changes
        fun updatePercentages() {
            val goals = nutritionGoals.value
            val consumed = todayLog.value

            if (goals != null) {
                mediator.value = mapOf(
                    "kcal" to ((consumed?.totalKcal ?: 0f) / goals.dailyKcal * 100).toInt().coerceIn(0, 100),
                    "protein" to ((consumed?.totalProtein ?: 0f) / goals.dailyProtein * 100).toInt().coerceIn(0, 100),
                    "fat" to ((consumed?.totalFat ?: 0f) / goals.dailyFat * 100).toInt().coerceIn(0, 100),
                    "carbs" to ((consumed?.totalCarbs ?: 0f) / goals.dailyCarbs * 100).toInt().coerceIn(0, 100)
                )
            }
        }

        addSource(nutritionGoals) { updatePercentages() }
        addSource(todayLog) { updatePercentages() }
    }

    // Change the selected date
    fun setDate(date: LocalDate) {
        _selectedDate.value = date
    }

    // Search foods
    fun searchFoods(query: String) {
        _searchQuery.value = query
    }

    // Add a food log
    fun addFoodLog(foodLog: FoodLog) {
        viewModelScope.launch {
            logRepository.insertFoodLog(foodLog)
            reloadLogsForDate(selectedDate.value ?: LocalDate.now()) // Refresh list
        }
    }

    // Delete a food log
    fun deleteFoodLog(foodLog: FoodLog) {
        viewModelScope.launch {
            logRepository.deleteFoodLog(foodLog)
            reloadLogsForDate(selectedDate.value ?: LocalDate.now()) // Refresh list
        }
    }
    
    // Save the current daily totals to history and clear the food logs
    fun saveAndClear() {
        viewModelScope.launch {
            val currentDate = selectedDate.value ?: LocalDate.now()
            val success = logRepository.saveAndClearDailyLogs(currentDate)
            if (success) {
                // Refresh the view
                reloadLogsForDate(currentDate)
            }
        }
    }

    private fun reloadLogsForDate(date: LocalDate) {
        // Re-assigning the same value will re-trigger the switchMap observer
        _selectedDate.value = date
    }
}