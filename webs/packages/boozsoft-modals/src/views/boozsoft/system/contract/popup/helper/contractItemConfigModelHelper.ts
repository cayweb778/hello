function createBaseRow(){
  return {
    id: '',
    conId: '',
    biaodiCount: '',
    biaodiQuality: '',
    biaodiPrice: '',
    tiaokuan: '',
    zeren: '',
    transTerm: '',
    transAddress: '',
    transType: '',
    solution: '',
  }
}
function ContractItemConfigModelHelper(){

  return  {
    createBaseRow,
    createContractItemConfig:function (){
      return {
        count: 0,
        base: {
          id:'',
          conDate: '',
          conNum: '',
          shengxiaoDate: '',
          startDate: '',
          endDate: '',
          conName: '',
          amount: '',
          firstAmount: '',
          transAddress: '',
          transType: '',
          transTerm: '',
          status: '',
          saveAddress: '',
          safekeepPsn: '',
          jiaName: '',
          jiaAddress: '',
          jiaShuihao: '',
          jiaPerson: '',
          jiaPhone: '',
          yiName: '',
          yiAddress: '',
          yiShuihao: '',
          yiPerson: '',
          yiPhone: '',
          bingName: '',
          bingAddress: '',
          bingShuihao: '',
          bingPerson: '',
          bingPhone: '',
          dingName: '',
          dingAddress: '',
          dingShuihao: '',
          dingPerson: '',
          dingPhone: '',
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
export default ContractItemConfigModelHelper
