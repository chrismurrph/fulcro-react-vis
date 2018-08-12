(ns general.dev
  (:require
    [fulcro.client.primitives :as prim]
    [cljs.reader :as reader]
    #?(:clj
    [clojure.pprint :as pprint])))

#?(:cljs (enable-console-print!))

#?(:cljs (def log-pr js/console.log)
   :clj  (def log-pr println))

;;
;; About instrumentation and probing that is really useful during development. Lots
;; of intentional crashing (i.e. asserts) here, but also instrumentation things like
;; warnings and pretty printing. Eventually we will elide the crashing out during runtime
;; and make into macros in case the asserts are heavy. For nuts and bolts of that (elide
;; crashing and macros) see a similar library:
;; https://github.com/astoeckley/clojure-assistant
;; When this application becomes 'really professional' then perhaps there will be no calls
;; to this ns, as spec and proper logging framework do the same thing.
;; That's why we call it 'dev'
;;

;;
;; My own invention, is it a list or a vector?
;; Guards against: UnsupportedOperationException nth not supported on this type: PersistentArrayMap
;; , which happens if you try nth on a map:
;; `(nth {1 2} 0)`
;; `(let [[a] {1 2}] a)`
;; , the destructuring case being the common bug
#_(defn n-able? [x]
    ((every-pred coll? (complement map?)) x))

(def n-able? (every-pred coll? (complement map?)))

;;
;; If it is a map (when it shouldn't be) I don't usually want the 'has elements in it'
;; test to pass. Usually a map is a single item, so it is the wrong type to be asking `seq` of.
;; Use this rather than seq
;;
(defn least-one? [x]
  (and (n-able? x) (seq x)))

;;
;; Need to loosen our checking for Fulcro Inspect, hence need this marker.
;; Change to (complement nil?) when don't have Fulcro Inspect loaded.
;; Having to nil? basically turns off the rest of the checks.
;; Solution - just don't assert, use assert-warn instead
;;
#_(def fi-lax? nil?)

;;
;; This means it is not {}!
;;
(defn filled-map? [x]
  (and (map? x) (seq x)))

(defn err-empty
  ([x]
   (assert x "Can't check if empty when nil")
   (assert (n-able? x))
   (assert (seq x) "Can't assign empty")
   x)
  ([msg x]
   (assert x "Can't check if empty when nil")
   (assert (n-able? x))
   (assert (seq x) (str "Can't assign empty, msg: " msg))
   x))

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

(defn sleep [t]
  #?(:clj  (Thread/sleep t)
     :cljs (constantly nil)))

(defn chk-v! [v]
  (assert v)
  (assert (n-able? v) v)
  (assert (seq v) v)
  (assert (every? (complement nil?) v) v))

;;
;; Fixing a terrible discovery that `(get-in {} nil)` will
;; just go on merrily, leading to bugs difficult to track down
;;
(defn get-inn
  ([st v]
   (chk-v! v)
   (clojure.core/get-in st v))
  ([st v default]
   (chk-v! v)
   (clojure.core/get-in st v default)))

;;
;; TODO
;; These two functions are only for clj, so ought to move them
;;

(def width 120)

#?(:clj  (defn pp-str
           ([n x]
            (binding [pprint/*print-right-margin* n]
              (-> x pprint/pprint with-out-str)))
           ([x]
            (pp-str width x)))
   :cljs (def pp-str identity))

;;
;; TODO
;; pp can just be a pass thru not matter the args
;; TODO
;; Need do rest as well...
;;

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

(defn first-no-less [xs]
  (assert (least-one? xs) "Don't even have one: purposeful crash")
  ;; Get a crash like this, but easier to debug with your own message!
  (nth xs 0))

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

(defn- -one-only [f xs msgs]
  (f (= [true false] ((juxt
                        #(boolean (seq (take 1 %)))
                        #(boolean (seq (take 1 (drop 1 %))))) xs))
     (if (nil? xs)
       ["Expect to be one exactly, but got a nil" msgs]
       ["Expect to be one exactly, but got:" xs "," msgs]))
  (first xs))

(defn one-only [xs & msgs]
  (-one-only assert-not-macro xs msgs))

(defn one-only-warn [xs & msgs]
  (-one-only assert-warn xs msgs))

(defn- -first-no-more [f xs]
  (f (n-able? xs))
  (f (= nil (second xs))
          (str "Only supposed to be one. However:\nFIRST:\n" (first xs) "\nSECOND:\n" (second xs)))
  (first xs))

(defn first-no-more [xs]
  (-first-no-more assert-not-macro xs))

(defn first-no-more-warn [xs]
  (-first-no-more assert-warn xs))

(defn summarize [x]
  (cond
    (map? x) (let [counted (count x)]
               (if (> counted 5)
                 (str counted " map-entries...")
                 (->> x
                      (map (fn [[k v]]
                             [k (summarize v)]))
                      (into {}))))
    (coll? x) (let [counted (count x)]
                (if (> counted 5)
                  (str counted " items...")
                  x))
    :else x))

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
;; Order a map's keys randomly, to prove that need an ordered map
;;
(defn jumble-up-map [m]
  (assert (map? m))
  (->> (loop [in (mapv identity m)
              out []]
         (if (seq in)
           (let [picked (rand-nth in)]
             (recur (vec (remove #(= % picked) in)) (conj out picked)))
           out))
       (into {})))

;(defn assert-info [name value]
;  (str name " (nil?, fn?, type, value-of): ["
;       (nil? value) ", " (fn? value)
;       (when (-> value fn? not)
;         (str ", " (type value) ", " value))
;       "]\n"))

(defn- init-state [comp data]
  (prim/tree->db comp (prim/get-initial-state comp data) true))

;;
;; Only needed in devcards
;; If Fulcro is given an atom then it assumes it is normalized.
;; This function does the normalization for us using a component that we choose.
;;
(defn init-state-atom [comp data]
  (atom (init-state comp data)))

(defmacro locals [] (into {} (map (juxt (comp keyword name) identity)) (keys &env)))

(declare probe-on)
(declare probe-off)

(defn probe-count-on [xs]
  (log-pr "COUNT" (count xs))
  xs)

(defn probe-count-off [xs]
  xs)

(defn probe-first-on [xs]
  (log-pr "FIRST" (pp-str (first xs)))
  xs)

(defn probe-first-off [xs]
  xs)

(defn probe-first-n-on [n xs]
  (println "Try take" n "from" (count xs) "\n" (pp-str (take n xs)))
  xs)

(defn probe-first-n-off [n xs]
  xs)

(defn err-nil-probe
  ([x]
   (assert x "Can't assign nil (or false)")
   x)
  ([x & msg]
   (assert x (apply str "Can't assign nil (or false), msg: " msg))
   x))

(defn err-fn-probe [f msg]
  (fn [x]
    (assert (not (f x)) (str msg ", got: <" x ">"))
    x))

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
