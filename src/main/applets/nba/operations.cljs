(ns applets.nba.operations
  (:require [fulcro.client.mutations :as m :refer [defmutation]]
            [general.dev :as dev]))

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

;; Don't need to do this, as being done by initial state. Structural stuff best not done dynamically wherever possible.
(defmutation complete-field-joins
  [{:keys []}]
  (action [{:keys [state]}]
          (swap! state #(-> %
                            (assoc-in [:chart/by-id :singleton :desired-labels] [:desired-labels/by-id :singleton])
                            (assoc-in [:interactive-components/by-id :singleton :desired-labels] [:desired-labels/by-id :singleton])
                            (assoc-in [:interactive-components/by-id :singleton :player-years] [:player-years/by-id :singleton])))))
