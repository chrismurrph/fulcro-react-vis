(ns applets.nba.together-root
  (:require [fulcro.client.primitives :as prim :refer [defsc defui]]
            [applets.nba.ui.together :as together]
            [general.my-prim :as mprim]))

(defsc NBARoot [_ {:keys [nba-application]}]
  {:query         [{:nba-application (mprim/get-one together/BothCharts)}]
   :initial-state (fn [_] {:nba-application (prim/get-initial-state together/BothCharts nil)})}
  (together/charts-ui nba-application))