
export function defineCacheShowGdzc(showGdzc){

  function setCacheShowGdzc(bol){
    showGdzc.value=bol
    if(bol==true){
      window.localStorage.setItem('aaaaa','1')
    }else{
      window.localStorage.setItem('aaaaa','0')
    }

  }


  function getCacheShowGdzc(){
    if(window.localStorage.getItem('aaaaa')=='1'){
      return true
    }else if(window.localStorage.getItem('aaaaa')=='0'){
      return false
    }

  }

  if(window.localStorage.getItem('aaaaa')==null){
    setCacheShowGdzc(false)
  }else{
    setCacheShowGdzc(getCacheShowGdzc())
  }

  return {
    setCacheShowGdzc,
    getCacheShowGdzc
  }
}
