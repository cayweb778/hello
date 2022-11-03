import {isProdMode} from "/@/utils/env";

const ncdn_path=isProdMode()?new URL(window.location.href).origin+'/ncdn':'http://localhost:3900/ncdn'
export function getNcdnPath(){
  return ncdn_path
}

