package com.phoenix.booklet.screen.home

import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.phoenix.booklet.R
import com.phoenix.booklet.data.model.Book
import com.phoenix.booklet.data.model.ReadingStatus
import com.phoenix.booklet.screen.home.component.BookWidget
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    books: List<Book>,
    onClickAdd: () -> Unit,
    selectedFilter: FilterStatus,
    onSelectFilter: (FilterStatus) -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Booklet") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onClickAdd() }) {
                Row(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                    Text(
                        text = "Add Book"
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            stickyHeader {
                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Spacer(Modifier.width(16.dp))
                    FilterChip(
                        selected = selectedFilter == FilterStatus.ALL,
                        onClick = { onSelectFilter(FilterStatus.ALL) },
                        label = { Text("All books") }
                    )
                    VerticalDivider(Modifier.fillMaxHeight())
                    FilterChip(
                        selected = selectedFilter == FilterStatus.WISHLIST,
                        onClick = { onSelectFilter(FilterStatus.WISHLIST) },
                        label = { Text("Wishlist") }
                    )
                    FilterChip(
                        selected = selectedFilter == FilterStatus.READING,
                        onClick = { onSelectFilter(FilterStatus.READING) },
                        label = { Text("Reading") }
                    )
                    FilterChip(
                        selected = selectedFilter == FilterStatus.FINISHED,
                        onClick = { onSelectFilter(FilterStatus.FINISHED) },
                        label = { Text("Finished") }
                    )
                    FilterChip(
                        selected = selectedFilter == FilterStatus.ARCHIVED,
                        onClick = { onSelectFilter(FilterStatus.ARCHIVED) },
                        label = { Text("Archive") }
                    )
                    Spacer(Modifier.width(16.dp))
                }
            }
            items(
                items = when(selectedFilter) {
                    FilterStatus.ALL -> books
                    FilterStatus.WISHLIST -> books.filter { it.status == ReadingStatus.WISHLIST }
                    FilterStatus.READING -> books.filter { it.status == ReadingStatus.READING }
                    FilterStatus.FINISHED -> books.filter { it.status == ReadingStatus.FINISHED }
                    FilterStatus.ARCHIVED -> books.filter { it.status == ReadingStatus.ARCHIVED }
                },
                key = { it.id }
            ) { book ->
                BookWidget(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    book = book
                )
            }

            if (books.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillParentMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = """
                            Oh, you haven't added any book!
                            Click button below, or import books from a backup in settings
                        """.trimIndent(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}