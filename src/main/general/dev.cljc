(ns general.dev
  (:require
    #?(:clj
    [clojure.pprint :as pprint])))

#?(:cljs (enable-console-print!))

#?(:cljs (def log-pr js/console.log)
   :clj  (def log-pr println))

;;
;; Using apply to get devtools to format it properly
;;
(defn warn [& args]
  (apply log-pr "WARN:" args))

(defn err [& args]
  (apply log-pr "PROBLEM:" args)
  (throw (ex-info "Correct Problem" {})))

;;
;; Will have to use this instead of asserts in all our components
;;
(defn assert-warn [pred-res & args]
  (when (not pred-res)
    (warn args)))

(defn assert-warn-off [pred-res & args])

;;
;; Theoretical (only) problem with this is no way of stopping a crash in production.
;; In reality the asserts will be working in production and that is fine.
;; And if it is really an issue we should use configuration rather than macros anyway.
;; Another advantage over straight assert is `& args`, which allows us to use devtools
;; while debugging.
;;
(defn assert-not-macro [pred-res & args]
  (when (not pred-res)
    #?(:cljs (throw (js/Error. (str "Assert failed: " (pr-str args))))
       :clj (throw (new AssertionError (str "Assert failed: " (pr-str args)))))))

(def width 120)

#?(:clj  (defn pp-str
           ([n x]
            (binding [pprint/*print-right-margin* n]
              (-> x pprint/pprint with-out-str)))
           ([x]
            (pp-str width x)))
   :cljs (def pp-str identity))

#?(:clj  (defn pp
           ([n x]
            (binding [pprint/*print-right-margin* n]
              (-> x pprint/pprint)))
           ([x]
            (pp width x)))
   :cljs (def pp println))

;;
;; Hide the clutter briefly
;;
(defn pp-hide
  ([n x])
  ([x]))

(def hard-error? true)

(defn err-warn [predicate-res & msg]
  (if-not predicate-res
    (if hard-error?
      (assert false msg)
      (do
        (apply log-pr "WARNING:" msg)
        predicate-res))
    predicate-res))

(defn type-and-value [value]
  (if (-> value fn? not)
    ["type" (type value) "value" value]
    []))

;;
;; name - of the thing we are asserting on
;; value - of the thing we are asserting on
;; Could do with being a macro...
;;
#?(:cljs (defn assert-info [name value]
           (into [name "nil?" (nil? value) "fn?" (fn? value)] (type-and-value value)))
   :clj  (defn assert-info [name value]
           (into [name "nil?" (nil? value) "fn?" (fn? value)] (type-and-value value))))

(declare log)
(defn chk-dup-hof []
  (log "Going to be checking dups")
  (let [keys-atom (atom #{})]
    (fn [key]
      (let [keys @keys-atom]
        (when (keys key)
          (err "dup key" key ", already collected" (count keys) "min" (apply min keys) "max" (apply max keys)))
        (swap! keys-atom conj key)))))

;;
;; Using apply to get devtools to format it properly
;; Using just log (w/out -on) means it is permanent
;;
(defn log [& args]
  (apply log-pr args))

;;
;; Using -on means we could turn it off, or into debug stuff
;;
(def log-on log)

(defn log-off [& _])

(defn probe-f-on [f xs & msgs]
  (apply log-pr (f xs) "<--" msgs)
  xs)

(defn probe-f-off [f xs & msgs])

(defn probe-off
  ([x]
   x)
  ([x & msgs]
   x))

(defn probe-on
  ([x]
   (-> x
       pp)
   x)
  ([x & msgs]
   (apply log-pr x "<--" msgs)
   x))
