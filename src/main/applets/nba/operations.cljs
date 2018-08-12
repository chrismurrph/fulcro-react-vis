(ns applets.nba.operations
  (:require [fulcro.client.mutations :as m :refer [defmutation]]))

(defmutation fill-desired-labels
  [{:keys []}]
  (action [{:keys [state]}]
          (let [st @state
                desired-player-idents (->> (get st :player-year/by-id)
                                           vals
                                           (filter :ui/desired?)
                                           (map :player-year-id)
                                           (mapv (fn [id] [:player-year/by-id id])))]
            (swap! state #(-> %
                              (assoc-in [:desired-labels/by-id :singleton :items] desired-player-idents)))))
  (refresh [env] [:nba-application]))
