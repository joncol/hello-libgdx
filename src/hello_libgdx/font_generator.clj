(ns hello-libgdx.font-generator
  (:import [com.badlogic.gdx Gdx]
           [com.badlogic.gdx.graphics Color]
           [com.badlogic.gdx.graphics.g2d BitmapFont]
           [com.badlogic.gdx.graphics.g2d.freetype
            FreeTypeFontGenerator FreeTypeFontGenerator$FreeTypeFontParameter]))

(defn- font-params [{:keys [size color border-width border-color border-straight
                            shadow-offset-x shadow-offset-y shadow-color]
                     :or   {size            16
                            color           (Color/WHITE)
                            border-width    0
                            border-color    (Color/BLACK)
                            border-straight false
                            shadow-offset-x 0
                            shadow-offset-y 0
                            shadow-color    (Color. 0 0 0 0.75)}}]
  (let [p (FreeTypeFontGenerator$FreeTypeFontParameter.)]
    (set! (.size p) size)
    (set! (.color p) color)
    (set! (.borderWidth p) border-width)
    (set! (.borderColor p) border-color)
    (set! (.borderStraight p) border-straight)
    (set! (.shadowOffsetX p) shadow-offset-x)
    (set! (.shadowOffsetY p) shadow-offset-y)
    (set! (.shadowColor p) shadow-color)
    p))

(defn generate-font [filename & params]
  (let [font-gen (FreeTypeFontGenerator. (.internal Gdx/files filename))
        font     (.generateFont font-gen (font-params params))]
    (.dispose font-gen)
    font))
