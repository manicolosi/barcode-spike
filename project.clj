(defproject barcode-spike "0.1.0-SNAPSHOT"
  :description "Spike for generating barcodes"

  :license
  {:name "Eclipse Public License"
   :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies
  [[org.clojure/clojure "1.6.0"]
   [compojure "1.1.8"]
   [com.google.zxing/core "3.2.0"]]

  :plugins
  [[lein-ring "0.8.11"]]

  :ring
  {:handler barcode-spike.core/app
   :port 8080
   :nrepl {:start? true}})
