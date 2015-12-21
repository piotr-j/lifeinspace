package io.piotrjastrzebski.lis.android

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import io.piotrjastrzebski.lis.LiSGame
import io.piotrjastrzebski.lis.PlatformBridge

/**
 * Created by EvilEntity on 20/12/2015.
 */
public class AndroidLauncher : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bridge = object : PlatformBridge {
            override fun getPixelScaleFactor() = 1f
        }

        val config = AndroidApplicationConfiguration()
        initialize(LiSGame(bridge), config)
    }
}
