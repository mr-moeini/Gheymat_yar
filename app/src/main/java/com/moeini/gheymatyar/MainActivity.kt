package com.moeini.gheymatyar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Accent = Color(0xFF5B63E6)
private val Muted = Color(0xFF778197)
private val assets = listOf(
    Asset("طلای ۱۸ عیار", "گرم", "۴٬۲۸۰٬۰۰۰", "+۱٫۲٪", true, "طلا"),
    Asset("سکه امامی", "عدد", "۵۱٬۷۵۰٬۰۰۰", "+۰٫۶٪", true, "طلا"),
    Asset("دلار آزاد", "USD", "۶۹٬۸۰۰", "−۰٫۳٪", false, "ارز"),
    Asset("یورو", "EUR", "۷۵٬۹۰۰", "+۰٫۲٪", true, "ارز"),
    Asset("بیت‌کوین", "BTC", "$۶۷٬۲۰۰", "+۲٫۴٪", true, "رمزارز"),
    Asset("اتریوم", "ETH", "$۲٬۵۴۰", "−۱٫۱٪", false, "رمزارز")
)
private data class Asset(val name:String,val unit:String,val price:String,val change:String,val up:Boolean,val category:String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PriceYarApp() }
    }
}

@Composable
private fun PriceYarApp() {
    var page by remember { mutableIntStateOf(0) }
    var dark by remember { mutableStateOf(false) }
    var currency by remember { mutableStateOf("تومان") }
    val scheme = if (dark) darkColorScheme(primary=Accent) else lightColorScheme(primary=Accent, background=Color(0xFFF5F7FB), surface=Color.White)
    MaterialTheme(colorScheme=scheme) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(page==0,{page=0},{Icon(Icons.Default.Home,null)},label={Text("خانه")})
                        NavigationBarItem(page==1,{page=1},{Icon(Icons.Default.ShowChart,null)},label={Text("بازارها")})
                        NavigationBarItem(page==2,{page=2},{Icon(Icons.Default.SwapHoriz,null)},label={Text("مبدل")})
                        NavigationBarItem(page==3,{page=3},{Icon(Icons.Default.Settings,null)},label={Text("تنظیمات")})
                    }
                }
            ) { padding ->
                Box(Modifier.fillMaxSize().padding(padding)) {
                    when(page) {
                        0 -> HomePage(currency)
                        1 -> MarketsPage()
                        2 -> ConverterPage()
                        else -> SettingsPage(currency,{currency=it},dark,{dark=it})
                    }
                }
            }
        }
    }
}

@Composable private fun PageHeader(title:String,subtitle:String) {
    Column(Modifier.fillMaxWidth().padding(bottom=14.dp)) {
        Text(title,fontSize=25.sp,fontWeight=FontWeight.Bold,color=MaterialTheme.colorScheme.onBackground)
        Text(subtitle,fontSize=13.sp,color=Muted)
    }
}

@Composable private fun HomePage(currency:String) {
    var selected by remember { mutableStateOf(setOf("طلای ۱۸ عیار","دلار آزاد","بیت‌کوین")) }
    var dialog by remember { mutableStateOf(false) }
    LazyColumn(Modifier.fillMaxSize().padding(horizontal=18.dp),contentPadding=PaddingValues(vertical=20.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
        item { Row(verticalAlignment=Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) { Text("قیمت‌یار",fontSize=27.sp,fontWeight=FontWeight.Bold); Text("نبض بازار، یک‌جا",color=Muted) }
            IconButton(onClick={dialog=true}) { Icon(Icons.Default.AddCircle,"افزودن") }
        } }
        item {
            Card(colors=CardDefaults.cardColors(containerColor=Color(0xFF172033)),shape=RoundedCornerShape(22.dp),modifier=Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                    Text("نمای کلی دارایی‌ها",color=Color.White.copy(.75f))
                    Text("۱۲۵٬۴۰۰٬۰۰۰ $currency",fontSize=27.sp,fontWeight=FontWeight.Bold,color=Color.White)
                    Text("سود و زیان امروز  +۲٫۴٪",color=Color(0xFF71E0B5))
                    Text("قیمت‌ها و ارقام این نسخه نمونه و نمایشی هستند.",fontSize=11.sp,color=Color.White.copy(.65f))
                }
            }
        }
        item { Text("دیده‌بان من",fontSize=19.sp,fontWeight=FontWeight.Bold) }
        items(assets.filter { it.name in selected }) { AssetRow(it) }
        item { OutlinedButton(onClick={dialog=true},modifier=Modifier.fillMaxWidth()) { Icon(Icons.Default.Add,null); Text("مدیریت دیده‌بان") } }
    }
    if(dialog) AlertDialog(onDismissRequest={dialog=false},title={Text("انتخاب دارایی‌ها")},text={
        Column { assets.forEach { a -> Row(verticalAlignment=Alignment.CenterVertically) {
            Checkbox(a.name in selected,{selected=if(it) selected+a.name else selected-a.name})
            Text(a.name)
        } } }
    },confirmButton={TextButton(onClick={dialog=false}) { Text("ذخیره") }})
}

@Composable private fun AssetRow(a:Asset) {
    Card(modifier=Modifier.fillMaxWidth(),shape=RoundedCornerShape(17.dp)) {
        Row(Modifier.fillMaxWidth().padding(15.dp),verticalAlignment=Alignment.CenterVertically) {
            Box(Modifier.size(42.dp).background(Color(0xFFEDEEFF),RoundedCornerShape(13.dp)),contentAlignment=Alignment.Center) {
                Text(a.unit.take(3),color=Accent,fontWeight=FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) { Text(a.name,fontWeight=FontWeight.SemiBold); Text(a.category,color=Muted,fontSize=12.sp) }
            Column(horizontalAlignment=Alignment.End) {
                Text(a.price,fontWeight=FontWeight.Bold)
                Text(a.change,color=if(a.up) Color(0xFF168A65) else Color(0xFFD94B61),fontSize=12.sp)
            }
        }
    }
}

@Composable private fun MarketsPage() {
    var category by remember { mutableStateOf("همه") }
    Column(Modifier.fillMaxSize().padding(18.dp)) {
        PageHeader("بازارها","قیمت دارایی‌ها")
        Row(horizontalArrangement=Arrangement.spacedBy(6.dp)) {
            listOf("همه","طلا","ارز","رمزارز").forEach { c -> FilterChip(category==c,{category=c},label={Text(c)}) }
        }
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement=Arrangement.spacedBy(10.dp)) {
            items(assets.filter { category=="همه" || it.category==category }) { AssetRow(it) }
            item { Text("قیمت‌ها نمایشی هستند؛ برای قیمت زنده باید API معتبر متصل شود.",color=Muted,fontSize=12.sp,modifier=Modifier.padding(8.dp)) }
        }
    }
}

@Composable private fun ConverterPage() {
    var amount by remember { mutableStateOf("1") }
    var from by remember { mutableStateOf("دلار") }
    var to by remember { mutableStateOf("تومان") }
    val choices=listOf("تومان","دلار","یورو","طلای ۱۸ عیار","بیت‌کوین")
    Column(Modifier.fillMaxSize().padding(18.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
        PageHeader("مبدل دارایی","تبدیل بین ارزها و دارایی‌ها")
        OutlinedTextField(amount,{amount=it},modifier=Modifier.fillMaxWidth(),label={Text("مقدار")},singleLine=true)
        Text("از")
        Choice(from,choices){from=it}
        Icon(Icons.Default.SwapVert,null,tint=Accent,modifier=Modifier.align(Alignment.CenterHorizontally).size(30.dp))
        Text("به")
        Choice(to,choices){to=it}
        Card(colors=CardDefaults.cardColors(containerColor=Accent),shape=RoundedCornerShape(20.dp)) {
            Column(Modifier.fillMaxWidth().padding(20.dp)) {
                Text("مقدار تقریبی دریافتی",color=Color.White.copy(.8f))
                Text("— $to",fontSize=25.sp,fontWeight=FontWeight.Bold,color=Color.White)
                Text("محاسبه پس از اتصال منبع نرخ انجام می‌شود.",fontSize=12.sp,color=Color.White.copy(.8f))
            }
        }
    }
}

@Composable private fun Choice(value:String,options:List<String>,onPick:(String)->Unit) {
    var open by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick={open=true},modifier=Modifier.fillMaxWidth()) {
            Text(value,modifier=Modifier.weight(1f),textAlign=TextAlign.Start); Icon(Icons.Default.ExpandMore,null)
        }
        DropdownMenu(open,{open=false}) { options.forEach { DropdownMenuItem(text={Text(it)},onClick={onPick(it);open=false}) } }
    }
}

@Composable private fun SettingsPage(currency:String,onCurrency:(String)->Unit,dark:Boolean,onDark:(Boolean)->Unit) {
    Column(Modifier.fillMaxSize().padding(18.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
        PageHeader("تنظیمات","شخصی‌سازی برنامه")
        Text("واحد نمایش",fontWeight=FontWeight.Bold)
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            listOf("تومان","ریال").forEach { FilterChip(currency==it,{onCurrency(it)},label={Text(it)}) }
        }
        HorizontalDivider()
        Row(verticalAlignment=Alignment.CenterVertically) {
            Text("حالت تاریک",modifier=Modifier.weight(1f)); Switch(dark,onDark)
        }
        HorizontalDivider()
        Text("قیمت‌یار نسخه ۱.۰.۰",fontWeight=FontWeight.Bold)
        Text("توسعه‌دهنده: محمدرضا معینی",color=Muted)
        Text("اطلاعات قیمت در این نسخه نمایشی است و هنوز به سرویس زنده متصل نشده است.",color=Muted,fontSize=12.sp)
    }
}
