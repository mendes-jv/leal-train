package com.mendes_jv.leal_train.feature_train.viewmodel // ktlint-disable package-name

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mendes_jv.leal_train.common.Result
import com.mendes_jv.leal_train.data.repositories.TrainRepository
import com.mendes_jv.leal_train.feature_train.events.TrainScreenUiEvent
import com.mendes_jv.leal_train.feature_train.side_effects.TrainScreenSideEffects
import com.mendes_jv.leal_train.feature_train.state.TrainScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainViewModel @Inject
    constructor(private val trainRepository: TrainRepository) : ViewModel() {

    private val _state: MutableStateFlow<TrainScreenUiState> =
        MutableStateFlow(TrainScreenUiState())
    val state: StateFlow<TrainScreenUiState> = _state.asStateFlow()

    private val _effect: Channel<TrainScreenSideEffects> = Channel()
    val effect = _effect.receiveAsFlow()

    init {
        sendEvent(TrainScreenUiEvent.GetTrain)
    }

    fun sendEvent(event: TrainScreenUiEvent) {
        reduce(oldState = _state.value, event = event)
    }

    private fun setEffect(builder: () -> TrainScreenSideEffects) {
        val effectValue = builder()
        viewModelScope.launch { _effect.send(effectValue) }
    }

    private fun setState(newState: TrainScreenUiState) {
        _state.value = newState
    }

    private fun reduce(oldState: TrainScreenUiState, event: TrainScreenUiEvent) {
        when (event) {
            is TrainScreenUiEvent.AddTrain -> {
                addTask(t)
            }

            is TrainScreenUiEvent.DeleteTrain -> {
                deleteNote(oldState = oldState, taskId = event.taskId)
            }

            TrainScreenUiEvent.GetTrain -> {
                getTasks(oldState = oldState)
            }

            is TrainScreenUiEvent.OnChangeAddTrainDialogState -> {
                onChangeAddTaskDialog(oldState = oldState, isShown = event.show)
            }

            is TrainScreenUiEvent.OnChangeUpdateTrainDialogState -> {
                onUpdateAddTaskDialog(oldState = oldState, isShown = event.show)
            }

            is TrainScreenUiEvent.OnChangeTaskBody -> {
                onChangeTaskBody(oldState = oldState, body = event.body)
            }

            is TrainScreenUiEvent.OnChangeTrainDescription -> {
                onChangeTaskTitle(oldState = oldState, title = event.title)
            }

            is TrainScreenUiEvent.SetTrainToBeUpdated -> {
                setTaskToBeUpdated(oldState = oldState, task = event.trainToBeUpdated)
            }

            TrainScreenUiEvent.UpdateTrain -> {
                updateNote(oldState = oldState)
            }
        }
    }

    private fun addTask(title: String, body: String, oldState: TrainScreenUiState) {
        viewModelScope.launch {
            setState(oldState.copy(isLoading = true))

            when (val result = trainRepository.addTask(title = title, body = body)) {
                is Result.Failure -> {
                    setState(oldState.copy(isLoading = false))

                    val errorMessage =
                        result.exception.message ?: "An error occurred when adding task"
                    setEffect { TrainScreenSideEffects.ShowSnackBarMessage(message = errorMessage) }
                }

                is Result.Success -> {
                    setState(
                        oldState.copy(
                            isLoading = false,
                            currentTextFieldTitle = "",
                            currentTextFieldBody = "",
                        ),
                    )

                    sendEvent(TrainScreenUiEvent.OnChangeAddTrainDialogState(show = false))

                    sendEvent(TrainScreenUiEvent.GetTrain)

                    setEffect { TrainScreenSideEffects.ShowSnackBarMessage(message = "Task added successfully") }
                }
            }
        }
    }

    private fun getTasks(oldState: TrainScreenUiState) {
        viewModelScope.launch {
            setState(oldState.copy(isLoading = true))

            when (val result = trainRepository.getAllTasks()) {
                is Result.Failure -> {
                    setState(oldState.copy(isLoading = false))

                    val errorMessage =
                        result.exception.message ?: "An error occurred when getting your task"
                    setEffect { TrainScreenSideEffects.ShowSnackBarMessage(message = errorMessage) }
                }

                is Result.Success -> {
                    val tasks = result.data
                    setState(oldState.copy(isLoading = false, tasks = tasks))
                }
            }
        }
    }

    private fun deleteNote(oldState: TrainScreenUiState, taskId: String) {
        viewModelScope.launch {
            setState(oldState.copy(isLoading = true))

            when (val result = trainRepository.deleteTask(taskId = taskId)) {
                is Result.Failure -> {
                    setState(oldState.copy(isLoading = false))

                    val errorMessage =
                        result.exception.message ?: "An error occurred when deleting task"
                    setEffect { TrainScreenSideEffects.ShowSnackBarMessage(message = errorMessage) }
                }

                is Result.Success -> {
                    setState(oldState.copy(isLoading = false))

                    setEffect { TrainScreenSideEffects.ShowSnackBarMessage(message = "Task deleted successfully") }

                    sendEvent(TrainScreenUiEvent.GetTrain)
                }
            }
        }
    }

    private fun updateNote(oldState: TrainScreenUiState) {
        viewModelScope.launch {
            setState(oldState.copy(isLoading = true))

            val title = oldState.currentTextFieldTitle
            val body = oldState.currentTextFieldBody
            val taskToBeUpdated = oldState.taskToBeUpdated

            when (
                val result = trainRepository.updateTask(
                    title = title,
                    body = body,
                    taskId = taskToBeUpdated?.taskId ?: "",
                )
            ) {
                is Result.Failure -> {
                    setState(oldState.copy(isLoading = false))

                    val errorMessage =
                        result.exception.message ?: "An error occurred when updating task"
                    setEffect { TrainScreenSideEffects.ShowSnackBarMessage(message = errorMessage) }
                }

                is Result.Success -> {
                    setState(
                        oldState.copy(
                            isLoading = false,
                            currentTextFieldTitle = "",
                            currentTextFieldBody = "",
                        ),
                    )

                    sendEvent(TrainScreenUiEvent.OnChangeUpdateTrainDialogState(show = false))

                    setEffect { TrainScreenSideEffects.ShowSnackBarMessage(message = "Task updated successfully") }

                    sendEvent(TrainScreenUiEvent.GetTrain)
                }
            }
        }
    }

    private fun onChangeAddTaskDialog(oldState: TrainScreenUiState, isShown: Boolean) {
        setState(oldState.copy(isShowAddTaskDialog = isShown))
    }

    private fun onUpdateAddTaskDialog(oldState: TrainScreenUiState, isShown: Boolean) {
        setState(oldState.copy(isShowUpdateTaskDialog = isShown))
    }

    private fun onChangeTaskBody(oldState: TrainScreenUiState, body: String) {
        setState(oldState.copy(currentTextFieldBody = body))
    }

    private fun onChangeTaskTitle(oldState: TrainScreenUiState, title: String) {
        setState(oldState.copy(currentTextFieldTitle = title))
    }

    private fun setTaskToBeUpdated(oldState: TrainScreenUiState, task: Task) {
        setState(oldState.copy(taskToBeUpdated = task))
    }
}
