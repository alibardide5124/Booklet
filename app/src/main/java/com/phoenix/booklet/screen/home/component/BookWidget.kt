package com.phoenix.booklet.screen.home.component

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.phoenix.booklet.R
import com.phoenix.booklet.data.model.Book
import com.phoenix.booklet.data.model.ReadingStatus
import java.io.File

@Composable
fun BookWidget(
    modifier: Modifier = Modifier,
    book: Book
) {
    Row(
        modifier = modifier
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(.25f)
                .aspectRatio(2 / 3f)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp)),
            contentAlignment = Alignment.Center,
        ) {
            if (book.cover != null)
                AsyncImage(
                    model = Uri.fromFile(File(book.cover)),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .aspectRatio(2 / 3f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                )
            else
                Icon(
                    painter = painterResource(R.drawable.ic_image),
                    contentDescription = "Add Cover Photo",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
        }
        Spacer(Modifier.width(16.dp))
        Column(
            Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                text = book.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = book.author,
                fontSize = 18.sp
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Released ${book.releaseYear}",
                    fontSize = 15.sp
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = book.status.name,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when(book.status) {
                                ReadingStatus.WISHLIST -> MaterialTheme.colorScheme.tertiaryContainer
                                ReadingStatus.READING -> MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp)
                                ReadingStatus.FINISHED -> MaterialTheme.colorScheme.primaryContainer
                                ReadingStatus.ARCHIVED -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    color = when(book.status) {
                        ReadingStatus.WISHLIST -> MaterialTheme.colorScheme.onTertiaryContainer
                        ReadingStatus.READING -> MaterialTheme.colorScheme.onSurface
                        ReadingStatus.FINISHED -> MaterialTheme.colorScheme.onPrimaryContainer
                        ReadingStatus.ARCHIVED -> MaterialTheme.colorScheme.onSurfaceVariant
                    },

                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}