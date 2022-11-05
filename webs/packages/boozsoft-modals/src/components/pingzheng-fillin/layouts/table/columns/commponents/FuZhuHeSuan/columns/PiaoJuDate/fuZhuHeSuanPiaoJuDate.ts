import {watch} from "vue";

export function datePnKeydownSpace(fun){
  window.fuZhuHeSuanDateKeupTemp = document.onkeydown
  setTimeout(() => {
    document.onkeydown = function (event) {
      var e = event || window.event || arguments.callee.caller.arguments[0];
      console.log(11111)
      if (e.key == ' ') {
        fun()
        e.preventDefault()
      }
    }
  }, 300)
}
export function cancelDatePnKeydownSpace(){
  document.onkeydown = window.fuZhuHeSuanDateKeupTemp
}
