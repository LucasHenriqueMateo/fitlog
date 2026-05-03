package com.fitlog.app.ui.workout.manage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fitlog.app.R
import com.fitlog.app.domain.model.Workout
import com.fitlog.app.ui.components.EmptyState
import com.fitlog.app.ui.components.LoadingOverlay
import com.fitlog.app.ui.components.WorkoutCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageWorkoutsScreen(
    onBack: () -> Unit,
    onCreateTemplate: () -> Unit,
    onEditTemplate: (String) -> Unit,
    viewModel: ManageWorkoutsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var templateToDelete by remember { mutableStateOf<Workout?>(null) }

    templateToDelete?.let { template ->
        AlertDialog(
            onDismissRequest = { templateToDelete = null },
            title = { Text(stringResource(R.string.delete_workout_title)) },
            text = { Text(stringResource(R.string.delete_workout_message)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteTemplate(template.id)
                    templateToDelete = null
                }) { Text(stringResource(R.string.btn_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { templateToDelete = null }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_manage_workouts)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateTemplate,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.cd_new_workout))
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is ManageWorkoutsUiState.Loading -> LoadingOverlay()
                is ManageWorkoutsUiState.Empty -> EmptyState(
                    title = stringResource(R.string.empty_templates_title),
                    subtitle = stringResource(R.string.empty_templates_subtitle),
                    actionLabel = stringResource(R.string.btn_create_template),
                    onAction = onCreateTemplate
                )
                is ManageWorkoutsUiState.Success -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.templates, key = { it.id }) { template ->
                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = { value ->
                                    if (value == SwipeToDismissBoxValue.EndToStart) {
                                        templateToDelete = template
                                    }
                                    false
                                }
                            )
                            SwipeToDismissBox(
                                state = dismissState,
                                enableDismissFromStartToEnd = false,
                                backgroundContent = {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 16.dp, vertical = 6.dp)
                                            .background(
                                                MaterialTheme.colorScheme.error,
                                                androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                                            ),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        Icon(
                                            Icons.Filled.Delete,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.padding(end = 16.dp)
                                        )
                                    }
                                }
                            ) {
                                WorkoutCard(
                                    workout = template,
                                    onClick = { onEditTemplate(template.id) }
                                )
                            }
                        }
                    }
                }
                is ManageWorkoutsUiState.Error -> EmptyState(
                    title = stringResource(R.string.empty_templates_title),
                    subtitle = state.message
                )
            }
        }
    }
}
