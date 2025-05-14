package be.foodflow.foodflow_101.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import be.foodflow.foodflow_101.MyApp
import be.foodflow.foodflow_101.R
import be.foodflow.foodflow_101.data.repository.LogRepository

class HistoryFragment : Fragment() {
    private lateinit var viewModel: HistoryViewModel
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var historyRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize views
        historyRecyclerView = view.findViewById(R.id.history_recycler_view)
        
        // Initialize view model
        val app = requireActivity().application as MyApp
        val logRepository = LogRepository(app.database.logDao())
        
        val factory = HistoryViewModelFactory(logRepository)
        viewModel = ViewModelProvider(this, factory)[HistoryViewModel::class.java]
        
        // Set up adapter
        historyAdapter = HistoryAdapter(emptyList())
        historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        historyRecyclerView.adapter = historyAdapter
        
        // Set up observers
        viewModel.logHistory.observe(viewLifecycleOwner) { logs ->
            historyAdapter.updateList(logs)
        }
    }
}
