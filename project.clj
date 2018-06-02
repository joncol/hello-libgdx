(defproject hello-libgdx "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[com.badlogicgames.gdx/gdx "1.9.8"]
                 [com.badlogicgames.gdx/gdx-backend-lwjgl "1.9.8"]
                 [com.badlogicgames.gdx/gdx-box2d "1.9.8"]
                 [com.badlogicgames.gdx/gdx-box2d-platform "1.9.8"
                  :classifier "natives-desktop"]
                 [com.badlogicgames.gdx/gdx-bullet "1.9.8"]
                 [com.badlogicgames.gdx/gdx-bullet-platform "1.9.8"
                  :classifier "natives-desktop"]
                 [com.badlogicgames.gdx/gdx-freetype "1.9.8"]
                 [com.badlogicgames.gdx/gdx-freetype-platform "1.9.8"
                  :classifier "natives-desktop"]
                 [com.badlogicgames.gdx/gdx-platform "1.9.8"
                  :classifier "natives-desktop"]
                 [org.clojure/clojure "1.8.0"]]
  :source-paths ["src"]
  :javac-options ["-target" "1.8" "-source" "1.8" "-Xlint:-options"]
  :aot [hello-libgdx.hud
        hello-libgdx.screens.main-screen
        hello-libgdx.core
        hello-libgdx.core.desktop-launcher]
  :main hello-libgdx.core.desktop-launcher
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
