package com.example.kasiruas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kasiruas.ui.theme.KasiruasTheme
import java.text.NumberFormat
import java.util.Locale

/* ---------- COLOR PALETTE ---------- */
val DeepOnyx = Color(0xFF121212)
val MutedGold = Color(0xFFC19A6B)
val PureIvory = Color(0xFFFDFDFD)
val SurfaceGray = Color(0xFFF5F5F7)

/* ---------- ACTIVITY ---------- */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
          enableEdgeToEdge()
        setContent {
            KasiruasTheme {
                MainScreen()
            }
        }
    }
}

/* ---------- DATA MODEL ---------- */
data class Barang(
    val nama: String,
    val harga: Int,
    var stok: Int
)

/* ---------- MAIN SCREEN ---------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {

    var selectedTab by remember { mutableStateOf(0) }
    val daftarBarang = remember { mutableStateListOf<Barang>() }

    Scaffold(
        containerColor = PureIvory,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "ATELIER KASIR",
                            letterSpacing = 6.sp,
                            fontWeight = FontWeight.ExtraLight,
                            fontSize = 14.sp
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = PureIvory
                    )
                )

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = PureIvory,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = MutedGold
                        )
                    },
                    divider = {}
                ) {
                    LuxuryTab(selectedTab == 0, "INVENTORY") { selectedTab = 0 }
                    LuxuryTab(selectedTab == 1, "TERMINAL") { selectedTab = 1 }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> ManajemenBarangScreen(daftarBarang)
                1 -> KasirScreen(daftarBarang)
            }
        }
    }
}

/* ---------- TAB ---------- */
@Composable
fun LuxuryTab(selected: Boolean, title: String, onClick: () -> Unit) {
    Tab(
        selected = selected,
        onClick = onClick,
        text = {
            Text(
                title,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                letterSpacing = 1.sp
            )
        }
    )
}

/* ---------- SCREEN 1 : INVENTORY ---------- */
@Composable
fun ManajemenBarangScreen(daftarBarang: MutableList<Barang>) {

    var nama by remember { mutableStateOf("") }
    var harga by remember { mutableStateOf("") }
    var stok by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            Text("Tambah Produk", fontSize = 26.sp, fontWeight = FontWeight.Thin)

            Spacer(Modifier.height(12.dp))

            ModernInput(nama, { nama = it }, "Nama Produk", Icons.Default.Edit)
            ModernInput(harga, { harga = it }, "Harga", Icons.Default.Info, KeyboardType.Number)
            ModernInput(stok, { stok = it }, "Stok", Icons.Default.List, KeyboardType.Number)

            Button(
                onClick = {
                    if (nama.isNotEmpty() && harga.isNotEmpty() && stok.isNotEmpty()) {
                        daftarBarang.add(
                            Barang(nama, harga.toInt(), stok.toInt())
                        )
                        nama = ""; harga = ""; stok = ""
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DeepOnyx)
            ) {
                Text("SIMPAN BARANG")
            }
        }

        items(daftarBarang) { barang ->
            Surface(
                color = SurfaceGray,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(barang.nama, fontWeight = FontWeight.Bold)
                        Text("Stok: ${barang.stok}", fontSize = 12.sp)
                    }
                    Text(formatCurrency(barang.harga), color = MutedGold)
                }
            }
        }
    }
}

/* ---------- SCREEN 2 : KASIR ---------- */
@Composable
fun KasirScreen(daftarBarang: MutableList<Barang>) {

    var total by remember { mutableStateOf(0) }

    Column(Modifier.fillMaxSize().padding(24.dp)) {

        Text("Transaksi", fontSize = 26.sp, fontWeight = FontWeight.Thin)

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(daftarBarang) { barang ->
                var qty by remember { mutableStateOf("") }

                Surface(shape = RoundedCornerShape(12.dp)) {
                    Column(Modifier.padding(16.dp)) {

                        Text(barang.nama, fontWeight = FontWeight.Bold)
                        Text(formatCurrency(barang.harga), color = MutedGold)

                        ModernInput(
                            value = qty,
                            onValueChange = { qty = it },
                            label = "Jumlah",
                            icon = Icons.Default.ShoppingCart,
                            keyboardType = KeyboardType.Number
                        )

                        Button(
                            onClick = {
                                if (qty.isNotEmpty() && qty.toInt() <= barang.stok) {
                                    barang.stok -= qty.toInt()
                                    total += qty.toInt() * barang.harga
                                    qty = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Tambah")
                        }
                    }
                }
            }
        }

        Text("TOTAL: ${formatCurrency(total)}", fontSize = 22.sp)

        Button(
            onClick = { total = 0 },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MutedGold)
        ) {
            Text("BAYAR")
        }
    }
}

/* ---------- INPUT ---------- */
@Composable
fun ModernInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth()
    )
}

/* ---------- FORMAT RUPIAH ---------- */
fun formatCurrency(value: Int): String {
    val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    return format.format(value)
}
