package be.foodflow.foodflow_101.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "log_history")
data class LogHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: LocalDate,
    val totalKcal: Float,
    val totalProtein: Float,
    val totalFat: Float,
    val totalCarbs: Float
) 