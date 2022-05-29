package com.plcoding.stockmarketapp.data.repository

import com.plcoding.stockmarketapp.data.csv.CSVParser
import com.plcoding.stockmarketapp.data.csv.CompanyListingsParser
import com.plcoding.stockmarketapp.data.local.StockDataBase
import com.plcoding.stockmarketapp.data.mapper.toCompanyListing
import com.plcoding.stockmarketapp.data.mapper.toCompanyListingEntity
import com.plcoding.stockmarketapp.data.remote.StockApi
import com.plcoding.stockmarketapp.domain.model.CompanyListing
import com.plcoding.stockmarketapp.domain.repository.StockRepository
import com.plcoding.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class StockRepositoryIml @Inject constructor(private val stockApi: StockApi,
                                             private val stockDataBase: StockDataBase,
                        private val  csvParser: CSVParser<CompanyListing>):StockRepository{
    private val stockDao = stockDataBase.stockDao
    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        return flow {
           emit(Resource.Loading(true))
           val localListings = stockDao.searchCompanyListing(query)
           emit(Resource.Success(data = localListings.map { it.toCompanyListing() }))
           val isDbEmpty = localListings.isEmpty() && query.isBlank()
           val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote
            if (shouldJustLoadFromCache){
                emit(Resource.Loading(false))
                return@flow
            }
            val remoteListing = try {
               val response = stockApi.getListings()
               csvParser.parse(response.byteStream())
            }catch (iOException:IOException){
             iOException.printStackTrace()
             emit(Resource.Error("Couldn't load data"))
                null
            }catch (httpException:HttpException){
             httpException.printStackTrace()
             emit(Resource.Error("Couldn't load data"))
                null
            }
            remoteListing?.let {listing ->
              stockDao.clearCompanyListings()
              stockDao.insertCompanyListings(
                  listing.map { it.toCompanyListingEntity() }
              )
                emit(Resource.Success(
                    data = stockDao.searchCompanyListing("")
                        .map { it.toCompanyListing() }
                ))
                emit(Resource.Loading(false))
            }
        }
    }
}