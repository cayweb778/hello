export function useUseKuaiJikeMuInputEvents(lockBlur,mainRef,funs){
  return {
    useKuaiJikeMuInputEvents(events) {
      return {
        ...events,
        blur(e) {
          setTimeout(() => {
            if (!lockBlur.value) {
              // if (!funs.isOpen()) {
              //   clearInfo();
              events.close(e);
            }
          }, 10);
        },
        keyup(e) {
          switch (e.key) {
            case 'Enter':
              if (mainRef.value != null) {
                mainRef.value.keyupEnter();
              } else {
                events.keydown(e);
              }
              break;
          }
        },
        keydown(e) {
          switch (e.key) {
            case 'ArrowUp'  :
              if (mainRef.value != null) {
                mainRef.value.keyupUp();
              } else {
                events.keydown(e);
              }
              break;
            case 'Enter':
              // let e = window.event || arguments[0];
              // console.log(e,e.keyCode)
              if (e.key == 'Enter' || e.code == 'Enter' || e.keyCode == 13) {
                e.returnValue = false;
                return false;
              }
              break;

            case ' ':
              var e = window.event || event;
              if (e.preventDefault) {
                e.preventDefault();
              } else {
                window.event.returnValue = false;
              }
              funs.triggerPopover();
              break;
            case 'ArrowDown':
              if (mainRef.value != null) {
                mainRef.value.keyupDown();
              } else {
                events.keydown(e);
              }
              break;
          }
        },
      };
    }
  }
}
