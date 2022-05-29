package com.plcoding.stockmarketapp.presentation.company_listing


sealed class CompanyListEvent{
    object Refresh:CompanyListEvent()
    data class OnSearchQueryChange(val query:String):CompanyListEvent()
}
