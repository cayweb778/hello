<template>
    <BasicModal
      v-bind="$attrs"
      width="600px"
      title="辅助核算编辑"
      loadingTip="数据初始化中"
      :loading="startLoading"
      :showOkBtn="!startLoading"
      :canFullscreen="false"
      @ok="handleOk()"
      @cancel="handleClose()"
      @register="register"
    >
      <div v-if="acccolumns.filter(item=>item.dataIndex == 'dbilldate' || item.dataIndex == 'inoid').length > 0 && containsThree">
        <template v-if="acccolumns.filter(item=>item.dataIndex == 'dbilldate').length > 0">
          <span>凭证日期:</span>
          <a-date-picker
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            v-model:value="thisRow['dbilldate']"
            style="width: 25%;margin-left: 2em;"
          />
        </template>
        <template v-if="acccolumns.filter(item=>item.dataIndex == 'inoid').length > 0">
          <span style="margin-left: 2em;">凭证号:&emsp;</span>
          <a-input
            v-model:value="thisRow['inoid']"
            style="width: 25%;margin-left: 2em;"
            @change="changeMinInoId(thisRow.caozuo)"
            type="number"
            min="1"
            max="9999999"
          />
        </template>
      </div>
      <div v-if="acccolumns.filter(item=>item.dataIndex == 'cdigest').length > 0 && containsThree">
        <span>摘要:&emsp;&emsp;</span>
        <a-input
          v-model:value="thisRow['cdigest']"
          :maxlength="100"
          style="width: 72%;margin-left: 2em"
        />
      </div>
      <Divider style="height: 1px; background-color: #0096c7" v-if="containsThree"/>
      <div v-if="acccolumns.filter(item=>item.dataIndex == 'md' || item.dataIndex == 'mc').length > 0">
        <template v-if="acccolumns.filter(item=>item.dataIndex == 'md').length > 0">
          <span>本币借方金额:</span>
          <a-input v-model:value="thisRow['md']" style="width: 22%;margin-left: 2em;text-align: right;" :step="0.01" />
        </template>
        <template v-if="acccolumns.filter(item=>item.dataIndex == 'mc').length > 0">
          <span  style="margin-left: 2em;">本币贷方金额:</span>
          <a-input v-model:value="thisRow['mc']" style="width: 22%;margin-left: 2em;text-align: right;" :step="0.01" />
        </template>
      </div>
      <div v-if="acccolumns.filter(item=>item.dataIndex == 'ljMd' || item.dataIndex == 'ljMc').length > 0">
        <template v-if="acccolumns.filter(item=>item.dataIndex == 'ljMd').length > 0">
          <span>累计借方金额:</span>
          <a-input
            v-model:value="thisRow['ljMd']"
            style="width: 22%;margin-left: 2em;text-align: right;"
            :step="0.01"
          />
        </template>
        <template v-if="acccolumns.filter(item=>item.dataIndex == 'ljMc').length > 0">
          <span  style="margin-left: 2em;">累计贷方金额:</span>
          <a-input
            v-model:value="thisRow['ljMc']"
            style="width: 22%;margin-left: 2em;text-align: right;"
            :step="0.01"
          />
        </template>
      </div>
      <Divider style="height: 1px; background-color: #0096c7" v-if="acccolumns.filter(item=>item.dataIndex == 'bnum').length > 0"/>
      <div v-if="acccolumns.filter(item=>item.dataIndex == 'bnum' || item.dataIndex == 'bnum2').length > 0">
        <template v-if="acccolumns.filter(item=>item.dataIndex == 'bnum').length > 0">
          <span>数量：&emsp;&emsp;</span>
          <a-input
            v-model:value="thisRow['bnum']"
            style="width: 25%;margin-left: 2em;"
            @change="changeBNum(thisRow.caozuo)"
          />
        </template>
      </div>
      <div v-if="acccolumns.filter(item=>item.dataIndex == 'ljSlMd' || item.dataIndex == 'ljSlMc').length > 0">
        <template v-if="acccolumns.filter(item=>item.dataIndex == 'ljSlMd').length > 0">
          <span>累计借方数量：</span>
          <a-input
            v-model:value="thisRow['ljSlMd']"
            style="width: 22%;margin-left: 1em;"
          />
        </template>
        <template v-if="acccolumns.filter(item=>item.dataIndex == 'ljSlMc').length > 0">
          <span style="margin-left: 2em;">累计贷方数量：</span>
          <a-input
            v-model:value="thisRow['ljSlMc']"
            style="width: 22%;margin-left: 2em;"
          />
        </template>
      </div>
      <Divider style="height: 1px; background-color: #0096c7" v-if="acccolumns.filter(item=>item.dataIndex == 'currency').length > 0"/>
      <div v-if="acccolumns.filter(item=>item.dataIndex == 'currency' || item.dataIndex == 'currency2').length > 0">
        <template v-if="acccolumns.filter(item=>item.dataIndex == 'currency').length > 0">
          <span>外币金额:</span>
          <a-input
            v-model:value="thisRow['currency']"
            style="width: 25%;margin-left: 2em;"
          />
        </template>
      </div>
      <div v-if="acccolumns.filter(item=>item.dataIndex == 'ljWbMd' || item.dataIndex == 'ljWbMc').length > 0">
        <template v-if="acccolumns.filter(item=>item.dataIndex == 'ljWbMd').length > 0">
          <span>累计外币借方:</span>
          <a-input
            v-model:value="thisRow['ljWbMd']"
            style="width: 22%;margin-left: 2em;"
          />
        </template>
        <template v-if="acccolumns.filter(item=>item.dataIndex == 'ljWbMc').length > 0">
          <span style="margin-left: 2em;">累计外币贷方:</span>
          <a-input
            v-model:value="thisRow['ljWbMc']"
            style="width: 22%;margin-left: 2em;"
          />
        </template>
      </div>
      <Divider style="height: 1px; background-color: #0096c7"/>
      <div v-if="acccolumns.filter(item=>item.dataIndex == 'bperson').length > 0">
        <span>{{ acccolumns.filter(item => item.dataIndex == 'bperson')[0]['title'] }}:&emsp;&emsp;</span>
        <a-select
          v-model:value="thisRow['bperson']"
          show-search
          placeholder="请选择人员"
          :filter-option="filterOption"
          style="width:72%;margin-left: 2em;"
          :options="bpersonList"
          @change="handleChange"
        >
        </a-select>
      </div>
      <div v-if="acccolumns.filter(item=>item.dataIndex == 'bdept').length > 0">
        <span>{{ acccolumns.filter(item => item.dataIndex == 'bdept')[0]['title'] }}:&emsp;&emsp;</span>
        <a-select
          v-model:value="thisRow['bdept']"
          show-search
          placeholder="请选择部门"
          :filter-option="filterOption"
          style="width:72%;margin-left: 2em;"
          :options="bdeptList"
          @change="handleChange"
        >
        </a-select>
      </div>
      <div v-if="acccolumns.filter(item=>item.dataIndex == 'bcus').length > 0">
        <span>{{ acccolumns.filter(item => item.dataIndex == 'bcus')[0]['title'] }}:&emsp;&emsp;</span>
        <a-select
          v-model:value="thisRow[ 'bcus']"
          show-search
          placeholder="请选择客户"
          :filter-option="filterOption"
          style="width:72%;margin-left: 2em;"
          :options="bcusList"
        >
        </a-select>
      </div>
      <div v-if="acccolumns.filter(item=>item.dataIndex == 'bsup').length > 0">
        <span>{{ acccolumns.filter(item => item.dataIndex == 'bsup')[0]['title'] }}:&emsp;</span>
        <a-select
          v-model:value="thisRow['bsup']"
          show-search
          :filter-option="filterOption"
          placeholder="请选择供应商"
          style="width:72%;margin-left: 2em;"
          :options="bsupList"
        >
        </a-select>
      </div>
      <div v-if="acccolumns.filter(item=>item.dataIndex == 'bitemClass').length > 0">
        <span>{{ acccolumns.filter(item => item.dataIndex == 'bitemClass')[0]['title'] }}:&emsp;&emsp;</span>
        <a-select
          v-model:value="thisRow['bitemClass']"
          show-search
          :filter-option="filterOption"
          placeholder="请选择项目大类"
          style="width:72%;margin-left: 2em;"
          :options="bitemClassList"
          @change="handleChangeBitemClass(thisRow.caozuo)"
        >
        </a-select>
      </div>
      <div v-if="acccolumns.filter(item=>item.dataIndex == 'bitem').length > 0">
        <span>{{ acccolumns.filter(item => item.dataIndex == 'bitem')[0]['title'] }}:&emsp;&emsp;</span>
        <a-select
          v-model:value="thisRow['bitem']"
          show-search
          placeholder="请选择项目"
          :filter-option="filterOption"
          style="width:72%;margin-left: 2em;"
          :options="bitemList"
          @change="handleChangeBitem(thisRow.caozuo)"
        >
        </a-select>
      </div>
      <template v-for="col in ['cdfine1']"
                v-if="acccolumns.filter(item=>item.dataIndex == 'cdfine1').length > 0">
        <div><span>{{ acccolumns.filter(item => item.dataIndex == col)[0]['title'] }}:</span>
          <a-select v-model:value="thisRow[col]" show-search :filter-option="filterOption"
                    placeholder="请选择" style="width:72%;margin-left: 2em;"
                    :options="cdfine1List"></a-select>
        </div>
      </template>
      <template v-for="col in ['cdfine2']"
                v-if="acccolumns.filter(item=>item.dataIndex == 'cdfine2').length > 0">
        <div><span>{{ acccolumns.filter(item => item.dataIndex == col)[0]['title'] }}:</span>
          <a-select v-model:value="thisRow[col]" show-search :filter-option="filterOption"
                    placeholder="请选择" style="width:72%;margin-left: 2em;"
                    :options="cdfine2List"></a-select>
        </div>
      </template>
      <template v-for="col in ['cdfine3']"
                v-if="acccolumns.filter(item=>item.dataIndex == 'cdfine3').length > 0">
        <div><span>{{ acccolumns.filter(item => item.dataIndex == col)[0]['title'] }}:</span>
          <a-select v-model:value="thisRow[col]" show-search :filter-option="filterOption"
                    placeholder="请选择" style="width:72%;margin-left: 2em;"
                    :options="cdfine3List"></a-select>
        </div>
      </template>
      <template v-for="col in ['cdfine4']"
                v-if="acccolumns.filter(item=>item.dataIndex == 'cdfine4').length > 0">
        <div><span>{{ acccolumns.filter(item => item.dataIndex == col)[0]['title'] }}:</span>
          <a-select v-model:value="thisRow[col]" show-search :filter-option="filterOption"
                    placeholder="请选择" style="width:72%;margin-left: 2em;"
                    :options="cdfine4List"></a-select>
        </div>
      </template>
      <template v-for="col in ['cdfine5']"
                v-if="acccolumns.filter(item=>item.dataIndex == 'cdfine5').length > 0">
        <div><span>{{ acccolumns.filter(item => item.dataIndex == col)[0]['title'] }}:</span>
          <a-select v-model:value="thisRow[col]" show-search :filter-option="filterOption"
                    placeholder="请选择" style="width:72%;margin-left: 2em;"
                    :options="cdfine5List"></a-select>
        </div>
      </template>
      <template v-for="col in ['cdfine6']"
                v-if="acccolumns.filter(item=>item.dataIndex == 'cdfine6').length > 0">
        <div><span>{{ acccolumns.filter(item => item.dataIndex == col)[0]['title'] }}:</span>
          <a-select v-model:value="thisRow[col]" show-search :filter-option="filterOption"
                    placeholder="请选择" style="width:72%;margin-left: 2em;"
                    :options="cdfine6List"></a-select>
        </div>
      </template>
      <template v-for="col in ['cdfine7']"
                v-if="acccolumns.filter(item=>item.dataIndex == 'cdfine7').length > 0">
        <div><span>{{ acccolumns.filter(item => item.dataIndex == col)[0]['title'] }}:</span>
          <a-select v-model:value="thisRow[col]" show-search :filter-option="filterOption"
                    placeholder="请选择" style="width:72%;margin-left: 2em;"
                    :options="cdfine7List"></a-select>
        </div>
      </template>
      <template v-for="col in ['cdfine8']"
                v-if="acccolumns.filter(item=>item.dataIndex == 'cdfine8').length > 0">
        <div><span>{{ acccolumns.filter(item => item.dataIndex == col)[0]['title'] }}:</span>
          <a-select v-model:value="thisRow[col]" show-search :filter-option="filterOption"
                    placeholder="请选择" style="width:72%;margin-left: 2em;"
                    :options="cdfine8List"></a-select>
        </div>
      </template>
      <template v-for="col in ['cdfine9']"
                v-if="acccolumns.filter(item=>item.dataIndex == 'cdfine9').length > 0">
        <div><span>{{ acccolumns.filter(item => item.dataIndex == col)[0]['title'] }}:</span>
          <a-select v-model:value="thisRow[col]" show-search :filter-option="filterOption"
                    placeholder="请选择" style="width:72%;margin-left: 2em;"
                    :options="cdfine9List"></a-select>
        </div>
      </template>

      <template v-for="col in ['cdfine10']"
                v-if="acccolumns.filter(item=>item.dataIndex == 'cdfine10').length > 0">
        <div><span>{{ acccolumns.filter(item => item.dataIndex == col)[0]['title'] }}:</span>
          <a-select v-model:value="thisRow[col]" show-search :filter-option="filterOption"
                    placeholder="请选择" style="width:72%;margin-left: 2em;"
                    :options="cdfine10List"></a-select>
        </div>
      </template>
      <template v-for="col in ['cdfine11']"
                v-if="acccolumns.filter(item=>item.dataIndex == 'cdfine11').length > 0">
        <div><span>{{ acccolumns.filter(item => item.dataIndex == col)[0]['title'] }}:</span>
          <a-select v-model:value="thisRow[col]" show-search :filter-option="filterOption"
                    placeholder="请选择" style="width:72%;margin-left: 2em;"
                    :options="cdfine11List"></a-select>
        </div>
      </template>
      <template v-for="col in ['cdfine12']"
                v-if="acccolumns.filter(item=>item.dataIndex == 'cdfine12').length > 0">
        <div><span>{{ acccolumns.filter(item => item.dataIndex == col)[0]['title'] }}:</span>
          <a-select v-model:value="thisRow[col]" show-search :filter-option="filterOption"
                    placeholder="请选择" style="width:72%;margin-left: 2em;"
                    :options="cdfine12List"></a-select>
        </div>
      </template>
      <template v-for="col in ['cdfine13']"
                v-if="acccolumns.filter(item=>item.dataIndex == 'cdfine13').length > 0">
        <div><span>{{ acccolumns.filter(item => item.dataIndex == col)[0]['title'] }}:</span>
          <a-select v-model:value="thisRow[col]" show-search :filter-option="filterOption"
                    placeholder="请选择" style="width:72%;margin-left: 2em;"
                    :options="cdfine13List"></a-select>
        </div>
      </template>
      <template v-for="col in ['cdfine14']"
                v-if="acccolumns.filter(item=>item.dataIndex == 'cdfine14').length > 0">
        <div><span>{{ acccolumns.filter(item => item.dataIndex == col)[0]['title'] }}:</span>
          <a-select v-model:value="thisRow[col]" show-search :filter-option="filterOption"
                    placeholder="请选择" style="width:72%;margin-left: 2em;"
                    :options="cdfine14List"></a-select>
        </div>
      </template>
      <template v-for="col in ['cdfine15']"
                v-if="acccolumns.filter(item=>item.dataIndex == 'cdfine15').length > 0">
        <div><span>{{ acccolumns.filter(item => item.dataIndex == col)[0]['title'] }}:</span>
          <a-select v-model:value="thisRow[col]" show-search :filter-option="filterOption"
                    placeholder="请选择" style="width:72%;margin-left: 2em;"
                    :options="cdfine15List"></a-select>
        </div>
      </template>
      <template v-for="col in ['cdfine16']"
                v-if="acccolumns.filter(item=>item.dataIndex == 'cdfine16').length > 0">
        <div><span>{{ acccolumns.filter(item => item.dataIndex == col)[0]['title'] }}:</span>
          <a-select v-model:value="thisRow[col]" show-search :filter-option="filterOption"
                    placeholder="请选择" style="width:72%;margin-left: 2em;"
                    :options="cdfine16List"></a-select>
        </div>
      </template>
      <template v-for="col in ['cdfine17']"
                v-if="acccolumns.filter(item=>item.dataIndex == 'cdfine17').length > 0">
        <div><span>{{ acccolumns.filter(item => item.dataIndex == col)[0]['title'] }}:</span>
          <a-select v-model:value="thisRow[col]" show-search :filter-option="filterOption"
                    placeholder="请选择" style="width:72%;margin-left: 2em;"
                    :options="cdfine17List"></a-select>
        </div>
      </template>
      <template v-for="col in ['cdfine18']"
                v-if="acccolumns.filter(item=>item.dataIndex == 'cdfine18').length > 0">
        <div><span>{{ acccolumns.filter(item => item.dataIndex == col)[0]['title'] }}:</span>
          <a-select v-model:value="thisRow[col]" show-search :filter-option="filterOption"
                    placeholder="请选择" style="width:72%;margin-left: 2em;"
                    :options="cdfine18List"></a-select>
        </div>
      </template>
      <template v-for="col in ['cdfine19']"
                v-if="acccolumns.filter(item=>item.dataIndex == 'cdfine19').length > 0">
        <div><span>{{ acccolumns.filter(item => item.dataIndex == col)[0]['title'] }}:</span>
          <a-select v-model:value="thisRow[col]" show-search :filter-option="filterOption"
                    placeholder="请选择" style="width:72%;margin-left: 2em;"
                    :options="cdfine19List"></a-select>
        </div>
      </template>

      <template v-for="col in ['cdfine20']"
                v-if="acccolumns.filter(item=>item.dataIndex == 'cdfine20').length > 0">
        <div><span>{{ acccolumns.filter(item => item.dataIndex == col)[0]['title'] }}:</span>
          <a-select v-model:value="thisRow[col]" show-search :filter-option="filterOption"
                    placeholder="请选择" style="width:72%;margin-left: 2em;"
                    :options="cdfine20List"></a-select>
        </div>
      </template>
      <template v-for="col in ['cdfine21']"
                v-if="acccolumns.filter(item=>item.dataIndex == 'cdfine21').length > 0">
        <div><span>{{ acccolumns.filter(item => item.dataIndex == col)[0]['title'] }}:</span>
          <a-select v-model:value="thisRow[col]" show-search :filter-option="filterOption"
                    placeholder="请选择" style="width:72%;margin-left: 2em;"
                    :options="cdfine21List"></a-select>
        </div>
      </template>
      <template v-for="col in ['cdfine22']"
                v-if="acccolumns.filter(item=>item.dataIndex == 'cdfine22').length > 0">
        <div><span>{{ acccolumns.filter(item => item.dataIndex == col)[0]['title'] }}:</span>
          <a-select v-model:value="thisRow[col]" show-search :filter-option="filterOption"
                    placeholder="请选择" style="width:72%;margin-left: 2em;"
                    :options="cdfine22List"></a-select>
        </div>
      </template>
      <template v-for="col in ['cdfine23']"
                v-if="acccolumns.filter(item=>item.dataIndex == 'cdfine23').length > 0">
        <div><span>{{ acccolumns.filter(item => item.dataIndex == col)[0]['title'] }}:</span>
          <a-select v-model:value="thisRow[col]" show-search :filter-option="filterOption"
                    placeholder="请选择" style="width:72%;margin-left: 2em;"
                    :options="cdfine23List"></a-select>
        </div>
      </template>
      <template v-for="col in ['cdfine24']"
                v-if="acccolumns.filter(item=>item.dataIndex == 'cdfine24').length > 0">
        <div><span>{{ acccolumns.filter(item => item.dataIndex == col)[0]['title'] }}:</span>
          <a-select v-model:value="thisRow[col]" show-search :filter-option="filterOption"
                    placeholder="请选择" style="width:72%;margin-left: 2em;"
                    :options="cdfine24List"></a-select>
        </div>
      </template>
      <template v-for="col in ['cdfine25']"
                v-if="acccolumns.filter(item=>item.dataIndex == 'cdfine25').length > 0">
        <div><span>{{ acccolumns.filter(item => item.dataIndex == col)[0]['title'] }}:</span>
          <a-select v-model:value="thisRow[col]" show-search :filter-option="filterOption"
                    placeholder="请选择" style="width:72%;margin-left: 2em;"
                    :options="cdfine25List"></a-select>
        </div>
      </template>
      <template v-for="col in ['cdfine26']"
                v-if="acccolumns.filter(item=>item.dataIndex == 'cdfine26').length > 0">
        <div><span>{{ acccolumns.filter(item => item.dataIndex == col)[0]['title'] }}:</span>
          <a-select v-model:value="thisRow[col]" show-search :filter-option="filterOption"
                    placeholder="请选择" style="width:72%;margin-left: 2em;"
                    :options="cdfine26List"></a-select>
        </div>
      </template>
      <template v-for="col in ['cdfine27']"
                v-if="acccolumns.filter(item=>item.dataIndex == 'cdfine27').length > 0">
        <div><span>{{ acccolumns.filter(item => item.dataIndex == col)[0]['title'] }}:</span>
          <a-select v-model:value="thisRow[col]" show-search :filter-option="filterOption"
                    placeholder="请选择" style="width:72%;margin-left: 2em;"
                    :options="cdfine27List"></a-select>
        </div>
      </template>
      <template v-for="col in ['cdfine28']"
                v-if="acccolumns.filter(item=>item.dataIndex == 'cdfine28').length > 0">
        <div><span>{{ acccolumns.filter(item => item.dataIndex == col)[0]['title'] }}:</span>
          <a-select v-model:value="thisRow[col]" show-search :filter-option="filterOption"
                    placeholder="请选择" style="width:72%;margin-left: 2em;"
                    :options="cdfine28List"></a-select>
        </div>
      </template>
      <template v-for="col in ['cdfine29']"
                v-if="acccolumns.filter(item=>item.dataIndex == 'cdfine29').length > 0">
        <div><span>{{ acccolumns.filter(item => item.dataIndex == col)[0]['title'] }}:</span>
          <a-select v-model:value="thisRow[col]" show-search :filter-option="filterOption"
                    placeholder="请选择" style="width:72%;margin-left: 2em;"
                    :options="cdfine29List"></a-select>
        </div>
      </template>
      <template v-for="col in ['cdfine30']"
                v-if="acccolumns.filter(item=>item.dataIndex == 'cdfine30').length > 0">
        <div><span>{{ acccolumns.filter(item => item.dataIndex == col)[0]['title'] }}:</span>
          <a-select v-model:value="thisRow[col]" show-search :filter-option="filterOption"
                    placeholder="请选择" style="width:72%;margin-left: 2em;"
                    :options="cdfine30List"></a-select>
        </div>
      </template>
    </BasicModal>
</template>
<script setup lang="ts">
import {BasicModal, useModal, useModalInner} from '/@/components/Modal'
import {cloneDeep} from "lodash-es";
import {
  defineComponent,
  reactive,
  ref,
  toRefs,
  UnwrapRef,
  computed,
  watch,
  unref,
  inject,
  provide
} from "vue";

const {
  createErrorModal,
  createSuccessModal,
  createConfirm
} = useMessage()

import {
  Drawer as ADrawer,
  DatePicker as ADatePicker,
  Table as ATable,
  Select as ASelect,
  Input as AInput,
  Modal as AModal,
  Row as ARow,
  Col as ACol,Divider,
  Statistic as AStatistic
} from 'ant-design-vue';
import {findKeyLabelAll} from "/@/api/record/system/fuZhuHeSuan";
import {deleteList} from "/@/api/record/system/task";
import {useMessage} from "/@/hooks/web/useMessage";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {
  delInitalBalance,
  saveSubjectInitialBalance,
  delInitalBalanceFuZhu,
  saveSubjectInitialBalanceFuZhu
} from "/@/api/subjectInitialBalance/subjectInitialBalance";
import {getCurrentAccountName, hasBlank} from "/@/api/task-api/tast-bus-api";
import {useUserStoreWidthOut} from "/@/store/modules/user";
import {NumberTool} from "/@/api/task-api/tools/universal-tools";

const modalVisible = ref(false);

const [registerOnePage, {openModal: openOnePage}] = useModal()

const emit = defineEmits(['close','register'])

const tenantId = ref("");
const currentAccountName = ref("");
const iyear = ref("");

const accflag = ref(false);

const bperson = ref('0');
const maxDate = ref('');
const bdept = ref('0');
const bcus = ref('0');
const bsup = ref('0');
const bitem = ref('0');

const bnum = ref('0');
const currency = ref('0');

const cdfine1 = ref('0');
const cdfine2 = ref('0');
const cdfine3 = ref('0');
const cdfine4 = ref('0');
const cdfine5 = ref('0');
const cdfine6 = ref('0');
const cdfine7 = ref('0');
const cdfine8 = ref('0');
const cdfine9 = ref('0');
const cdfine10 = ref('0');
const cdfine11 = ref('0');
const cdfine12 = ref('0');
const cdfine13 = ref('0');
const cdfine14 = ref('0');
const cdfine15 = ref('0');
const cdfine16 = ref('0');
const cdfine17 = ref('0');
const cdfine18 = ref('0');
const cdfine19 = ref('0');
const cdfine20 = ref('0');
const cdfine21 = ref('0');
const cdfine22 = ref('0');
const cdfine23 = ref('0');
const cdfine24 = ref('0');
const cdfine25 = ref('0');
const cdfine26 = ref('0');
const cdfine27 = ref('0');
const cdfine28 = ref('0');
const cdfine29 = ref('0');
const cdfine30 = ref('0');

const bpersonList = ref([]);
const bdeptList = ref([]);
const bcusList = ref([]);
const bsupList = ref([]);
const bitemListBig = ref([]);
const bitemList = ref([]);
const bitemClassList = ref([]);

const cdfine1List = ref([]);
const cdfine2List = ref([]);
const cdfine3List = ref([]);
const cdfine4List = ref([]);
const cdfine5List = ref([]);
const cdfine6List = ref([]);
const cdfine7List = ref([]);
const cdfine8List = ref([]);
const cdfine9List = ref([]);
const cdfine10List = ref([]);
const cdfine11List = ref([]);
const cdfine12List = ref([]);
const cdfine13List = ref([]);
const cdfine14List = ref([]);
const cdfine15List = ref([]);
const cdfine16List = ref([]);
const cdfine17List = ref([]);
const cdfine18List = ref([]);
const cdfine19List = ref([]);
const cdfine20List = ref([]);
const cdfine21List = ref([]);
const cdfine22List = ref([]);
const cdfine23List = ref([]);
const cdfine24List = ref([]);
const cdfine25List = ref([]);
const cdfine26List = ref([]);
const cdfine27List = ref([]);
const cdfine28List = ref([]);
const cdfine29List = ref([]);
const cdfine30List = ref([]);

const startLoading = ref(false);

const baocunflag = ref(false);
const containsThree = ref(false);
const fuzhus = ref([]);
const fzhsList = ref([]);
const ccode = ref("");
const ccodeName = ref("");
const yeFx = ref("借");
const wbCurrenry = ref("");

const acccolumns = ref([])

const userinfo = ref({
  username: useUserStoreWidthOut().getUserInfo.username,
  realName: useUserStoreWidthOut().getUserInfo.realName,
  name: useUserStoreWidthOut().getUserInfo.name,
  phone: useUserStoreWidthOut().getUserInfo.phone,
});
const props = defineProps(['database'])
const datasourcePicker = inject('datasourcePicker')
const datasourceLock = inject('datasourceLock')
const usePageRouterApi = inject('usePageRouterApi')

const accli = ref([]);
const thisRow = ref({});
const stauts = ref('add');
const [register, {closeModal}] = useModalInner(async (data) => {
  startLoading.value = true
  stauts.value = data.type
  thisRow.value = data.row
  accli.value = data.accList
  acccolumns.value = data.showCol
  tenantId.value = data.map.kemuCode.tenantId;
  bnum.value = data.map.kemuCode.bnum == '1' ? '1' : '0';
  currency.value = data.map.kemuCode.currency == '1' ? '1' : '0';

  bperson.value = data.map.kemuCode.bperson == '1' ? '1' : '0';
  bdept.value = data.map.kemuCode.bdept == '1' ? '1' : '0';
  bcus.value = data.map.kemuCode.bcus == '1' ? '1' : '0';
  bsup.value = data.map.kemuCode.bsup == '1' ? '1' : '0';
  bitem.value = data.map.kemuCode.bitem == '1' ? '1' : '0';

  cdfine1.value = data.map.kemuCode.cdfine1 == '1' ? '1' : '0';
  cdfine2.value = data.map.kemuCode.cdfine2 == '1' ? '1' : '0';
  cdfine3.value = data.map.kemuCode.cdfine3 == '1' ? '1' : '0';
  cdfine4.value = data.map.kemuCode.cdfine4 == '1' ? '1' : '0';
  cdfine5.value = data.map.kemuCode.cdfine5 == '1' ? '1' : '0';
  cdfine6.value = data.map.kemuCode.cdfine6 == '1' ? '1' : '0';
  cdfine7.value = data.map.kemuCode.cdfine7 == '1' ? '1' : '0';
  cdfine8.value = data.map.kemuCode.cdfine8 == '1' ? '1' : '0';
  cdfine9.value = data.map.kemuCode.cdfine9 == '1' ? '1' : '0';
  cdfine10.value = data.map.kemuCode.cdfine10 == '1' ? '1' : '0';
  cdfine11.value = data.map.kemuCode.cdfine11 == '1' ? '1' : '0';
  cdfine12.value = data.map.kemuCode.cdfine12 == '1' ? '1' : '0';
  cdfine13.value = data.map.kemuCode.cdfine13 == '1' ? '1' : '0';
  cdfine14.value = data.map.kemuCode.cdfine14 == '1' ? '1' : '0';
  cdfine15.value = data.map.kemuCode.cdfine15 == '1' ? '1' : '0';
  cdfine16.value = data.map.kemuCode.cdfine16 == '1' ? '1' : '0';
  cdfine17.value = data.map.kemuCode.cdfine17 == '1' ? '1' : '0';
  cdfine18.value = data.map.kemuCode.cdfine18 == '1' ? '1' : '0';
  cdfine19.value = data.map.kemuCode.cdfine19 == '1' ? '1' : '0';
  cdfine20.value = data.map.kemuCode.cdfine20 == '1' ? '1' : '0';
  cdfine21.value = data.map.kemuCode.cdfine21 == '1' ? '1' : '0';
  cdfine22.value = data.map.kemuCode.cdfine22 == '1' ? '1' : '0';
  cdfine23.value = data.map.kemuCode.cdfine23 == '1' ? '1' : '0';
  cdfine24.value = data.map.kemuCode.cdfine24 == '1' ? '1' : '0';
  cdfine25.value = data.map.kemuCode.cdfine25 == '1' ? '1' : '0';
  cdfine26.value = data.map.kemuCode.cdfine26 == '1' ? '1' : '0';
  cdfine27.value = data.map.kemuCode.cdfine27 == '1' ? '1' : '0';
  cdfine28.value = data.map.kemuCode.cdfine28 == '1' ? '1' : '0';
  cdfine29.value = data.map.kemuCode.cdfine29 == '1' ? '1' : '0';
  cdfine30.value = data.map.kemuCode.cdfine30 == '1' ? '1' : '0';
  maxDate.value = data.maxDate;
  fuzhus.value  = data.fuzhus.split(',')
  const bbb = await useRouteApi(findKeyLabelAll, {schemaName: props.database})({
    require: data.fuzhus,
    toTarget: "false"
  });
  startLoading.value = false
  /*
  const index1 = acccolumns.value.findIndex(v=>  v.dataIndex === 'dbilldate');
  if(index1 >= 0) {acccolumns.value.splice(index1, 1);}
  const index2 = acccolumns.value.findIndex(v=>  v.dataIndex === 'inoid');
  if(index2 >= 0) {acccolumns.value.splice(index2, 1);}
  */
  containsThree.value = false
  if (bdept.value == '1') {
    bdeptList.value = bbb.filter(item => "fzDept" == item.key)[0].list;
    bdeptList.value.filter(v => v.value = v.key);
  } else {
    const index = acccolumns.value.findIndex(v => v.dataIndex === 'bdept');
    if (index >= 0) {
      acccolumns.value.splice(index, 1);
    }
  }
  if (bperson.value == '1') {
    containsThree.value = true
    bpersonList.value =  bbb.filter(item => "fzEmp" == item.key)[0].list;
    bpersonList.value.filter(v => v.value = v.key);
  } else {
    const index = acccolumns.value.findIndex(v => v.dataIndex === 'bperson');
    if (index >= 0) {
      acccolumns.value.splice(index, 1);
    }
  }
  if (bcus.value == '1') {
    containsThree.value = true
    bcusList.value = bbb.filter(item => "fzCustom" == item.key)[0].list;
    bcusList.value.filter(v => v.value = v.key);
  } else {
    const index = acccolumns.value.findIndex(v => v.dataIndex === 'bcus');
    if (index >= 0) {
      acccolumns.value.splice(index, 1);
    }
  }
  if (bsup.value == '1') {
    containsThree.value = true
    bsupList.value = bbb.filter(item => "fzGys" == item.key)[0].list;
    bsupList.value.filter(v => v.value = v.key);
  } else {
    const index = acccolumns.value.findIndex(v => v.dataIndex === 'bsup');
    if (index >= 0) {
      acccolumns.value.splice(index, 1);
    }
  }
  if (bitem.value == '1') {
    bitemClassList.value = bbb.filter(item => "fzItemClass" == item.key)[0].list;
    bitemListBig.value = bbb.filter(item => "fzItem" == item.key)[0].list;
    bitemList.value = bbb.filter(item => "fzItem" == item.key)[0].list;

    bitemClassList.value.filter(v => v.value = v.key);
    bitemListBig.value.filter(v => v.value = v.key);
    bitemList.value.filter(v => v.value = v.key);

  } else {
    const index1 = acccolumns.value.findIndex(v => v.dataIndex === 'bitemClass');
    if (index1 >= 0) {
      acccolumns.value.splice(index1, 1);
    }
    const index2 = acccolumns.value.findIndex(v => v.dataIndex === 'bitem');
    if (index2 >= 0) {
      acccolumns.value.splice(index2, 1);
    }
  }

  if (cdfine1.value == '1') {cdfine1List.value = bbb.filter(item => 1 === item.key)[0].list;cdfine1List.value.filter(v => v.value = v.key);}
  if (cdfine2.value == '1') {cdfine2List.value = bbb.filter(item => 2 === item.key)[0].list;cdfine2List.value.filter(v => v.value = v.key);}
  if (cdfine3.value == '1') {cdfine3List.value = bbb.filter(item => 3 === item.key)[0].list;cdfine3List.value.filter(v => v.value = v.key);}
  if (cdfine4.value == '1') {cdfine4List.value = bbb.filter(item => 4 === item.key)[0].list;cdfine4List.value.filter(v => v.value = v.key);}
  if (cdfine5.value == '1') {cdfine5List.value = bbb.filter(item => 5 === item.key)[0].list;cdfine5List.value.filter(v => v.value = v.key);}
  if (cdfine6.value == '1') {cdfine6List.value = bbb.filter(item => 6 === item.key)[0].list;cdfine6List.value.filter(v => v.value = v.key);}
  if (cdfine7.value == '1') {cdfine7List.value = bbb.filter(item => 7 === item.key)[0].list;cdfine7List.value.filter(v => v.value = v.key);}
  if (cdfine8.value == '1') {cdfine8List.value = bbb.filter(item => 8 === item.key)[0].list;cdfine8List.value.filter(v => v.value = v.key);}
  if (cdfine9.value == '1') {cdfine9List.value = bbb.filter(item => 9 === item.key)[0].list;cdfine9List.value.filter(v => v.value = v.key);}
  if (cdfine10.value == '1') {cdfine10List.value = bbb.filter(item => 10 === item.key)[0].list;cdfine10List.value.filter(v => v.value = v.key);}
  if (cdfine11.value == '1') {cdfine11List.value = bbb.filter(item => 11 === item.key)[0].list;cdfine11List.value.filter(v => v.value = v.key);}
  if (cdfine12.value == '1') {cdfine12List.value = bbb.filter(item => 12 === item.key)[0].list;cdfine12List.value.filter(v => v.value = v.key);}
  if (cdfine13.value == '1') {cdfine13List.value = bbb.filter(item => 13 === item.key)[0].list;cdfine13List.value.filter(v => v.value = v.key);}
  if (cdfine14.value == '1') {cdfine14List.value = bbb.filter(item => 14 === item.key)[0].list;cdfine14List.value.filter(v => v.value = v.key);}
  if (cdfine15.value == '1') {cdfine15List.value = bbb.filter(item => 15 === item.key)[0].list;cdfine15List.value.filter(v => v.value = v.key);}
  if (cdfine16.value == '1') {cdfine16List.value = bbb.filter(item => 16 === item.key)[0].list;cdfine16List.value.filter(v => v.value = v.key);}
  if (cdfine17.value == '1') {cdfine17List.value = bbb.filter(item => 17 === item.key)[0].list;cdfine17List.value.filter(v => v.value = v.key);}
  if (cdfine18.value == '1') {cdfine18List.value = bbb.filter(item => 18 === item.key)[0].list;cdfine18List.value.filter(v => v.value = v.key);}
  if (cdfine19.value == '1') {cdfine19List.value = bbb.filter(item => 19 === item.key)[0].list;cdfine19List.value.filter(v => v.value = v.key);}
  if (cdfine20.value == '1') {cdfine20List.value = bbb.filter(item => 20 === item.key)[0].list;cdfine20List.value.filter(v => v.value = v.key);}
  if (cdfine21.value == '1') {cdfine21List.value = bbb.filter(item => 21 === item.key)[0].list;cdfine21List.value.filter(v => v.value = v.key);}
  if (cdfine22.value == '1') {cdfine22List.value = bbb.filter(item => 22 === item.key)[0].list;cdfine22List.value.filter(v => v.value = v.key);}
  if (cdfine23.value == '1') {cdfine23List.value = bbb.filter(item => 23 === item.key)[0].list;cdfine23List.value.filter(v => v.value = v.key);}
  if (cdfine24.value == '1') {cdfine24List.value = bbb.filter(item => 24 === item.key)[0].list;cdfine24List.value.filter(v => v.value = v.key);}
  if (cdfine25.value == '1') {cdfine25List.value = bbb.filter(item => 25 === item.key)[0].list;cdfine25List.value.filter(v => v.value = v.key);}
  if (cdfine26.value == '1') {cdfine26List.value = bbb.filter(item => 26 === item.key)[0].list;cdfine26List.value.filter(v => v.value = v.key);}
  if (cdfine27.value == '1') {cdfine27List.value = bbb.filter(item => 27 === item.key)[0].list;cdfine27List.value.filter(v => v.value = v.key);}
  if (cdfine28.value == '1') {cdfine28List.value = bbb.filter(item => 28 === item.key)[0].list;cdfine28List.value.filter(v => v.value = v.key);}
  if (cdfine29.value == '1') {cdfine29List.value = bbb.filter(item => 29 === item.key)[0].list;cdfine29List.value.filter(v => v.value = v.key);}
  if (cdfine30.value == '1') {cdfine30List.value = bbb.filter(item => 30 === item.key)[0].list;cdfine30List.value.filter(v => v.value = v.key);}
});

const shanchu = async (data: any, text) => {
  alert("shanchu" + "123" + "md:" + data.md + "mc:" + data.mc + "id:" + text);
};

async function handleOk() {
  baocun(thisRow.value.caozuo)

}

async function handleClose() {
  startLoading.value = false
  emit('close',{key: thisRow.value.key,type: stauts.value})
  return true;
}


const filterOption = (input: string, option: any) => {
  return option.label.indexOf(input) >= 0;
};


const handleChangeBitem = (caozuoId: string) => {
  let codeList = bitemList.value.filter(v => v.value === thisRow.value.bitem);
  if (codeList.length > 0) {
    let citemClass = codeList[0].itemClass;
    let classList = bitemClassList.value.filter(v => v.value === citemClass);
    if (classList.length > 0) {
      let classnum = classList[0].value;
      thisRow.value.bitemClass = classnum;
    }
  }
}

const handleChangeBitemClass = (caozuoId: string) => {
  //alert(caozuoId + "==" + thisRow.value.bitemClass);
}

const changeMd = (caozuoId: string) => {
  thisRow.value.mc = "";
  thisRow.value.md = NumberTool.inputAmountLimit(thisRow.value.md)+"";
  if (bnum.value == '1') {
    jisuanNumAndNum2(caozuoId);
  }
}

const changeMc = (caozuoId: string) => {
  thisRow.value.md = "";
  thisRow.value.mc = NumberTool.inputAmountLimit(thisRow.value.mc)+"";
  if (bnum.value == '1') {
    jisuanNumAndNum2(caozuoId);
  }
}

// 金额格式化
function money(val: any) {
  if (val == null) val = '';
  val = val.toString().replace(/\$|\,\-/g, '');
  if (isNaN(val)) {
    val = '0';
  }
  let fs = val.indexOf('-') != -1
  const sign = val === (val = Math.abs(val));
  val = Math.floor(val * 100 + 0.50000000001);
  let cents = val % 100;
  val = Math.floor(val / 100).toString();
  if (cents < 10) {
    cents = '0' + cents;
  }
  for (let i = 0; i < Math.floor((val.length - (1 + i)) / 3); i++) {
    val =
      val.substring(0, val.length - (4 * i + 3)) + ',' + val.substring(val.length - (4 * i + 3));
  }
  return (sign ? '' : '') +(fs?'-':'')+ val + '.' + cents;
}

const changeBNum = (caozuoId: string) => {
  thisRow.value.bnum = NumberTool.inputLimit(thisRow.value.bnum)
  thisRow.value.bnum2 = '';
  // jisuanNumAndNum2(caozuoId);
}

const changeBNum2 = (caozuoId: string) => {
  jisuanNumAndNum2(caozuoId);
}

const jisuanNumAndNum2 = (caozuoId: string) => {
  var md = thisRow.value.md;
  var mc = thisRow.value.mc;
  var num = thisRow.value.bnum;
  var num2 = thisRow.value.bnum2;
  if (isNaN(md)) {
    md = 0;
  }
  if (isNaN(mc)) {
    mc = 0;
  }
  if (isNaN(num)) {
    num = 0;
  }
  if (isNaN(num2)) {
    num2 = 0;
  }
  num = parseFloat(num);
  num2 = parseFloat(num2);
  var money = md + mc;
  if (num != 0 && money != 0) {
    thisRow.value.bnum2 = Math.abs((money / num).toFixed(2)+'');
    if (num < 0)  thisRow.value.md =md>0?('-'+thisRow.value.md):('-'+thisRow.value.mc)
    if (isNaN(thisRow.value.bnum2)) {
      thisRow.value.bnum2 = '';
    }
  } else {
    thisRow.value.bnum2 = '';
  }
}

const changecurrency = (caozuoId: string) => {
  thisRow.value.currency = NumberTool.inputAmountLimit(thisRow.value.currency)+"";
}

const changecurrency2 = (caozuoId: string) => {

}

const changeMinInoId = (caozuoId: string) => {

}

const datainfo = ref([]);

const baocun = async (key: string) => {

  var bpersonValue = thisRow.value.bperson;
  var bdeptValue = thisRow.value.bdept;
  var bcusValue = thisRow.value.bcus;
  var bsupValue = thisRow.value.bsup;
  handleChangeBitem(key);
  var bitemClassValue = thisRow.value.bitemClass;
  var bitemValue = thisRow.value.bitem;

  var cdfine1Value = thisRow.value.cdfine1;
  var cdfine2Value = thisRow.value.cdfine2;
  var cdfine3Value = thisRow.value.cdfine3;
  var cdfine4Value = thisRow.value.cdfine4;
  var cdfine5Value = thisRow.value.cdfine5;
  var cdfine6Value = thisRow.value.cdfine6;
  var cdfine7Value = thisRow.value.cdfine7;
  var cdfine8Value = thisRow.value.cdfine8;
  var cdfine9Value = thisRow.value.cdfine9;

  var cdfine10Value = thisRow.value.cdfine10;
  var cdfine11Value = thisRow.value.cdfine11;
  var cdfine12Value = thisRow.value.cdfine12;
  var cdfine13Value = thisRow.value.cdfine13;
  var cdfine14Value = thisRow.value.cdfine14;
  var cdfine15Value = thisRow.value.cdfine15;
  var cdfine16Value = thisRow.value.cdfine16;
  var cdfine17Value = thisRow.value.cdfine17;
  var cdfine18Value = thisRow.value.cdfine18;
  var cdfine19Value = thisRow.value.cdfine19;

  var cdfine20Value = thisRow.value.cdfine20;
  var cdfine21Value = thisRow.value.cdfine21;
  var cdfine22Value = thisRow.value.cdfine22;
  var cdfine23Value = thisRow.value.cdfine23;
  var cdfine24Value = thisRow.value.cdfine24;
  var cdfine25Value = thisRow.value.cdfine25;
  var cdfine26Value = thisRow.value.cdfine26;
  var cdfine27Value = thisRow.value.cdfine27;
  var cdfine28Value = thisRow.value.cdfine28;
  var cdfine29Value = thisRow.value.cdfine29;
  var cdfine30Value = thisRow.value.cdfine30;

  var dbilldateValue = thisRow.value.dbilldate;

  if (dbilldateValue.length == undefined) {
    dbilldateValue = new Date(dbilldateValue);
    dbilldateValue = dbilldateValue.getFullYear() + "-" + (dbilldateValue.getMonth() > 9 ? (dbilldateValue.getMonth() + 1) : "0" + (dbilldateValue.getMonth() + 1)) + "-" + (dbilldateValue.getDate() > 9 ? (dbilldateValue.getDate()) : "0" + (dbilldateValue.getDate()));
    thisRow.value.dbilldate = dbilldateValue;
  }

  var inoidValue = thisRow.value.inoid;
  var cdigestValue = thisRow.value.cdigest;
  var uniqueCodeValue = thisRow.value.uniqueCode;
  var vouchUnCodeValue = thisRow.value.vouchUnCode;
  if (dbilldateValue == undefined || dbilldateValue == '' || dbilldateValue.length != 10 || dbilldateValue > maxDate.value) {
    thisRow.value.dbilldate = maxDate.value;
    dbilldateValue = maxDate.value;
  }

  if (bperson.value == '1' && (bpersonValue == undefined || bpersonValue == '')) {
    showMsg("请选择个人辅助核算");
    return;
  }
  if (bdept.value == '1' && (bdeptValue == undefined || bdeptValue == '')) {
    showMsg("请选择部门辅助核算");
    return;
  }
  if (bcus.value == '1' && (bcusValue == undefined || bcusValue == '')) {
    showMsg("请选择客户辅助核算");
    return;
  }
  if (bsup.value == '1' && (bsupValue == undefined || bsupValue == '')) {
    showMsg("请选择供应商辅助核算");
    return;
  }
  if (bitem.value == '1' && (bitemClassValue == undefined || bitemClassValue == '')) {
    showMsg("请选择项目辅助核算");
    return;
  }
  if (bitem.value == '1' && (bitemValue == undefined || bitemValue == '')) {
    showMsg("请选择项目辅助核算");
    return;
  }
  if (cdfine1.value == '1' && (cdfine1Value == undefined || cdfine1Value == '')) {
    showMsg("请选择" + findZiDingYiName("自定义1") + "辅助核算");
    return;
  }
  if (cdfine2.value == '1' && (cdfine2Value == undefined || cdfine2Value == '')) {
    showMsg("请选择" + findZiDingYiName("自定义2") + "辅助核算");
    return;
  }
  if (cdfine3.value == '1' && (cdfine3Value == undefined || cdfine3Value == '')) {
    showMsg("请选择" + findZiDingYiName("自定义3") + "辅助核算");
    return;
  }
  if (cdfine4.value == '1' && (cdfine4Value == undefined || cdfine4Value == '')) {
    showMsg("请选择" + findZiDingYiName("自定义4") + "辅助核算");
    return;
  }
  if (cdfine5.value == '1' && (cdfine5Value == undefined || cdfine5Value == '')) {
    showMsg("请选择" + findZiDingYiName("自定义5") + "辅助核算");
    return;
  }
  if (cdfine6.value == '1' && (cdfine6Value == undefined || cdfine6Value == '')) {
    showMsg("请选择" + findZiDingYiName("自定义6") + "辅助核算");
    return;
  }
  if (cdfine7.value == '1' && (cdfine7Value == undefined || cdfine7Value == '')) {
    showMsg("请选择" + findZiDingYiName("自定义7") + "辅助核算");
    return;
  }
  if (cdfine8.value == '1' && (cdfine8Value == undefined || cdfine8Value == '')) {
    showMsg("请选择" + findZiDingYiName("自定义8") + "辅助核算");
    return;
  }
  if (cdfine9.value == '1' && (cdfine9Value == undefined || cdfine9Value == '')) {
    showMsg("请选择" + findZiDingYiName("自定义9") + "辅助核算");
    return;
  }
  if (cdfine10.value == '1' && (cdfine10Value == undefined || cdfine10Value == '')) {
    showMsg("请选择" + findZiDingYiName("自定义10") + "辅助核算");
    return;
  }
  if (cdfine11.value == '1' && (cdfine11Value == undefined || cdfine11Value == '')) {
    showMsg("请选择" + findZiDingYiName("自定义11") + "辅助核算");
    return;
  }
  if (cdfine12.value == '1' && (cdfine12Value == undefined || cdfine12Value == '')) {
    showMsg("请选择" + findZiDingYiName("自定义12") + "辅助核算");
    return;
  }
  if (cdfine13.value == '1' && (cdfine13Value == undefined || cdfine13Value == '')) {
    showMsg("请选择" + findZiDingYiName("自定义13") + "辅助核算");
    return;
  }
  if (cdfine14.value == '1' && (cdfine14Value == undefined || cdfine14Value == '')) {
    showMsg("请选择" + findZiDingYiName("自定义14") + "辅助核算");
    return;
  }
  if (cdfine15.value == '1' && (cdfine15Value == undefined || cdfine15Value == '')) {
    showMsg("请选择" + findZiDingYiName("自定义15") + "辅助核算");
    return;
  }
  if (cdfine16.value == '1' && (cdfine16Value == undefined || cdfine16Value == '')) {
    showMsg("请选择" + findZiDingYiName("自定义16") + "辅助核算");
    return;
  }
  if (cdfine17.value == '1' && (cdfine17Value == undefined || cdfine17Value == '')) {
    showMsg("请选择" + findZiDingYiName("自定义17") + "辅助核算");
    return;
  }
  if (cdfine18.value == '1' && (cdfine18Value == undefined || cdfine18Value == '')) {
    showMsg("请选择" + findZiDingYiName("自定义18") + "辅助核算");
    return;
  }
  if (cdfine19.value == '1' && (cdfine19Value == undefined || cdfine19Value == '')) {
    showMsg("请选择" + findZiDingYiName("自定义19") + "辅助核算");
    return;
  }
  if (cdfine20.value == '1' && (cdfine20Value == undefined || cdfine20Value == '')) {
    showMsg("请选择" + findZiDingYiName("自定义20") + "辅助核算");
    return;
  }
  if (cdfine21.value == '1' && (cdfine21Value == undefined || cdfine21Value == '')) {
    showMsg("请选择" + findZiDingYiName("自定义21") + "辅助核算");
    return;
  }
  if (cdfine22.value == '1' && (cdfine22Value == undefined || cdfine22Value == '')) {
    showMsg("请选择" + findZiDingYiName("自定义22") + "辅助核算");
    return;
  }
  if (cdfine23.value == '1' && (cdfine23Value == undefined || cdfine23Value == '')) {
    showMsg("请选择" + findZiDingYiName("自定义23") + "辅助核算");
    return;
  }
  if (cdfine24.value == '1' && (cdfine24Value == undefined || cdfine24Value == '')) {
    showMsg("请选择" + findZiDingYiName("自定义24") + "辅助核算");
    return;
  }
  if (cdfine25.value == '1' && (cdfine25Value == undefined || cdfine25Value == '')) {
    showMsg("请选择" + findZiDingYiName("自定义25") + "辅助核算");
    return;
  }
  if (cdfine26.value == '1' && (cdfine26Value == undefined || cdfine26Value == '')) {
    showMsg("请选择" + findZiDingYiName("自定义26") + "辅助核算");
    return;
  }
  if (cdfine27.value == '1' && (cdfine27Value == undefined || cdfine27Value == '')) {
    showMsg("请选择" + findZiDingYiName("自定义27") + "辅助核算");
    return;
  }
  if (cdfine28.value == '1' && (cdfine28Value == undefined || cdfine28Value == '')) {
    showMsg("请选择" + findZiDingYiName("自定义28") + "辅助核算");
    return;
  }
  if (cdfine29.value == '1' && (cdfine29Value == undefined || cdfine29Value == '')) {
    showMsg("请选择" + findZiDingYiName("自定义29") + "辅助核算");
    return;
  }

  if (cdfine30.value == '1' && (cdfine30Value == undefined || cdfine30Value == '')) {
    showMsg("请选择" + findZiDingYiName("自定义30") + "辅助核算");
    return;
  }
  thisRow.value.md = thisRow.value.md == null? '':thisRow.value.md.replace(/,/g,'')
  thisRow.value.mc = thisRow.value.mc == null? '':thisRow.value.mc.replace(/,/g,'')
  if (isNaN(thisRow.value.md)) {
    thisRow.value.md = "";
  }
  if (isNaN(thisRow.value.mc)) {
    thisRow.value.mc = "";
  }

  if (thisRow.value.md != '') {
    thisRow.value.mc = '';
  }

  if (thisRow.value.md != '') {
    if (parseFloat(thisRow.value.md) == 0.0) {
      thisRow.value.md = '';
    }
  }
  if (thisRow.value.mc != '') {
    if (parseFloat(thisRow.value.mc) == 0.0) {
      thisRow.value.mc = '';
    }
  }

  if (thisRow.value.md != '') {
    thisRow.value.md = money(thisRow.value.md);
  }
  if (thisRow.value.mc != '') {
    thisRow.value.mc = money(thisRow.value.mc);
  }

  var mdValue = thisRow.value.md;
  var mcValue = thisRow.value.mc;
  var ljMdValue = thisRow.value.ljMd ;
  var ljMcValue = thisRow.value.ljMc ;
  var mmdd = 0.0;
  var mmcc = 0.0;

  if (mdValue != '') {
    mmdd = mdValue;
  }
  if (mcValue != '') {
    mmcc = mcValue;
  }

  if (parseFloat(mmdd) + parseFloat(mmcc) == 0.0) {
    showMsg("请填写借贷方金额");
    return;
  }

  if (bnum.value == '1') {
    if (isNaN(thisRow.value.bnum)) {
      thisRow.value.bnum = "";
    }
   /* if (isNaN(thisRow.value.bnum2)) {
      thisRow.value.bnum2 = "";
    }*/
    var bnumValue = thisRow.value.bnum;
    var ljSlMdValue = thisRow.value.ljSlMd;
    var ljSlMcValue = thisRow.value.ljSlMc;
    // var bnum2Value = thisRow.value.bnum2;
    if ((bnumValue == undefined || bnumValue == '')) {
      showMsg("请填写数量");
      return;
    }
    /*if ((bnum2Value == undefined || bnum2Value == '')) {
      showMsg("请填写单价");
      return;
    }*/
    if ((!hasBlank(ljMdValue) && hasBlank(ljSlMdValue)) || !hasBlank(ljMcValue) && hasBlank(ljSlMcValue)){
      showMsg("请填写累计借贷对应方向的数量");
      return;
    }
    if ((parseFloat(ljMdValue) < 0 && parseInt(ljSlMdValue) >0) || (parseFloat(ljMdValue) > 0 && parseInt(ljSlMdValue) <0)
      || (parseFloat(ljMcValue) < 0 && parseInt(ljSlMcValue) >0) || (parseFloat(ljMcValue) > 0 && parseInt(ljSlMcValue) <0)
    ){
      showMsg("累计借贷金额以及对应累计数量方向的值正负保持一致！");
      return;
    }
  }
  if (currency.value == '1') {
    var currencyValue = thisRow.value.currency;
    let cc =  thisRow.value.currency == null? '':thisRow.value.currency.replace(/,/g,'')
    if (isNaN(cc)) {
      thisRow.value.currency = "";
    }
    /*if (isNaN(thisRow.value.currency2)) {
      thisRow.value.currency2 = "";
    }*/

    // var currency2Value = thisRow.value.currency2;
    if ((currencyValue == undefined || currencyValue == '')) {
      showMsg("请填写外币金额");
      return;
    }
    var ljWbMdValue = thisRow.value.ljWbMd;
    var ljWbMcValue = thisRow.value.ljWbMc;
    if ((!hasBlank(ljMdValue) && hasBlank(ljWbMdValue)) || !hasBlank(ljMcValue) && hasBlank(ljWbMcValue)){
      showMsg("请填写累计借贷对应方向的原币金额");
      return;
    }
    if ((parseFloat(ljMdValue) < 0 && parseFloat(ljWbMdValue) >0) || (parseFloat(ljMdValue) > 0 && parseFloat(ljWbMdValue) <0)
      || (parseFloat(ljMcValue) < 0 && parseFloat(ljWbMcValue) >0) || (parseFloat(ljMcValue) > 0 && parseFloat(ljWbMcValue) <0)
    ){
      showMsg("累计借贷金额以及对应累计外币方向的值正负保持一致！");
      return;
    }
  }

  if (bnum.value == '1' && currency.value == '1' && ((parseInt(thisRow.value.bnum) > 0 && parseFloat(thisRow.value.currency) < 0) || (parseInt(thisRow.value.bnum) < 0 && parseFloat(thisRow.value.currency) > 0))){
    showMsg("数量核算与外币核算参数值正负方向必须相同！");
    return;
  }

  datainfo.value = [];

  let mx = {};
  mx["bperson"] = "0";
  mx["bdept"] = "0";
  mx["bcus"] = "0";
  mx["bsup"] = "0";
  mx["bitem"] = "0";
  mx["bitemClass"] = "0";

  mx["bnum"] = "0";
  mx["currency"] = "0";

  mx["ncS"] = "0";
  mx["ndS"] = "0";
  mx["ncnum"] = "0";

  mx["nfratMc"] = "0";
  mx["nfratMd"] = "0";
  mx["nfrat"] = "0";

  mx["cdfine1"] = "0";
  mx["cdfine2"] = "0";
  mx["cdfine3"] = "0";
  mx["cdfine4"] = "0";
  mx["cdfine5"] = "0";
  mx["cdfine6"] = "0";
  mx["cdfine7"] = "0";
  mx["cdfine8"] = "0";
  mx["cdfine9"] = "0";
  mx["cdfine10"] = "0";
  mx["cdfine11"] = "0";
  mx["cdfine12"] = "0";
  mx["cdfine13"] = "0";
  mx["cdfine14"] = "0";
  mx["cdfine15"] = "0";
  mx["cdfine16"] = "0";
  mx["cdfine17"] = "0";
  mx["cdfine18"] = "0";
  mx["cdfine19"] = "0";
  mx["cdfine20"] = "0";
  mx["cdfine21"] = "0";
  mx["cdfine22"] = "0";
  mx["cdfine23"] = "0";
  mx["cdfine24"] = "0";
  mx["cdfine25"] = "0";
  mx["cdfine26"] = "0";
  mx["cdfine27"] = "0";
  mx["cdfine28"] = "0";
  mx["cdfine29"] = "0";
  mx["cdfine30"] = "0";

  mx["dbillDate"] = dbilldateValue;
  mx["inoId"] = inoidValue;
  mx["cdigest"] = cdigestValue;
  mx["uniqueCode"] = uniqueCodeValue;
  mx["vouchUnCode"] = vouchUnCodeValue;

  if (thisRow.value.caozuo > 0) {
    mx["accvouid"] = thisRow.value.caozuo;
  }
  mx["iyear"] = iyear.value;
  mx["ccode"] = ccode.value;
  mx["ccodeName"] = ccodeName.value;

  if (bperson.value == '1') {
    mx["bperson"] = "1";
    mx["cpersonId"] = bpersonValue;
  }
  if (bdept.value == '1') {
    mx["bdept"] = "1";
    mx["cdeptId"] = bdeptValue;
  }
  if (bcus.value == '1') {
    mx["bcus"] = "1";
    mx["ccusId"] = bcusValue;
  }
  if (bsup.value == '1') {
    mx["bsup"] = "1";
    mx["csupId"] = bsupValue;
  }
  if (bitem.value == '1') {
    mx["bitem"] = "1";
    mx["projectId"] = bitemValue;
    mx["projectClassId"] = bitemClassValue;
  }

  //数量核算
  if (bnum.value == '1') {
    mx["bnum"] = "1";
    if (mdValue == '') {
      mx["ncS"] = bnumValue;
    } else {
      mx["ndS"] = bnumValue;
    }
    // mx["ncnum"] = bnum2Value;
  } else {
    mx["bnum"] = "0";
  }

  //外币核算
  if (currency.value == '1') {
    mx["currency"] = "1";
    if (mdValue == '') {
      mx["nfratMc"] = currencyValue;
    } else {
      mx["nfratMd"] = currencyValue;
    }
    // mx["nfrat"] = currency2Value;
  } else {
    mx["currency"] = "0";
  }

  mx["md"] = mdValue;
  mx["mc"] = mcValue;

  if (cdfine1.value == '1') {
    mx["cdfine1"] = "1";
    mx["cdfine1Id"] = cdfine1Value;
  }
  if (cdfine2.value == '1') {
    mx["cdfine2"] = "1";
    mx["cdfine2Id"] = cdfine2Value;
  }
  if (cdfine3.value == '1') {
    mx["cdfine3"] = "1";
    mx["cdfine3Id"] = cdfine3Value;
  }
  if (cdfine4.value == '1') {
    mx["cdfine4"] = "1";
    mx["cdfine4Id"] = cdfine4Value;
  }
  if (cdfine5.value == '1') {
    mx["cdfine5"] = "1";
    mx["cdfine5Id"] = cdfine5Value;
  }
  if (cdfine6.value == '1') {
    mx["cdfine6"] = "1";
    mx["cdfine6Id"] = cdfine6Value;
  }
  if (cdfine7.value == '1') {
    mx["cdfine7"] = "1";
    mx["cdfine7Id"] = cdfine7Value;
  }
  if (cdfine8.value == '1') {
    mx["cdfine8"] = "1";
    mx["cdfine8Id"] = cdfine8Value;
  }
  if (cdfine9.value == '1') {
    mx["cdfine9"] = "1";
    mx["cdfine9Id"] = cdfine9Value;
  }

  if (cdfine10.value == '1') {
    mx["cdfine10"] = "1";
    mx["cdfine10Id"] = cdfine10Value;
  }
  if (cdfine11.value == '1') {
    mx["cdfine11"] = "1";
    mx["cdfine11Id"] = cdfine11Value;
  }
  if (cdfine12.value == '1') {
    mx["cdfine12"] = "1";
    mx["cdfine12Id"] = cdfine12Value;
  }
  if (cdfine13.value == '1') {
    mx["cdfine13"] = "1";
    mx["cdfine13Id"] = cdfine13Value;
  }
  if (cdfine14.value == '1') {
    mx["cdfine14"] = "1";
    mx["cdfine14Id"] = cdfine14Value;
  }
  if (cdfine15.value == '1') {
    mx["cdfine15"] = "1";
    mx["cdfine15Id"] = cdfine15Value;
  }
  if (cdfine16.value == '1') {
    mx["cdfine16"] = "1";
    mx["cdfine16Id"] = cdfine16Value;
  }
  if (cdfine17.value == '1') {
    mx["cdfine17"] = "1";
    mx["cdfine17Id"] = cdfine17Value;
  }
  if (cdfine18.value == '1') {
    mx["cdfine18"] = "1";
    mx["cdfine18Id"] = cdfine18Value;
  }
  if (cdfine19.value == '1') {
    mx["cdfine19"] = "1";
    mx["cdfine19Id"] = cdfine19Value;
  }

  if (cdfine20.value == '1') {
    mx["cdfine20"] = "1";
    mx["cdfine20Id"] = cdfine20Value;
  }
  if (cdfine21.value == '1') {
    mx["cdfine21"] = "1";
    mx["cdfine21Id"] = cdfine21Value;
  }
  if (cdfine22.value == '1') {
    mx["cdfine22"] = "1";
    mx["cdfine22Id"] = cdfine22Value;
  }
  if (cdfine23.value == '1') {
    mx["cdfine23"] = "1";
    mx["cdfine23Id"] = cdfine23Value;
  }
  if (cdfine24.value == '1') {
    mx["cdfine24"] = "1";
    mx["cdfine24Id"] = cdfine24Value;
  }
  if (cdfine25.value == '1') {
    mx["cdfine25"] = "1";
    mx["cdfine25Id"] = cdfine25Value;
  }
  if (cdfine26.value == '1') {
    mx["cdfine26"] = "1";
    mx["cdfine26Id"] = cdfine26Value;
  }
  if (cdfine27.value == '1') {
    mx["cdfine27"] = "1";
    mx["cdfine27Id"] = cdfine27Value;
  }
  if (cdfine28.value == '1') {
    mx["cdfine28"] = "1";
    mx["cdfine28Id"] = cdfine28Value;
  }
  if (cdfine29.value == '1') {
    mx["cdfine29"] = "1";
    mx["cdfine29Id"] = cdfine29Value;
  }
  if (cdfine30.value == '1') {
    mx["cdfine30"] = "1";
    mx["cdfine30Id"] = cdfine30Value;
  }
  datainfo.value = mx;
  //最后的数据
  baocunflag.value = true;
  console.log(datainfo.value)

  function checkLen() {
   return accli.value.filter(it=>{
     let f = false
      for (let x = 0;x<fuzhus.value.length;x++){
        if ((fuzhus.value[x] == 'fzDept' && mx['cdeptId'] == it['bdept']) ||  (fuzhus.value[x] == 'fzItemClass' && mx['projectClassId'] == it['bitemClass']) ||  (fuzhus.value[x] == 'fzItem' && mx['projectId'] == it['bitem'])){
          f = true;
        }else if (!isNaN(fuzhus.value[x])){
          let k = 'cdfine'+fuzhus.value[x]
          if (mx[(k+'Id')] == it[k]) f = true;
        }
      }
      return  f
    })
  }
  // 非个人、客户、供应商只能添加一次
  if (fuzhus.value.indexOf('fzEmp') == -1 && fuzhus.value.indexOf('fzCustom') == -1 && fuzhus.value.indexOf('fzItem') == -1 && (checkLen()).length != 1){
    showMsg('非个人、客户、供应商,同一辅助核算值只能添加一条记录！')
    return false
  }
  closeModal();
};

const showMsg = (msg: string) => {
  createErrorModal({
    iconType: 'warning',
    title: '提示',
    content: msg
  })
};


const findZiDingYiName = (msg: string) => {
  var name = msg;
  let fzhsList2 = fzhsList.value;
  for (let i = 0; i < fzhsList2.length; i++) {
    if (name == ("自定义" + fzhsList2[i].cdfine)) {
      name = fzhsList2[i].cname;
    }
  }
  return name;
}
const handleChange = () => {

}
</script>
<style scoped>
.scrollbar__view > div  > div{
  padding-left: 8%;
  margin: 12px 0;
  height: 32px;
  line-height: 32px;

}
:deep(.ant-calendar-picker-input.ant-input),
.ant-select-single:not(.ant-select-customize-input) :deep(.ant-select-selector),
.ant-input{
  border: none;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
  width: 100%;
}
</style>
