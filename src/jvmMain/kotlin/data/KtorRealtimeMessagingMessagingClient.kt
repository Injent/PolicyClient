package data

import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.channels.consumeEach
import packets.ClientPacketSerializer
import packets.Packet
import packets.ServerPacketSerializer
import utils.jsonFormat

class KtorRealtimeMessagingMessagingClient(
    private val client: HttpClient
) : RealtimeMessagingClient {

    private var session: WebSocketSession? = null

    override suspend fun getPacketListener(
        ip: String,
        playerName: String,
        rgbColor: String,
        onPacketReceived: (packet: Packet) -> Unit,
        onCatchError: (exception: Exception) -> Unit
    ) {
        session = client.webSocketSession {
            url("ws://$ip/play/$playerName/$rgbColor")
        }
        session!!
            .incoming
            .consumeEach {
                try {
                    val json = String(it.data)
                    val packet = jsonFormat.decodeFromString(ClientPacketSerializer, json)
                    println(json)
                    onPacketReceived(packet)
                } catch (e: Exception) {
                    onCatchError(e)
                }
            }
    }

    override suspend fun sendPacket(packet: Packet) {
        session?.outgoing?.send(
            Frame.Text(jsonFormat.encodeToString(ServerPacketSerializer, packet))
        )
    }

    override suspend fun close() {
        session?.close()
        session = null
    }
}