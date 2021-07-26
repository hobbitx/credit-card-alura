(ns credit-card-alura.core
  (:require [credit-card-alura.Client :as clients]
            [credit-card-alura.Card :as cards]))


(clients/show-clients)
(clients/create-client "Robert" "13528838647" "robert.cristian@nubank.com.br")
(clients/create-client "Tha" "1325" "th")
(println "Buscando  cliente" (clients/get-client "13528838647"))
(clients/show-clients)

(cards/new-card "002929" "13528838647" "455" "10/2031" 1000)
(cards/new-card "002129" "13528838644" "455" "10/2031" 1000)
(println (cards/get-client-cards "13528838647"))
(println (cards/get-limit "002929"))