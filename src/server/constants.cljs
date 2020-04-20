(ns server.constants)

(def currencies
  {0 "Ξ"
   1 "$"
   2 "€"
   3 "£"
   4 "\u20BD"
   5 "¥"
   6 "¥"})

(def currency-code->id
  {:ETH 0
   :USD 1
   :EUR 2
   :GBP 3
   :RUB 4
   :CNY 5
   :JPY 6})

(def currency-id->code
  {0 :ETH
   1 :USD
   2 :EUR
   3 :GBP
   4 :RUB
   5 :CNY
   6 :JPY})

#_(def job-recommendations-cron
  {2 "0 4,16 * * *"
   3 "0 12 * * *"
   4 "0 14 */3 * *"
   5 "0 13 * * 0"})

(def routes
  ["/" [["how-it-works" :how-it-works]
        ["about" :about]
        ["edit-profile" :user/edit]
        ["become-freelancer" :freelancer/create]
        ["become-employer" :employer/create]
        ["find/" {"work" :search/jobs
                  "candidates" :search/freelancers}]
        ["search/" {"jobs" :search/jobs
                    "freelancers" :search/freelancers}]
        ["freelancer/" {"my-invoices" :freelancer/invoices
                        "my-contracts" :freelancer/contracts}]
        ["employer/" {"my-invoices" :employer/invoices
                      "my-jobs" :employer/jobs
                      "my-contracts" :employer/contracts}]
        [["freelancer/" :user/id] :freelancer/detail]
        [["employer/" :user/id] :employer/detail]
        ["job/create" :job/create]
        [["job/" :job/id] :job/detail]
        [["job-proposal/" :contract/id] :contract/detail]
        [["contract/" :contract/id "/invoices"] :contract/invoices]
        ["invoice/create" :invoice/create]
        [["invoice/" :invoice/id] :invoice/detail]
        [true :home]]])

(def web3-events
  {:ethlance-invoice/on-invoice-added [:ethlance-invoice :on-invoice-added]
   :ethlance-invoice/on-invoice-paid [:ethlance-invoice :on-invoice-paid]
   :ethlance-invoice/on-invoice-cancelled [:ethlance-invoice :on-invoice-cancelled]
   :ethlance-contract/on-job-proposal-added [:ethlance-contract :on-job-proposal-added]
   :ethlance-contract/on-job-contract-added [:ethlance-contract :on-job-contract-added]
   :ethlance-contract/on-job-contract-cancelled [:ethlance-contract :on-job-contract-cancelled]
   :ethlance-feedback/on-job-contract-feedback-added [:ethlance-feedback :on-job-contract-feedback-added]
   :ethlance-contract/on-job-invitation-added [:ethlance-contract :on-job-invitation-added]
   :ethlance-message/on-job-contract-message-added [:ethlance-message :on-job-contract-message-added]
   ;; NOTE: to be simplified and reimplemented
   #_:ethlance-job/on-job-added #_[:ethlance-job :on-job-added]
   ;; NOTE: empty listeners for these in the legacy implementation
   ;; :ethlance-user/on-freelancer-added [:ethlance-user :on-freelancer-added]
   ;; :ethlance-user/on-employer-added [:ethlance-user :on-employer-added]
   :ethlance-sponsor/on-job-sponsorship-added [:ethlance-sponsor :on-job-sponsorship-added]
   :ethlance-sponsor/on-job-sponsorship-refunded [:ethlance-sponsor :on-job-sponsorship-refunded]
   :ethlance-job/on-sponsorable-job-approved [:ethlance-job :on-sponsorable-job-approved]})
