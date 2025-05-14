package be.foodflow.foodflow_101.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import be.foodflow.foodflow_101.data.model.DailyLog
import be.foodflow.foodflow_101.data.model.FoodLog
import be.foodflow.foodflow_101.data.model.LogHistory
import java.time.LocalDate
import java.time.LocalDateTime

@Dao
interface LogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodLog(foodLog: FoodLog): Long

    @Update
    suspend fun updateFoodLog(foodLog: FoodLog)

    @Delete
    suspend fun deleteFoodLog(foodLog: FoodLog)

    @Transaction
    @Query("""
        SELECT 
            DATE(dateTime) as date,
            SUM(totalKcal) as totalKcal,
            SUM(totalProtein) as totalProtein,
            SUM(totalFat) as totalFat,
            SUM(totalCarbs) as totalCarbs
        FROM food_log
        WHERE DATE(dateTime) = :date
        GROUP BY DATE(dateTime)
    """)
    fun getDailyLogByDate(date: LocalDate): LiveData<DailyLog?>

    @Transaction
    @Query("""
        SELECT * FROM food_log
        WHERE DATE(dateTime) = :date
        ORDER BY dateTime ASC
    """)
    fun getFoodLogsByDate(date: LocalDate): LiveData<List<FoodLog>>
    
    @Transaction
    @Query("""
        SELECT * FROM food_log
        WHERE DATE(dateTime) = :date
        ORDER BY dateTime ASC
    """)
    suspend fun getFoodLogsByDateSync(date: LocalDate): List<FoodLog>

    @Transaction
    @Query("""
        SELECT 
            DATE(dateTime) as date,
            SUM(totalKcal) as totalKcal,
            SUM(totalProtein) as totalProtein,
            SUM(totalFat) as totalFat,
            SUM(totalCarbs) as totalCarbs
        FROM food_log
        WHERE dateTime BETWEEN :startDate AND :endDate
        GROUP BY DATE(dateTime)
        ORDER BY DATE(dateTime) DESC
    """)
    fun getDailyLogsBetweenDates(startDate: LocalDateTime, endDate: LocalDateTime): LiveData<List<DailyLog>>
    
    // LogHistory related methods
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogHistory(logHistory: LogHistory): Long
    
    @Query("SELECT * FROM log_history ORDER BY date DESC")
    fun getAllLogHistory(): LiveData<List<LogHistory>>
}
