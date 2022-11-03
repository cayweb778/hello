
export function usePopoverInputEvents(events,isWillClosePopover,showOpenSelect) {
    return {
        ...events,
        blur() {
            if (isWillClosePopover.value) {
                showOpenSelect.value = false
            } else {
                events.blur()
                isWillClosePopover.value = true
            }
        }
    }
}
