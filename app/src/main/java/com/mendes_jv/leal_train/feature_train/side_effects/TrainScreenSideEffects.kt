package com.mendes_jv.leal_train.feature_train.side_effects

sealed class TrainScreenSideEffects {
    data class ShowSnackBarMessage(val message: String) : TrainScreenSideEffects()
}
