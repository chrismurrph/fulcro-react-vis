(ns dev-nba.config
  (:require [applets.nba.core :as nba]))

(defn get-start-info!
  "Possible :stage values: :naive :nyt :improved
   Possible :file-size values: :small :medium :medium-random :large"
  []
  (let [{:keys [start-f app-config]}
        {:start-f    nba/init
         :app-config {:stage                  :improved
                      :file-size              :large
                      :chk-dups?              false
                      :view-voronoi-lines?    false
                      :incl-better-than-line? false}}]
    [start-f app-config]))
