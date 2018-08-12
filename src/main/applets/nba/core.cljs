(ns applets.nba.core
  (:require [fulcro.client :as fc]
            [applets.nba.naive-root :as naive-root]
            [applets.nba.non-naive-root :as non-naive-root]
            [applets.nba.together-root :as together-root]
            [applets.nba.ui.common :as common]
            [applets.nba.constants :as constants]
            [fulcro.client.primitives :as prim]
            [applets.nba.operations :as ops]))

(defonce app (atom nil))

(defn mount [comp]
  (reset! app (fc/mount @app comp "app")))

(defn ^:export init [{:keys [stage file-size together? chk-dups?]}]
  (reset! app (fc/new-fulcro-client
                :started-callback (fn [app]
                                    (let [rec (:reconciler app)]
                                      (common/load-nba-games! common/PlayerYears
                                                              file-size
                                                              chk-dups?
                                                              (= :improved stage)
                                                              constants/desired-labels
                                                              rec)
                                      (prim/transact! rec `[(ops/fill-desired-labels)])))))
  (mount (cond
           together? together-root/NBARoot
           (= stage :naive) naive-root/NBARoot
           (#{:nyt :improved} stage) non-naive-root/NBARoot)))
