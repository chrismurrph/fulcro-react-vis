(ns general.my-prim
  (:require [fulcro.client.primitives :as prim]))

;;
;; Helps with readability, even though is the same function.
;;

(def get-one prim/get-query)

(def get-many prim/get-query)
