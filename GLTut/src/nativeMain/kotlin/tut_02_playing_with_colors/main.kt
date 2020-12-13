package tut_02_playing_with_colors

import framework.Framework
import gl_wrapper.GLWrapper


fun main() {
    val glWrapper = GLWrapper()
    val tutorial = FragPosition(glWrapper)
    val framework = Framework()
    framework.launchTutorial(tutorial)

}