package org.succlz123.app.acfun

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.view.View
import android.view.animation.DecelerateInterpolator
import java.util.*

fun View.requestFocusTV() {
    isFocusable = true
    isFocusableInTouchMode = true
    requestFocus()
    requestFocusFromTouch()
}

const val DEFAULT_TRAN_DUR_ANIM = 200
const val DEFAULT_SCALE = 1.1f

fun View.onScale(scale: Float, duration: Int) {
    animate().scaleX(scale).scaleY(scale).setDuration(duration.toLong()).start()
}

fun View.onScale(scaleX: Float, scaleY: Float, duration: Int) {
    animate().scaleX(scaleX).scaleY(scaleY).setDuration(duration.toLong()).start()
}

fun View.onScale(needPromptly: Boolean, scale: Float, duration: Int) {
    var animTime = duration
    if (needPromptly) {
        animTime = 0
    }
    animate().scaleX(scale).scaleY(scale).setDuration(animTime.toLong()).start()
}

fun View.onScaleViewWithFocus(hasFocus: Boolean) {
    if (hasFocus) {
        onScale(DEFAULT_SCALE, DEFAULT_TRAN_DUR_ANIM)
    } else {
        onScale(1.0f, DEFAULT_TRAN_DUR_ANIM)
    }
}

fun View.onScaleViewWithFocus(scale: Float, hasFocus: Boolean) {
    if (hasFocus) {
        onScale(scale, DEFAULT_TRAN_DUR_ANIM)
    } else {
        onScale(1.0f, DEFAULT_TRAN_DUR_ANIM)
    }
}

fun View.onScaleDefault() {
    animate().scaleX(1.0f).scaleY(1.0f).setDuration(0).start()
}

fun onScaleNewAndOldView(newView: View, oldView: View, scale: Float = DEFAULT_SCALE, duration: Int = DEFAULT_TRAN_DUR_ANIM) {
    val scaleAnimatorSet = AnimatorSet()
    val animatorList = ArrayList<Animator>()
    animatorList.addAll(getScaleAnimator(oldView, scale, false))
    animatorList.addAll(getScaleAnimator(newView, scale, true))

    scaleAnimatorSet.playTogether(animatorList)
    scaleAnimatorSet.interpolator = DecelerateInterpolator(1f)
    scaleAnimatorSet.duration = duration.toLong()
    scaleAnimatorSet.start()
}

private fun getScaleAnimator(view: View, scale: Float, isScale: Boolean): List<Animator> {
    val animatorList = ArrayList<Animator>(2)

    var scaleBefore = 1.0f
    var scaleAfter = scale

    if (!isScale) {
        scaleBefore = scale
        scaleAfter = 1.0f
    }

    val scaleX = ObjectAnimator.ofFloat(view, "scaleX", scaleBefore, scaleAfter)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
        scaleX.setAutoCancel(true)
    }
    val scaleY = ObjectAnimator.ofFloat(view, "scaleY", scaleBefore, scaleAfter)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
        scaleX.setAutoCancel(true)
    }
    animatorList.add(scaleX)
    animatorList.add(scaleY)
    return animatorList
}

fun toggleHideBar(decorView: View) {
    // The UI options currently enabled are represented by a bitfield.
    // getSystemUiVisibility() gives us that bitfield.
    val uiOptions = decorView.systemUiVisibility
    var newUiOptions = uiOptions
    val isImmersiveModeEnabled = uiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY == uiOptions
    if (isImmersiveModeEnabled) {
        //            Log.i(TAG, "Turning immersive mode mode off. ");
    } else {
        //            Log.i(TAG, "Turning immersive mode mode on.");
    }

    // Navigation bar hiding:  Backwards compatible to ICS.
    if (Build.VERSION.SDK_INT >= 14) {
        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }

    // Status bar hiding: Backwards compatible to Jellybean
    if (Build.VERSION.SDK_INT >= 16) {
        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    // Immersive mode: Backward compatible to KitKat.
    // Note that this flag doesn't do anything by itself, it only augments the behavior
    // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
    // all three flags are being toggled together.
    // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
    // Sticky immersive mode differs in that it makes the navigation and status bars
    // semi-transparent, and the UI flag does not get cleared when the user interacts with
    // the screen.
    if (Build.VERSION.SDK_INT >= 18) {
        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    if (Build.VERSION.SDK_INT >= 19) {
        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_IMMERSIVE
    }

    decorView.systemUiVisibility = newUiOptions
}