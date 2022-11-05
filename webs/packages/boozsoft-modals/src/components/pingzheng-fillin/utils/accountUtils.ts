export function toAccountNumLength(inoid, accvouchDec) {
  return (parseInt(inoid)).toString().padStart(parseInt(accvouchDec), "0")
}
