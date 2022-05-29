package com.plcoding.stockmarketapp.presentation.company_listing

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.plcoding.stockmarketapp.domain.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class CompanyListingsViewModel @Inject constructor(
    private val repository: StockRepository
):ViewModel(){

    val state by mutableStateOf(CompanyListingState())

    fun onEvent(companyListEvent: CompanyListEvent){
        when(companyListEvent){
            is CompanyListEvent.Refresh->{

            }
            is CompanyListEvent.OnSearchQueryChange->{

            }
        }
    }
}