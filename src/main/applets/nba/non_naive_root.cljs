(ns applets.nba.non-naive-root
  (:require [fulcro.client.primitives :as prim :refer [defsc defui]]
            [applets.nba.ui.static :as ui]
            [general.my-prim :as mprim]))

(defsc NBARoot [_ {:keys [nba-application]}]
  {:query         [{:nba-application (mprim/get-one ui/StaticChart)}]
   :initial-state (fn [_] {:nba-application (prim/get-initial-state ui/StaticChart nil)})}
  (ui/chart-ui nba-application))