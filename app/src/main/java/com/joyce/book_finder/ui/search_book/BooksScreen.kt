package com.joyce.book_finder.ui.search_book

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.joyce.book_finder.customViews.ButtonProgress
import com.joyce.book_finder.models.*
import com.joyce.book_finder.theme.PRIMARY

@Composable
fun BooksScreen(booksVM: BooksViewModel) {
    val state by booksVM.state.collectAsState()
    BooksContent(booksVM = booksVM, state = state)
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BooksContent(booksVM: BooksViewModel, state: BooksState?) {
    val isLoading by booksVM.loading.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        var fieldValue by remember {
            mutableStateOf(TextFieldValue(""))
        }
        TextField(
            value = fieldValue,
            onValueChange = { fieldValue = it },
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, top = 20.dp)
                .fillMaxWidth(),
            singleLine = true,
            label = { Text(text = "DIGITE AUTOR OU NOME DO LIVRO") },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = PRIMARY,
                unfocusedIndicatorColor = PRIMARY
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search,
                )
            )
        Spacer(modifier = Modifier.height(26.dp))
        val keyboardController = LocalSoftwareKeyboardController.current
        ButtonProgress(
            isLoading = isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp),
            text = "Procurar",
            loadingText = "Procurando...",
            onClicked = {
                booksVM.getAllBooks(fieldValue.text.trim())
                keyboardController?.hide()
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        when (state) {
            is BooksState.Success -> {
                val books = remember { state.books }
                if (state.books.status.success == true){
                    RenderList(books = books)
                } else{
                    RenderEmptyBooks(books.status.message!!)
                }
            }
            is BooksState.Error -> {
                Toast.makeText(LocalContext.current, state.message, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }
}

@Composable
fun RenderEmptyBooks(message: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(10.dp)
    ) {
        Text(text = message,  textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
fun RenderList(books: ResponseGetBooks) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        if (books.books != null) {
            items(books.books) { book ->
                BookContent(book)
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun BookContent(book: BookItem?) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color.White)
    ) {
        val strings = BookDetailStrings(
            autor = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Autor: ") }
                withStyle(SpanStyle(fontWeight = FontWeight.Light)) {
                    if (book != null) {
                        var contribuicao = "Autor Desconhecido"
                        if (!book.contribuicao.isNullOrEmpty())contribuicao = book.contribuicao[0]?.nome + " " + book.contribuicao[0]?.sobrenome
                        append(contribuicao)
                    }
                }
            },
            titulo = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Título: ") }
                withStyle(SpanStyle(fontWeight = FontWeight.Light)) {
                    if (book != null) {
                        append(book.titulo ?: "")
                    }
                }
            },
            desc = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Descrição: ") }
                withStyle(SpanStyle(fontWeight = FontWeight.Light)) {
                    if (book != null) {
                        append(book.sinopse ?: "")
                    }
                }
            })

        if (book != null) {
            val url = book.imagens.imagem_primeira_capa.pequena
            val painter = rememberImagePainter(data = url, builder = {})

            Image(
                painter = painter, contentDescription = "",
                modifier = Modifier
                    .width(90.dp)
                    .height(90.dp)
                    .padding(top = 10.dp, start = 10.dp))
            
            Column {
                Text(
                    text = strings.autor,
                    fontSize = 16.sp,
                    modifier = Modifier
                )

                Text(
                    text = strings.titulo,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(top = 8.dp)
                )

                if (!book.sinopse.isNullOrEmpty()) {
                Text(
                    text = strings.desc,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(top = 8.dp)
                )
            }
            }
        }
        
    }
}

@Preview
@Composable
fun BooksScreenPreview() {
    val co = ContribuicaoItem(
        nome = "James",
        sobrenome = "Joyce"
    )
    val imagens = Imagens(imagem_primeira_capa("https://s.dicio.com.br/descricao.jpg", "", ""))
    val book = BookItem(
        titulo = "O Ladrao e a policia",
        sumario = "A descrição é a enumeração das características próprias dos seres, coisas, cenários, ambientes, costumes, impressões etc. A visão, o tato, a audição, o olfato e o paladar constituem a base da descrição.",
        contribuicao = listOf(co),
        imagens = imagens
    )
    BookContent(book)
}

@Preview
@Composable
fun RenderEmptyBooks() {
    RenderEmptyBooks(message = "Nenhum registro foi encontrado")
}


