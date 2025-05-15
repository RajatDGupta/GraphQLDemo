package com.demo.graphqldemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import com.apollographql.apollo.ApolloClient
import com.demo.grapqldemo.HistoriesQuery

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            var historyState by remember { mutableStateOf(emptyList<HistoryData>()) }

            val finalList : MutableList<HistoryData> = mutableListOf()

            val apolloClient = ApolloClient.Builder()
                .serverUrl("https://spacex-production.up.railway.app/")
                .build()

            LaunchedEffect(Unit) {
                val response = apolloClient.query(HistoriesQuery()).execute()
                if (response.data != null) {
                    val histories = response.data?.histories
                    histories?.forEach { history ->
                        finalList.add(HistoryData(history?.title.toString(),history?.details.toString()))
                    }
                    historyState = finalList
                } else if (response.errors != null) {
                    println("GraphQL Errors: ${response.errors}")
                } else if (response.exception != null) {
                    println("Network or Parsing Errors: ${response.exception}")
                }
            }
            if(historyState.isEmpty()){
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                }
            }
            MyListScreen(historyState)
        }
    }

    @Composable
    fun MyListScreen(listData: List<HistoryData>) {
        Column (
            Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
        ){
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items (listData){
                    MyListItem(it)
                }
            }
        }
    }

    @Composable
    fun MyListItem(item: HistoryData) {
        Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Text(text = item.title , modifier = Modifier.fillMaxWidth().padding(8.dp) ,style = MaterialTheme.typography.titleMedium,)
            Text(text = item.details , modifier = Modifier.fillMaxWidth().padding(8.dp),style = MaterialTheme.typography.bodySmall)
        }
    }

}
