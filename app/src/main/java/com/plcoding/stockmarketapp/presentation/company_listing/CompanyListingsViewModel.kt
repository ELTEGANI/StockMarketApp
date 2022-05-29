package com.plcoding.stockmarketapp.presentation.company_listing

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.stockmarketapp.domain.repository.StockRepository
import com.plcoding.stockmarketapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CompanyListingsViewModel @Inject constructor(
    private val repository: StockRepository
):ViewModel(){

    var state by mutableStateOf(CompanyListingState())
    private var searchJob:Job? = null

    fun onEvent(companyListEvent: CompanyListEvent){
        when(companyListEvent){
            is CompanyListEvent.Refresh->{
                getCompanyListings(fetchFromRemote = true)
            }
            is CompanyListEvent.OnSearchQueryChange->{
                state = state.copy(searchQuery = companyListEvent.query)
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(500L)
                    getCompanyListings()
                }
            }
        }
    }

    private fun getCompanyListings(query:String=state.searchQuery.lowercase(), fetchFromRemote:Boolean=false){
        viewModelScope.launch {
            repository
                .getCompanyListings(fetchFromRemote,query)
                .collect{result->
                    when(result){
                        is Resource.Success->{
                           result.data?.let {listing->
                               state = state.copy(companies = listing)
                           }
                        }
                        is Resource.Error->Unit
                        is Resource.Loading->{
                            state = state.copy(isLoading = result.isLoading)
                        }
                    }
                }
        }
    }
}