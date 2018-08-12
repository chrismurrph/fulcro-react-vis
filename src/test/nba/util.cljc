(ns nba.util
  (:require [clojure.string :as str]
            [general.cljc-util :as gu]
    #?(:clj
            [clojure.java.io :as io]
       :cljs [applets.nba.cljs-util :as cljs-util])))

(def nba-file-name "public/data-tidy.csv")

#?(:clj  (defn data []
           (let [lines (->> nba-file-name
                            io/resource
                            io/reader
                            line-seq)]
             (drop 1 lines)))
   :cljs (defn data []
           (cljs-util/read-text nba-file-name cljs-util/process)))

(defn clean-row [in]
  (let [row (str/split in #",")
        [player pname year] row
        extreme (fn [d f] (->> d (map :y) (apply f)))
        cleaned-data (->> row
                          (drop 3)
                          (map-indexed (fn [idx val]
                                         {:x idx :y val}))
                          (remove #(= (:y %) "NA"))
                          (mapv (fn [{:keys [x y]}]
                                  {:x x :y (gu/to-int y)})))]
    {:player player
     :pname  pname
     :year   year
     :min    (extreme cleaned-data min)
     :max    (extreme cleaned-data max)
     :games  cleaned-data}))
