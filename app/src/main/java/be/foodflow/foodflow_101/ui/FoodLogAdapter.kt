package be.foodflow.foodflow_101.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import be.foodflow.foodflow_101.R
import be.foodflow.foodflow_101.data.model.Food
import be.foodflow.foodflow_101.data.model.FoodLog
import kotlinx.coroutines.Job

class FoodListAdapter(private var items: List<Food>, private val onDeleteClick: (Any) -> Unit) :
    RecyclerView.Adapter<FoodListAdapter.FoodViewHolder>() {

    class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.text_food_name)
        val macros: TextView = itemView.findViewById(R.id.text_macros)
        val deleteButton: ImageButton = itemView.findViewById(R.id.button_delete_food)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food_row, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = items[position]
        holder.name.text = "${food.name} (${food.amount}${food.measure})"
        holder.macros.text = "Kcal: ${food.kcal}, P: ${food.protein}, F: ${food.fat}, C: ${food.carbs}"
        
        // Set click listener on delete button
        holder.deleteButton.setOnClickListener {
            onDeleteClick(food)
        }
    }

    override fun getItemCount() = items.size

    fun updateList(newItems: List<Food>) {
        items = newItems
        notifyDataSetChanged()
    }
}

// Adapter for food logs displayed in the dashboard
class FoodLogAdapter(private var foodLogs: List<FoodLog>, private val onDeleteClick: (FoodLog) -> Unit) :
    RecyclerView.Adapter<FoodLogAdapter.FoodLogViewHolder>() {

    class FoodLogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.text_food_name)
        val macros: TextView = itemView.findViewById(R.id.text_food_macros)
        val deleteButton: ImageButton = itemView.findViewById(R.id.button_delete_food)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodLogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food_log_row, parent, false)
        return FoodLogViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodLogViewHolder, position: Int) {
        val foodLog = foodLogs[position]
        holder.name.text = "${foodLog.foodName} (${foodLog.amount}${foodLog.measure})"
        holder.macros.text = "${foodLog.totalKcal.toInt()} kcal · P: ${foodLog.totalProtein}g · F: ${foodLog.totalFat}g · C: ${foodLog.totalCarbs}g"
        
        holder.deleteButton.setOnClickListener {
            onDeleteClick(foodLog)
        }
    }

    override fun getItemCount() = foodLogs.size

    fun updateList(newItems: List<FoodLog>) {
        foodLogs = newItems
        notifyDataSetChanged()
    }
}

// Adapter for food search results
class FoodSearchAdapter(
    private var foods: List<Food>,
    private val onFoodSelected: (Food) -> Unit
) : RecyclerView.Adapter<FoodSearchAdapter.FoodSearchViewHolder>() {

    class FoodSearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.text_food_name)
        val macros: TextView = itemView.findViewById(R.id.text_food_macros)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodSearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food_search_result, parent, false)
        return FoodSearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodSearchViewHolder, position: Int) {
        val food = foods[position]
        holder.name.text = "${food.name} (${food.amount}${food.measure})"
        holder.macros.text = "Per ${food.amount}${food.measure}: ${food.kcal} kcal, P: ${food.protein}g, F: ${food.fat}g, C: ${food.carbs}g"
        
        holder.itemView.setOnClickListener {
            onFoodSelected(food)
        }
    }

    override fun getItemCount() = foods.size

    fun updateList(newItems: List<Food>) {
        foods = newItems
        notifyDataSetChanged()
    }
}
