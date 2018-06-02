(ns hello-libgdx.core.desktop-launcher
  (:gen-class)
  (:import com.badlogic.gdx.backends.lwjgl.LwjglApplication
           org.lwjgl.input.Keyboard))

(defn -main []
  (LwjglApplication. (hello-libgdx.core.Game.) "hello, libgdx" 1920 1200))
