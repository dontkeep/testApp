package com.exal.testapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.exal.testapp.databinding.ActivityMainBinding
import com.exal.testapp.LineSample

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val adapter = ItemAdapter()

        val items = listOf(
            ListItem(1, "Item 1: This is a longer title for the first item", "Summary 1: A brief description of item 1."),
            ListItem(2, "Item 2: Another title for the second item", "Summary 2: A concise summary of item 2."),
            ListItem(3, "Item 3: Title for the third item", "Summary 3: A short overview of item 3."),
            ListItem(4, "Item 4: Title for the fourth item", "Summary 4: A quick summary of item 4."),
            ListItem(5, "Item 5: Title for the fifth item", "Summary 5: A brief description of item 5."),
            ListItem(6, "Item 6: Title for the sixth item", "Summary 6: A concise summary of item 6."),
            ListItem(7, "Item 7: Title for the seventh item", "Summary 7: A short overview of item 7."),
            ListItem(8, "Item 8: Title for the eighth item", "Summary 8: A quick summary of item 8."),
            ListItem(9, "Item 9: Title for the ninth item", "Summary 9: A brief description of item 9."),
            ListItem(10, "Item 10: Title for the tenth item", "Summary 10: A concise summary of item 10.")
        )

        val layoutManager = LinearLayoutManager(this)
        binding.itemRecycler.layoutManager = layoutManager

        adapter.submitList(items)

        binding.itemRecycler.adapter = adapter

//        binding.composeView.setContent { bikin compose view dulu kalo mau pake
//            MaterialTheme {
//                LineSample()
//            }
//        }
    }
}
