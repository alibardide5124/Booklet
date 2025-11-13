package com.phoenix.booklet.screen.home.component

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.phoenix.booklet.R
import com.phoenix.booklet.data.FileResult
import com.phoenix.booklet.data.model.Book
import com.phoenix.booklet.data.model.ReadingStatus
import com.phoenix.booklet.utils.deleteFileFromPath
import com.phoenix.booklet.utils.getUriFromFile
import com.phoenix.booklet.utils.saveUriAsPhoto
import java.util.Date
import java.util.UUID

@Composable
fun InsertBookBottomSheet(
    modifier: Modifier = Modifier,
    book: Book? = null,
    onClickClose: () -> Unit,
    onClickSave: (Book) -> Unit
) {
    var photoUri: Uri? by remember { mutableStateOf(null) }
    var name by remember { mutableStateOf(book?.name ?: "") }
    var author by remember { mutableStateOf(book?.author ?: "") }
    var isTranslated by remember { mutableStateOf(book?.translator != null) }
    var translator by remember { mutableStateOf(book?.translator ?: "") }
    var status by remember { mutableStateOf(book?.status ?: ReadingStatus.WISHLIST) }
    var publisher by remember { mutableStateOf(book?.publisher ?: "") }
    var releaseYear by remember { mutableStateOf(book?.releaseYear ?: "") }
    var publishYear by remember { mutableStateOf(book?.publishYear ?: "") }

    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                photoUri = uri
            } else {
                Toast.makeText(context, "Failed to load photo", Toast.LENGTH_SHORT).show()
            }
        }

    Column(
        modifier
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp, horizontal = 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "Book Info",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = { onClickClose() }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close"
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .aspectRatio(2 / 3f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                contentAlignment = Alignment.Center,
            ) {
                if (photoUri != null)
                    AsyncImage(
                        model = photoUri,
                        contentDescription = null,
                        modifier = Modifier
                            .aspectRatio(2 / 3f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(8.dp))
                    )
                else
                    Icon(
                        painter = painterResource(R.drawable.ic_add_photo),
                        contentDescription = "Add Cover Photo",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
            }
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Book Name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    placeholder = { Text("Author Name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.align(Alignment.CenterHorizontally)) {
            FilterChip(
                selected = !isTranslated,
                onClick = { isTranslated = false },
                label = { Text("Original Language") }
            )
            Spacer(Modifier.width(8.dp))
            FilterChip(
                selected = isTranslated,
                onClick = { isTranslated = true },
                label = { Text("Translated") }
            )
        }
        AnimatedVisibility(isTranslated) {
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = translator,
                onValueChange = { translator = it },
                placeholder = { Text("Translator Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Status",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
        ) {
            FilterChip(
                selected = status == ReadingStatus.WISHLIST,
                onClick = { status = ReadingStatus.WISHLIST },
                label = { Text("Wishlist") }
            )
            Spacer(Modifier.width(8.dp))
            FilterChip(
                selected = status == ReadingStatus.READING,
                onClick = { status = ReadingStatus.READING },
                label = { Text("Reading") }
            )
            Spacer(Modifier.width(8.dp))
            FilterChip(
                selected = status == ReadingStatus.FINISHED,
                onClick = { status = ReadingStatus.FINISHED },
                label = { Text("Finished") }
            )
            Spacer(Modifier.width(8.dp))
            FilterChip(
                selected = status == ReadingStatus.ARCHIVED,
                onClick = { status = ReadingStatus.ARCHIVED },
                label = { Text("Archived") }
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Publishing info",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = publisher,
            onValueChange = { publisher = it },
            placeholder = { Text("Publisher Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        Spacer(Modifier.height(8.dp))
        Row {
            OutlinedTextField(
                value = releaseYear,
                onValueChange = { releaseYear = it },
                placeholder = { Text("Release Year") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(
                value = publishYear,
                onValueChange = { publishYear = it },
                placeholder = { Text("Publish Year") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                val uuid = book?.id ?: UUID.randomUUID()
                val pathUri = getUriFromFile(book?.cover)
                var filePath: String? = book?.cover // Or null
                // TODO: SAVE ONLY if changed / added
                if (pathUri != null && pathUri != photoUri) {
                    deleteFileFromPath(book?.cover)
                    filePath = null
                }
                if (photoUri != null && pathUri != photoUri) {
                    val result = saveUriAsPhoto(
                        context = context,
                        uri = photoUri,
                        name = uuid.toString()
                    )
                    when(result) {
                        is FileResult.Error -> Unit
                        is FileResult.Success -> filePath = result.filePath
                    }
                }
                val book = Book(
                    id = uuid,
                    name = name,
                    author = author,
                    translator = if (isTranslated) translator else null,
                    publisher = publisher,
                    releaseYear = releaseYear,
                    publishYear = publishYear,
                    cover = filePath,
                    status = status,
                    dateCreated = book?.dateCreated ?: Date(System.currentTimeMillis()),
                    dateUpdated = Date(System.currentTimeMillis())
                )
                isLoading = true
                onClickSave(book)
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && name.isNotBlank() && author.isNotBlank() && (!isTranslated || translator.isNotBlank()) && publisher.isNotBlank() && releaseYear.isNotBlank()
        ) {
            Crossfade(isLoading) { target ->
                if (target) {
                    CircularProgressIndicator()
                } else {
                    Text("Save Book")
                }
            }
        }
    }
}
