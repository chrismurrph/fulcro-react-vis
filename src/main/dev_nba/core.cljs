(ns dev-nba.core
  (:require [dev-nba.config :as config]
            [general.dev :as dev]))

;;
;; Note this namespace is 'not for production'.
;; So "app-config" will not exist in production.
;; In production we will call the start-f! directly.
;;

(def internal-version 2)

(defn ^:export init []
  (let [[current-config start-f! {:keys [] :as app-config}] (config/get-start-info!)]
    (dev/log "To start" current-config "(all code) ver" internal-version "with app-config:" app-config)
    (start-f! app-config)))
