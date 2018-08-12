(ns general.stopwatch
  (:require [general.dev :as dev]))

(defn now-millisecs []
  #?(:clj  (.getTime (java.util.Date.))
     :cljs (.getTime (js/Date.))))

(defn start []
  (let [started-at (now-millisecs)]
    (fn []
      (let [ended-at (now-millisecs)]
        (- ended-at started-at)))))

(defn take-intervals-hof
  "Prints the time taken for each interval of work. Pass to the inner fn a reasonable amount of time,
  beyond which you wish to be informed"
  [interval-names]
  (assert (vector? interval-names))
  (let [last-elapsed (atom 0)
        iter (atom 0)
        elapsed-f (start)]
    (fn [limit]
      (let [elapsed (elapsed-f)
            diff (- elapsed @last-elapsed)]
        (when (> diff limit)
          (dev/log (nth interval-names @iter) diff "msecs"))
        (reset! last-elapsed elapsed)
        (swap! iter inc))
      nil)))