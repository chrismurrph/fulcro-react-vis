(ns applets.nba.ui.common
  (:require [applets.nba.data.large :as large-data]
            [general.my-prim :as mprim]
            [fulcro.client.primitives :as prim :refer [defsc]]
            [applets.nba.data.small :as small-data]
            [applets.nba.data.medium :as medium-data]
            [applets.nba.data.random :as random-data]
    ;; import {scaleLinear} from 'd3-scale';
            ["d3-scale" :refer (scaleLinear)]
            ["d3-scale-chromatic" :refer (interpolateWarm)]
    ;; import simplify from 'simplify-js';
            ["simplify-js" :default simplify]
            [clojure.string :as str]
            [general.dev :as dev]
            [goog.functions :as gfun]))

(defsc Game [_ _]
  {:ident [:game/by-id :id]
   :query [:id :x :y :key]})

(defsc PlayerYear [_ _]
  {:ident [:player-year/by-id :player-year-id]
   :query [:player-year-id
           ;; :y and :max are the same
           :idx :pname :year :min :max :key :x :y
           {:games (mprim/get-many Game)}]})

(defsc PlayerYears [_ _]
  {:ident         (fn [] [:player-years/by-id :singleton])
   :query         [{:items (mprim/get-many PlayerYear)}
                   :ui/file-size :ui/chk-dups? :ui/view-voronoi-lines? :ui/incl-better-than-line?]
   :initial-state {:items []}})

(defsc DesiredLabels [_ _]
  {:ident         (fn [] [:desired-labels/by-id :singleton])
   :query         [{:items (mprim/get-many PlayerYear)}]
   :initial-state {:items []}})

(defn make-player-year-id [pname year]
  (str pname "_" year))

(defn ->clj [x]
  (js->clj x :keywordize-keys true))

(def only-stephen-curry-games
  [{:x 0, :y 5}, {:x 1, :y 9}, {:x 2, :y 17}, {:x 3, :y 21}, {:x 4, :y 28}, {:x 5, :y 36}, {:x 6, :y 38}, {:x 7, :y 41}, {:x 8, :y 44},
   {:x 9, :y 52}, {:x 10, :y 57}, {:x 11, :y 62}, {:x 12, :y 68}, {:x 13, :y 71}, {:x 14, :y 74}, {:x 15, :y 78}, {:x 16, :y 87},
   {:x 17, :y 90}, {:x 18, :y 94}, {:x 19, :y 102}, {:x 20, :y 111}, {:x 21, :y 116}, {:x 22, :y 119}, {:x 23, :y 125}, {:x 24, :y 127},
   {:x 25, :y 129}, {:x 26, :y 131}, {:x 27, :y 133}, {:x 28, :y 134}, {:x 29, :y 140}, {:x 30, :y 141}, {:x 31, :y 146}, {:x 32, :y 150},
   {:x 33, :y 154}, {:x 34, :y 162}, {:x 35, :y 166}, {:x 36, :y 171}, {:x 37, :y 179}, {:x 38, :y 186}, {:x 39, :y 193}, {:x 40, :y 196},
   {:x 41, :y 204}, {:x 42, :y 210}, {:x 43, :y 213}, {:x 44, :y 218}, {:x 45, :y 221}, {:x 46, :y 232}, {:x 47, :y 233}, {:x 48, :y 240},
   {:x 49, :y 245}, {:x 50, :y 252}, {:x 51, :y 255}, {:x 52, :y 260}, {:x 53, :y 266}, {:x 54, :y 276}, {:x 55, :y 288}, {:x 56, :y 293},
   {:x 57, :y 294}, {:x 58, :y 301}, {:x 59, :y 304}, {:x 60, :y 311}, {:x 61, :y 318}, {:x 62, :y 322}, {:x 63, :y 330}, {:x 64, :y 336},
   {:x 65, :y 337}, {:x 66, :y 339}, {:x 67, :y 343}, {:x 68, :y 348}, {:x 69, :y 350}, {:x 70, :y 356}, {:x 71, :y 361}, {:x 72, :y 369},
   {:x 73, :y 378}, {:x 74, :y 382}, {:x 75, :y 385}, {:x 76, :y 388}, {:x 77, :y 392}, {:x 78, :y 402}])

(defn extreme [f d]
  (->> d (map :y) (apply f)))

(def better-than-curry-line {:pname "Stephen Curry"
                             :year  2016
                             :min   (extreme min only-stephen-curry-games)
                             :max   (extreme max only-stephen-curry-games)
                             :games (map (fn [{:keys [x y]}] {:x x :y (+ y 20)})
                                         only-stephen-curry-games)})
(def simplification 3)
;;
;; The data doesn't have a player-year so we create it now. Also the games need to be
;; unique on their own, as they are stored in their own table.
;; Also the simplification of the lines for stage :improved is handled here.
;; The work for :nyt stage can be done always as :naive doesn't use those keys.
;;
(defn identities-and-stages-hof [desired-labels {:keys [stage]}]
  (dev/assert-warn (set? desired-labels))
  (fn [idx {:keys [pname year games max] :as player-year}]
    (dev/assert-warn (map? player-year) "Not a map:" player-year)
    (dev/assert-warn max "No max" player-year)
    (let [player-year-id (make-player-year-id pname year)
          label {:pname pname :year year}
          make-simple (fn [games n]
                        (if (= :improved stage)
                          (->clj (simplify (clj->js games) n))
                          games))]
      (assoc player-year
        :idx idx
        :key idx
        :ui/desired? (boolean (desired-labels label))
        :player-year-id player-year-id
        :games (->> games
                    (mapv (fn [{:keys [x y]}]
                            (let [id (str player-year-id "_" x)]
                              {:id id :key id :x x :y y})))
                    (#(make-simple % simplification)))
        ;; Easier to put these in than have a playerMap (not even used at :naive stage, but no harm having here)
        :x 41
        :y max))))

(defn data-rows! [file-size]
  (case file-size
    :small (small-data/data-rows!)
    :medium (medium-data/data-rows!)
    :medium-random (random-data/data-rows!)
    :large (large-data/data-rows!)))

(defn load-nba-games! [comp
                       {:keys [file-size stage chk-dups? view-voronoi-lines? incl-better-than-line?] :as opts}
                       desired-labels
                       reconciler]
  (let [player-year-mods (identities-and-stages-hof desired-labels opts)
        d {:items                     (->> (cond-> (data-rows! file-size)
                                                   (and (= :improved stage) incl-better-than-line?)
                                                   (conj better-than-curry-line))
                                           (map-indexed player-year-mods)
                                           vec)
           :ui/file-size              file-size
           :ui/chk-dups?              chk-dups?
           :ui/view-voronoi-lines?    view-voronoi-lines?
           :ui/incl-better-than-line? incl-better-than-line?
           }]
    (prim/merge-component! reconciler comp d)))

(defn max-max [player-years]
  (->> player-years
       (map :max)
       (apply max)))

;; If all lines start from the origin then this is pointless - just hard-code 0
(defn min-min [player-years]
  (->> player-years
       (map :min)
       (apply min)))

;; Domain is the real world, range is where you want to put it, often
;; somewhere on the the screen.
(defn scale-hof [domain-js-array range-js-array]
  (-> (scaleLinear)
      (.domain domain-js-array)
      (.range range-js-array)))

(defn colour-scale-hof [player-years]
  (let [min-y (min-min player-years)
        max-y (max-max player-years)
        domain-scale-f (scale-hof #js [min-y max-y]
                                  #js [1 0])]
    (fn [x]
      (-> x domain-scale-f interpolateWarm))))

(defn debounced-upd-state-hof [interval]
  (fn [this k v & more]
    (gfun/debounce (apply prim/update-state! this assoc k v more) interval)))

(defn attainment-f [js-player-year]
  (-> js-player-year ->clj :games last :y))

(defn format-player-year [js-player-year]
  (let [{:keys [pname year]} (->clj js-player-year)]
    (str (str/upper-case pname) ", " year)))

;; tickFormat={d => !d ? '1st game' : (!(d % 10) ? `${d}th` : '')}/>
(defn tick-format [n]
  (dev/assert-warn (number? n))
  (if (or (zero? n) (= 1 n))
    "1st game"
    (str n "th")))

(def font-style #js {:fontSize "10px" :fontFamily "sans-serif"})




