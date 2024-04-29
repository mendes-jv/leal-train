package com.mendes_jv.leal_train.feature_tasks.state

import com.mendes_jv.leal_train.data.model.Train

data class TasksScreenUiState(
    val isLoading: Boolean = false,
    val tasks: List<Train> = emptyList(),
    val errorMessage: String? = null,
    val taskToBeUpdated: Train? = null,
    val isShowAddTaskDialog: Boolean = false,
    val isShowUpdateTaskDialog: Boolean = false,
    val currentTextFieldTitle: String = "",
    val currentTextFieldBody: String = "",
)
