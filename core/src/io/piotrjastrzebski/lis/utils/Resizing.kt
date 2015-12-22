package io.piotrjastrzebski.lis.utils

/**
 * Marks System as resizing, resize will be called when size of the window changes
 *
 * Created by PiotrJ on 22/12/15.
 */
interface Resizing {
    fun resize(width: Int, height: Int)
}
