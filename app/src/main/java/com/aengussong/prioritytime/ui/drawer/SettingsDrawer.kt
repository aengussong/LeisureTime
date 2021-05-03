package com.aengussong.prioritytime.ui.drawer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.aengussong.prioritytime.R
import com.aengussong.prioritytime.TaskDataViewModel
import com.aengussong.prioritytime.worker.Work
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

private const val MAIN = "main"
private const val ERASURE = "erasure"

@Composable
fun SettingsDrawer(
    isOpen: State<Boolean>,
    onEraseCounter: () -> Unit
) {
    MaterialTheme {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        if (isOpen.value) {
            scope.launch { drawerState.open() }
        } else {
            scope.launch { drawerState.close() }
        }
        ModalDrawer(
            drawerBackgroundColor = colorResource(id = R.color.masala),
            drawerContent = {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = MAIN) {
                    composable(MAIN) { MainSettings(onEraseCounter) { navController.navigate(ERASURE) } }
                    composable(ERASURE) { EraseSettings() }
                }
            },
            drawerState = drawerState,
            gesturesEnabled = false
        ) {}
    }
}

@Composable
fun MainSettings(onEraseCounter: () -> Unit, toEraseSettings: () -> Unit) {
    Column {
        SettingsOption {
            Text(
                text = stringResource(id = R.string.erase_interval),
                modifier = Modifier.clickable { toEraseSettings() })
        }
        SettingsOption {
            Text(
                text = stringResource(id = R.string.option_erase_counters),
                modifier = Modifier.clickable { onEraseCounter() })
        }
    }
}

@Composable
private fun SettingsOption(content: @Composable () -> Unit) {
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            content()
        }
        Divider(color = Color.Black)
    }
}

@Composable
fun EraseSettings(vm: TaskDataViewModel = getViewModel()) {
    val options = listOf(Work.ERASE_NEVER, Work.ERASE_MONTHLY)
    val state: State<Work> = vm.getEraseSetting().collectAsState(initial = Work.ERASE_NEVER)

    val onSelected = { option: Work -> if (option != state.value) vm.launchEraseWorker(option) }

    Column {
        options.forEach { option ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .selectable(selected = option == state.value, onClick = { onSelected(option) })
            ) {
                RadioButton(
                    selected = option == state.value,
                    onClick = { onSelected(option) }
                )
                Text(
                    text = stringResource(option.titleRes),
                    style = MaterialTheme.typography.body1.merge(),
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}