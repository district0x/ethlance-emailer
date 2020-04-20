(ns server.contracts.ethlance-search-freelancers
  (:require [com.wsscode.async.async-cljs :refer [<!p go-catch]]
            [district.server.smart-contracts :as smart-contracts]
            [server.constants :as constants]
            [taoensso.timbre :as log]))

(defn search-freelancers [category skills job-recommendations offset limit]
  (let [rates (take (count constants/currencies) (repeat 0))]
    (go-catch
     (<!p (smart-contracts/contract-call :ethlance-search-freelancers
                                         :search-freelancers
                                         [category [] skills 0 0 rates rates [0 0 0 job-recommendations offset limit 0]])))))
