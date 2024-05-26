package com.app.qaimobile.ui.conversation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.app.qaimobile.R
import com.app.qaimobile.util.Result
import javax.inject.Inject

class ConversationActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var conversationViewModel: ConversationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        conversationViewModel = ViewModelProvider(this, viewModelFactory)[ConversationViewModel::class.java]

        observeSyncStatus()
        conversationViewModel.syncConversations()
    }

    private fun observeSyncStatus() {
        conversationViewModel.syncStatus.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    // Show a loading indicator
                    showLoadingIndicator()
                }
                is Result.Success -> {
                    // Sync completed successfully
                    hideLoadingIndicator()
                    showConversations()
                }
                is Result.Error -> {
                    // Handle the error case
                    hideLoadingIndicator()
                    showErrorMessage(result.exception.message)
                }
            }
        }
    }

    private fun showErrorMessage(message: String?) {

    }

    private fun showConversations() {
        TODO("Not yet implemented")
    }

    private fun hideLoadingIndicator() {
        TODO("Not yet implemented")
    }

    private fun showLoadingIndicator() {
        TODO("Not yet implemented")
    }

    // Implement other functions as needed
}