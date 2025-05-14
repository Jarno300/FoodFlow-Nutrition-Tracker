package be.foodflow.foodflow_101.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import be.foodflow.foodflow_101.data.model.NutritionGoals
import be.foodflow.foodflow_101.data.model.UserProfile


@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(userProfile: UserProfile)

    @Update
    suspend fun updateUserProfile(userProfile: UserProfile)

    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): LiveData<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNutritionGoals(nutritionGoals: NutritionGoals)

    @Update
    suspend fun updateNutritionGoals(nutritionGoals: NutritionGoals)

    @Query("SELECT * FROM nutrition_goals WHERE userId = 1")
    fun getNutritionGoals(): LiveData<NutritionGoals?>
}


