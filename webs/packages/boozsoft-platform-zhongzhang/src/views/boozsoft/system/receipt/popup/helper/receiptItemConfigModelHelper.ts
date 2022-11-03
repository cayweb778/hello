function createBaseRow(){
  return {
    id:'',
    stockName: '',
    stockNum: '',
    unit: '',
    price: '',
    amount: ''
  }
}
function ReceiptItemConfigModelHelper(){

  return  {
    createBaseRow,
    createReceiptItemConfig:function(){
      return {
        count: 0,
        base: {
          id:'',
          uniqueCode: '',
          receCode: '',
          receName: '',
          receClass: '',
          receDate: '',
          buyName: '',
          salesName: '',
          recePsn: '',
          totalAmount: '',
          vouchCode: '',
          table: [
            createBaseRow(),
            createBaseRow()
          ]
        }
      }
    }
  }
}
export default ReceiptItemConfigModelHelper
