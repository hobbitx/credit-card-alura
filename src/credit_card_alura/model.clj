(ns credit-card-alura.model
  (:require [schema.core :as s]))

(use 'java-time)
(s/set-fn-validation! true)

(def Client
  {
   :cpf   s/Str,
   :name  s/Str,
   :email s/Str
   })

(def Card
  {
   :cpf          s/Str,
   :number       s/Str,
   :cvv          s/Num,
   :validate     s/Str,
   :limit        s/Num,
   :actual-limit s/Num
   })

(defn maior-ou-igual-a-zero? [x] (>= x 0))
(def ValorFinanceiro (s/constrained s/Num maior-ou-igual-a-zero?))

(def Purchase
  {
   :card-number     s/Str,
   :date            s/Any,
   :value           ValorFinanceiro,
   :category        s/Str,
   :establishment   s/Str,
   :pay             s/Bool
   })

