package io.piotrjastrzebski.lis.android

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import io.piotrjastrzebski.lis.LiSGame

/**
 * Created by EvilEntity on 20/12/2015.
 */
public class AndroidLauncher : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = AndroidApplicationConfiguration()
        initialize(LiSGame(), config)
    }
}
