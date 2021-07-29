(ns credit-card-alura.Card
  (:require [credit-card-alura.Client :as clients]))
(use 'java-time)

(defn exist-client? [cpf clients] (clients/get-client cpf clients))

(defn new-card [number cpf cvv validate limit cards clients]
  (if (exist-client? cpf clients)
    (conj cards {:cpf cpf, :number number, :cvv cvv, :validate validate :limit limit :actual-limit limit})
    (println "Cliente não encontrado")))

(defn get-client-cards [cpf cards]
  (filter #(= cpf (:cpf %)) cards))

(defn select-cards [card-number cards]
  (first (filter #(= card-number (:number %)) cards)))

(defn get-limit [card]
  (:limit card))





