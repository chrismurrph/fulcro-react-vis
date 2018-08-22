(ns applets.nba.core
  (:require [fulcro.client :as fc]
            [applets.nba.naive-root :as naive-root]
            [applets.nba.non-naive-root :as non-naive-root]
            [applets.nba.ui.common :as common]
            [applets.nba.constants :as constants]
            [fulcro.client.primitives :as prim]
            [applets.nba.operations :as ops]
            [general.dev :as dev]
            [general.user :as user]))

(defn ^:export init [{:keys [stage] :as opts}]
  (reset! user/app (fc/new-fulcro-client
                          :started-callback (fn [app]
                                              (let [rec (:reconciler app)]
                                                (js/setTimeout #(do (common/load-nba-games! common/PlayerYears
                                                                                            opts
                                                                                            constants/desired-labels
                                                                                            rec)
                                                                    (prim/transact! rec `[(ops/fill-desired-labels)]))
                                                               50)))))
  (user/mount (cond
                (= stage :naive) naive-root/NBARoot
                (#{:nyt :improved} stage) non-naive-root/NBARoot
                :else (dev/err "Unrecognised stage" stage))))
