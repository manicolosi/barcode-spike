(ns barcode-spike.core
  (:require
    [compojure.core :refer [defroutes GET]]
    [ring.middleware.params :refer [wrap-params]]
    [ring.middleware.keyword-params :refer [wrap-keyword-params]])
  (:import
    [java.awt.image BufferedImage]
    [java.io ByteArrayOutputStream]
    [javax.imageio ImageIO]
    [com.google.zxing BarcodeFormat]
    [com.google.zxing.qrcode QRCodeWriter]))

(defn encode [data width height]
  (.encode (QRCodeWriter.)
           data
           BarcodeFormat/QR_CODE
           width
           height))

(defn set-image [matrix image x y]
  (.setRGB image x y
           (if (.get matrix x y)
             0x000000
             0xffffff)))

(defn write-image [image]
  (let [output-stream (ByteArrayOutputStream.)]
    (ImageIO/write image "png" output-stream)
    output-stream))

(defn matrix->image [matrix]
  (let [width (.getWidth matrix)
        height (.getHeight matrix)
        image (BufferedImage. width height BufferedImage/TYPE_INT_RGB)]
    (doseq [i (range height)]
      (doseq [j (range width)]
        (set-image matrix image i j)))
    image))

(defn generate [data]
  (-> data
      (encode 100 100)
      matrix->image
      write-image))

(defn adjust-byte [the-byte]
  (if (neg? the-byte)
    (+ 256 the-byte)
    the-byte))

(defn read-bytes [the-bytes]
  (->> the-bytes
      (map (comp char adjust-byte))
      (apply str)))

(defroutes handler
  (GET "/generate" req
       (let [stream (generate (get-in req [:params :data]))]
         {:status 200
          :body (read-bytes (.toByteArray stream))
          :headers {"Content-Type" "image/png"}})))

(def app
  (-> #'handler
      wrap-keyword-params
      wrap-params))
