package spoti.di

import aws.sdk.kotlin.runtime.auth.credentials.EnvironmentCredentialsProvider
import aws.sdk.kotlin.runtime.auth.credentials.ProfileCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.smithy.kotlin.runtime.http.engine.ktor.KtorEngine
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import org.koin.dsl.module
import spoti.repo.CardRepository
import spoti.repo.RoomRepository
import spoti.service.CardService
import spoti.service.CardServiceImpl

val gameModule = module {
    single<RoomRepository> { RoomRepository() }
    single<CardRepository> { CardRepository() }
    single<CardServiceImpl> {CardServiceImpl(get())}
    single<Gson>{GsonBuilder().create()}
    single<S3Client> {
        runBlocking { S3Client.fromEnvironment {
            region="eu-central-1"
        }
    }}
}