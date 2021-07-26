(ns credit-card-alura.Client)
(def clients [])

(defn create-client [name cpf email]
  (def clients (conj clients  { :cpf cpf, :name name, :email email})))


(defn get-client [cpf]
  (first (filter #(= cpf (:cpf %)) clients)))

(defn show-clients []
  (println "-----------------------------")
  (println clients))


