(ns applets.nba.constants
  (:require [general.dev :as dev]))

(def -desired-labels [["Brian Taylor" 1980]
                      ["Mike Bratz" 1981]
                      ["Don Buse" 1982]
                      ["Mike Dunleavy" 1983]
                      ["Larry Bird" 1986]
                      ["Danny Ainge" 1988]
                      ["Michael Adams" 1989]
                      ["Michael Adams" 1990]
                      ["Vernon Maxwell" 1991]
                      ["Vernon Maxwell" 1992]
                      ["Dan Majerle" 1994]
                      ["John Starks" 1995]
                      ["Dennis Scott" 1996]
                      ["Reggie Miller" 1997]
                      ["Dee Brown" 1999]
                      ["Gary Payton" 2000]
                      ["Antoine Walker" 2001]
                      ["Jason Richardson" 2008]
                      ["Stephen Curry" 2013]
                      ["Stephen Curry" 2014]
                      ["Stephen Curry" 2015]
                      ["Stephen Curry" 2016]])

(defn ->maps [-labels]
  (set (->> -labels
            (map (fn [[name year]]
                   {:pname name :year (str year)})))))

(def desired-labels (->maps -desired-labels))

(def layout {:height 1000
             :width  800
             :margin {:left 20 :right 200 :bottom 100 :top 20}})

(def number-of-games 82)
(def max-number-of-three-pointers 405)
(def hovering-nba-chart-interval 40)
