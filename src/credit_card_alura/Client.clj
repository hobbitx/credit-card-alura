(ns credit-card-alura.Client)
(def clients [])

(defn create-client [name cpf email]
  (def clients (conj clients  { :cpf cpf, :name name, :email email})))


(defn get-client [cpf]
  (first (filter #(= cpf (:cpf %)) clients)))

(defn show-clients []
  (println "-----------------------------")
  (println clients))


(show-clients)
(create-client "Robert" "13528838647" "robert.cristian@nubank.com.br")
(create-client "Tha" "1325" "th")
(println "Buscando  cliente" (get-client "13528838647"))
(show-clients)