(ns applets.nba.ui.naive
  (:require
    [fulcro.client.primitives :as prim :refer [defsc]]
    [general.my-prim :as mprim]
    [applets.nba.constants :as constants]
    [applets.nba.ui.common :as common]
    [applets.nba.viz :as viz]
    [fulcro.client.dom :as dom]
    [general.dev :as dev]
    [applets.nba.operations :as ops]))

(defsc NaiveChart [this {:keys [player-years desired-labels]}]
  {:ident              (fn [] [:chart/by-id :singleton])
   :query              [{:player-years (mprim/get-one common/PlayerYears)}
                        {:desired-labels (mprim/get-one common/DesiredLabels)}
                        :highlight-series]
   :initial-state      (fn [_] {:player-years   (prim/get-initial-state common/PlayerYears nil)
                                :desired-labels (prim/get-initial-state common/DesiredLabels nil)})
   ; I hoped this would get us to see loading but it didn't
   ;:componentWillMount (fn [] (common/load-data Chart (-> this prim/props :ui/small-data?) (prim/get-reconciler this)))
   ;:componentDidMount (fn [] (prim/transact! this `[(ops/fill-desired-labels)]))
   }
  (let [chk-dup! (when (:ui/chk-dups? player-years)
                   (dev/chk-dup-hof))
        player-years-items (:items player-years)
        desired-labels-items (:items desired-labels)
        colour-scale-f (common/colour-scale-hof player-years-items)]
    (if (and (seq player-years-items) (seq desired-labels-items))
      (viz/xy-plot (clj->js constants/layout)
                   (map (fn [{:keys [games player-year-id key max]}]
                          (when chk-dup! (chk-dup! key keys))
                          (viz/line-series #js {:strokeWidth       1
                                                :key               key
                                                :data              (clj->js games)
                                                :onSeriesMouseOver #(prim/update-state! this assoc :highlight-series player-year-id)
                                                :onSeriesMouseOut  #(prim/update-state! this assoc :highlight-series nil)
                                                :stroke            (if (= player-year-id (-> this prim/get-state :highlight-series))
                                                                     "black"
                                                                     (colour-scale-f max))}))
                        player-years-items)
                   (viz/label-series #js {:data         (clj->js desired-labels-items)
                                          :style        common/font-style
                                          :getY         common/attainment-f
                                          :getX         (fn [_] constants/number-of-games)
                                          :labelAnchorX "start"
                                          :getLabel     (fn [js-player-year]
                                                          (let [{:keys [pname games]} (common/->clj js-player-year)
                                                                total (-> games last :y)]
                                                            (str pname " - " total)))})
                   (viz/x-axis #js {:style      #js {:ticks common/font-style}
                                    :tickFormat common/tick-format}))
      (do
        (dev/log "Never see loading..." player-years-items desired-labels-items)
        (dom/div "LOADING LOADING LOADING LOADING LOADING LOADING LOADING LOADING LOADING LOADING LOADING LOADING")))))

(def chart-ui (prim/factory NaiveChart))
