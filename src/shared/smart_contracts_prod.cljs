(ns shared.smart-contracts-prod)

(def smart-contracts
  {:ethlance-db {:name "EthlanceDB" :address "0x5371a8d8d8a86c76de935821ad1a3e9b908cfced"}
   :ethlance-user {:name "EthlanceUser" :address "0x27d233fa6032e848a016092d70493b2a5f13a95f"}
   :ethlance-contract {:name "EthlanceContract" :address "0x8F24AF20ad202C77686B771AD3dBc6b1fe28dDdD"}
   :ethlance-job {:name "EthlanceJob" :address "0xB9E80ce5A7CbbA0Aab685797F6585AD1f3c90028"}
   :ethlance-invoice {:name "EthlanceInvoice" :address "0x78f1072964d7f110e06670c229794afbdce7e474"}
   :ethlance-search-freelancers {:name "EthlanceSearchFreelancers" :address "0x43386ad7af76ca5384bc06ae0c74e230f32744ee"}
   :ethlance-search-jobs {:name "EthlanceSearchJobs" :address "0x9e2f85eea233047e527039681ad84448c8926690"}
   :ethlance-feedback {:name "EthlanceFeedback" :address "0x2249713725c8a4a070a61de0bdce6b1081014185"}
   :ethlance-message {:name "EthlanceMessage" :address "0xf94aa98bde7589719f1f08c6fb032debd0d7e9e6"}
   :ethlance-sponsor {:name "EthlanceSponsor" :address "0xb9f7d3b60ec29bd73fd66428f140ed5b0e1ef6ec"}})
