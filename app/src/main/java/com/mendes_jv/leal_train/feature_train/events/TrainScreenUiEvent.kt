package com.mendes_jv.leal_train.feature_train.events

import com.mendes_jv.leal_train.data.model.Train

sealed class TrainScreenUiEvent {
    object GetTrain : TrainScreenUiEvent()

    data class AddTrain(
        val number: Number,
        val description: String,
        val data: String,
        val exercises: List<Train>
    ) : TrainScreenUiEvent()

    object UpdateTrain : TrainScreenUiEvent()

    data class DeleteTrain(val name: Number) : TrainScreenUiEvent()

    data class OnChangeTrainDescription(val name: Number) : TrainScreenUiEvent()

    data class OnChangeAddTrainDialogState(val show: Boolean) : TrainScreenUiEvent()

    data class OnChangeUpdateTrainDialogState(val show: Boolean) : TrainScreenUiEvent()

    data class SetTrainToBeUpdated(val trainToBeUpdated: Train) : TrainScreenUiEvent()
}
