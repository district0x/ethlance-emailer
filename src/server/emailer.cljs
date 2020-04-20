(ns server.emailer
  (:require [cljs-web3-next.utils :as web3-utils]
            [cljs.core.async :refer [<!]]
            [com.wsscode.async.async-cljs :refer [go-catch]]
            [district.sendgrid :refer [send-email]]
            [district.server.config :as config]
            [district.server.web3 :refer [ping-start ping-stop web3]]
            [district.server.web3-events :as web3-events]
            [mount.core :as mount :refer [defstate]]
            [server.contracts.ethlance-db :as ethlance-db]
            [server.contracts.ethlance-search-freelancers
             :as
             ethlance-search-freelancers]
            [server.templates :as templates]
            [server.utils :as u]
            [taoensso.timbre :as log]))

(defn placehodler [event]
  (log/info "Handler not implemented" event))

(defn- dispatcher [callback]
  (fn [error {:keys [:latest-event? :args] :as event}]
    ;; TODO
    (when #_true latest-event?
      (callback args))))

(defn on-invoice-added [{:keys [:invoice-id :employer-id :freelancer-id] :as args}]
  (go-catch
   (let [{:keys [:from :template-id :api-key :print-mode?] :as opts} (get-in @config/config [:emailer])
         employer (<! (ethlance-db/get-user employer-id))
         freelancer (<! (ethlance-db/get-user freelancer-id))
         invoice (<! (ethlance-db/get-invoice invoice-id))]
     (send-email
      {:from from
       :to (:user/email employer)
       :subject "You received an invoice to pay"
       :content (templates/on-invoice-added invoice freelancer)
       :substitutions {"%name%" (:user/name employer)
                       "%open-detail-button-text%" "Open Invoice"
                       "%open-detail-button-href%" (u/full-path-for :invoice/detail :invoice/id invoice-id)}
       :on-success #(log/info "Successfully sent on-invoice-added email"
                              {:args args :opts opts})
       :on-error #(log/info "Error sending on-invoice-added email"
                            {:args args :opts opts :error %})
       :template-id template-id
       :api-key api-key
       :print-mode? print-mode?}))))

(defn on-invoice-paid [{:keys [:invoice-id :employer-id :freelancer-id] :as args}]
  (go-catch
   (let [{:keys [:from :template-id :api-key :print-mode?] :as opts} (get-in @config/config [:emailer])
         employer (<! (ethlance-db/get-user employer-id))
         freelancer (<! (ethlance-db/get-user freelancer-id))
         invoice (<! (ethlance-db/get-invoice invoice-id))]
     (send-email
      {:from from
       :to (:user/email freelancer)
       :subject "Your invoice was paid"
       :content (templates/on-invoice-paid invoice employer)
       :substitutions {"%name%" (:user/name freelancer)
                       "%open-detail-button-text%" "Open Invoice"
                       "%open-detail-button-href%" (u/full-path-for :invoice/detail :invoice/id invoice-id)}
       :on-success #(log/info "Successfully sent on-invoice-paid email"
                              {:args args :opts opts})
       :on-error #(log/info "Error sending on-invoice-paid email"
                            {:args args :opts opts :error %})
       :template-id template-id
       :api-key api-key
       :print-mode? print-mode?}))))

(defn on-invoice-cancelled [{:keys [:invoice-id :employer-id :freelancer-id] :as args}]
  (go-catch
   (let [{:keys [:from :template-id :api-key :print-mode?] :as opts} (get-in @config/config [:emailer])
         employer (<! (ethlance-db/get-user employer-id))
         freelancer (<! (ethlance-db/get-user freelancer-id))
         invoice (<! (ethlance-db/get-invoice invoice-id))]
     (send-email
      {:from from
       :to (:user/email employer)
       :subject "Invoice you previously received was cancelled"
       :content (templates/on-invoice-cancelled invoice freelancer)
       :substitutions {"%name%" (:user/name employer)
                       "%open-detail-button-text%" "Open Invoice"
                       "%open-detail-button-href%" (u/full-path-for :invoice/detail :invoice/id invoice-id)}
       :on-success #(log/info "Successfully sent on-invoice-cancelled email"
                              {:args args :opts opts})
       :on-error #(log/info "Error sending on-invoice-cancelled email"
                            {:args args :opts opts :error %})
       :template-id template-id
       :api-key api-key
       :print-mode? print-mode?}))))

(defn on-job-proposal-added [{:keys [:contract-id :employer-id :freelancer-id] :as args}]
  (go-catch
   (let [{:keys [:from :template-id :api-key :print-mode?] :as opts} (get-in @config/config [:emailer])
         {job-id :contract/job :as contract} (<! (ethlance-db/get-contract contract-id))
         job (<! (ethlance-db/get-job job-id))
         employer (<! (ethlance-db/get-user employer-id))
         freelancer (<! (ethlance-db/get-user freelancer-id))]
     (send-email
      {:from from
       :to (:user/email employer)
       :subject "Your job received a proposal"
       :content (templates/on-job-proposal-added job contract freelancer)
       :substitutions {"%name%" (:user/name employer)
                       "%open-detail-button-text%" "Open Proposal"
                       "%open-detail-button-href%" (u/full-path-for :contract/detail :contract/id contract-id)}
       :on-success #(log/info "Successfully sent on-job-proposal-added email"
                              {:args args :opts opts})
       :on-error #(log/info "Error sending on-job-proposal-added email"
                            {:args args :opts opts :error %})
       :template-id template-id
       :api-key api-key
       :print-mode? print-mode?}))))

(defn on-job-contract-added [{:keys [:contract-id :employer-id :freelancer-id] :as args}]
  (go-catch
   (let [{:keys [:from :template-id :api-key :print-mode?] :as opts} (get-in @config/config [:emailer])
         {job-id :contract/job :as contract} (<! (ethlance-db/get-contract contract-id))
         job (<! (ethlance-db/get-job job-id))
         employer (<! (ethlance-db/get-user employer-id))
         freelancer (<! (ethlance-db/get-user freelancer-id))]
     (send-email
      {:from from
       :to (:user/email freelancer)
       :subject "You got hired!"
       :content (templates/on-job-contract-added job contract)
       :substitutions {"%name%" (:user/name freelancer)
                       "%open-detail-button-text%" "Open Contract"
                       "%open-detail-button-href%" (u/full-path-for :contract/detail :contract/id contract-id)}
       :on-success #(log/info "Successfully sent on-job-contract-added email"
                              {:args args :opts opts})
       :on-error #(log/info "Error sending on-job-contract-added email"
                            {:args args :opts opts :error %})
       :template-id template-id
       :api-key api-key
       :print-mode? print-mode?}))))

(defn on-job-contract-cancelled [{:keys [:contract-id :employer-id :freelancer-id] :as args}]
  (go-catch
   (let [{:keys [:from :template-id :api-key :print-mode?] :as opts} (get-in @config/config [:emailer])
         {job-id :contract/job :as contract} (<! (ethlance-db/get-contract contract-id))
         job (<! (ethlance-db/get-job job-id))
         employer (<! (ethlance-db/get-user employer-id))
         freelancer (<! (ethlance-db/get-user freelancer-id))]
     (send-email
      {:from from
       :to (:user/email employer)
       :subject "Freelancer cancelled your contract"
       :content (templates/on-job-contract-cancelled job contract freelancer)
       :substitutions {"%name%" (:user/name employer)
                       "%open-detail-button-text%" "Open Contract"
                       "%open-detail-button-href%" (u/full-path-for :contract/detail :contract/id contract-id)}
       :on-success #(log/info "Successfully sent on-job-contract-cancelled email"
                              {:args args :opts opts})
       :on-error #(log/info "Error sending on-job-contract-cancelled email"
                            {:args args :opts opts :error %})
       :template-id template-id
       :api-key api-key
       :print-mode? print-mode?}))))

(defn on-job-contract-feedback-added [{:keys [:contract-id :receiver-id :sender-id :is-sender-freelancer] :as args}]
  (go-catch
   (let [{:keys [:from :template-id :api-key :print-mode?] :as opts} (get-in @config/config [:emailer])
         {job-id :contract/job :as contract} (<! (ethlance-db/get-contract contract-id))
         sender (<! (ethlance-db/get-user sender-id))
         receiver (<! (ethlance-db/get-user receiver-id))]
     (send-email
      {:from from
       :to (:user/email receiver)
       :subject "You received feedback"
       :content (templates/on-job-contract-feedback-added
                 (get contract (if is-sender-freelancer :contract/freelancer-feedback-rating
                                   :contract/employer-feedback-rating))
                 (get contract (if is-sender-freelancer :contract/freelancer-feedback
                                   :contract/employer-feedback))
                 sender)
       :substitutions {"%name%" (:user/name receiver)
                       "%open-detail-button-text%" "Open Contract"
                       "%open-detail-button-href%" (u/full-path-for :contract/detail :contract/id contract-id)}
       :on-success #(log/info "Successfully sent on-job-contract-feedback-added email"
                              {:args args :opts opts})
       :on-error #(log/info "Error sending on-job-contract-feedback-added email"
                            {:args args :opts opts :error %})
       :template-id template-id
       :api-key api-key
       :print-mode? print-mode?}))))

(defn on-job-invitation-added [{:keys [:contract-id :freelancer-id] :as args}]
  (go-catch
   (let [{:keys [:from :template-id :api-key :print-mode?] :as opts} (get-in @config/config [:emailer])
         {job-id :contract/job :as contract} (<! (ethlance-db/get-contract contract-id))
         job (<! (ethlance-db/get-job job-id))
         freelancer (<! (ethlance-db/get-user freelancer-id))]
     (send-email
      {:from from
       :to (:user/email freelancer)
       :subject "You've been invited to apply for a job"
       :content (templates/on-job-invitation-added job contract)
       :substitutions {"%name%" (:user/name freelancer)
                       "%open-detail-button-text%" "Open Job"
                       "%open-detail-button-href%" (u/full-path-for :job/detail :job/id job-id)}
       :on-success #(log/info "Successfully sent on-job-invitation-added email"
                              {:args args :opts opts})
       :on-error #(log/info "Error sending on-job-invitation-added email"
                            {:args args :opts opts :error %})
       :template-id template-id
       :api-key api-key
       :print-mode? print-mode?}))))

(defn on-job-contract-message-added [{:keys [:message-id :contract-id :sender-id :receiver-id] :as args}]
  (go-catch
   (let [{:keys [:from :template-id :api-key :print-mode?] :as opts} (get-in @config/config [:emailer])
         message (<! (ethlance-db/get-message message-id))
         sender (<! (ethlance-db/get-user sender-id))
         receiver (<! (ethlance-db/get-user receiver-id))]
     (send-email
      {:from from
       :to (:user/email receiver)
       :subject "You received a message"
       :content (templates/on-job-contract-message-added message sender)
       :substitutions {"%name%" (:user/name receiver)
                       "%open-detail-button-text%" "Open Job"
                       "%open-detail-button-href%" (u/full-path-for :contract/detail :contract/id contract-id)}
       :on-success #(log/info "Successfully sent on-job-contract-message-added email"
                              {:args args :opts opts})
       :on-error #(log/info "Error sending on-job-contract-message-added email"
                            {:args args :opts opts :error %})
       :template-id template-id
       :api-key api-key
       :print-mode? print-mode?}))))

;; TODO
(defn on-job-added [{:keys [:job-id] :as job}]
  (go-catch
   (let [{:keys [:from :template-id :api-key :print-mode?] :as opts} (get-in @config/config [:emailer])
         {:job/keys [category skills-count] :as job} (<! (ethlance-db/get-job job-id))

         {:job/keys [skills] :as job-skills} (<! (ethlance-db/get-job-skills job-id skills-count))

         [offset limit] [0 10]
         freelancers (ethlance-search-freelancers/search-freelancers category
                                                                     job-skills
                                                                     1
                                                                     offset
                                                                     limit)

         _ (log/debug "@@@ on-job-added" {:job job
                                          :skills job-skills
                                          :freelancers freelancers})
         ]

     )))

(defn on-job-sponsorship-added [{:keys [:sponsorship-id :job-id :employer-id :amount] :as args}]
  (go-catch
   (let [{:keys [:from :template-id :api-key :print-mode?] :as opts} (get-in @config/config [:emailer])
         job (<! (ethlance-db/get-job job-id))
         employer (<! (ethlance-db/get-user employer-id))
         sponsorship (<! (ethlance-db/get-sponsorship sponsorship-id))]
     (send-email
      {:from from
       :to (:user/email employer)
       :subject "Your job received a sponsorship"
       :content (templates/on-job-sponsorship-added job sponsorship (-> (web3-utils/from-wei @web3 amount :ether) #_bn/number #_(.toFixed 3)))
       :substitutions {"%name%" (:user/name employer)
                       "%open-detail-button-text%" "Open Job"
                       "%open-detail-button-href%" (u/full-path-for :job/detail :job/id job-id)}
       :on-success #(log/info "Successfully sent on-job-sponsorship-added email"
                              {:args args :opts opts})
       :on-error #(log/info "Error sending on-job-sponsorship-added email"
                            {:args args :opts opts :error %})
       :template-id template-id
       :api-key api-key
       :print-mode? print-mode?}))))

(defn on-job-sponsorship-refunded [{:keys [:sponsorship-id :job-id :receiver-id :amount] :as args}]
  (go-catch
   (let [{:keys [:from :template-id :api-key :print-mode?] :as opts} (get-in @config/config [:emailer])
         job (<! (ethlance-db/get-job job-id))
         receiver (<! (ethlance-db/get-user receiver-id))]
     (send-email
      {:from from
       :to (:user/email receiver)
       :subject "You were refunded"
       :content (templates/on-job-sponsorship-refunded job (-> (web3-utils/from-wei @web3 amount :ether)))
       :substitutions {"%name%" (:user/name receiver)
                       "%open-detail-button-text%" "Open Job"
                       "%open-detail-button-href%" (u/full-path-for :job/detail :job/id job-id)}
       :on-success #(log/info "Successfully sent on-job-sponsorship-refunded email"
                              {:args args :opts opts})
       :on-error #(log/info "Error sending on-job-sponsorship-refunded email"
                            {:args args :opts opts :error %})
       :template-id template-id
       :api-key api-key
       :print-mode? print-mode?}))))

(defn on-sponsorable-job-approved [{:keys [:job-id :employer-id :approver] :as args}]
  (go-catch
   (let [{:keys [:from :template-id :api-key :print-mode?] :as opts} (get-in @config/config [:emailer])
         job (<! (ethlance-db/get-job job-id))
         employer (<! (ethlance-db/get-user employer-id))]
     (send-email
      {:from from
       ;; TODO: DEBUG
       :to "filip@district0x.io" #_ (:user/email employer)
       :subject "Your job got approval"
       :content (templates/on-sponsorable-job-approved job approver)
       :substitutions {"%name%" (:user/name employer)
                       "%open-detail-button-text%" "Open Job"
                       "%open-detail-button-href%" (u/full-path-for :job/detail :job/id job-id)}
       :on-success #(log/info "Successfully sent on-sponsorable-job-approved email"
                              {:args args :opts opts})
       :on-error #(log/info "Error sending on-sponsorable-job-approved email"
                            {:args args :opts opts :error %})
       :template-id template-id
       :api-key api-key
       :print-mode? print-mode?}))))

(defn start [opts]
  (let [callback-ids
        [(web3-events/register-callback! :ethlance-invoice/on-invoice-added (dispatcher on-invoice-added))
         (web3-events/register-callback! :ethlance-invoice/on-invoice-paid (dispatcher on-invoice-paid))
         (web3-events/register-callback! :ethlance-invoice/on-invoice-cancelled (dispatcher on-invoice-cancelled))
         (web3-events/register-callback! :ethlance-contract/on-job-proposal-added (dispatcher on-job-proposal-added))
         (web3-events/register-callback! :ethlance-contract/on-job-contract-added (dispatcher on-job-contract-added))
         (web3-events/register-callback! :ethlance-contract/on-job-contract-cancelled (dispatcher on-job-contract-cancelled))
         (web3-events/register-callback! :ethlance-feedback/on-job-contract-feedback-added (dispatcher on-job-contract-feedback-added))
         (web3-events/register-callback! :ethlance-contract/on-job-invitation-added (dispatcher on-job-invitation-added))
         (web3-events/register-callback! :ethlance-message/on-job-contract-message-added (dispatcher on-job-contract-message-added))
         ;; TODO : reimplement
         #_(web3-events/register-callback! :ethlance-job/on-job-added (dispatcher on-job-added))
         ;; NOTE : not implemented in legacy code
         #_(web3-events/register-callback! :ethlance-user/on-freelancer-added (dispatcher placehodler))
         #_(web3-events/register-callback! :ethlance-user/on-employer-added (dispatcher placehodler))
         (web3-events/register-callback! :ethlance-sponsor/on-job-sponsorship-added (dispatcher on-job-sponsorship-added))
         (web3-events/register-callback! :ethlance-sponsor/on-job-sponsorship-refunded (dispatcher on-job-sponsorship-refunded))
         (web3-events/register-callback! :ethlance-job/on-sponsorable-job-approved (dispatcher on-sponsorable-job-approved))]]
    (ping-start {:ping-interval 10000})
    (assoc opts :callback-ids callback-ids)))

(defn stop [emailer]
  (ping-stop)
  (web3-events/unregister-callbacks! (:callback-ids @emailer)))

(defstate emailer
  :start (start (merge (:emailer @config/config)
                       (:emailer (mount/args))))
  :stop (stop emailer))
