(ns nba.core
  (:require [general.dev :as dev]
            [nba.util :as util]))

(defn x-1 []
  (let [data-rows (util/data)]
    (->> data-rows
         (map util/clean-row)
         (map #(select-keys % [:pname :year :max]))
         (sort-by (comp - :max))
         (take 50)
         dev/pp)))

(defn x-2 []
  (let [data-rows (util/data)]
    (->> data-rows
         (map util/clean-row)
         (map #(select-keys % [:min :max]))
         (sort-by (comp - :max))
         (map (juxt :min :max))
         (take 10)
         dev/pp)))

;; small 40
;; medium/random 150
;; large all, no take

;; Run this and manually transfer the answer to applets.nba.data
(defn x-3 []
  (let [data-rows (util/data)]
    (->> data-rows
         (map util/clean-row)
         (sort-by (comp - :max))
         ;shuffle
         ;(take 150)
         vec
         dev/pp)))
