(ns user
  (:require
    [clojure.pprint :refer [pprint]]
    [clojure.stacktrace :refer [print-stack-trace]]
    [clojure.tools.namespace.repl :as tools-ns :refer [set-refresh-dirs]]))

(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)

(set-refresh-dirs "dev/server" "src/main" "src/test")

(defn refresh [& args]
  (apply tools-ns/refresh args))