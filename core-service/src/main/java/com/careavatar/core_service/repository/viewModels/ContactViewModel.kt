package com.careavatar.core_service.repository.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.careavatar.core_service.repository.adapters.Contact
import kotlinx.coroutines.flow.Flow

class ContactViewModel(application: Application) : AndroidViewModel(application) {

    fun getContacts(query: String = "", excludedNumbers: List<String>): Flow<PagingData<Contact>> {
        val context = getApplication<Application>().applicationContext

        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { ContactPagingSource(context, query, excludedNumbers) }
        ).flow.cachedIn(viewModelScope)
    }
}
