(ns general.stopwatch
  (:require [general.dev :as dev]))

(defn now-millisecs []
  #?(:clj (.getTime (java.util.Date.))
     :cljs (.getTime (js/Date.))))

(defn start []
  (let [started-at (now-millisecs)]
    (fn []
      (let [ended-at (now-millisecs)]
        (- ended-at started-at)))))

;;
;; Prints the time for each segment of work
;; Useful when top level function is created as last thing in let, then
;; inner function in a threading marco, which might or might not be in the
;; let.
;;
(defn time-probe-hof
  ([named]
   (time-probe-hof named true false))
  ([named pr-every? show-number?]
   (let [last-elapsed (atom 0)
         ;; Good to number as quick ones will
         iter (atom 1)
         elapsed-f (start)]
     (fn [x]
       (let [elapsed (elapsed-f)
             diff (- elapsed @last-elapsed)]
         (when (or pr-every? (> diff 1))
           (if show-number?
             (dev/log-on @iter named diff "msecs")
             (dev/log-on named diff "msecs")))
         (reset! last-elapsed elapsed)
         (swap! iter inc))
       x))))