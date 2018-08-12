(ns dev-nba.config
  (:require [applets.nba.core :as nba]))

;; Possible :stage values: :naive :nyt :improved.
;; Possible :file-size values: :small :medium :medium-random :large
(defn get-start-info! []
  (let [{:keys [start-f app-config]}
        {:start-f    nba/init
         :app-config {:stage               :improved
                      :file-size           :medium-random
                      :chk-dups?           false
                      :view-voronoi-lines? false}}]
    [start-f app-config]))
