package be.foodflow.foodflow_101.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import be.foodflow.foodflow_101.data.model.Food

@Dao
interface FoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(food: Food): Long

    @Update
    suspend fun update(food: Food)

    @Delete
    suspend fun delete(food: Food)

    @Query("SELECT * FROM food ORDER BY name ASC")
    fun getAllFoods(): LiveData<List<Food>>

    @Query("SELECT * FROM food")
    suspend fun getAllFoodsOnce(): List<Food>

    @Query("SELECT COUNT(*) FROM food")
    suspend fun getFoodCount(): Int

    @Query("SELECT * FROM food WHERE id = :id")
    suspend fun getFoodById(id: Long): Food?

    @Query("SELECT * FROM food WHERE name LIKE '%' || :searchQuery || '%' ORDER BY name ASC")
    fun searchFoods(searchQuery: String): LiveData<List<Food>>
}

