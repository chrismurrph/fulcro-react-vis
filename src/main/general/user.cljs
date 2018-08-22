(ns general.user
  (:require [general.dev :as dev]
            [fulcro.client.primitives :as prim]
            [fulcro.client :as fc]))

;; Start a REPL:
;; npx shadow-cljs cljs-repl main
;; Then test it:
;; (x-1)
;; The returned data structure will go to the REPL

(defonce app (atom nil))
(defonce root-comp (atom nil))

(defn mount [comp]
  (reset! app (fc/mount @app comp "app")))

(defn get-state []
  @(prim/app-state (-> app deref :reconciler)))

;; Should appear in browser. Can be fickle. You might want to re-start the watch process
(dev/log-off "====> Pre-loaded ns where can dump")

#_(defn ^:dev/before-load stop []
    (js/console.log "stop"))

(defn ^:dev/after-load start []
  (mount @root-comp))

(defn x-1 []
  (dev/log "Testing x-1")
  [:a :b {:a [1 2 3]}])
