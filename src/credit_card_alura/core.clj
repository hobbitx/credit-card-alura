(ns credit-card-alura.core
  (:require [credit-card-alura.Client :as clients]
            [credit-card-alura.Card :as cards]
            [credit_card-alura.Purchase :as purchases]))
(use 'java-time)

(def clients [])
(def purchases [])
(def cards [])

(clients/show-clients clients)
(def clients (clients/create-client "Robert" "13528838647" "robert.cristian@nubank.com.br" clients))
(def clients (clients/create-client "Tha" "1325" "th" clients))

(println "\nBuscando  cliente" (clients/get-client "13528838647" clients))

(def cards (cards/new-card "002929" "13528838647" "455" "10/2031" 1000.0 cards clients))

(println "\nSelecionando cartao" (def card (cards/select-cards "002929" cards)))
(println "\nLimite do cartao" (:number card) ": R$" (cards/get-limit card))

(def purchases (purchases/new-purchase (local-date) 700.0 "padaria" "alimentacao" card purchases))
(def purchases (purchases/new-purchase (local-date) 1000.0 "farmacia" "saude" card purchases))
(def purchases (purchases/new-purchase (local-date) 200.0 "farmacia" "saude" card purchases))

(println "\nCompras" (purchases/get-purchases (:number card) purchases))
(println "\nPor categoria" (purchases/total-by-category (:number card) purchases))
(println "\nFatura total" (purchases/invoice (:number card) purchases))
(println "\nCompras entre 10 e 400 R$" (purchases/filter-purchases 10 400 :value purchases))