(ns nba.cljs-util
  (:require [cljs.reader :as reader]
            [clojure.string :as str]
    ;["fs" :as fs]
            ))

;; Uncaught ReferenceError: require is not defined
;; That's because it is a node.js thing. Pointless act!
(def fs (js/require "fs"))

(defn read-text [path f]
  (.readFile fs path "utf8" (fn [err data] (f data))))

(defn- read-chunk [fd]
  (let [length 128
        b (js/Buffer. length)
        bytes-read (.readSync fs fd b 0 length nil)]
    (if (> bytes-read 0)
      (.toString b "utf8" 0 bytes-read))))

(defn line-seq
  ([fd]
   (line-seq fd nil))
  ([fd line]
   (if-let [chunk (read-chunk fd)]
     (if (re-find #"\n" (str line chunk))
       (let [lines (str/split (str line chunk) #"\n")]
         (if (= 1 (count lines))
           (lazy-cat lines (line-seq fd))
           (lazy-cat (butlast lines) (line-seq fd (last lines)))))
       (recur fd (str line chunk)))
     (if line
       (list line)
       ()))))

(defn process [data]
  (line-seq data))
