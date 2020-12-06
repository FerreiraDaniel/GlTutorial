package tut1_hello_triangle

import framework.Framework
import gl_wrapper.GLWrapper


fun main() {
    val glWrapper = GLWrapper()
    val tutorial = Tutorial1(glWrapper)
    val framework = Framework()
    framework.launchTutorial(tutorial)

}