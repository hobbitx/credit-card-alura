(ns credit-card-alura.model
  (:require [schema.core :as s]
            [clojure.string :as str]))

(use 'java-time)
(s/set-fn-validation! true)
(defn more-or-equal-zero [x] (>= x 0))
(def Money (s/constrained s/Num more-or-equal-zero))
(defn notBlank? [x] (not (str/blank? x)))
(def StrNotBlank (s/constrained s/Str notBlank?))


(def Card
  {
   :cpf          StrNotBlank,
   :number       StrNotBlank,
   :cvv          s/Num,
   :validate     StrNotBlank,
   :limit        Money,
   :actual-limit Money
   })


(println (s/validate Money 1000.0))

(def Purchase
  {
   :card-number     StrNotBlank,
   :date            StrNotBlank,
   :value           Money,
   :category        StrNotBlank,
   :establishment   StrNotBlank,
   :pay             s/Bool
   })



(def Client
  {
   :cpf   StrNotBlank,
   :name  StrNotBlank,
   :email StrNotBlank,
   })


(def Invoice
  {
   :card-number   StrNotBlank
   :invoice-value Money
   })

