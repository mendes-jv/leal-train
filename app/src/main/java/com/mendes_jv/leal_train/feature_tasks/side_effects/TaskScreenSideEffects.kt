package com.mendes_jv.leal_train.feature_tasks.side_effects

sealed class TaskScreenSideEffects {
    data class ShowSnackBarMessage(val message: String) : TaskScreenSideEffects()
}
