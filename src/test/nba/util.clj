(ns nba.util
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))

(def nba-file-name "public/data-tidy.csv")

(defn data []
  (let [lines (->> nba-file-name
                   io/resource
                   io/reader
                   line-seq)]
    (drop 1 lines)))

(defn clean-row [in]
  (let [row (str/split in #",")
        [player pname year] row
        extreme (fn [f d] (->> d (map :y) (apply f)))
        cleaned-data (->> row
                          (drop 3)
                          (map-indexed (fn [idx val]
                                         {:x idx :y val}))
                          (remove #(= (:y %) "NA"))
                          (mapv (fn [{:keys [x y]}]
                                  {:x x :y (Integer/parseInt y)})))]
    {:player player
     :pname  pname
     :year   year
     :min    (extreme min cleaned-data)
     :max    (extreme max cleaned-data)
     :games  cleaned-data}))
