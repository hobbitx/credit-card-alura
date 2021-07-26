(ns credit-card-alura.Card
  (:require [credit-card-alura.Client :as clients]))


(def cards [])

(defn exist-client? [cpf] (clients/get-client cpf))

(defn new-card [number cpf cvv validate limit]
  (if (exist-client? cpf)
    (def cards (conj cards  { :cpf cpf, :number number, :cvv cvv, :validate validate :limit limit}))
    (println "Cliente não encontrado"))
  )

(defn get-client-cards [cpf]
  (filter #(= cpf (:cpf %)) cards))

(defn get-limit [number]
  (:limit (first (filter #(= number (:number %)) cards))))
