(ns generate
  (:require [kura.site.page :as page]
            ["fs" :as fs]))
(fs/mkdirSync "public" #js{:recursive true})
(fs/writeFileSync "public/index.html" (page/render))
(println "wrote public/index.html" (count (page/render)) "bytes")
