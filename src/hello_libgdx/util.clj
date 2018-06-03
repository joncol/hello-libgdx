(ns hello-libgdx.util
  (:require [clojure.pprint :refer [print-table]]))

(defn object-fields [obj]
  (into [] (for [f (.. obj getClass getDeclaredFields)]
             (do (.setAccessible f true)
                 {:name  (.getName f)
                  :value (.get f obj)}))))

(defn print-object-fields [obj]
  (print-table [:name :value] (object-fields obj)))

(defmacro with-disposable
  "bindings => [name init ...]

  Evaluates body in a try expression with names bound to the values
  of the inits, and a finally clause that calls (.dispose name) on each
  name in reverse order."
  [bindings & body]
  (#'clojure.core/assert-args
   (vector? bindings) "a vector for its binding"
   (even? (count bindings)) "an even number of forms in binding vector")
  (cond
    (= (count bindings) 0) `(do ~@body)
    (symbol? (bindings 0)) `(let ~(subvec bindings 0 2)
                              (try
                                (with-disposable ~(subvec bindings 2) ~@body)
                                (finally
                                  (. ~(bindings 0) dispose))))
    :else (throw (IllegalArgumentException.
                  "with-disposable only allows Symbols in bindings"))))
