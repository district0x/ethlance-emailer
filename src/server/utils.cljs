(ns server.utils
  (:require [bidi.bidi :as bidi]
            [clojure.string :as string]
            [server.constants :as constants]))

(def http-url-pattern #"(?i)^(?:(?:https?)://)(?:\S+(?::\S*)?@)?(?:(?!(?:10|127)(?:\.\d{1,3}){3})(?!(?:169\.254|192\.168)(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,}))\.?)(?::\d{2,5})?(?:[/?#]\S*)?$")

(defn replace-comma [x]
  (string/replace x \, \.))

(defn parse-float [number]
  (if (string? number)
    (js/parseFloat (replace-comma number))
    number))

(defn big-num->num [x]
  (if (and x (aget x "toNumber"))
    (.toNumber ^js x)
    x))

(defn truncate
  "Truncate a string with suffix (ellipsis by default) if it is
   longer than specified length."
  ([string length]
   (truncate string length "..."))
  ([string length suffix]
   (let [string-len (count string)
         suffix-len (count suffix)]
     (if (<= string-len length)
       string
       (str (subs string 0 (- length suffix-len)) suffix)))))

(defn http-url? [x & [{:keys [:allow-empty?]}]]
  (if (and allow-empty? (empty? x))
    true
    (when (string? x)
      (boolean (re-matches http-url-pattern x)))))

(defn rating->star [rating]
  (/ (or rating 0) 20))

(defn number-fraction-part [x]
  (let [frac (second (string/split (str x) #"\."))]
    (if frac
      (str "." frac)
      "")))

(defn to-locale-string [x max-fraction-digits]
  (let [parsed-x (cond
                   (string? x) (parse-float x)
                   (nil? x) ""
                   :else x)]
    (if-not (js/isNaN parsed-x)
      (.toLocaleString ^js parsed-x js/undefined #js {:maximumFractionDigits max-fraction-digits})
      x)))

(defn with-currency-symbol [value currency]
  (case currency
    1 (str (constants/currencies 1) value)
    (str value (constants/currencies currency))))

(defn format-currency [value currency & [{:keys [:full-length? :display-code?]}]]
  (let [value (-> (or value 0)
                  big-num->num)
        value (if full-length?
                (str (to-locale-string (js/parseInt value) 0) (number-fraction-part value))
                (to-locale-string value (if (= currency 0) 3 2)))]
    (if display-code?
      (str value " " (name (constants/currency-id->code currency)))
      (with-currency-symbol value currency))))

(defn path-for [& args]
  (str "#" (apply bidi/path-for constants/routes args)))

(defn full-path-for [& args]
  (str "http://ethlance.com/" (apply path-for args)))
