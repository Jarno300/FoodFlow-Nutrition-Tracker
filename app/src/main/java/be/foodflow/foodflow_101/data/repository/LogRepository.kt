package be.foodflow.foodflow_101.data.repository

import be.foodflow.foodflow_101.data.database.LogDao
import be.foodflow.foodflow_101.data.model.DailyLog
import be.foodflow.foodflow_101.data.model.FoodLog
import be.foodflow.foodflow_101.data.model.LogHistory
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class LogRepository(private val logDao: LogDao) {

    suspend fun insertFoodLog(foodLog: FoodLog): Long {
        return logDao.insertFoodLog(foodLog)
    }

    suspend fun updateFoodLog(foodLog: FoodLog) {
        logDao.updateFoodLog(foodLog)
    }

    suspend fun deleteFoodLog(foodLog: FoodLog) {
        logDao.deleteFoodLog(foodLog)
    }

    fun getDailyLogByDate(date: LocalDate): LiveData<DailyLog?> {
        return logDao.getDailyLogByDate(date)
    }

    fun getFoodLogsByDate(date: LocalDate): LiveData<List<FoodLog>> {
        return logDao.getFoodLogsByDate(date)
    }

    fun getRecentDailyLogs(days: Int): LiveData<List<DailyLog>> {
        val endDate = LocalDateTime.of(LocalDate.now(), LocalTime.MAX)
        val startDate = LocalDateTime.of(LocalDate.now().minusDays(days.toLong()), LocalTime.MIN)
        return logDao.getDailyLogsBetweenDates(startDate, endDate)
    }
    
    suspend fun saveAndClearDailyLogs(date: LocalDate): Boolean {
        // Get the current food logs for the day
        val foodLogs = logDao.getFoodLogsByDateSync(date)
        
        // If there are no logs, there's nothing to save or clear
        if (foodLogs.isEmpty()) {
            return false
        }
        
        // Calculate the totals
        var totalKcal = 0f
        var totalProtein = 0f
        var totalFat = 0f
        var totalCarbs = 0f
        
        foodLogs.forEach { log ->
            totalKcal += log.totalKcal
            totalProtein += log.totalProtein
            totalFat += log.totalFat
            totalCarbs += log.totalCarbs
        }
        
        // Create and save a LogHistory entry
        val logHistory = LogHistory(
            date = date,
            totalKcal = totalKcal,
            totalProtein = totalProtein,
            totalFat = totalFat,
            totalCarbs = totalCarbs
        )
        
        // Insert the LogHistory entry
        logDao.insertLogHistory(logHistory)
        
        // Delete all food logs for the given date
        foodLogs.forEach { foodLog ->
            deleteFoodLog(foodLog)
        }
        
        return true
    }
    
    // Get all log history entries
    fun getAllLogHistory(): LiveData<List<LogHistory>> {
        return logDao.getAllLogHistory()
    }
}
