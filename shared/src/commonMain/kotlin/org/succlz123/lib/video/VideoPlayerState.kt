package org.succlz123.lib.video

sealed class VideoPlayerState {

    object Init : VideoPlayerState()

    class Buffering(val progress: Float = 0f) : VideoPlayerState() {

        override fun toString(): String {
            return "Buffering: $progress"
        }
    }

    object Playing : VideoPlayerState()

    object Pause : VideoPlayerState()

    object Seeking : VideoPlayerState()

    object Stop : VideoPlayerState()

    object Error : VideoPlayerState()
}
