(ns dev-nba.core
  (:require [dev-nba.config :as config]
            [general.dev :as dev]))

;;
;; Note this namespace is 'not for production'.
;; So "app-config" will not exist in production.
;; In production we will call the start-f! directly.
;;

(defn ^:export init []
  (let [[start-f! app-config] (config/get-start-info!)]
    (dev/log-on "To start NBA app with" app-config)
    (start-f! app-config)))
