package io.piotrjastrzebski.lis.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import io.piotrjastrzebski.lis.LiSGame

/**
 * Created by EvilEntity on 20/12/2015.
 */
fun main(args: Array<String>) {
    val config = LwjglApplicationConfiguration()
    config.width = 1280
    config.height = 720
    LwjglApplication(LiSGame(), config)
}
