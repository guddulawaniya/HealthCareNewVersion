package com.careavatar.core_service.repository.viewModels

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.careavatar.core_service.repository.adapters.Contact

class ContactPagingSource(
    private val context: Context,
    private val query: String,
    private val excludedNumbers: List<String>? = null
) : PagingSource<Int, Contact>() {

    companion object {
        private const val PAGE_SIZE = 20
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Contact> {
        val page = params.key ?: 0
        val offset = page * PAGE_SIZE
        val contactsPage = mutableListOf<Contact>()

        try {
            // Build selection and args for filtering by query if any
            val selection = if (query.isNotEmpty()) {
                "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ? OR ${ContactsContract.CommonDataKinds.Phone.NUMBER} LIKE ?"
            } else null

            val selectionArgs = if (query.isNotEmpty()) {
                arrayOf("%$query%", "%$query%")
            } else null

            // Query the contacts sorted by display name
            val cursor = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.PHOTO_URI
                ),
                selection,
                selectionArgs,
                "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
            )

            cursor?.use {
                // Move cursor to start of the page
                if (it.moveToPosition(offset)) {
                    var loaded = 0
                    do {
                        val name = it.getString(0) ?: ""
                        val number = it.getString(1)?.replace(" ", "")?.trim() ?: ""
                        val photoUri = it.getString(2)?.let { uriStr -> Uri.parse(uriStr) }

                        // Filter excluded numbers
                        if (excludedNumbers.isNullOrEmpty() || excludedNumbers.none { ex -> ex.replace(" ", "").trim() == number }) {
                            contactsPage.add(Contact(name, number, photoUri))
                        }

                        loaded++
                        if (loaded >= PAGE_SIZE) break
                    } while (it.moveToNext())
                }
            }

            // If fewer contacts returned than PAGE_SIZE, we've reached the end
            val nextKey = if (contactsPage.size < PAGE_SIZE) null else page + 1

            return LoadResult.Page(
                data = contactsPage,
                prevKey = if (page == 0) null else page - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Contact>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
