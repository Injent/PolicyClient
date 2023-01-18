package data

import packets.Packet

interface RealtimeMessagingClient {
    suspend fun getPacketListener(
        ip: String = "127.0.0.1:25565",
        playerName: String,
        rgbColor: String = "0;0;0",
        onPacketReceived: (packet: Packet) -> Unit,
        onCatchError: (exception: Exception) -> Unit
    )
    suspend fun sendPacket(packet: Packet)
    suspend fun close()
}