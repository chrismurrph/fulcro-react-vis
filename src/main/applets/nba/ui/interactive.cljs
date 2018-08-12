(ns applets.nba.ui.interactive
  (:require [fulcro.client.primitives :as prim :refer [defsc]]
            [applets.nba.ui.common :as common]
            [general.my-prim :as mprim]
            [fulcro.client.dom :as dom]
            [applets.nba.viz :as viz]
            [applets.nba.constants :as constants]
            [general.dev :as dev]
            [general.stopwatch :as sw]))

(def debounced-upd-state (common/debounced-upd-state-hof constants/hovering-nba-chart-interval))

(defsc InteractiveComponents [this {:keys [player-years desired-labels]}]
  {:ident         (fn [] [:interactive-components/by-id :singleton])
   :query         [{:player-years (mprim/get-one common/PlayerYears)}
                   {:desired-labels (mprim/get-one common/DesiredLabels)}]
   :initial-state (fn [_] {:player-years   (prim/get-initial-state common/PlayerYears nil)
                           :desired-labels (prim/get-initial-state common/DesiredLabels nil)})
   }
  (let [stop-w (sw/time-probe-hof "interactive render" true true)
        player-years-items (:items player-years)
        desired-labels-items (:items desired-labels)
        highlight-series (-> this prim/get-state :highlight-series)
        highlight-tip (-> this prim/get-state :highlight-tip)
        {:keys [height width margin]} constants/layout
        x-f (common/scale-hof #js [0 constants/number-of-games]
                              #js [(:left margin) (- width (:right margin))])
        y-f (common/scale-hof #js [0 constants/max-number-of-three-pointers]
                              #js [(- height (:top margin) (:bottom margin)) 0])
        max-y (y-f constants/max-number-of-three-pointers)
        ]
    (dom/div :.absolute.full
             (viz/xy-plot
               (clj->js (merge constants/layout
                               {:onMouseLeave (fn [_]
                                                (prim/update-state! this assoc :highlight-series nil :highlight-tip nil))
                                :xDomain      [0 constants/number-of-games]
                                :yDomain      [0 (inc constants/max-number-of-three-pointers)]}))
               (viz/label-series
                 (clj->js {:labelAnchorX "start"
                           :data         (clj->js desired-labels-items)
                           :style        common/font-style
                           :getY         (fn [d] (:y (common/->clj d)))
                           :getX         (fn [_] constants/number-of-games)
                           :getLabel     common/format-player-year
                           }))
               (when highlight-series (viz/line-series (clj->js {:animation "animation"
                                                                 :curve     ""
                                                                 :data      highlight-series
                                                                 :color     "black"})))
               (when highlight-tip (viz/hint (clj->js {:value {:y (:y highlight-tip) :x constants/number-of-games}
                                                       :align {:horizontal "right"}})
                                             (str (:name highlight-tip) " " (:y highlight-tip))))
               (stop-w)                                     ;;small number
               (viz/voronoi (clj->js {:extent  [[0 max-y]
                                                [width (- height (:bottom margin))]]
                                      :nodes   player-years-items
                                      ; Uncomment to see voronoi lines
                                      ;:polygonStyle #js {:stroke "rgba(0, 0, 0, .2)"}
                                      :onHover (fn [js]
                                                 (let [p-year (common/->clj js)]
                                                   (debounced-upd-state this
                                                                        :highlight-series (:games p-year)
                                                                        :highlight-tip {:y    (:max p-year)
                                                                                        :name (:pname p-year)})))
                                      :x       #(-> % common/->clj :x x-f)
                                      :y       #(-> % common/->clj :y y-f)}))
               (stop-w)                                     ;;big number, hence player-years-items simplified
               ))))

(def interactive-components-ui (prim/factory InteractiveComponents))


