(ns hello-libgdx.util
  (:require [clojure.pprint :refer [print-table]]))

(defn object-fields [obj]
  (into [] (for [f (.. obj getClass getDeclaredFields)]
             (do (.setAccessible f true)
                 {:name  (.getName f)
                  :value (.get f obj)}))))

(defn print-object-fields [obj]
  (print-table [:name :value] (object-fields obj)))
