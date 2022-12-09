package bme.spoti.redflags.di

import bme.spoti.redflags.data.model.websocket.WebsocketRequestModel
import bme.spoti.redflags.features.nearby.NearbyService
import bme.spoti.redflags.features.nearby.NearbyServiceImpl
import bme.spoti.redflags.network.http.cats.SetupApi
import bme.spoti.redflags.network.ws.CustomWebSocketRequestModelAdapter
import bme.spoti.redflags.network.ws.RedFlagsSocketService
import bme.spoti.redflags.network.ws.RedFlagsSocketServiceImpl
import bme.spoti.redflags.other.Constants
import bme.spoti.redflags.receiver.WebSocketReceiver
import bme.spoti.redflags.repository.GameRepository
import bme.spoti.redflags.repository.GameRepositoryImpl
import bme.spoti.redflags.repository.SetupRepository
import bme.spoti.redflags.repository.SetupRepositoryImpl
import bme.spoti.redflags.ui.ingame.adapters.CardAdapter
import bme.spoti.redflags.ui.ingame.screens.date_creation.DateCreationViewModel
import bme.spoti.redflags.ui.ingame.screens.end.EndViewModel
import bme.spoti.redflags.ui.ingame.screens.leaderboard.LeaderboardViewModel
import bme.spoti.redflags.ui.ingame.screens.lobby.LobbyViewModel
import bme.spoti.redflags.ui.ingame.screens.reconnect.ReconnectViewModel
import bme.spoti.redflags.ui.ingame.screens.sabotage.SabotageViewModel
import bme.spoti.redflags.ui.ingame.screens.showcase_all.ShowcaseAllViewModel
import bme.spoti.redflags.ui.ingame.screens.showcase_one.ShowcaseOneViewModel
import bme.spoti.redflags.ui.ingame.screens.single_reveal.SingleRevealViewModel
import bme.spoti.redflags.ui.ingame.screens.waiting_for_reconnect.WaitingForReconnectViewModel
import bme.spoti.redflags.ui.ingame.screens.winner_showcase.WinnerAnnouncementViewModel
import bme.spoti.redflags.ui.setup.screens.landing.LandingViewModel
import bme.spoti.redflags.ui.setup.screens.nearby.NearbyRoomsViewModel
import bme.spoti.redflags.ui.setup.screens.new_game.NewGameViewModel
import bme.spoti.redflags.ui.setup.screens.player_creation.PlayerCreationViewModel
import bme.spoti.redflags.utils.clientID
import bme.spoti.redflags.utils.dataStore
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.annotation.KoinReflectAPI
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun provideOkHttpClient(clientID: String): OkHttpClient {
	return OkHttpClient.Builder()
		.addInterceptor { chain ->
			val url =
				chain.request().url.newBuilder().addQueryParameter("client_id", clientID).build()
			val request = chain.request().newBuilder()
				.url(url)
				.build()
			chain.proceed(request)
		}
		.addInterceptor(HttpLoggingInterceptor().apply {
			level = HttpLoggingInterceptor.Level.BODY
		})
		.build()
}

fun provideSetupApi(okHttpClient: OkHttpClient): SetupApi {
	return Retrofit.Builder()
		.baseUrl(if (Constants.USE_LOCALHOST) Constants.HTTP_BASE_URL_LOCALHOST else Constants.HTTP_BASE_URL)
		.addConverterFactory(GsonConverterFactory.create())
		.client(okHttpClient)
		.build()
		.create(SetupApi::class.java)
}

val networkModule = module {
	single { provideOkHttpClient(get(named("clientID"))) }
	single { provideSetupApi(get()) }
	single { androidContext().dataStore }
	single<String>(named("clientID")) { runBlocking { androidContext().dataStore.clientID() } }
	single<HttpClient> {
		val client = HttpClient(CIO) {
			install(WebSockets){
				pingInterval = 3000
			}
		}
		client.sendPipeline.intercept(HttpSendPipeline.State){
			context.parameter("client_id",get<String>(named("clientID")))
		}
		client
	}
	single<RedFlagsSocketService> {RedFlagsSocketServiceImpl(get(), get(named("WebSocketGson")))}
	single<Gson>(named("WebSocketGson")) {
		GsonBuilder()
			.registerTypeAdapter(
				WebsocketRequestModel::class.java,
				CustomWebSocketRequestModelAdapter()
			).create()
	}

}

val repositoryModule = module {
	single<SetupRepository> { SetupRepositoryImpl(get(), androidContext()) }
	single<GameRepository> { GameRepositoryImpl(get(),get()) }
}


val othersModule = module {
	factory { params->CardAdapter(androidContext(),params.get()) }
	factory<WebSocketReceiver> { WebSocketReceiver(get(), get()) }
	single<NearbyService> {
		NearbyServiceImpl(androidContext())
	}
}

@OptIn(KoinReflectAPI::class)
val viewModelModule = module {
	viewModel<LandingViewModel>()
	viewModel<NewGameViewModel>()
	viewModel<PlayerCreationViewModel>()
	viewModel<LobbyViewModel>(){
		LobbyViewModel(
			get(),
			get(),
			get(),
			get(),
			get<String>(named("clientID"))

		)
	}
	viewModel<SingleRevealViewModel>()
	viewModel<DateCreationViewModel>()
	viewModel<ShowcaseOneViewModel>()
	viewModel<ShowcaseAllViewModel>()
	viewModel<WinnerAnnouncementViewModel>()
	viewModel<SabotageViewModel>()
	viewModel<EndViewModel>()
	viewModel<LeaderboardViewModel>()
	viewModel<WaitingForReconnectViewModel>()
	viewModel<ReconnectViewModel>()
	viewModel<NearbyRoomsViewModel> {
		NearbyRoomsViewModel(
			get(),
			get<String>(named("clientID"))
			)
	}
}