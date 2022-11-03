import * as fs from 'fs';

const platformEnname='YingShouZhang'
const projectPath = __dirname + '/.'
const arr=[{"enName":"YingShouKuanQiChuYuE","name":"应收款期初余额"},{"enName":"YingShouDan","name":"应收单"},{"enName":"XiaoShouFaPiao","name":"销售发票"},{"enName":"FeiYongDan","name":"费用单"},{"enName":"YingShouDanLieBiao","name":"应收单列表"},{"enName":"XiaoShouFaPiaoLieBiao","name":"销售发票列表"},{"enName":"FeiYongDanLieBiao","name":"费用单列表"},{"enName":"YingShouKuanTongJiBiao","name":"应收款统计表"},{"enName":"XiaoShouFaPiaoTongJiBiao","name":"销售发票统计表"},{"enName":"FeiYongTongJiBiao","name":"费用统计表"},{"enName":"YingShouYuEBiao","name":"应收余额表"},{"enName":"XuanZeShouKuan","name":"选择收款"},{"enName":"YingShouShouKuanDan","name":"应收收款单"},{"enName":"YingShouTuiKuanDan","name":"应收退款单"},{"enName":"ShouKuanDanLieBiao","name":"收款单列表"},{"enName":"TuiKuanDanLieBiao","name":"退款单列表"},{"enName":"ShouKuanMingXiBiao","name":"收款明细表"},{"enName":"ShouKuanTongJiBiao","name":"收款统计表"},{"enName":"ShouGongHeXiao","name":"手工核销"},{"enName":"QuXiaoHeXiao","name":"取消核销"},{"enName":"YingShouHeXiaoMingXiBiao","name":"应收核销明细表"},{"enName":"YuShouChongYingShou","name":"预收冲应收"},{"enName":"YingShouChongYingShou","name":"应收冲应收"},{"enName":"YingShouChongShouFu","name":"应收冲收付"},{"enName":"YuShouChongYingShouLieBiao","name":"预收冲应收列表"},{"enName":"YingShouChongYingShouLieBiao","name":"应收冲应收列表"},{"enName":"YingShouChongShouFuLieBiao","name":"应收冲收付列表"},{"enName":"YingShouKuanZhiDan","name":"应收款制单"},{"enName":"ShouKuanDanZhiDan","name":"收款单制单"},{"enName":"PingZhengLieBiao","name":"凭证列表"},{"enName":"XiaoShouWuLiuDanLieBiao","name":"销售物流单列表"},{"enName":"YingShouYuEBiao","name":"应收余额表"},{"enName":"YingShouZongZhang","name":"应收总账"},{"enName":"YingShouMingXiZhang","name":"应收明细账"},{"enName":"ShouKuanZhiXingBiao","name":"收款执行表"},{"enName":"YingShouZhangLingFenXi","name":"应收账龄分析"},{"enName":"ZiJinYuCeFenXi","name":"资金预测分析"},{"enName":"YueMoJieZhang","name":"月末结账"},{"enName":"QuXiaoJieZhang","name":"取消结账"},{"enName":"XiTongXuanXiang","name":"系统选项"},{"enName":"YeWuLiuCheng","name":"业务流程"},{"enName":"ShouKuanZhangHu","name":"收款账户"},{"enName":"JieSuanFangShi","name":"结算方式"},{"enName":"FeiYongDangAn","name":"费用档案"},{"enName":"YingShouKeMuSheZhi","name":"应收科目设置"},{"enName":"ShouKuanKeMuSheZhi","name":"收款科目设置"},{"enName":"HeXiaoKeMuSheZhi","name":"核销科目设置"}]
    arr.forEach(it=>{

    fs.mkdirSync(projectPath+'/boozsoft/YingShouZhang/'+it.enName, {recursive: true});
    fs.mkdirSync(projectPath+'/boozsoft/YingShouZhang/'+it.enName+'/layouts', {recursive: true});

        fs.appendFile(projectPath+'/boozsoft/YingShouZhang/'+it.enName+'/layouts/RouteCache.vue', `<template>
  <Index></Index>
</template>
<script lang="ts">
import Index from '../index.vue';
import { defineComponent, onActivated, provide } from 'vue';

export default defineComponent({
  name: '${platformEnname}XX${it.enName}',
  components: {
    Index,
  },
  setup() {
  },
  activated() {
  },
  created() {},
});
</script>`, err => {
        });

    fs.appendFile(projectPath+'/boozsoft/YingShouZhang/'+it.enName+'/index.vue', `<template>
        <!-- 请注释下行，不要删除下行，方便快速定位页面  -->
   <div><textarea>${it.name}的页面</textarea>复制内容快速定位页面</div>
</template>
<script setup></script>
<style></style>
`, err => {
    });
})
