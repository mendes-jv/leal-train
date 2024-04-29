package com.mendes_jv.leal_train.di

import com.mendes_jv.leal_train.data.repositories.TrainRepository
import com.mendes_jv.leal_train.data.repositories.TrainRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideTrainRepository(
        firebaseFirestore: FirebaseFirestore,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): TrainRepository = TrainRepositoryImpl(firebaseFirestore, ioDispatcher)
}
