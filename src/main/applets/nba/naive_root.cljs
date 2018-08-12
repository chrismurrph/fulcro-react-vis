(ns applets.nba.naive-root
  (:require [fulcro.client.primitives :as prim :refer [defsc defui]]
            [applets.nba.ui.naive :as ui]
            [general.my-prim :as mprim]))

(defsc NBARoot [_ {:keys [nba-application]}]
  {:query         [{:nba-application (mprim/get-one ui/NaiveChart)}]
   :initial-state (fn [_] {:nba-application (prim/get-initial-state ui/NaiveChart nil)})}
  (ui/chart-ui nba-application))