(ns applets.nba.ui.together
  (:require [fulcro.client.primitives :as prim :refer [defsc]]
            [general.my-prim :as mprim]
            [applets.nba.ui.static :as static]
            [applets.nba.ui.naive :as naive]
            [fulcro.client.dom :as dom]))

(defsc BothCharts [this {:keys [naive-chart static-chart]}]
  {:ident         (fn [] [:both-charts/by-id :singleton])
   :query         [{:naive-chart (mprim/get-one naive/NaiveChart)}
                   {:static-chart (mprim/get-one static/StaticChart)}]
   :initial-state (fn [_] {:naive-chart  (prim/get-initial-state naive/NaiveChart nil)
                           :static-chart (prim/get-initial-state static/StaticChart nil)})}
  (dom/div (naive/chart-ui naive-chart)
           (static/chart-ui static-chart)))

(def charts-ui (prim/factory BothCharts))
