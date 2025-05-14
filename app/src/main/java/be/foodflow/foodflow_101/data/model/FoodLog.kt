package be.foodflow.foodflow_101.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import be.foodflow.foodflow_101.data.model.Food
import java.time.LocalDateTime

@Entity(tableName = "food_log")
data class FoodLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val foodId: Long,
    val foodName: String,  // Name of the food for display purposes
    val amount: Float,
    val measure: String,   // Unit of measurement (g, ml, etc.)
    val mealType: String,  // e.g., "Breakfast", "Lunch", "Dinner", "Snack"
    val dateTime: LocalDateTime,

    // Calculated values
    val totalKcal: Float,
    val totalProtein: Float,
    val totalFat: Float,
    val totalCarbs: Float
)