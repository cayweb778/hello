import {isProdMode} from "/@/utils/env";

export function useNcdn(path) {
 if(isProdMode()){
    return "/ncdn"
 } else{

   return import.meta.env.VITE_CDN_PATH+path
 }
}

function setGlobal(app) {

  app.config.globalProperties.useNcdn = useNcdn
  app.config.globalProperties.withNcdn = function (path){
    return "url("+useNcdn(path)+")"
  }
}

export function setupNcdn(app) {
  setGlobal(app)
}
