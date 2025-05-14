package be.foodflow.foodflow_101.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food")
data class Food(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val measure: String,
    val amount: Float,
    val kcal: Float,
    val protein: Float,
    val fat: Float,
    val carbs: Float,
    val notes: String? = null

)