<template>
  <div class="app-container">

    <div id="content" style="color:black;min-width:940px;margin-top:-10px;height: 50px;background:inherit;padding-top:5px;position:relative;box-shadow: none;">
      <input type="hidden" id="month" value="004">
      <input type="hidden" id="year" value="2021">
      <input type="hidden" id="max_year" value="2021">
      <input type="hidden" id="min_year" value="2021">
      <div style="float:left;width: 30%;margin-top: 0.8%;">
        <div style="float: left">
          <label class="layui-form-label" style="font-weight: 700;font-family: '微软雅黑';padding: 3px;text-align: center">会计期间:</label>
          <input type="text" class="layui-input" id="test3" placeholder="请选择日期区间" style="width: 100px;height: 25px;text-align: center;border: 1px solid #7b7a7a; border-radius: 4px;padding-left: 0px" lay-key="1">
        </div>
        <div class="selectChoose" style="width: 80px;margin-top: 0.5%;margin-left: 7%;">
          <button class="g-btn-primary" style="cursor: pointer;background-color: #169976;width: 75px;font-size: 14px;float: left" onclick="window.location.reload()">刷新</button>
        </div>
      </div>
      <div style="display: inline-block;text-align: center;position:absolute;left:45%;width: 120px;font-size:18px">
        <div style="display: inline-block;margin-left: 0;    margin-top: 5px;">
          <div align="center">
            <label size="4" style="font-size:22px;color:black;font-weight: bold;">资产负债表</label>
          </div>
        </div>
      </div>

      <div style="float:right;margin-top: 0.8%;">
        <div class="selectChoose" style="width:105px">
          <button class="g-btn-primary" style="cursor: pointer;background-color: #169976;width: 100px;font-size: 14px;float: left">打印&amp;预览</button>
          <ul style="display: none;top: 24px !important;">
            <li id="dataPrintBtn">打印当前</li>
            <li id="dataPrintBatchBtn">批量打印</li>
          </ul>
        </div>
        <div class="selectChoose" style="width:105px">
          <button class="g-btn-primary" style="cursor: pointer;background-color: #169976;width: 100px;font-size: 14px;float: left">报表公式</button>
          <ul style="display: none;top: 24px !important;">
            <li id="system">预置公式</li>
            <li id="copy">复制公式</li>
          </ul>
        </div>
        <div class="selectChoose" style="width:100px">
          <button class="g-btn-primary" id="dataExportBtn" style="cursor: pointer;background-color: #169976;width: 100px;font-size: 14px;float: left">导出Excel</button>
        </div>
      </div>
    </div>

    <table class="layui-table" id="tableId" style="height: calc( 100% - 20px );margin-top: 5px;background-color:#f2f4f6;">
      <thead>
      <tr class="dy_table_thead_tr even">
        <th>选择</th>
        <th>序号</th>
        <th>资产名称</th>
        <th>显示级次</th>
        <th>公式</th>
        <th>取值方式</th>
        <th>行次</th>
        <th>选择</th>
        <th>序号</th>
        <th>负债和所有者权益</th>
        <th>显示级次</th>
        <th>公式</th>
        <th>取值方式</th>
        <th>行次</th>
      </tr>
<!--      <tr class="dy_table_thead_tr even">
        <th style="text-align: center;width: 22%;font-weight: bold;">资产</th>
        <th style="text-align: center;width: 6%;font-weight: bold;">行次</th>
        <th style="text-align: center;font-weight: bold;">期末余额</th>
        <th style="text-align: center;font-weight: bold;">年初余额</th>
        <th style="text-align: center;width: 22%;font-weight: bold;">负债和所有者权益</th>
        <th style="text-align: center;width: 6%;font-weight: bold;">行次</th>
        <th style="text-align: center;font-weight: bold;">期末余额</th>
        <th style="text-align: center;font-weight: bold;">年初余额</th>
      </tr>-->
      </thead>
      <tbody id="tbody" style="height: calc( 100% - 100px );">
      <tr>
        <td></td>
        <td>1</td>
        <td>流动资产</td>
        <td>1</td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
      </tr>
      </tbody>
<!--      <tbody id="tbody" style="height: calc( 100% - 100px );">
      <tr class="odd">
        <td style="width: 22%;text-align: left;"><b>流动资产</b></td>
        <td style="width: 6%"></td>
        <td></td>
        <td></td>
        <td style="width: 22%;text-align: left;"><b>流动负债</b></td>
        <td style="width: 6%"></td>
        <td></td>
        <td></td>
      </tr>
      <tr class="even">


        <td class="indent" style="width: 22%;text-align: left;">货币资金<a onclick="gongshi_pop('1')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%">1</td>
        <td style="text-align: right;">

          1,015,000.00

        </td>
        <td style="text-align: right;">

          15,000.00

        </td>




























































































        <td class="indent" style="width: 22%;text-align: left;">短期借款 <a onclick="gongshi_pop('24')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">31</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>






















































































      </tr>
      <tr class="odd">





        <td class="indent" style="width: 22%;text-align: left;">短期投资<a onclick="gongshi_pop('2')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">2</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>




























































































        <td class="indent" style="width: 22%;text-align: left;">应付票据<a onclick="gongshi_pop('25')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">32</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>



















































































      </tr>
      <tr class="even">








        <td class="indent" style="width: 22%;text-align: left;">应收票据<a onclick="gongshi_pop('3')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">3</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>




























































































        <td class="indent" style="width: 22%;text-align: left;">应付账款<a onclick="gongshi_pop('26')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">33</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>
















































































      </tr>
      <tr class="odd">











        <td class="indent" style="width: 22%;text-align: left;">应收账款<a onclick="gongshi_pop('4')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">4</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>




























































































        <td class="indent" style="width: 22%;text-align: left;">预收账款<a onclick="gongshi_pop('27')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">34</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>













































































      </tr>
      <tr class="even">














        <td class="indent" style="width: 22%;text-align: left;">预付账款<a onclick="gongshi_pop('5')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">5</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>




























































































        <td class="indent" style="width: 22%;text-align: left;">应付职工酬薪<a onclick="gongshi_pop('28')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">35</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>










































































      </tr>
      <tr class="odd">

















        <td class="indent" style="width: 22%;text-align: left;">应收股利<a onclick="gongshi_pop('6')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">6</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>




























































































        <td class="indent" style="width: 22%;text-align: left;">应交税费<a onclick="gongshi_pop('29')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">36</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>







































































      </tr>
      <tr class="even">




















        <td class="indent" style="width: 22%;text-align: left;">应收利息<a onclick="gongshi_pop('7')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">7</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>




























































































        <td class="indent" style="width: 22%;text-align: left;">应付利息<a onclick="gongshi_pop('30')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">37</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>




































































      </tr>
      <tr class="odd">























        <td class="indent" style="width: 22%;text-align: left;">其他应收款<a onclick="gongshi_pop('8')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">8</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>




























































































        <td class="indent" style="width: 22%;text-align: left;">应付利润<a onclick="gongshi_pop('31')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">38</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>

































































      </tr>
      <tr class="even">


























        <td class="indent" style="width: 22%;text-align: left;">存货<a onclick="gongshi_pop('9')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">9</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>




























































































        <td class="indent" style="width: 22%;text-align: left;">其他应付款<a onclick="gongshi_pop('32')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">39</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>






























































      </tr>
      <tr class="odd">





























        <td class="indent" style="width: 22%;text-align: left;text-indent: 30px;">其中：原材料<a onclick="gongshi_pop('153')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">10</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>




























































































        <td class="indent" style="width: 22%;text-align: left;">其他流动负债<a onclick="gongshi_pop('33')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">40</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>



























































      </tr>
      <tr class="even">






















        <td class="indent" style="width: 22%;text-align: left;text-indent: 70px;">在产品<a onclick="gongshi_pop('154')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">11</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>


































































































        <td style="text-align: left;width: 22%;background-color: rgba(229,229,229,0.67);">流动负债合计 <a onclick="total_equation('41','31+32+33+34+35+36+37+38+39+40')" style="cursor: pointer;" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;background-color: rgba(229,229,229,0.67);" title="31+32+33+34+35+36+37+38+39+40">41</td>
        <td style="text-align: right;background-color: rgba(229,229,229,0.67);" title="31+32+33+34+35+36+37+38+39+40">

        </td>
        <td style="text-align: right;background-color: rgba(229,229,229,0.67);" title="31+32+33+34+35+36+37+38+39+40">

        </td>
      </tr>
      <tr class="odd">
























        <td class="indent" style="width: 22%;text-align: left;text-indent: 70px;">库存商品<a onclick="gongshi_pop('155')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">12</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>
































































































        <td style="width: 22%;text-align: left;"><b>非流动负债</b></td>
        <td style="width: 6%"></td>
        <td></td>
        <td></td>
      </tr>
      <tr class="even">






































        <td class="indent" style="width: 22%;text-align: left;text-indent: 70px;">周转材料<a onclick="gongshi_pop('156')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">13</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>

























































































        <td class="indent" style="width: 22%;text-align: left;">长期借款<a onclick="gongshi_pop('34')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">42</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>





















































      </tr>
      <tr class="odd">









































        <td class="indent" style="width: 22%;text-align: left;text-indent: 30px;">其他流动资产<a onclick="gongshi_pop('10')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">14</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>

























































































        <td class="indent" style="width: 22%;text-align: left;">长期应付款<a onclick="gongshi_pop('35')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">43</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>


















































      </tr>
      <tr class="even">
        <td style="background-color: rgba(229,229,229,0.67);text-align: left;width: 22%;">流动资产合计 <a onclick="total_equation('15','1+2+3+4+5+6+7+8+9+14')" style="cursor: pointer;" class="bjgs">编辑公式 </a></td>
        <td style="background-color: rgba(229,229,229,0.67);text-align: center;width: 6%;" title="1+2+3+4+5+6+7+8+9+14">15</td>
        <td style="background-color: rgba(229,229,229,0.67);text-align: right;" title="1+2+3+4+5+6+7+8+9+14">

          1,015,000.00

        </td>
        <td style="background-color: rgba(229,229,229,0.67);text-align: right;" title="1+2+3+4+5+6+7+8+9+14">

          15,000.00

        </td>
























































































        <td class="indent" style="width: 22%;text-align: left;">递延收益<a onclick="gongshi_pop('36')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">44</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>
































      </tr>
      <tr class="odd">
        <td style="width: 22%;text-align: left;"><b>非流动资产</b></td>
        <td style="width: 6%"></td>
        <td></td>
        <td></td>


























































































        <td class="indent" style="width: 22%;text-align: left;">其他非流动负债<a onclick="gongshi_pop('37')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">45</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>






























      </tr>
      <tr class="even">
































        <td class="indent" style="width: 22%;text-align: left;">长期债券投资<a onclick="gongshi_pop('11')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">16</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>
























































































        <td style="width: 22%;text-align: left;background-color: rgba(229,229,229,0.67);">非流动负债合计<a onclick="total_equation('46','42+43+44+45+46')" style="cursor: pointer;" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;background-color: rgba(229,229,229,0.67);" title="42+43+44+45+46">46</td>
        <td style="text-align: right;background-color: rgba(229,229,229,0.67);" title="42+43+44+45+46">

        </td>
        <td style="text-align: right;background-color: rgba(229,229,229,0.67);" title="42+43+44+45+46">

        </td>
      </tr>
      <tr class="odd">


































        <td class="indent" style="width: 22%;text-align: left;">长期股权投资<a onclick="gongshi_pop('12')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">17</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>






















































































        <td style="width: 22%;background-color: rgba(180,180,180,0.67);text-align: left;">负债合计<a onclick="total_equation('47','41+46')" style="cursor: pointer;" class="bjgs">编辑公式</a></td>
        <td style="text-align: center;width: 6%;background-color: rgba(229,229,229,0.67);" title="41+46">47</td>
        <td style="text-align: right;background-color: rgba(229,229,229,0.67);" title="41+46">

        </td>
        <td style="text-align: right;background-color: rgba(229,229,229,0.67);" title="41+46">

        </td>
      </tr>
      <tr class="even">




































        <td class="indent" style="width: 22%;text-align: left;">固定资产原价<a onclick="gongshi_pop('13')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">18</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>




















































































        <td style="width: 22%;text-align: left;"></td>
        <td style="width: 6%"></td>
        <td></td>
        <td></td>
      </tr>
      <tr class="odd">






































        <td class="indent" style="width: 22%;text-align: left;">减：累计折旧<a onclick="gongshi_pop('14')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">19</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>


















































































        <td style="width: 22%;text-align: left;"></td>
        <td style="width: 6%"></td>
        <td></td>
        <td></td>
      </tr>
      <tr class="even">








































        <td class="indent" style="width: 22%;text-align: left;">固定资产账面价值<a onclick="gongshi_pop('15')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">20</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>
















































































        <td style="width: 22%;text-align: left;"></td>
        <td style="width: 6%"></td>
        <td></td>
        <td></td>
      </tr>
      <tr class="odd">










































        <td style="width: 22%;text-align: left;text-indent: 30px;">在建工程<a onclick="gongshi_pop('16')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">21</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>














































































        <td style="width: 22%;text-align: left;"></td>
        <td style="width: 6%"></td>
        <td></td>
        <td></td>
      </tr>
      <tr class="even">












































        <td class="indent" style="width: 22%;text-align: left;">工程物资<a onclick="gongshi_pop('17')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">22</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>












































































        <td style="width: 22%"></td>
        <td style="width: 6%"></td>
        <td></td>
        <td></td>
      </tr>
      <tr class="odd">














































        <td class="indent" style="width: 22%;text-align: left;">固定资产清理<a onclick="gongshi_pop('18')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">23</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>










































































        <td style="width: 22%"></td>
        <td style="width: 6%"></td>
        <td></td>
        <td></td>
      </tr>
      <tr class="even">


















































        <td class="indent" style="width: 22%;text-align: left;">生产性生物资产<a onclick="gongshi_pop('20')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">24</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>






































































        <td style="width: 22%;text-align: left;"><b>所有者权益</b></td>
        <td style="text-align: center;width: 6%;"></td>
        <td></td>
        <td></td>
      </tr>
      <tr class="odd">







































































        <td class="indent" style="width: 22%;text-align: left;">无形资产<a onclick="gongshi_pop('19')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">25</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>










































































        <td class="indent" style="width: 22%;text-align: left;">实收资本（或股本）<a onclick="gongshi_pop('38')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">48</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>



































      </tr>
      <tr class="even">













































































        <td class="indent" style="width: 22%;text-align: left;">开发支出<a onclick="gongshi_pop('21')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">26</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>







































































        <td class="indent" style="width: 22%;text-align: left;">资本公积<a onclick="gongshi_pop('39')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">49</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>
































      </tr>
      <tr class="odd">
















































































        <td class="indent" style="width: 22%;text-align: left;">长期待摊费用<a onclick="gongshi_pop('22')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">27</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>







































































        <td class="indent" style="width: 22%;text-align: left;">盈余公积<a onclick="gongshi_pop('40')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">50</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>





























      </tr>
      <tr class="even">



















































































        <td class="indent" style="width: 22%;text-align: left;">其他非流动资产<a onclick="gongshi_pop('23')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">28</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>







































































        <td class="indent" style="width: 22%;text-align: left;">未分配利润<a onclick="gongshi_pop('41')" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;">51</td>
        <td style="text-align: right;">

        </td>
        <td style="text-align: right;">

        </td>





      </tr>
      <tr style="background-color: rgba(229,229,229,0.67);" class="odd">
        <td style="width: 22%;text-align: left">非流动资产合计<a onclick="total_equation('29','16+17+20+21+22+23+24+25+26+27+28')" style="cursor: pointer;" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;" title="16+17+20+21+22+23+24+25+26+27+28">29</td>
        <td style="text-align: right;" title="16+17+20+21+22+23+24+25+26+27+28">

        </td>
        <td style="text-align: right;" title="16+17+20+21+22+23+24+25+26+27+28">

        </td>
        <td style="width: 22%;text-align: left;background-color: rgba(180,180,180,0.67);">所有者权益合计<a onclick="total_equation('52','48+49+50+51')" style="cursor: pointer;" class="bjgs">编辑公式 </a></td>
        <td style="text-align: center;width: 6%;" title="48+49+50+51">52</td>
        <td style="text-align: right;" title="48+49+50+51">

        </td>
        <td style="text-align: right;" title="48+49+50+51">

        </td>
      </tr>
      <tr class="dy_table_thead_tr even">
        <td style="width: 22%"><b>资产总计</b><a onclick="total_equation('30','15+29')" style="cursor: pointer;" class="bjgs">编辑公式</a></td>
        <td style="width: 6%" title="15+29">30</td>
        <td style="text-align: right;" id="zcTotalALL" title="15+29">

          1,015,000.00

        </td>
        <td style="text-align: right;" title="15000.0">

          15,000.00

        </td>
        <td style="width: 22%" title="47+52"><b>负债和所有者权益总计</b><a onclick="total_equation('53','47+52')" style="cursor: pointer;" class="bjgs">编辑公式</a></td>
        <td style="width: 6%" title="47+52">53</td>
        <td style="text-align: right;" id="fuTotalALL" title="47+52">



        </td>
        <td style="text-align: right;" title="47+52">



        </td>
      </tr>
      </tbody>-->
    </table>


    <div id="copydiv" style="display: none">
      <div style="margin-top: 10px;margin-left: 20px">
        <h2 style="text-align: center"><b>公式复制</b></h2><br>
        <p><b>复制步骤说明：</b></p>
        <p style="margin-left: 26px"><b>第一步,软件将删除并清空当前报表的公式设置</b></p>
        <p style="margin-left: 26px"><b>第二步,软件将判断来源账套会计准则、科目级次与当前账套是否一致</b></p>
        <p style="margin-left: 26px"><b>第三步,软件将判断来源账套公式设置添加到本账套中</b></p><br>

        <div style="margin-left: 30px">
          复制公式来源:
          <select id="copysel" style="width: 200px;height: 30px;border-radius: 10px;">
            <option value="">请选择</option>
          </select>
          <input type="button" class="layui-btn layui-btn-sm" id="copybtn" value="提交">
        </div>
        <label id="error" color="red" style="margin-left: 150px"></label>
      </div>
    </div>
    <div id="choose-box-wrapper4" style="display: none">
      <div class="widget flat radius-bordered" style="padding:0 10px 0;">
        <div style="width: 755px;height: 276px;overflow:auto;" id="helpContent">
        </div>
      </div>
    </div>
    <form class="layui-form" id="testdiv" style="display: none"><br>
      <div style="margin: 15px 0 0">
        <label class="layui-form-label" style="font-weight: 700;font-family: '微软雅黑'">会计期间:</label>
        <div class="layui-input-inline">
          <input type="text" class="layui-input" id="interregional" readonly="readonly" placeholder="请选择打印范围" style="text-align: center;" lay-key="2">
        </div>
      </div>
    </form>


  </div>
</template>
<style src="../../../../assets/styles/global-menu-index-block.less" lang="less" scope>

</style><style lang='less' scoped ></style>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style><style lang='less' scoped ></style>
<style scoped lang="less">
:deep(.ant-card-body) {
  padding: 16px;
  border-left: 2px solid rgb(1, 143, 251);
  box-shadow: rgb(72 113 140) -3px 1px 7px -1px;
}
</style>


<style lang="less" scope>
@import "../../../../assets/styles/layui.less";
@import "../../../../assets/styles/theme.less";
</style>
<style type="text/css">
#content {
  background: #fff;
  padding: 0 2em;
  position: relative;
  z-index: 2;
  -moz-border-radius: 0 5px 5px 5px;
  -webkit-border-radius: 0 5px 5px 5px;
  border-radius: 0 5px 5px 5px;
  -moz-box-shadow: 0 -2px 3px -2px rgba(0, 0, 0, .5);
  -webkit-box-shadow: 0 -2px 3px -2px rgba(0, 0, 0, .5);
  box-shadow: 0 -2px 3px -2px rgba(0, 0, 0, .5);
}
/*body {
  width: 1100px;
  margin: 100px auto 0 auto;
  font-family: Arial, Helvetica;
  font-size: small;
}*/

.even {
  background: #FCFCFC;
}

.odd {
  background: #FFFFFF;
}

td:hover {
  background: none;
}

td .bjgs {
  display: none;
}

td:hover .bjgs {
  display: block;
  position: absolute;
  top: 8px;
  left: 130px;
  /*background-color: whitesmoke;*/
  color: royalblue;
}

/*#tableId > tbody {
  display: block;
  height: 500px;
  overflow-y: scroll;
  overflow-x: hidden;
}*/

#tableId > thead,
#tableId > tbody > tr {
  display: table;
  width: 100%;
  table-layout: fixed; /**表格列的宽度由表格宽度决定，不由内容决定*/
  text-align: center;
}

#tableId th  {
  border: 1px solid #6f696f;
}
#tableId tr td  {
  border: 1px solid #6f696f;
  border-top: hidden;
}

.dy_ul li {
  cursor: pointer;
}

.dy_ul li {
  cursor: pointer;
}
.dy_ul img {
  width: 100%
}

.dy_ul {
  color: white;
}


.dy_ul li {
  cursor: pointer;
  box-sizing: border-box;
  list-style: none;
  display: inline-block;
  width: 81px;
  height: 25px;
  vertical-align: top;
  padding: 0;
  margin: 0;
  margin-right: -5px;
  margin-top: 10px;
  padding: 4px 0 0 0px;
  text-align: center;
}

.dy_ul li:nth-child(even) {
  background: rgb(181, 181, 180);
}

.dy_ul li:nth-child(odd) {
  background: rgb(153, 153, 153);
}
.dy_ul a{
  color:white;
}
a{
  cursor: pointer;
}
/*td缩进*/
.indent{
  text-indent:30px;
}
</style>
