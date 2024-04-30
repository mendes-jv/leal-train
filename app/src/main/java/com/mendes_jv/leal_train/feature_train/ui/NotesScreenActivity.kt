package com.mendes_jv.leal_train.feature_train.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mendes_jv.leal_train.common.SIDE_EFFECTS_KEY
import com.mendes_jv.leal_train.feature_train.events.TrainScreenUiEvent
import com.mendes_jv.leal_train.feature_train.side_effects.TrainScreenSideEffects
import com.mendes_jv.leal_train.feature_train.ui.components.AddTaskDialogComponent
import com.mendes_jv.leal_train.feature_train.ui.components.EmptyComponent
import com.mendes_jv.leal_train.feature_train.ui.components.LoadingComponent
import com.mendes_jv.leal_train.feature_train.ui.components.TaskCardComponent
import com.mendes_jv.leal_train.feature_train.ui.components.UpdateTaskDialogComponent
import com.mendes_jv.leal_train.feature_train.viewmodel.TrainViewModel
import com.mendes_jv.leal_train.theme.TodoChampTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class NotesScreenActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val trainViewModel: TrainViewModel = viewModel()

            val uiState = trainViewModel.state.collectAsState().value
            val effectFlow = trainViewModel.effect

            val snackBarHostState = remember { SnackbarHostState() }

            LaunchedEffect(key1 = SIDE_EFFECTS_KEY) {
                effectFlow.onEach { effect ->
                    when (effect) {
                        is TrainScreenSideEffects.ShowSnackBarMessage -> {
                            snackBarHostState.showSnackbar(
                                message = effect.message,
                                duration = SnackbarDuration.Short,
                                actionLabel = "DISMISS",
                            )
                        }
                    }
                }.collect()
            }

            TodoChampTheme {
                if (uiState.isShowAddTaskDialog) {
                    AddTaskDialogComponent(
                        uiState = uiState,
                        setTaskTitle = { title ->
                            trainViewModel.sendEvent(
                                event = TrainScreenUiEvent.OnChangeTrainDescription(title = title),
                            )
                        },
                        setTaskBody = { body ->
                            trainViewModel.sendEvent(
                                event = TrainScreenUiEvent.OnChangeTaskBody(body = body),
                            )
                        },
                        saveTask = {
                            trainViewModel.sendEvent(
                                event = TrainScreenUiEvent.AddTrain(
                                    title = uiState.currentTextFieldTitle,
                                    body = uiState.currentTextFieldBody,
                                ),
                            )
                        },
                        closeDialog = {
                            trainViewModel.sendEvent(
                                event = TrainScreenUiEvent.OnChangeAddTrainDialogState(show = false),
                            )
                        },
                    )
                }

                if (uiState.isShowUpdateTaskDialog) {
                    UpdateTaskDialogComponent(
                        uiState = uiState,
                        setTaskTitle = { title ->
                            trainViewModel.sendEvent(
                                event = TrainScreenUiEvent.OnChangeTrainDescription(title = title),
                            )
                        },
                        setTaskBody = { body ->
                            trainViewModel.sendEvent(
                                event = TrainScreenUiEvent.OnChangeTaskBody(body = body),
                            )
                        },
                        saveTask = {
                            trainViewModel.sendEvent(event = TrainScreenUiEvent.UpdateTrain)
                        },
                        closeDialog = {
                            trainViewModel.sendEvent(
                                event = TrainScreenUiEvent.OnChangeUpdateTrainDialogState(show = false),
                            )
                        },
                        task = uiState.taskToBeUpdated,
                    )
                }

                Scaffold(
                    snackbarHost = {
                        SnackbarHost(snackBarHostState)
                    },
                    floatingActionButton = {
                        Column {
                            ExtendedFloatingActionButton(
                                icon = {
                                    Icon(
                                        Icons.Rounded.AddCircle,
                                        contentDescription = "Add Task",
                                        tint = Color.White,
                                    )
                                },
                                text = {
                                    Text(
                                        text = "Add Task",
                                        color = Color.White,
                                    )
                                },
                                onClick = {
                                    trainViewModel.sendEvent(
                                        event = TrainScreenUiEvent.OnChangeAddTrainDialogState(show = true),
                                    )
                                },
                                modifier = Modifier.padding(horizontal = 12.dp),
                                containerColor = Color.Black,
                                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp),
                            )
                        }
                    },
                    containerColor = Color(0XFFFAFAFA),
                ) {
                    Box(modifier = Modifier.padding(it)) {
                        when {
                            uiState.isLoading -> {
                                LoadingComponent()
                            }

                            !uiState.isLoading && uiState.tasks.isNotEmpty() -> {
                                LazyColumn(contentPadding = PaddingValues(14.dp)) {
                                    item {
                                        com.mendes_jv.leal_train.feature_train.ui.components.WelcomeMessageComponent()

                                        androidx.compose.foundation.layout.Spacer(
                                            modifier = Modifier.height(
                                                30.dp,
                                            ),
                                        )
                                    }

                                    items(uiState.tasks) { task ->
                                        TaskCardComponent(
                                            task = task,
                                            deleteTask = { taskId ->
                                                Log.d("TASK_ID: ", taskId)
                                                trainViewModel.sendEvent(
                                                    event = TrainScreenUiEvent.DeleteTrain(taskId = taskId),
                                                )
                                            },
                                            updateTask = { taskToBeUpdated ->
                                                trainViewModel.sendEvent(
                                                    TrainScreenUiEvent.OnChangeUpdateTrainDialogState(
                                                        show = true,
                                                    ),
                                                )

                                                trainViewModel.sendEvent(
                                                    event = TrainScreenUiEvent.SetTrainToBeUpdated(
                                                        trainToBeUpdated = taskToBeUpdated,
                                                    ),
                                                )
                                            },
                                        )
                                    }
                                }
                            }

                            !uiState.isLoading && uiState.tasks.isEmpty() -> {
                                EmptyComponent()
                            }
                        }
                    }
                }
            }
        }
    }
}
