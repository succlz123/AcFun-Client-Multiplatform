package org.succlz123.lib.video.vlc

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ImageInfo
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer
import uk.co.caprica.vlcj.player.embedded.videosurface.CallbackVideoSurface
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoSurfaceAdapters
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormat
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormatCallback
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.RenderCallback
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.format.RV32BufferFormat
import java.nio.ByteBuffer
import java.util.*

private fun isMacOS(): Boolean {
    val os = System.getProperty("os.name", "generic").lowercase(Locale.ENGLISH)
    return os.indexOf("mac") >= 0 || os.indexOf("darwin") >= 0
}

@Composable
internal fun VideoPlayerImpl(url: String) {
    val mediaPlayerComponent = remember {
        // see https://github.com/caprica/vlcj/issues/887#issuecomment-503288294 for why we're using CallbackMediaPlayerComponent for macOS.
        if (isMacOS()) {
            CallbackMediaPlayerComponent()
        } else {
            EmbeddedMediaPlayerComponent()
        }
    }
    LaunchedEffect(Unit) {
        mediaPlayerComponent.mediaPlayer().media().play(url)
    }
    return SwingPanel(
        background = Color.Transparent,
        modifier = Modifier.fillMaxSize(),
        factory = {
            mediaPlayerComponent
        }
    )
}

private fun Any.mediaPlayer(): MediaPlayer {
    return when (this) {
        is CallbackMediaPlayerComponent -> mediaPlayer()
        is EmbeddedMediaPlayerComponent -> mediaPlayer()
        else -> throw IllegalArgumentException("You can only call mediaPlayer() on vlcj player component")
    }
}

class VideoPlayerVlcWrapper(val imageBitmapState: MutableState<ImageBitmap?>) {
    var mediaPlayer = mutableStateOf<EmbeddedMediaPlayer?>(null)

    suspend fun init(): EmbeddedMediaPlayer? {
        if (mediaPlayer.value != null) {
            return null
        }
        var byteArray: ByteArray? = null
        var info: ImageInfo? = null

        val embeddedMediaPlayer = if (isMacOS()) {
            CallbackMediaPlayerComponent().mediaPlayer()
        } else {
            EmbeddedMediaPlayerComponent().mediaPlayer()
        }
        val callbackVideoSurface = CallbackVideoSurface(
            object : BufferFormatCallback {
                override fun getBufferFormat(sourceWidth: Int, sourceHeight: Int): BufferFormat {
                    info = ImageInfo.makeN32(sourceWidth, sourceHeight, ColorAlphaType.OPAQUE)
                    return RV32BufferFormat(sourceWidth, sourceHeight)
                }

                override fun allocatedBuffers(buffers: Array<out ByteBuffer>) {
                    byteArray = ByteArray(buffers[0].limit())
                }
            },
            object : RenderCallback {
                override fun display(
                    mediaPlayer: MediaPlayer, nativeBuffers: Array<out ByteBuffer>, bufferFormat: BufferFormat?
                ) {
                    val byteBuffer = nativeBuffers[0]

                    byteBuffer.get(byteArray)
                    byteBuffer.rewind()

                    val bmp = Bitmap()
                    bmp.allocPixels(info!!)
                    bmp.installPixels(byteArray)
                    imageBitmapState.value = bmp.asComposeImageBitmap()
                }
            },
            true,
            VideoSurfaceAdapters.getVideoSurfaceAdapter(),
        )
        embeddedMediaPlayer.videoSurface().set(callbackVideoSurface)
        return embeddedMediaPlayer
    }
}

@Composable
fun rememberMediaPlayer(imageBitmapState: MutableState<ImageBitmap?>): VideoPlayerVlcWrapper {
    return remember {
        VideoPlayerVlcWrapper(imageBitmapState)
    }
}
