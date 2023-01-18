package di

import data.KtorRealtimeMessagingMessagingClient
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.logging.*
import io.ktor.client.features.websocket.*
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import ui.PlayViewModel
import ui.menu.JoinViewModel

val appModule = module {
    single {
        PlayViewModel(
            client = ktorClient()
        )
    }
    single { JoinViewModel() }
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(appModule)
    }

fun ktorClient(): KtorRealtimeMessagingMessagingClient {
    val client = HttpClient(CIO) {
        install(Logging)
        install(WebSockets)
    }

    return KtorRealtimeMessagingMessagingClient(client)
}