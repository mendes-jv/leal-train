package com.mendes_jv.leal_train.feature_train.state

import com.mendes_jv.leal_train.data.model.Train

data class TrainScreenUiState(
    val isLoading: Boolean = false,
    val trains: List<Train> = emptyList(),
    val errorMessage: String? = null,
    val trainToBeUpdated: Train? = null,
    val isShowAddTrainDialog: Boolean = false,
    val isShowUpdateTrainDialog: Boolean = false,
    val currentTextFieldDescription: String = "",
)
