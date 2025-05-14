package be.foodflow.foodflow_101.data.model

import androidx.room.Ignore
import java.time.LocalDate

data class DailyLog @JvmOverloads constructor(
    val date: LocalDate,
    val totalKcal: Float,
    val totalProtein: Float,
    val totalFat: Float,
    val totalCarbs: Float,
    @Ignore
    val foodLogs: List<FoodLog> = emptyList()
)
