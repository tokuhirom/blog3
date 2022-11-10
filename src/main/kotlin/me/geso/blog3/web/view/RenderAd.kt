package me.geso.blog3.web.view

import kotlinx.html.DIV
import kotlinx.html.script
import kotlinx.html.unsafe

internal fun DIV.renderAd() {
    comment("ads")
    script {
        unsafe {
            +"""
              function insertScript(src) {
                const script = document.createElement("script")
                script.setAttribute("src", src)
                script.async = true;
                document.body.appendChild(script);
              }
            
              if (window.innerWidth > 728) {
                google_ad_client = "ca-pub-9032322815824634";
                /* blog2 */
                google_ad_slot = "7680858718";
                google_ad_width = 728;
                google_ad_height = 90;
            
                insertScript("http://pagead2.googlesyndication.com/pagead/show_ads.js");
              } else {
                insertScript("http://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js");
            
                const ins = document.createElement("ins");
                ins.setAttribute("class", "adsbygoogle");
                ins.setAttribute("style", "display:inline-block;width:320px;height:50px")
                ins.setAttribute("data-ad-client", "ca-pub-9032322815824634")
                ins.setAttribute("data-ad-slot", "7500646436");
                (adsbygoogle = window.adsbygoogle || []).push({});
              }
            """.trimIndent()
        }
    }
    comment("/ads")
}
