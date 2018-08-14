(ns applets.nba.core
  (:require [fulcro.client :as fc]
            [applets.nba.naive-root :as naive-root]
            [applets.nba.non-naive-root :as non-naive-root]
            [applets.nba.ui.common :as common]
            [applets.nba.constants :as constants]
            [fulcro.client.primitives :as prim]
            [applets.nba.operations :as ops]
            [general.dev :as dev]))

(defonce app-atom (atom nil))

(defn mount [comp]
  (reset! app-atom (fc/mount @app-atom comp "app")))

(defn ^:export init [{:keys [stage] :as opts}]
  (reset! app-atom (fc/new-fulcro-client
                     :started-callback (fn [app]
                                         (let [rec (:reconciler app)]
                                           (js/setTimeout #(do (common/load-nba-games! common/PlayerYears
                                                                                       opts
                                                                                       constants/desired-labels
                                                                                       rec)
                                                               (prim/transact! rec `[(ops/fill-desired-labels)]))
                                                          50)))))
  (mount (cond
           (= stage :naive) naive-root/NBARoot
           (#{:nyt :improved} stage) non-naive-root/NBARoot
           :else (dev/err "Unrecognised stage" stage))))
