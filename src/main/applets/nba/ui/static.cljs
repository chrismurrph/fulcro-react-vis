(ns applets.nba.ui.static
  (:require
    [fulcro.client.primitives :as prim :refer [defsc]]
    [general.my-prim :as mprim]
    [applets.nba.constants :as constants]
    [applets.nba.ui.common :as common]
    [applets.nba.viz :as viz]
    [applets.nba.ui.interactive :as interactive]
    [fulcro.client.dom :as dom]
    [general.dev :as dev]))

(defsc StaticChart [this {:keys [player-years interactive-components]}]
  {:ident             (fn [] [:chart/by-id :singleton])
   :query             [{:player-years (mprim/get-one common/PlayerYears)}
                       {:interactive-components (mprim/get-one interactive/InteractiveComponents)}]
   :initial-state (fn [_] {:player-years (prim/get-initial-state common/PlayerYears nil)
                           :interactive-components (prim/get-initial-state interactive/InteractiveComponents nil)})
   }
  (let [player-years-items (:items player-years)
        colour-scale-f (common/colour-scale-hof player-years-items)]
    (when (seq player-years-items)
      (dev/log-on "rendering static")
      (dom/div :.relative
               (dom/div
                 (viz/xy-plot (clj->js (merge constants/layout
                                              {:xDomain [0 constants/number-of-games]
                                               :yDomain [0 constants/max-number-of-three-pointers]}))
                              (map (fn [{:keys [games player-year-id key max]}]
                                     (viz/line-series-canvas
                                       #js {:strokeWidth 1
                                            :key         key
                                            :data        (clj->js games)
                                            :onNearestX  #(prim/update-state! this assoc :cross-value player-year-id)
                                            :stroke      (colour-scale-f max)}))
                                   player-years-items)
                              ;; moved this x-axis out from interactive as it is truly static
                              (viz/x-axis #js {:style      #js {:ticks common/font-style}
                                               :tickFormat common/tick-format})))
               (interactive/interactive-components-ui interactive-components)))))

(def chart-ui (prim/factory StaticChart))
