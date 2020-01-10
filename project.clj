(defproject ethlance-emailer "0.1.0-SNAPSHOT"
  :description "Process listens for Ethlance events and sends notifications emails"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[bidi "2.0.14"]
                 [camel-snake-kebab "0.4.0"]
                 [cljs-web3 "0.18.2-0"]
                 [com.andrewmcveigh/cljs-time "0.4.0"]
                 [medley "0.8.3"]
                 [org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.520"]
                 [org.clojure/tools.nrepl "0.2.10"]
                 [print-foo-cljs "2.0.3"]]
  :plugins [[lein-npm "0.6.2"]
            [lein-cljsbuild "1.1.5"]
            [lein-figwheel "0.5.9"]]
  :npm {:dependencies [[source-map-support "0.4.0"]
                       [web3 "0.18.2"]
                       [ws "2.0.1"]
                       [solidity-sha3 "0.4.1"]
                       [sendgrid "4.7.1"]
                       [node-schedule "1.2.0"]]}
  :profiles {:dev {:dependencies [[figwheel-sidecar "0.5.19"]
                                  [cider/piggieback "0.4.0"]]}}
  :repl-options {:nrepl-middleware [cider.piggieback/wrap-cljs-repl]}
  :source-paths ["src" "dev"]
  :cljsbuild {:builds [{:id "emailer"
                        :source-paths ["src"]
                        :compiler {:main "ethlance-emailer.cmd"
                                   :output-to "emailer/ethlance-emailer.js"
                                   :output-dir "emailer"
                                   :target :nodejs
                                   :optimizations :simple
                                   :source-map "emailer/ethlance-emailer.js.map"
                                   :externs ["src/ethlance_emailer/externs.js"]}}]}
  :clean-targets ["emailer" "target"])
