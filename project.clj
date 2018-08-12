(defproject fulcro-react-vis "0.1.0-SNAPSHOT"
  :description "Translation to cljs of Advanced React-Viz article about NBA three-pointers"
  :license {:name "" :url ""}
  :min-lein-version "2.7.0"

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [thheller/shadow-cljs "2.4.22"]
                 [fulcrologic/fulcro "2.6.0-RC5"]]

  :source-paths ["src/main"]
  :test-paths ["src/test"]
  :clean-targets ^{:protect false} ["target" "resources/public/js" "resources/private"]

  :profiles {:cljs       {:source-paths ["src/main" "src/test"]
                          :dependencies [[thheller/shadow-cljs "2.2.18"]
                                         [binaryage/devtools "0.9.8"]
                                         [fulcrologic/fulcro-inspect "2.2.1"]
                                         ]
                          }})
