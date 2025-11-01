package com.asyscraft.alzimer_module

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.asyscraft.alzimer_module.adapter.AdapterGame
import com.careavatar.core_model.alzimer.game_response
import com.asyscraft.alzimer_module.databinding.ActivitySuggestedGamesBinding
import com.asyscraft.alzimer_module.utils.GridSpacing
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SuggestedGames : BaseActivity() {

    private lateinit var binding: ActivitySuggestedGamesBinding

    private lateinit var adapter: AdapterGame
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySuggestedGamesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        binding.back.setOnClickListener{
            finish()
        }


        // 🔁 Change layout here
        adapter.setAlternativeLayout(true)
    }



    private fun setupRecyclerView() {
        val datalist = listOf(
            game_response(R.drawable.game1_imag, "Ninja war", "Rating 1.3k"),
            game_response(R.drawable.game2_img, "Space Farm", "Rating 1.3k"),
            game_response(R.drawable.game3_img, "Space Farm", "Rating 1.3k"),
        )



        val gridLayoutManager = GridLayoutManager(this, 2)
        binding.recyclerview.layoutManager = gridLayoutManager



        val spacingInPixels = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
        binding.recyclerview.addItemDecoration(GridSpacing(2, spacingInPixels, true))
        adapter = AdapterGame(this, datalist, { game ->
            Toast.makeText(this, "Clicked on: ${game.gameName}", Toast.LENGTH_SHORT).show()
        })
        binding.recyclerview.adapter = adapter

    }
}