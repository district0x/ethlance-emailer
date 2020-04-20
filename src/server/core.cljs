(ns server.core
  (:require [cljs.nodejs :as nodejs]
            [district.server.config :refer [config]]
            [district.server.logging]
            [district.server.smart-contracts]
            [district.server.web3-events]
            [district.server.web3]
            [mount.core :as mount :refer [defstate]]
            [server.constants :as constants]
            [server.emailer]
            [shared.smart-contracts-prod :as smart-contracts-prod]
            [taoensso.timbre :as log]))

(nodejs/enable-util-print!)

(defn start! []
(-> (mount/with-args
        {:config {:default {:logging {:level :debug
                                      :console? true}
                            :time-source :js-date
                            :web3 {:url "ws://127.0.0.1:8545"
                                   :on-offline (fn []
                                                 (log/warn "Ethereum node went offline, stopping syncing modules" ::web3-watcher)
                                                 (mount/stop #'district.server.web3-events/web3-events
                                                             #'server.emailer/emailer))
                                   :on-online (fn []
                                                (log/warn "Ethereum node went online again, starting syncing modules" ::web3-watcher)
                                                (mount/start #'district.server.web3-events/web3-events
                                                             #'server.emailer/emailer))}
                            :smart-contracts {:contracts-var #'smart-contracts-prod/smart-contracts}
                            :emailer {:private-key "PLACEHOLDER"
                                      :api-key "PLACEHOLDER"
                                      :template-id "PLACEHOLDER"
                                      :from "noreply@ethlance.com"
                                      :print-mode? true}
                            :web3-events {:events constants/web3-events}}}})
      (mount/start)
      (as-> $ (log/warn "Started" {:components $
                                   :config @config}))))

(defn stop! []
  (mount/stop))

(defn main []
  ;; executed once, on startup, can do one time setup here
  (start!))
