package be.foodflow.foodflow_101.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import be.foodflow.foodflow_101.MyApp
import be.foodflow.foodflow_101.R
import be.foodflow.foodflow_101.data.model.Food
import be.foodflow.foodflow_101.data.model.FoodLog
import be.foodflow.foodflow_101.data.repository.FoodRepository
import be.foodflow.foodflow_101.data.repository.LogRepository
import be.foodflow.foodflow_101.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class DashboardFragment : Fragment() {
    private lateinit var viewModel: DashboardViewModel
    private lateinit var foodSearchAdapter: FoodSearchAdapter
    private lateinit var foodLogAdapter: FoodLogAdapter
    
    private var selectedFood: Food? = null
    
    // Views
    private lateinit var dateTimeText: TextView
    private lateinit var searchInput: EditText
    private lateinit var searchResultsRecyclerView: RecyclerView
    private lateinit var selectedFoodLayout: LinearLayout
    private lateinit var selectedFoodText: TextView
    private lateinit var foodMacrosText: TextView
    private lateinit var amountInput: EditText
    private lateinit var measureText: TextView
    private lateinit var addFoodButton: Button
    private lateinit var foodLogsRecyclerView: RecyclerView
    private lateinit var totalKcalText: TextView
    private lateinit var totalProteinText: TextView
    private lateinit var totalFatText: TextView
    private lateinit var totalCarbsText: TextView
    private lateinit var remainingKcalText: TextView
    private lateinit var remainingProteinText: TextView
    private lateinit var remainingFatText: TextView
    private lateinit var remainingCarbsText: TextView
    private lateinit var saveAndClearButton: Button
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize views
        initViews(view)
        
        // Initialize view model
        val app = requireActivity().application as MyApp
        val logRepository = LogRepository(app.database.logDao())
        val userRepository = UserRepository(app.database.userDao())
        val foodRepository = FoodRepository(app.database.foodDao())
        
        val factory = DashboardViewModelFactory(logRepository, userRepository, foodRepository)
        viewModel = ViewModelProvider(this, factory)[DashboardViewModel::class.java]
        
        // Set up adapters
        setupAdapters()
        
        // Set up observers
        setupObservers()
        
        // Set up listeners
        setupListeners()
        
        // Update datetime
        updateDateTime()
    }
    
    private fun initViews(view: View) {
        dateTimeText = view.findViewById(R.id.text_date_time)
        searchInput = view.findViewById(R.id.input_food_search)
        searchResultsRecyclerView = view.findViewById(R.id.search_results_recycler_view)
        selectedFoodLayout = view.findViewById(R.id.layout_selected_food)
        selectedFoodText = view.findViewById(R.id.text_selected_food)
        foodMacrosText = view.findViewById(R.id.text_food_macros)
        amountInput = view.findViewById(R.id.input_amount)
        measureText = view.findViewById(R.id.text_measure)
        addFoodButton = view.findViewById(R.id.button_add_food)
        foodLogsRecyclerView = view.findViewById(R.id.food_logs_recycler_view)
        totalKcalText = view.findViewById(R.id.text_total_kcal)
        totalProteinText = view.findViewById(R.id.text_total_protein)
        totalFatText = view.findViewById(R.id.text_total_fat)
        totalCarbsText = view.findViewById(R.id.text_total_carbs)
        remainingKcalText = view.findViewById(R.id.text_remaining_kcal)
        remainingProteinText = view.findViewById(R.id.text_remaining_protein)
        remainingFatText = view.findViewById(R.id.text_remaining_fat)
        remainingCarbsText = view.findViewById(R.id.text_remaining_carbs)
        saveAndClearButton = view.findViewById(R.id.button_save_and_clear)
    }
    
    private fun setupAdapters() {
        // Search results adapter
        foodSearchAdapter = FoodSearchAdapter(emptyList()) { food ->
            selectedFood = food
            selectedFoodLayout.visibility = View.VISIBLE
            selectedFoodText.text = food.name
            foodMacrosText.text = "Macros per ${food.amount}${food.measure}: ${food.kcal} kcal, P: ${food.protein}g, F: ${food.fat}g, C: ${food.carbs}g"
            measureText.text = food.measure
            searchResultsRecyclerView.visibility = View.GONE
        }
        
        searchResultsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        searchResultsRecyclerView.adapter = foodSearchAdapter
        
        // Food logs adapter
        foodLogAdapter = FoodLogAdapter(emptyList()) { foodLog ->
            viewModel.deleteFoodLog(foodLog)
        }
        
        foodLogsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        foodLogsRecyclerView.adapter = foodLogAdapter
    }
    
    private fun setupObservers() {
        // Search results
        viewModel.searchResults.observe(viewLifecycleOwner) { foods ->
            if (foods.isNotEmpty()) {
                searchResultsRecyclerView.visibility = View.VISIBLE
                foodSearchAdapter.updateList(foods)
            } else {
                searchResultsRecyclerView.visibility = View.GONE
            }
        }
        
        // Food logs for today
        viewModel.foodLogs.observe(viewLifecycleOwner) { logs ->
            foodLogAdapter.updateList(logs)
        }
        
        // Daily totals
        viewModel.todayLog.observe(viewLifecycleOwner) { log ->
            if (log != null) {
                totalKcalText.text = "${log.totalKcal.toInt()} kcal"
                totalProteinText.text = "${log.totalProtein.toInt()} g"
                totalFatText.text = "${log.totalFat.toInt()} g"
                totalCarbsText.text = "${log.totalCarbs.toInt()} g"
            } else {
                totalKcalText.text = "0 kcal"
                totalProteinText.text = "0 g"
                totalFatText.text = "0 g"
                totalCarbsText.text = "0 g"
            }
        }
        
        // Remaining macros
        viewModel.remainingMacros.observe(viewLifecycleOwner) { macros ->
            remainingKcalText.text = "Remain: ${macros["kcal"]?.toInt() ?: 0}"
            remainingProteinText.text = "Remain: ${macros["protein"]?.toInt() ?: 0}g"
            remainingFatText.text = "Remain: ${macros["fat"]?.toInt() ?: 0}g"
            remainingCarbsText.text = "Remain: ${macros["carbs"]?.toInt() ?: 0}g"
        }
    }
    
    private fun setupListeners() {
        // Remove search button click listener since we're updating on text changes
        
        // Search input
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrBlank()) {
                    searchResultsRecyclerView.visibility = View.GONE
                } else if (s.length >= 1) { // Change to 1 character to make it more responsive
                    viewModel.searchFoods(s.toString())
                    // Always show search results when user is typing
                    searchResultsRecyclerView.visibility = View.VISIBLE
                }
            }
            
            override fun afterTextChanged(s: Editable?) {}
        })
        
        // Add food button
        addFoodButton.setOnClickListener {
            val food = selectedFood ?: return@setOnClickListener
            val amountStr = amountInput.text.toString()
            
            if (amountStr.isEmpty()) {
                Toast.makeText(context, "Please enter an amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val amount = amountStr.toFloatOrNull()
            if (amount == null || amount <= 0) {
                Toast.makeText(context, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // Calculate proportion for the new amount
            val proportion = amount / food.amount
            
            // Create a food log
            val foodLog = FoodLog(
                foodId = food.id,
                foodName = food.name,
                amount = amount,
                measure = food.measure,
                mealType = "Meal", // Default meal type
                dateTime = LocalDateTime.now(),
                totalKcal = food.kcal * proportion,
                totalProtein = food.protein * proportion,
                totalFat = food.fat * proportion,
                totalCarbs = food.carbs * proportion
            )
            
            // Add to database
            viewModel.addFoodLog(foodLog)
            
            // Reset UI
            selectedFoodLayout.visibility = View.GONE
            selectedFood = null
            amountInput.text.clear()
            searchInput.text.clear()
            
            Toast.makeText(context, "Food added to log", Toast.LENGTH_SHORT).show()
        }
        
        // Save and Clear button
        saveAndClearButton.setOnClickListener {
            if (viewModel.todayLog.value == null || 
                (viewModel.todayLog.value?.totalKcal ?: 0f) <= 0f) {
                Toast.makeText(context, "No food logs to save", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            viewModel.saveAndClear()
            Toast.makeText(context, "Food log saved to history and cleared", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun updateDateTime() {
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", Locale("nl", "BE"))
        dateTimeText.text = now.format(formatter)
    }
    
    override fun onResume() {
        super.onResume()
        updateDateTime()
    }
}

// Factory for creating DashboardViewModel with dependencies
class DashboardViewModelFactory(
    private val logRepository: LogRepository,
    private val userRepository: UserRepository,
    private val foodRepository: FoodRepository
) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(logRepository, userRepository, foodRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
