package io.piotrjastrzebski.lis.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import io.piotrjastrzebski.lis.LiSGame
import io.piotrjastrzebski.lis.PlatformBridge
import org.lwjgl.opengl.Display

/**
 * Created by EvilEntity on 20/12/2015.
 */
fun main(args: Array<String>) {
    val config = LwjglApplicationConfiguration()
    config.width = 1280
    config.height = 720
    LwjglApplication(LiSGame(bridge), config)
}

object bridge : PlatformBridge {
    override fun getPixelScaleFactor() = Display.getPixelScaleFactor()
}
