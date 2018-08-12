(ns dev-nba.config
  (:require [applets.nba.core :as nba]))

(def canvas-index-name "canvas.html")

;; :index-file-name is just for documentation, so you know which .html file to use
(def possibilities {:nba              {:start-f         nba/init
                                       :index-file-name canvas-index-name
                                       :app-config      {:stage     :naive
                                                         :file-size :random
                                                         :together? true
                                                         :chk-dups? false}}})

(def current-config :nba)

(defn get-start-info! []
  (let [{:keys [start-f app-config]} (get possibilities current-config)]
    [current-config start-f app-config]))
