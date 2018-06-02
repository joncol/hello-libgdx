(ns hello-libgdx.core.desktop-launcher
  (:gen-class)
  (:import [com.badlogic.gdx.backends.lwjgl
            LwjglApplication LwjglApplicationConfiguration]
           [org.lwjgl.input Keyboard]))

(defn -main []
  (let [config (LwjglApplicationConfiguration.)]
    (set! (.title config) "hello, libgdx")
    (set! (.width config) 1920)
    (set! (.height config) 1200)
    (set! (.samples config) 3)
    (LwjglApplication. (hello-libgdx.core.Game.) config)))
