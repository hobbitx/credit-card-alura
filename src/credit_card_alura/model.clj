(ns credit-card-alura.model
  (:require [schema.core :as s]))

(use 'java-time)
(s/set-fn-validation! true)
(defn maior-ou-igual-a-zero? [x] (>= x 0))
(def ValorFinanceiro (s/constrained s/Num maior-ou-igual-a-zero?))


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
   :limit        ValorFinanceiro,
   :actual-limit ValorFinanceiro
   })


(println (s/validate ValorFinanceiro 1000.0))

(def Date java.time.LocalDate)
(def Purchase
  {
   :card-number     s/Str,
   :date            Date,
   :value           ValorFinanceiro,
   :category        s/Str,
   :establishment   s/Str,
   :pay             s/Bool
   })

(def Invoice
  {
   :card-number   s/Str
   :invoice-value ValorFinanceiro
   })

