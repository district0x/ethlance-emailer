(ns server.contracts.ethlance-db
  (:require [bignumber.core :as bn]
            [cljs-web3-next.utils :as web3-utils]
            [clojure.string :as string]
            [com.wsscode.async.async-cljs :refer [<!p go-catch]]
            [district.server.smart-contracts :as smart-contracts]
            [district.server.web3 :refer [web3]]
            [taoensso.timbre :as log]))

(def str-delimiter "99--DELIMITER--11")
(def list-delimiter "99--DELIMITER-LIST--11")

(def field->solidity-type
  {:boolean 1
   :uint8 2
   :uint 3
   :address 4
   :bytes32 5
   :int 6
   :string 7
   :big-num 3})

(defn get-user [user-id]
  (go-catch
   (let [response (<!p (smart-contracts/contract-call :ethlance-db
                                                      :get-entity-list
                                                      [[(web3-utils/solidity-sha3 @web3 "user/name" user-id)
                                                        (web3-utils/solidity-sha3 @web3 "user/email" user-id)]
                                                       [(field->solidity-type :string)
                                                        (field->solidity-type :string)]]))
         [name email] (-> (.-strs ^js response)
                          (string/split str-delimiter))]
     {:user/name (subs name 1)
      :user/email (subs email 1)})))

(defn get-invoice [invoice-id]
  (go-catch
   (let [response (<!p (smart-contracts/contract-call :ethlance-db
                                                      :get-entity-list
                                                      [[(web3-utils/solidity-sha3 @web3 "invoice/amount" invoice-id)
                                                        (web3-utils/solidity-sha3 @web3 "invoice/description" invoice-id)]
                                                       [(field->solidity-type :uint)
                                                        (field->solidity-type :string)]]))
         [description] (-> (.-strs ^js response)
                           (string/split str-delimiter))
         [amount] (.-items ^js response)]
     {:invoice/description (subs description 1)
      :invoice/amount (-> (web3-utils/from-wei @web3 amount :ether) bn/number (.toFixed 3))})))

(defn get-contract [address]
  (go-catch
   (let [response (<!p (smart-contracts/contract-call :ethlance-db
                                                      :get-entity-list
                                                      [[(web3-utils/solidity-sha3 @web3 "contract/job" address)
                                                        (web3-utils/solidity-sha3 @web3 "proposal/description" address)
                                                        (web3-utils/solidity-sha3 @web3 "proposal/rate" address)
                                                        (web3-utils/solidity-sha3 @web3 "contract/description" address)
                                                        (web3-utils/solidity-sha3 @web3 "contract/cancel-description" address)
                                                        (web3-utils/solidity-sha3 @web3 "contract/freelancer-feedback-rating" address)
                                                        (web3-utils/solidity-sha3 @web3 "contract/freelancer-feedback" address)
                                                        (web3-utils/solidity-sha3 @web3 "contract/employer-feedback-rating" address)
                                                        (web3-utils/solidity-sha3 @web3 "contract/employer-feedback" address)
                                                        (web3-utils/solidity-sha3 @web3 "invitation/description" address)]
                                                       [(field->solidity-type :uint)
                                                        (field->solidity-type :string)
                                                        (field->solidity-type :uint)
                                                        (field->solidity-type :string)
                                                        (field->solidity-type :string)
                                                        (field->solidity-type :uint8)
                                                        (field->solidity-type :string)
                                                        (field->solidity-type :uint8)
                                                        (field->solidity-type :string)
                                                        (field->solidity-type :string)]]))
         [proposal-description contract-description
          contract-cancel-description
          freelancer-feedback employer-feedback
          invitation-description] (-> (.-strs ^js response)
                                      (string/split str-delimiter))
         [job-id rate freelancer-feedback-rating employer-feedback-rating] (.-items ^js response)]
     {:proposal/description (subs proposal-description 1)
      :proposal/rate (-> (web3-utils/from-wei @web3 rate :ether) bn/number (.toFixed 3))
      :contract/job job-id
      :contract/description (subs contract-description 1)
      :contract/cancel-description (subs contract-cancel-description 1)
      :contract/freelancer-feedback-rating freelancer-feedback-rating
      :contract/freelancer-feedback (subs freelancer-feedback 1)
      :contract/employer-feedback-rating employer-feedback-rating
      :contract/employer-feedback (subs employer-feedback 1)
      :invitation/description (subs invitation-description 1)})))

(defn get-job [id]
  (go-catch
   (let [response (<!p (smart-contracts/contract-call :ethlance-db
                                                      :get-entity-list
                                                      [[(web3-utils/solidity-sha3 @web3 "job/title" id)
                                                        (web3-utils/solidity-sha3 @web3 "job/reference-currency" id)
                                                        (web3-utils/solidity-sha3 @web3 "job/skills-count" id)
                                                        (web3-utils/solidity-sha3 @web3 "job/description" id)
                                                        (web3-utils/solidity-sha3 @web3 "job/category" id)
                                                        (web3-utils/solidity-sha3 @web3 "job/invitation-only?" id)]
                                                       [(field->solidity-type :string)
                                                        (field->solidity-type :uint8)
                                                        (field->solidity-type :uint)
                                                        (field->solidity-type :string)
                                                        (field->solidity-type :uint8)
                                                        (field->solidity-type :boolean)]]))
         [title job-description] (-> (.-strs ^js response)
                                     (string/split str-delimiter))
         [currency skill-count category invitation-only?] (.-items ^js response)]
     {:job/title (subs title 1)
      :job/reference-currency (bn/number currency)
      :job/skills-count skill-count
      :job/description job-description
      :job/category category
      :job/invitation-only? (get {"0" false
                                  "1" true} invitation-only?)})))

(defn get-message [id]
  (go-catch
   (let [response (<!p (smart-contracts/contract-call :ethlance-db
                                                      :get-entity-list
                                                      [[(web3-utils/solidity-sha3 @web3 "message/text" id)]
                                                       [(field->solidity-type :string)]]))
         [text] (-> (.-strs ^js response)
                    (string/split str-delimiter))]
     {:message/text (subs text 1)})))

;; TODO
(defn get-job-skills [job-id skill-count]

  (log/debug "@@@ get-job-skills" {:job-id job-id
                                   :skill-count skill-count})

  (go-catch
   (let [response (<!p (smart-contracts/contract-call :ethlance-db
                                                      :get-entity-list
                                                      [[(web3-utils/solidity-sha3 @web3 "job/skills" job-id 0)]
                                                       [3]]))

         _ (log/debug "@@@ get-job-skills" {:response {:items (.-items ^js response)}})

         ]

     )))

(defn get-sponsorship [id]
  (go-catch
   (let [response (<!p (smart-contracts/contract-call :ethlance-db
                                                      :get-entity-list
                                                      [[(web3-utils/solidity-sha3 @web3 "sponsorship/name" id)
                                                        (web3-utils/solidity-sha3 @web3 "sponsorship/link" id)]
                                                       [(field->solidity-type :string)
                                                        (field->solidity-type :string)]]))
         [name link] (-> (.-strs ^js response)
                         (string/split str-delimiter))]
     {:sponsorship/name (subs name 1)
      :sponsorship/link (subs link 1)})))
