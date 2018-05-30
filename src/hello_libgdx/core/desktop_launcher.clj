(ns hello-libgdx.core.desktop-launcher
  (:require [hello-libgdx.core :refer :all])
  (:import [com.badlogic.gdx.backends.lwjgl LwjglApplication]
           [org.lwjgl.input Keyboard])
  (:gen-class))

(defn -main []
  (LwjglApplication. (hello-libgdx.core.Game.) "hello, libgdx" 1920 1200)
  (Keyboard/enableRepeatEvents true))
