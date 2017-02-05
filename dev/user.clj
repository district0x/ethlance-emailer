(ns user
  (:require [figwheel-sidecar.repl-api :as ra]))


(defn start-fig []
  (ra/start-figwheel!
    {:figwheel-options {:server-port 5621}
     :build-ids        ["dev"]
     :all-builds       [{:id           "dev"
                         :figwheel     true
                         :source-paths ["src"]
                         :compiler     {:main           'ethlance-emailer.cmd
                                        :output-to      "out/ethlance_emailer.js"
                                        :output-dir     "out"
                                        :target         :nodejs,
                                        :optimizations  :none,
                                        :pretty-print   true,
                                        :parallel-build true
                                        :verbose        false}}]})
  (ra/cljs-repl))

(defn stop-fig []
  (ra/stop-figwheel!))

(comment
  (start-fig)
  (ra/stop-figwheel!))