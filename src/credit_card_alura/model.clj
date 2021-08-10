(ns credit-card-alura.model
  (:require [schema.core :as s]
            [clojure.string :as str]))

(s/set-fn-validation! true)
(defn more-or-equal-zero [x] (>= x 0))
(def Money (s/constrained s/Num more-or-equal-zero))
(defn notBlank? [x] (not (str/blank? x)))
(def StrNotBlank (s/constrained s/Str notBlank?))
(def Map (s/pred map?))

(defn uuid []
  (java.util.UUID/randomUUID))

(def Card
  {
   :number       StrNotBlank,
   :cvv          s/Num,
   :validate     StrNotBlank,
   :limit        Money,
   :actual-limit Money
   })


(def Purchase
  {

   :purchase/card          Map,
   :purchase/value         Money,
   :purchase/category      StrNotBlank,
   :purchase/date          StrNotBlank
   :purchase/establishment StrNotBlank,
   :purchase/pay           s/Bool
   :purchase/uuid          s/Uuid
   })



(def Client
  {
   :cpf   StrNotBlank,
   :name  StrNotBlank,
   :email StrNotBlank,
   })


(def Invoice
  {
   :card-number   s/Num
   :invoice-value Money
   })

