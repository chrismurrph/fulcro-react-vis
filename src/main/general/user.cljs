(ns general.user
  (:require [general.dev :as dev]
            [fulcro.client.primitives :as prim]))

(def app-atom (atom nil))

(defn get-state []
  @(prim/app-state (-> app-atom deref :reconciler)))

;; Start a REPL:
;; npx shadow-cljs cljs-repl main
;; Then test it:
;; (x-1)
;; The returned data structure will go to the REPL

;; Should appear in browser. Can be fickle. You might want to re-start the watch process
(dev/log-off "************************ ====> Pre-loaded ns where can dump")

(defn x-1 []
  (dev/log "Testing x-1")
  [:a :b {:a [1 2 3]}])
