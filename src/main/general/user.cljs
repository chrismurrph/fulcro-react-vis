(ns general.user
  (:require [general.dev :as dev]
            [fulcro.client.primitives :as prim]))

;;
;; Note that this has not been set up. Fulcro Inspect will probably be all you need.
;; I've only ever needed this browser REPL to get a copy of state, something that FI can't do.
;; One way to make this ns serviceable would be to delete the applets.nba.core/app-atom, and
;; instead use this app-atom from applets.nba.core
;;
(def app-atom (atom nil))

(defn get-state []
  @(prim/app-state (-> app-atom deref :reconciler)))

;; Start a REPL:
;; npx shadow-cljs cljs-repl main
;; Then test it:
;; (x-1)
;; The returned data structure will go to the REPL

;; Should appear in browser. Can be fickle. You might want to re-start the watch process
(dev/log-off "************************ Pre-loaded ns where can dump state")

(defn x-1 []
  (dev/log "Testing x-1")
  [:a :b {:a [1 2 3]}])
