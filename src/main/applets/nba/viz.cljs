(ns applets.nba.viz
  (:require ["react-vis" :refer (XYPlot LineSeries LineSeriesCanvas LabelSeries XAxis Hint Voronoi)]))

(defn factory-apply
  [class]
  (fn [props & children]
    (apply js/React.createElement
           class
           props
           children)))

(def xy-plot (factory-apply XYPlot))
(def line-series (factory-apply LineSeries))
(def line-series-canvas (factory-apply LineSeriesCanvas))
(def label-series (factory-apply LabelSeries))
(def x-axis (factory-apply XAxis))
(def hint (factory-apply Hint))
(def voronoi (factory-apply Voronoi))

