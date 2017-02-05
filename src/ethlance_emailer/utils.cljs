(ns ethlance-emailer.utils
  (:require
    [bidi.bidi :as bidi]
    [camel-snake-kebab.core :as cs :include-macros true]
    [camel-snake-kebab.extras :refer [transform-keys]]
    [ethlance-emailer.routes :refer [routes]]
    [clojure.string :as string]
    [goog.string :as gstring]))

(def SoliditySha3 (aget (js/require "solidity-sha3") "default"))

(defn js-val [clj-or-js-dict]
  (cond
    (map? clj-or-js-dict) (clj->js clj-or-js-dict)
    (vector? clj-or-js-dict) (clj->js clj-or-js-dict)
    :else clj-or-js-dict))

(def js->cljk #(js->clj % :keywordize-keys true))

(def js->cljkk (comp (partial transform-keys cs/->kebab-case) js->cljk))

(def cljkk->js (comp clj->js (partial transform-keys cs/->camelCase)))

(defn callback-js->clj [x]
  (if (fn? x)
    (fn [err res]
      (x err (js->cljkk res)))
    x))

(defn args-cljkk->js [args]
  (map (comp cljkk->js callback-js->clj) args))

(defn js-apply
  ([this method-name]
   (js-apply this method-name nil))
  ([this method-name args]
   (let [method-name (name method-name)]
     (if-let [method (aget this (if (string/includes? method-name "-") ; __callback gets wrongly transformed
                                  (cs/->camelCase method-name)
                                  method-name))]
       (js->cljkk (.apply method this (clj->js (args-cljkk->js args))))
       (throw (str "Method: " method-name " was not found in object."))))))

(defn safe-js-apply
  ([this method-name]
   (js-apply this method-name []))
  ([this method-name args]
   (let [method-name (name method-name)]
     (if-let [method (aget this method-name)]
       (.apply method this (clj->js args))
       (throw (str "Method: " method-name " was not found in object."))))))

(defn js-prototype-apply [js-obj method-name args]
  (js-apply (aget js-obj "prototype") method-name args))

(defn prop-or-clb-fn [& ks]
  (fn [web3 & args]
    (if (fn? (first args))
      (js-apply (apply aget web3 (butlast ks)) (str "get" (cs/->PascalCase (last ks))) args)
      (js->cljkk (apply aget web3 ks)))))

(defn to-number [x]
  (safe-js-apply x "toNumber"))

(defn big-num->num [x]
  (if (and x (aget x "toNumber"))
    (to-number x)
    x))

(defn big-nums->nums [coll]
  (map big-num->num coll))

(defn ns+name [x]
  (when x
    (str (when-let [n (namespace x)] (str n "/")) (name x))))

(defn sha3 [& args]
  (apply SoliditySha3 (map #(if (keyword? %) (ns+name %) %) args)))

(defn remove-zero-chars [s]
  (string/join (take-while #(< 0 (.charCodeAt % 0)) s)))

(defn prepend-address-zeros [address]
  (let [n (- 42 (count address))]
    (if (pos? n)
      (->> (subs address 2)
        (str (string/join (take n (repeat "0"))))
        (str "0x"))
      address)))

(defn uint8? [x]
  (and x (not (neg? x))))

(defn uint? [x]
  (and x (not (neg? x))))

(defn address? [x]
  (string? x))

(defn bytes32? [x]
  (string? x))

(defn bytes32-or-nil? [x]
  (or (nil? x) (string? x)))

(defn uint-coll? [x]
  (and x (every? uint? x)))

(defn string-or-nil? [x]
  (or (nil? x) (string? x)))

(defn big-num? [x]
  (and x (aget x "toNumber")))

(defn split-include-empty [s re]
  (butlast (string/split (str s " ") re)))

(defn map-val [x]
  (second (first x)))

(defn path-for [& args]
  (str "#" (apply bidi/path-for routes args)))

(defn full-path-for [& args]
  (str "http://ethlance.com/" (apply path-for args)))

(defn format-currency [x]
  (gstring/format "%.3fΞ" x))

(defn rating->star [rating]
  (/ (or rating 0) 20))
