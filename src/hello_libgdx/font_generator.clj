(ns hello-libgdx.font-generator
  (:import [com.badlogic.gdx Gdx]
           [com.badlogic.gdx.graphics.g2d BitmapFont]
           [com.badlogic.gdx.graphics.g2d.freetype
            FreeTypeFontGenerator FreeTypeFontGenerator$FreeTypeFontParameter]))

(defn- font-params [size]
  (let [params (FreeTypeFontGenerator$FreeTypeFontParameter.)]
    (set! (.size params) size)
    params))

(defn generate-font [filename size]
  (let [font-gen (FreeTypeFontGenerator. (.internal Gdx/files filename))
        font     (.generateFont font-gen (font-params size))]
    (.dispose font-gen)
    font))
