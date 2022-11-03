<template>
  <div>
    <BasicModal
      v-bind="$attrs"
      width="900px"
      :minHeight="420"
      title="辅助核算录入"
      @ok="handleOk()"
      :showOkBtn="lock0K"
      @cancel="handleClose()"
      @register="register"
    >
      <template #title>
        <div style="display: flex;" class="vben-basic-title">
          <img src="/create.svg" style="width:25px;margin-right: 10px;"/>
          <span style="line-height: 25px;font-size: 16px;">辅助核算科目期初余额录入</span>
        </div>
      </template>
      <OneModal @register="registerOnePage"></OneModal>
      <RowEdit @close="delRow" :database="props.database"  @register="registerRowEditPage"/>
      <a-button class="editable-add-btn" @click="handleAdd" style="margin-bottom: 8px;color: black;font-weight: bold">增行</a-button>
      <a-button class="editable-add-btn" @click="handleEdit" style="margin-bottom: 8px;color: black;font-weight: bold">修改</a-button>
      <a-button class="editable-add-btn" @click="handleDel" style="margin-bottom: 8px;color: black;font-weight: bold">删行</a-button>
      <a-table
        v-if="accflag"
        class="customize-atable"
        :pagination="false"
        :columns="acccolumns"
        :row-selection="{ type: 'checkbox' ,fixed: true,selectedRowKeys: tableSelectedRowKeys, onChange: onSelectChange }"
        :data-source="accli"
        :scroll="{ x: 1050, y: 300 }"
      >
        <template v-for="col in ['dbilldate']" #[col]="{ text, record }" :key="col">
          <div>
            <a-date-picker
              v-if="editableData2[record.caozuo]"
              v-model:value="editableData2[record.caozuo][col]"
              style="margin: -5px 0"
            />
            <template v-else>
              {{ text }}
            </template>
          </div>
        </template>

        <template v-for="col in ['inoid']" #[col]="{ text, record }" :key="col">
          <div>
            <a-input
              v-if="editableData2[record.caozuo] && false"
              v-model:value="editableData2[record.caozuo][col]"
              style="margin: -5px 0"
              @change="changeMinInoId(record.caozuo)"
              type="number"
              min="1"
              max="9999999"
            />
            <template v-else>
              {{ text }}
            </template>
          </div>
        </template>

        <template v-for="col in ['cdigest']" #[col]="{ text, record }" :key="col">
          <div>
            <a-input
              v-if="editableData2[record.caozuo] && false"
              v-model:value="editableData2[record.caozuo][col]"
              maxlength="100"
              style="margin: -5px 0"
            />
            <template v-else>
              {{ text }}
            </template>
          </div>
        </template>

        <template v-for="col in ['md']" #[col]="{ text, record }" :key="col">
          <div>
            <a-input
              v-if="editableData2[record.caozuo] && false"
              v-model:value="editableData2[record.caozuo][col]"
              style="margin: -5px 0"
              :step="0.01"
              @change="changeMd(record.caozuo)"
            />
            <template v-else>
              {{ text }}
            </template>
          </div>
        </template>

        <template v-for="col in ['mc']" #[col]="{ text, record }" :key="col">
          <div>
            <a-input
              v-if="editableData2[record.caozuo] && false"
              v-model:value="editableData2[record.caozuo][col]"
              style="margin: -5px 0"
              :step="0.01"
              @change="changeMc(record.caozuo)"
            />
            <template v-else>
              {{ text }}
            </template>
          </div>
        </template>

        <template v-for="col in ['bnum']" #[col]="{ text, record }" :key="col">
          <div>
            <a-input
              v-if="editableData2[record.caozuo] && false"
              v-model:value="editableData2[record.caozuo][col]"
              style="margin: -5px 0"
              @change="changeBNum(record.caozuo)"
            />
            <template v-else>
              {{ text }}
            </template>
          </div>
        </template>

        <template v-for="col in ['bnum2']" #[col]="{ text, record }" :key="col">
          <div>
            <a-input
              v-if="editableData2[record.caozuo] && false"
              v-model:value="editableData2[record.caozuo][col]"
              style="margin: -5px 0"
              @change="changeBNum2(record.caozuo)"
            />
            <template v-else>
              {{ text }}
            </template>
          </div>
        </template>

        <template v-for="col in ['currency']" #[col]="{ text, record }" :key="col">
          <div>
            <a-input
              v-if="editableData2[record.caozuo] && false"
              v-model:value="editableData2[record.caozuo][col]"
              style="margin: -5px 0"
              @change="changecurrency(record.caozuo)"
            />
            <template v-else>
              {{ text }}
            </template>
          </div>
        </template>

        <template v-for="col in ['currency2']" #[col]="{ text, record }" :key="col">
          <div>
            <a-input
              v-if="editableData2[record.caozuo] && false"
              v-model:value="editableData2[record.caozuo][col]"
              style="margin: -5px 0"
              @change="changecurrency2(record.caozuo)"
            />
            <template v-else>
              {{ text }}
            </template>
          </div>
        </template>

        <template v-for="col in ['bperson']" #[col]="{ text, record }" :key="col">
          <div>
            <a-select
              v-if="editableData2[record.caozuo] && false"
              v-model:value="editableData2[record.caozuo][col]"
              show-search
              :filter-option="filterOption"
              placeholder="请选择人员"
              style="width:100%;"
              :options="bpersonList"
              @change="handleChange"
            >
            </a-select>
            <template v-else>
              {{ findPsnName(text) }}
            </template>
          </div>
        </template>

        <template v-for="col in ['bdept']" #[col]="{ text, record }" :key="col">
          <div>
            <a-select
              v-if="editableData2[record.caozuo] && false"
              v-model:value="editableData2[record.caozuo][col]"
              show-search
              :filter-option="filterOption"
              placeholder="请选择部门"
              style="width:100%;"
              :options="bdeptList"
              @change="handleChange"
            >
            </a-select>
            <template v-else>
              {{ findDeptName(text) }}
            </template>
          </div>
        </template>

        <template v-for="col in ['bcus']" #[col]="{ text, record }" :key="col">
          <div>
            <a-select
              v-if="editableData2[record.caozuo] && false"
              v-model:value="editableData2[record.caozuo][col]"
              show-search
              :filter-option="filterOption"
              placeholder="请选择客户"
              style="width:100%;"
              :options="bcusList"
            >
            </a-select>
            <template v-else>
              {{ findCusName(text) }}
            </template>
          </div>
        </template>

        <template v-for="col in ['bsup']" #[col]="{ text, record }" :key="col">
          <div>
            <a-select
              v-if="editableData2[record.caozuo] && false"
              v-model:value="editableData2[record.caozuo][col]"
              show-search
              :filter-option="filterOption"
              placeholder="请选择供应商"
              style="width:100%;"
              :options="bsupList"
            >
            </a-select>
            <template v-else>
              {{ findSupName(text) }}
            </template>
          </div>
        </template>

        <template v-for="col in ['bitemClass']" #[col]="{ text, record }" :key="col">
          <div>
            <a-select
              v-if="editableData2[record.caozuo] && false"
              v-model:value="editableData2[record.caozuo][col]"
              show-search
              :filter-option="filterOption"
              placeholder="请选择项目大类"
              style="width:100%;"
              :options="bitemClassList"
              @change="handleChangeBitemClass(record.caozuo)"
            >
            </a-select>
            <template v-else>
              {{ findItemClassName(text) }}
            </template>
          </div>
        </template>

        <template v-for="col in ['bitem']" #[col]="{ text, record }" :key="col">
          <div>
            <a-select
              v-if="editableData2[record.caozuo] && false"
              v-model:value="editableData2[record.caozuo][col]"
              show-search
              :filter-option="filterOption"
              placeholder="请选择项目"
              style="width:100%;"
              :options="bitemList"
              @change="handleChangeBitem(record.caozuo)"
            >
            </a-select>
            <template v-else>
              {{ findItemName(text) }}
            </template>
          </div>
        </template>

        <template v-for="col in ['cdfine1']" #[col]="{ text, record }" :key="col"><div><a-select v-if="editableData2[record.caozuo]" v-model:value="editableData2[record.caozuo][col]" show-search :filter-option="filterOption" placeholder="请选择" style="width:100%;" :options="cdfine1List"></a-select><template v-else>{{ findCdfine1Name(text) }}</template></div></template>
        <template v-for="col in ['cdfine2']" #[col]="{ text, record }" :key="col"><div><a-select v-if="editableData2[record.caozuo]" v-model:value="editableData2[record.caozuo][col]" show-search :filter-option="filterOption" placeholder="请选择" style="width:100%;" :options="cdfine2List"></a-select><template v-else>{{ findCdfine2Name(text) }}</template></div></template>
        <template v-for="col in ['cdfine3']" #[col]="{ text, record }" :key="col"><div><a-select v-if="editableData2[record.caozuo]" v-model:value="editableData2[record.caozuo][col]" show-search :filter-option="filterOption" placeholder="请选择" style="width:100%;" :options="cdfine3List"></a-select><template v-else>{{ findCdfine3Name(text) }}</template></div></template>
        <template v-for="col in ['cdfine4']" #[col]="{ text, record }" :key="col"><div><a-select v-if="editableData2[record.caozuo]" v-model:value="editableData2[record.caozuo][col]" show-search :filter-option="filterOption" placeholder="请选择" style="width:100%;" :options="cdfine4List"></a-select><template v-else>{{ findCdfine4Name(text) }}</template></div></template>
        <template v-for="col in ['cdfine5']" #[col]="{ text, record }" :key="col"><div><a-select v-if="editableData2[record.caozuo]" v-model:value="editableData2[record.caozuo][col]" show-search :filter-option="filterOption" placeholder="请选择" style="width:100%;" :options="cdfine5List"></a-select><template v-else>{{ findCdfine5Name(text) }}</template></div></template>
        <template v-for="col in ['cdfine6']" #[col]="{ text, record }" :key="col"><div><a-select v-if="editableData2[record.caozuo]" v-model:value="editableData2[record.caozuo][col]" show-search :filter-option="filterOption" placeholder="请选择" style="width:100%;" :options="cdfine6List"></a-select><template v-else>{{ findCdfine6Name(text) }}</template></div></template>
        <template v-for="col in ['cdfine7']" #[col]="{ text, record }" :key="col"><div><a-select v-if="editableData2[record.caozuo]" v-model:value="editableData2[record.caozuo][col]" show-search :filter-option="filterOption" placeholder="请选择" style="width:100%;" :options="cdfine7List"></a-select><template v-else>{{ findCdfine7Name(text) }}</template></div></template>
        <template v-for="col in ['cdfine8']" #[col]="{ text, record }" :key="col"><div><a-select v-if="editableData2[record.caozuo]" v-model:value="editableData2[record.caozuo][col]" show-search :filter-option="filterOption" placeholder="请选择" style="width:100%;" :options="cdfine8List"></a-select><template v-else>{{ findCdfine8Name(text) }}</template></div></template>
        <template v-for="col in ['cdfine9']" #[col]="{ text, record }" :key="col"><div><a-select v-if="editableData2[record.caozuo]" v-model:value="editableData2[record.caozuo][col]" show-search :filter-option="filterOption" placeholder="请选择" style="width:100%;" :options="cdfine9List"></a-select><template v-else>{{ findCdfine9Name(text) }}</template></div></template>

        <template v-for="col in ['cdfine10']" #[col]="{ text, record }" :key="col"><div><a-select v-if="editableData2[record.caozuo]" v-model:value="editableData2[record.caozuo][col]" show-search :filter-option="filterOption" placeholder="请选择" style="width:100%;" :options="cdfine10List"></a-select><template v-else>{{ findCdfine10Name(text) }}</template></div></template>
        <template v-for="col in ['cdfine11']" #[col]="{ text, record }" :key="col"><div><a-select v-if="editableData2[record.caozuo]" v-model:value="editableData2[record.caozuo][col]" show-search :filter-option="filterOption" placeholder="请选择" style="width:100%;" :options="cdfine11List"></a-select><template v-else>{{ findCdfine11Name(text) }}</template></div></template>
        <template v-for="col in ['cdfine12']" #[col]="{ text, record }" :key="col"><div><a-select v-if="editableData2[record.caozuo]" v-model:value="editableData2[record.caozuo][col]" show-search :filter-option="filterOption" placeholder="请选择" style="width:100%;" :options="cdfine12List"></a-select><template v-else>{{ findCdfine12Name(text) }}</template></div></template>
        <template v-for="col in ['cdfine13']" #[col]="{ text, record }" :key="col"><div><a-select v-if="editableData2[record.caozuo]" v-model:value="editableData2[record.caozuo][col]" show-search :filter-option="filterOption" placeholder="请选择" style="width:100%;" :options="cdfine13List"></a-select><template v-else>{{ findCdfine13Name(text) }}</template></div></template>
        <template v-for="col in ['cdfine14']" #[col]="{ text, record }" :key="col"><div><a-select v-if="editableData2[record.caozuo]" v-model:value="editableData2[record.caozuo][col]" show-search :filter-option="filterOption" placeholder="请选择" style="width:100%;" :options="cdfine14List"></a-select><template v-else>{{ findCdfine14Name(text) }}</template></div></template>
        <template v-for="col in ['cdfine15']" #[col]="{ text, record }" :key="col"><div><a-select v-if="editableData2[record.caozuo]" v-model:value="editableData2[record.caozuo][col]" show-search :filter-option="filterOption" placeholder="请选择" style="width:100%;" :options="cdfine15List"></a-select><template v-else>{{ findCdfine15Name(text) }}</template></div></template>
        <template v-for="col in ['cdfine16']" #[col]="{ text, record }" :key="col"><div><a-select v-if="editableData2[record.caozuo]" v-model:value="editableData2[record.caozuo][col]" show-search :filter-option="filterOption" placeholder="请选择" style="width:100%;" :options="cdfine16List"></a-select><template v-else>{{ findCdfine16Name(text) }}</template></div></template>
        <template v-for="col in ['cdfine17']" #[col]="{ text, record }" :key="col"><div><a-select v-if="editableData2[record.caozuo]" v-model:value="editableData2[record.caozuo][col]" show-search :filter-option="filterOption" placeholder="请选择" style="width:100%;" :options="cdfine17List"></a-select><template v-else>{{ findCdfine17Name(text) }}</template></div></template>
        <template v-for="col in ['cdfine18']" #[col]="{ text, record }" :key="col"><div><a-select v-if="editableData2[record.caozuo]" v-model:value="editableData2[record.caozuo][col]" show-search :filter-option="filterOption" placeholder="请选择" style="width:100%;" :options="cdfine18List"></a-select><template v-else>{{ findCdfine18Name(text) }}</template></div></template>
        <template v-for="col in ['cdfine19']" #[col]="{ text, record }" :key="col"><div><a-select v-if="editableData2[record.caozuo]" v-model:value="editableData2[record.caozuo][col]" show-search :filter-option="filterOption" placeholder="请选择" style="width:100%;" :options="cdfine19List"></a-select><template v-else>{{ findCdfine19Name(text) }}</template></div></template>

        <template v-for="col in ['cdfine20']" #[col]="{ text, record }" :key="col"><div><a-select v-if="editableData2[record.caozuo]" v-model:value="editableData2[record.caozuo][col]" show-search :filter-option="filterOption" placeholder="请选择" style="width:100%;" :options="cdfine20List"></a-select><template v-else>{{ findCdfine20Name(text) }}</template></div></template>
        <template v-for="col in ['cdfine21']" #[col]="{ text, record }" :key="col"><div><a-select v-if="editableData2[record.caozuo]" v-model:value="editableData2[record.caozuo][col]" show-search :filter-option="filterOption" placeholder="请选择" style="width:100%;" :options="cdfine21List"></a-select><template v-else>{{ findCdfine21Name(text) }}</template></div></template>
        <template v-for="col in ['cdfine22']" #[col]="{ text, record }" :key="col"><div><a-select v-if="editableData2[record.caozuo]" v-model:value="editableData2[record.caozuo][col]" show-search :filter-option="filterOption" placeholder="请选择" style="width:100%;" :options="cdfine22List"></a-select><template v-else>{{ findCdfine22Name(text) }}</template></div></template>
        <template v-for="col in ['cdfine23']" #[col]="{ text, record }" :key="col"><div><a-select v-if="editableData2[record.caozuo]" v-model:value="editableData2[record.caozuo][col]" show-search :filter-option="filterOption" placeholder="请选择" style="width:100%;" :options="cdfine23List"></a-select><template v-else>{{ findCdfine23Name(text) }}</template></div></template>
        <template v-for="col in ['cdfine24']" #[col]="{ text, record }" :key="col"><div><a-select v-if="editableData2[record.caozuo]" v-model:value="editableData2[record.caozuo][col]" show-search :filter-option="filterOption" placeholder="请选择" style="width:100%;" :options="cdfine24List"></a-select><template v-else>{{ findCdfine24Name(text) }}</template></div></template>
        <template v-for="col in ['cdfine25']" #[col]="{ text, record }" :key="col"><div><a-select v-if="editableData2[record.caozuo]" v-model:value="editableData2[record.caozuo][col]" show-search :filter-option="filterOption" placeholder="请选择" style="width:100%;" :options="cdfine25List"></a-select><template v-else>{{ findCdfine25Name(text) }}</template></div></template>
        <template v-for="col in ['cdfine26']" #[col]="{ text, record }" :key="col"><div><a-select v-if="editableData2[record.caozuo]" v-model:value="editableData2[record.caozuo][col]" show-search :filter-option="filterOption" placeholder="请选择" style="width:100%;" :options="cdfine26List"></a-select><template v-else>{{ findCdfine26Name(text) }}</template></div></template>
        <template v-for="col in ['cdfine27']" #[col]="{ text, record }" :key="col"><div><a-select v-if="editableData2[record.caozuo]" v-model:value="editableData2[record.caozuo][col]" show-search :filter-option="filterOption" placeholder="请选择" style="width:100%;" :options="cdfine27List"></a-select><template v-else>{{ findCdfine27Name(text) }}</template></div></template>
        <template v-for="col in ['cdfine28']" #[col]="{ text, record }" :key="col"><div><a-select v-if="editableData2[record.caozuo]" v-model:value="editableData2[record.caozuo][col]" show-search :filter-option="filterOption" placeholder="请选择" style="width:100%;" :options="cdfine28List"></a-select><template v-else>{{ findCdfine28Name(text) }}</template></div></template>
        <template v-for="col in ['cdfine29']" #[col]="{ text, record }" :key="col"><div><a-select v-if="editableData2[record.caozuo]" v-model:value="editableData2[record.caozuo][col]" show-search :filter-option="filterOption" placeholder="请选择" style="width:100%;" :options="cdfine29List"></a-select><template v-else>{{ findCdfine29Name(text) }}</template></div></template>
        <template v-for="col in ['cdfine30']" #[col]="{ text, record }" :key="col"><div><a-select v-if="editableData2[record.caozuo]" v-model:value="editableData2[record.caozuo][col]" show-search :filter-option="filterOption" placeholder="请选择" style="width:100%;" :options="cdfine30List"></a-select><template v-else>{{ findCdfine30Name(text) }}</template></div></template>

<!--        <template #caozuo="{ record }">
          <div>
            <div class="editable-row-operations"
                 v-if="editableData2[record.caozuo]">
              <a @click="baocun(record.caozuo)">保存</a>&nbsp;&nbsp;<a @click="quxiao(record.caozuo)">取消</a>
            </div>
            <template v-else>
              <a @click="editTable(record.caozuo)">编辑</a>&nbsp;&nbsp;<a @click="deleteTable(record.caozuo)">删除</a>
            </template>
          </div>
        </template>-->
      </a-table>
    </BasicModal>
  </div>
</template>
<script setup lang="ts">
import {BasicModal, useModal, useModalInner} from '/@/components/Modal'
import OneModal from './oneModal.vue'
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
  Popconfirm as APopconfirm,
  Popover as APopover,
  Checkbox as ACheckbox,
  Modal as AModal,
  Upload as AUpload,
  Row as ARow,
  Col as ACol,
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
import {useUserStoreWidthOut} from "/@/store/modules/user";
import RowEdit from "/@/views/boozsoft/system/subjectInitialBalanceFuZhu/popup/RowEdit.vue";
import {uuid, StrTool, JsonTool} from "/@/api/task-api/tools/universal-tools";

const props = defineProps(['database'])


const lock0K = ref(true);

const [registerOnePage, { openModal: openOnePage }] = useModal()
const [registerRowEditPage, {openModal: openRowEditPageM}] = useModal()
const emit=defineEmits(['register'])

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

const bpersonList:any = ref([]);
const bdeptList:any = ref([]);
const bcusList:any = ref([]);
const bsupList:any = ref([]);
const bitemListBig = ref([]);
const bitemList:any = ref([]);
const bitemClassList:any = ref([]);

const cdfine1List:any = ref([]);
const cdfine2List:any = ref([]);
const cdfine3List:any = ref([]);
const cdfine4List:any = ref([]);
const cdfine5List:any = ref([]);
const cdfine6List:any = ref([]);
const cdfine7List:any = ref([]);
const cdfine8List:any = ref([]);
const cdfine9List:any = ref([]);
const cdfine10List:any = ref([]);
const cdfine11List:any = ref([]);
const cdfine12List:any = ref([]);
const cdfine13List:any = ref([]);
const cdfine14List:any = ref([]);
const cdfine15List:any = ref([]);
const cdfine16List:any = ref([]);
const cdfine17List:any = ref([]);
const cdfine18List:any = ref([]);
const cdfine19List:any = ref([]);
const cdfine20List:any = ref([]);
const cdfine21List:any = ref([]);
const cdfine22List:any = ref([]);
const cdfine23List:any = ref([]);
const cdfine24List:any = ref([]);
const cdfine25List:any = ref([]);
const cdfine26List:any = ref([]);
const cdfine27List:any = ref([]);
const cdfine28List:any = ref([]);
const cdfine29List:any = ref([]);
const cdfine30List:any = ref([]);

const baocunflag = ref(false);
const containsThree = ref(false);
const fuzhus = ref([]);
const tableSelectedRowKeys = ref([]);
const fzhsList = ref([]);
const ccode = ref("");
const ccodeName = ref("");
const yeFx = ref("借");
const fzxNames = ref("");
const wbCurrenry = ref("");

const acccolumns2 = [
  {title: "凭证日期", dataIndex: "dbilldate", width: 100, ellipsis: true,slots: {customRender: "dbilldate"}},
  {title: "凭证号", dataIndex: "inoid", width: 100, ellipsis: true,slots: {customRender: "inoid"}},
  {title: "摘要", dataIndex: "cdigest", width: 100, ellipsis: true,slots: {customRender: "cdigest"}},
  {title: "部门名称", dataIndex: "bdept", width: 130, ellipsis: true,slots: {customRender: "bdept"}},
  {title: "人员姓名", dataIndex: "bperson", width: 150, ellipsis: true,slots: {customRender: "bperson"}},
  {title: "客户名称", dataIndex: "bcus", width: 200, ellipsis: true,slots: {customRender: "bcus"}},
  {title: "供应商名称", dataIndex: "bsup", width: 200, ellipsis: true,slots: {customRender: "bsup"}},
  {title: "项目大类", dataIndex: "bitemClass", width: 150, ellipsis: true,slots: {customRender: "bitemClass"}},
  {title: "项目名称", dataIndex: "bitem", width: 300, ellipsis: true,slots: {customRender: "bitem"}},
  {title: "自定义1", dataIndex: 'cdfine1', width: 150, ellipsis: true,slots: {customRender: "cdfine1"}},
  {title: "自定义2", dataIndex: 'cdfine2', width: 150, ellipsis: true,slots: {customRender: "cdfine2"}},
  {title: "自定义3", dataIndex: 'cdfine3', width: 150, ellipsis: true,slots: {customRender: "cdfine3"}},
  {title: "自定义4", dataIndex: 'cdfine4', width: 150, ellipsis: true,slots: {customRender: "cdfine4"}},
  {title: "自定义5", dataIndex: 'cdfine5', width: 150, ellipsis: true,slots: {customRender: "cdfine5"}},
  {title: "自定义6", dataIndex: 'cdfine6', width: 150, ellipsis: true,slots: {customRender: "cdfine6"}},
  {title: "自定义7", dataIndex: 'cdfine7', width: 150, ellipsis: true,slots: {customRender: "cdfine7"}},
  {title: "自定义8", dataIndex: 'cdfine8', width: 150, ellipsis: true,slots: {customRender: "cdfine8"}},
  {title: "自定义9", dataIndex: 'cdfine9', width: 150, ellipsis: true,slots: {customRender: "cdfine9"}},
  {title: "自定义10", dataIndex: 'cdfine10', width: 150, ellipsis: true,slots: {customRender: "cdfine10"}},
  {title: "自定义11", dataIndex: 'cdfine11', width: 150, ellipsis: true,slots: {customRender: "cdfine11"}},
  {title: "自定义12", dataIndex: 'cdfine12', width: 150, ellipsis: true,slots: {customRender: "cdfine12"}},
  {title: "自定义13", dataIndex: 'cdfine13', width: 150, ellipsis: true,slots: {customRender: "cdfine13"}},
  {title: "自定义14", dataIndex: 'cdfine14', width: 150, ellipsis: true,slots: {customRender: "cdfine14"}},
  {title: "自定义15", dataIndex: 'cdfine15', width: 150, ellipsis: true,slots: {customRender: "cdfine15"}},
  {title: "自定义16", dataIndex: 'cdfine16', width: 150, ellipsis: true,slots: {customRender: "cdfine16"}},
  {title: "自定义17", dataIndex: 'cdfine17', width: 150, ellipsis: true,slots: {customRender: "cdfine17"}},
  {title: "自定义18", dataIndex: 'cdfine18', width: 150, ellipsis: true,slots: {customRender: "cdfine18"}},
  {title: "自定义19", dataIndex: 'cdfine19', width: 150, ellipsis: true,slots: {customRender: "cdfine19"}},
  {title: "自定义20", dataIndex: 'cdfine20', width: 150, ellipsis: true,slots: {customRender: "cdfine20"}},
  {title: "自定义21", dataIndex: 'cdfine21', width: 150, ellipsis: true,slots: {customRender: "cdfine21"}},
  {title: "自定义22", dataIndex: 'cdfine22', width: 150, ellipsis: true,slots: {customRender: "cdfine22"}},
  {title: "自定义23", dataIndex: 'cdfine23', width: 150, ellipsis: true,slots: {customRender: "cdfine23"}},
  {title: "自定义24", dataIndex: 'cdfine24', width: 150, ellipsis: true,slots: {customRender: "cdfine24"}},
  {title: "自定义25", dataIndex: 'cdfine25', width: 150, ellipsis: true,slots: {customRender: "cdfine25"}},
  {title: "自定义26", dataIndex: 'cdfine26', width: 150, ellipsis: true,slots: {customRender: "cdfine26"}},
  {title: "自定义27", dataIndex: 'cdfine27', width: 150, ellipsis: true,slots: {customRender: "cdfine27"}},
  {title: "自定义28", dataIndex: 'cdfine28', width: 150, ellipsis: true,slots: {customRender: "cdfine28"}},
  {title: "自定义29", dataIndex: 'cdfine29', width: 150, ellipsis: true,slots: {customRender: "cdfine29"}},
  {title: "自定义30", dataIndex: 'cdfine30', width: 150, ellipsis: true,slots: {customRender: "cdfine30"}},

  {title: "借方金额", dataIndex: "md", width: 150, className: "column-money", slots: {customRender: "md"}},
  {title: "贷方金额", dataIndex: "mc", width: 150, className: "column-money", slots: {customRender: "mc"}},
  {title: "累计借方金额", dataIndex: "ljMd", width: 150, className: "column-money", slots: {customRender: "ljMd"}},
  {title: "累计贷方金额", dataIndex: "ljMc", width: 150, className: "column-money", slots: {customRender: "ljMc"}},
  {title: '数量', dataIndex: "bnum", width: 100,slots: {customRender: "bnum"}},
  {title: "累计借方数量", dataIndex: "ljSlMd", width: 120, slots: {customRender: "ljSlMd"}},
  {title: "累计贷方数量", dataIndex: "ljSlMc", width: 120, slots: {customRender: "ljSlMc"}},
/*  {title: '单价', dataIndex: 'bnum2', width: 150,className: "column-money",slots: {customRender: "bnum2"}},*/
  {title: "外币金额", dataIndex: 'currency', width: 150,className: "column-money",slots: {customRender: "currency"}},
  {title: "累计外币借方", dataIndex: "ljWbMd", width: 150, slots: {customRender: "ljWbMd"}},
  {title: "累计外币贷方", dataIndex: "ljWbMc", width: 150, slots: {customRender: "ljWbMc"}},
/*  {title: "汇率", dataIndex: 'currency2', width: 150,slots: {customRender: "currency2"}},*/
  /*{title: "操作", dataIndex: "caozuo", width: 100, className: "column-caozuo", slots: {customRender: "caozuo"}}*/
];
const acccolumns:any = ref([])

const userinfo = ref({
  username: useUserStoreWidthOut().getUserInfo.username,
  realName: useUserStoreWidthOut().getUserInfo.realName,
  name: useUserStoreWidthOut().getUserInfo.name,
  phone: useUserStoreWidthOut().getUserInfo.phone,
});

const datasourcePicker=inject('datasourcePicker')
const datasourceLock=inject('datasourceLock')
const usePageRouterApi=inject('usePageRouterApi')

const accli:any = ref([]);
const thisMap = ref({})
const isLj = ref(false)
const [register, { closeModal }] = useModalInner(async (data) => {
  acccolumns.value = JSON.parse(JSON.stringify(acccolumns2))
  accli.value = [];
  delList.value = []
  tableSelectedRowKeys.value = []
  delete editableData2[-1];
  delete editableData2[1];
  delete editableData2[2];
  baocunflag.value = false;
  fzhsList.value = data.map.fzhsList;

  ccode.value = data.map.kemuCode.ccode;
  fuzhus.value = data.fuzhus;
  isLj.value = data.isLj
  ccodeName.value = data.map.kemuCode.ccodeName;
  yeFx.value = data.map.kemuCode.bprogerty == '1' ? '借' : '贷';
  fzxNames.value = data.ffuuzz;
  iyear.value = data.map.kemuCode.iyear;
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
  thisMap.value = data.map
  const bbb= await useRouteApi(findKeyLabelAll,{schemaName: props.database})({
    require:data.fuzhus,
    toTarget:"false"
  });

  /*
  const index1 = acccolumns.value.findIndex(v=>  v.dataIndex === 'dbilldate');
  if(index1 >= 0) {acccolumns.value.splice(index1, 1);}
  const index2 = acccolumns.value.findIndex(v=>  v.dataIndex === 'inoid');
  if(index2 >= 0) {acccolumns.value.splice(index2, 1);}
  */
  containsThree.value = false
  if (bdept.value == '1') {
    bdeptList.value = bbb.filter(item => "fzDept" == item.key)[0].list;
    bdeptList.value.filter(v=> v.value = v.key);
  }else{
    const index = acccolumns.value.findIndex(v=>  v.dataIndex === 'bdept');
    if(index >= 0) {acccolumns.value.splice(index, 1);}
  }
  if (bperson.value == '1') {
    containsThree.value = true
    bpersonList.value = bbb.filter(item => "fzEmp" == item.key)[0].list;
    bpersonList.value.filter(v=> v.value = v.key);
  }else{
    const index = acccolumns.value.findIndex(v=>  v.dataIndex === 'bperson');
    if(index >= 0) {acccolumns.value.splice(index, 1);}
  }
  if (bcus.value == '1') {
  containsThree.value = true
    bcusList.value = bbb.filter(item => "fzCustom" == item.key)[0].list;
    bcusList.value.filter(v=> v.value = v.key);
  }else{
    const index = acccolumns.value.findIndex(v=>  v.dataIndex === 'bcus');
    if(index >= 0) {acccolumns.value.splice(index, 1);}
  }
  if (bsup.value == '1') {
  containsThree.value = true
    bsupList.value = bbb.filter(item => "fzGys" == item.key)[0].list;
    bsupList.value.filter(v=> v.value = v.key);
  }else{
    const index = acccolumns.value.findIndex(v=>  v.dataIndex === 'bsup');
    if(index >= 0) {acccolumns.value.splice(index, 1);}
  }
  if (bitem.value == '1') {
    bitemClassList.value = bbb.filter(item => "fzItemClass" == item.key)[0].list;
    bitemListBig.value = bbb.filter(item => "fzItem" == item.key)[0].list;
    bitemList.value = bbb.filter(item => "fzItem" == item.key)[0].list;

    bitemClassList.value.filter(v=> v.value = v.key);
    bitemListBig.value.filter(v=> v.value = v.key);
    bitemList.value.filter(v=> v.value = v.key);

  }else{
    const index1 = acccolumns.value.findIndex(v=>  v.dataIndex === 'bitemClass');
    if(index1 >= 0) {acccolumns.value.splice(index1, 1);}
    const index2 = acccolumns.value.findIndex(v=>  v.dataIndex === 'bitem');
    if(index2 >= 0) {acccolumns.value.splice(index2, 1);}
  }
  if (cdfine1.value == '1') {cdfine1List.value = bbb.filter(item => 1 === item.key)[0].list;}
  if (cdfine2.value == '1') {cdfine2List.value = bbb.filter(item => 2 === item.key)[0].list;}
  if (cdfine3.value == '1') {cdfine3List.value = bbb.filter(item => 3 === item.key)[0].list;}
  if (cdfine4.value == '1') {cdfine4List.value = bbb.filter(item => 4 === item.key)[0].list;}
  if (cdfine5.value == '1') {cdfine5List.value = bbb.filter(item => 5 === item.key)[0].list;}
  if (cdfine6.value == '1') {cdfine6List.value = bbb.filter(item => 6 === item.key)[0].list;}
  if (cdfine7.value == '1') {cdfine7List.value = bbb.filter(item => 7 === item.key)[0].list;}
  if (cdfine8.value == '1') {cdfine8List.value = bbb.filter(item => 8 === item.key)[0].list;}
  if (cdfine9.value == '1') {cdfine9List.value = bbb.filter(item => 9 === item.key)[0].list;}
  if (cdfine10.value == '1') {cdfine10List.value = bbb.filter(item => 10 === item.key)[0].list;}
  if (cdfine11.value == '1') {cdfine11List.value = bbb.filter(item => 11 === item.key)[0].list;}
  if (cdfine12.value == '1') {cdfine12List.value = bbb.filter(item => 12 === item.key)[0].list;}
  if (cdfine13.value == '1') {cdfine13List.value = bbb.filter(item => 13 === item.key)[0].list;}
  if (cdfine14.value == '1') {cdfine14List.value = bbb.filter(item => 14 === item.key)[0].list;}
  if (cdfine15.value == '1') {cdfine15List.value = bbb.filter(item => 15 === item.key)[0].list;}
  if (cdfine16.value == '1') {cdfine16List.value = bbb.filter(item => 16 === item.key)[0].list;}
  if (cdfine17.value == '1') {cdfine17List.value = bbb.filter(item => 17 === item.key)[0].list;}
  if (cdfine18.value == '1') {cdfine18List.value = bbb.filter(item => 18 === item.key)[0].list;}
  if (cdfine19.value == '1') {cdfine19List.value = bbb.filter(item => 19 === item.key)[0].list;}
  if (cdfine20.value == '1') {cdfine20List.value = bbb.filter(item => 20 === item.key)[0].list;}
  if (cdfine21.value == '1') {cdfine21List.value = bbb.filter(item => 21 === item.key)[0].list;}
  if (cdfine22.value == '1') {cdfine22List.value = bbb.filter(item => 22 === item.key)[0].list;}
  if (cdfine23.value == '1') {cdfine23List.value = bbb.filter(item => 23 === item.key)[0].list;}
  if (cdfine24.value == '1') {cdfine24List.value = bbb.filter(item => 24 === item.key)[0].list;}
  if (cdfine25.value == '1') {cdfine25List.value = bbb.filter(item => 25 === item.key)[0].list;}
  if (cdfine26.value == '1') {cdfine26List.value = bbb.filter(item => 26 === item.key)[0].list;}
  if (cdfine27.value == '1') {cdfine27List.value = bbb.filter(item => 27 === item.key)[0].list;}
  if (cdfine28.value == '1') {cdfine28List.value = bbb.filter(item => 28 === item.key)[0].list;}
  if (cdfine29.value == '1') {cdfine29List.value = bbb.filter(item => 29 === item.key)[0].list;}
  if (cdfine30.value == '1') {cdfine30List.value = bbb.filter(item => 30 === item.key)[0].list;}

  if (cdfine1.value == '1') {
    cdfine1List.value.filter(v=> v.value = v.key);
    let name = "自定义1";
    name = findZiDingYiName(name);
    acccolumns.value.filter(v => v.dataIndex === 'cdfine1')[0].title = name;
  }else{
    const index = acccolumns.value.findIndex(v=>  v.dataIndex === 'cdfine1');
    if(index >= 0) {acccolumns.value.splice(index, 1);}
  }
  if (cdfine2.value == '1') {
    cdfine2List.value.filter(v=> v.value = v.key);
    let name = "自定义2";
    name = findZiDingYiName(name);
    acccolumns.value.filter(v => v.dataIndex === 'cdfine2')[0].title = name;
  }else{
    const index = acccolumns.value.findIndex(v=>  v.dataIndex === 'cdfine2');
    if(index >= 0) {acccolumns.value.splice(index, 1);}
  }
  if (cdfine3.value == '1') {
    cdfine3List.value.filter(v=> v.value = v.key);
    let name = "自定义3";
    name = findZiDingYiName(name);
    acccolumns.value.filter(v => v.dataIndex === 'cdfine3')[0].title = name;
  }else{
    const index = acccolumns.value.findIndex(v=>  v.dataIndex === 'cdfine3');
    if(index >= 0) {acccolumns.value.splice(index, 1);}
  }
  if (cdfine4.value == '1') {
    let name = "自定义4";
    name = findZiDingYiName(name);
    acccolumns.value.filter(v => v.dataIndex === 'cdfine4')[0].title = name;
  }else{
    const index = acccolumns.value.findIndex(v=>  v.dataIndex === 'cdfine4');
    if(index >= 0) {acccolumns.value.splice(index, 1);}
  }
  if (cdfine5.value == '1') {
    let name = "自定义5";
    name = findZiDingYiName(name);
    acccolumns.value.filter(v => v.dataIndex === 'cdfine5')[0].title = name;
  }else{
    const index = acccolumns.value.findIndex(v=>  v.dataIndex === 'cdfine5');
    if(index >= 0) {acccolumns.value.splice(index, 1);}
  }
  if (cdfine6.value == '1') {
    let name = "自定义6";
    name = findZiDingYiName(name);
    acccolumns.value.filter(v => v.dataIndex === 'cdfine6')[0].title = name;
  }else{
    const index = acccolumns.value.findIndex(v=>  v.dataIndex === 'cdfine6');
    if(index >= 0) {acccolumns.value.splice(index, 1);}
  }
  if (cdfine7.value == '1') {
    let name = "自定义7";
    name = findZiDingYiName(name);
    acccolumns.value.filter(v => v.dataIndex === 'cdfine7')[0].title = name;
  }else{
    const index = acccolumns.value.findIndex(v=>  v.dataIndex === 'cdfine7');
    if(index >= 0) {acccolumns.value.splice(index, 1);}
  }
  if (cdfine8.value == '1') {
    let name = "自定义8";
    name = findZiDingYiName(name);
    acccolumns.value.filter(v => v.dataIndex === 'cdfine8')[0].title = name;
  }else{
    const index = acccolumns.value.findIndex(v=>  v.dataIndex === 'cdfine8');
    if(index >= 0) {acccolumns.value.splice(index, 1);}
  }
  if (cdfine9.value == '1') {
    let name = "自定义9";
    name = findZiDingYiName(name);
    acccolumns.value.filter(v => v.dataIndex === 'cdfine9')[0].title = name;
  }else{
    const index = acccolumns.value.findIndex(v=>  v.dataIndex === 'cdfine9');
    if(index > 0) {acccolumns.value.splice(index, 1);}
  }

  if (cdfine10.value == '1') {
    let name = "自定义10";
    name = findZiDingYiName(name);
    acccolumns.value.filter(v => v.dataIndex === 'cdfine10')[0].title = name;
  }else{
    const index = acccolumns.value.findIndex(v=>  v.dataIndex === 'cdfine10');
    if(index > 0) {acccolumns.value.splice(index, 1);}
  }
  if (cdfine11.value == '1') {
    let name = "自定义11";
    name = findZiDingYiName(name);
    acccolumns.value.filter(v => v.dataIndex === 'cdfine11')[0].title = name;
  }else{
    const index = acccolumns.value.findIndex(v=>  v.dataIndex === 'cdfine11');
    if(index > 0) {acccolumns.value.splice(index, 1);}
  }
  if (cdfine12.value == '1') {
    let name = "自定义12";
    name = findZiDingYiName(name);
    acccolumns.value.filter(v => v.dataIndex === 'cdfine12')[0].title = name;
  }else{
    const index = acccolumns.value.findIndex(v=>  v.dataIndex === 'cdfine12');
    if(index > 0) {acccolumns.value.splice(index, 1);}
  }
  if (cdfine13.value == '1') {
    let name = "自定义13";
    name = findZiDingYiName(name);
    acccolumns.value.filter(v => v.dataIndex === 'cdfine13')[0].title = name;
  }else{
    const index = acccolumns.value.findIndex(v=>  v.dataIndex === 'cdfine13');
    if(index > 0) {acccolumns.value.splice(index, 1);}
  }
  if (cdfine14.value == '1') {
    let name = "自定义14";
    name = findZiDingYiName(name);
    acccolumns.value.filter(v => v.dataIndex === 'cdfine14')[0].title = name;
  }else{
    const index = acccolumns.value.findIndex(v=>  v.dataIndex === 'cdfine14');
    if(index > 0) {acccolumns.value.splice(index, 1);}
  }
  if (cdfine15.value == '1') {
    let name = "自定义15";
    name = findZiDingYiName(name);
    acccolumns.value.filter(v => v.dataIndex === 'cdfine15')[0].title = name;
  }else{
    const index = acccolumns.value.findIndex(v=>  v.dataIndex === 'cdfine15');
    if(index > 0) {acccolumns.value.splice(index, 1);}
  }
  if (cdfine16.value == '1') {
    let name = "自定义16";
    name = findZiDingYiName(name);
    acccolumns.value.filter(v => v.dataIndex === 'cdfine16')[0].title = name;
  }else{
    const index = acccolumns.value.findIndex(v=>  v.dataIndex === 'cdfine16');
    if(index > 0) {acccolumns.value.splice(index, 1);}
  }
  if (cdfine17.value == '1') {
    let name = "自定义17";
    name = findZiDingYiName(name);
    acccolumns.value.filter(v => v.dataIndex === 'cdfine17')[0].title = name;
  }else{
    const index = acccolumns.value.findIndex(v=>  v.dataIndex === 'cdfine17');
    if(index > 0) {acccolumns.value.splice(index, 1);}
  }
  if (cdfine18.value == '1') {
    let name = "自定义18";
    name = findZiDingYiName(name);
    acccolumns.value.filter(v => v.dataIndex === 'cdfine18')[0].title = name;
  }else{
    const index = acccolumns.value.findIndex(v=>  v.dataIndex === 'cdfine18');
    if(index > 0) {acccolumns.value.splice(index, 1);}
  }
  if (cdfine19.value == '1') {
    let name = "自定义19";
    name = findZiDingYiName(name);
    acccolumns.value.filter(v => v.dataIndex === 'cdfine19')[0].title = name;
  }else{
    const index = acccolumns.value.findIndex(v=>  v.dataIndex === 'cdfine19');
    if(index > 0) {acccolumns.value.splice(index, 1);}
  }

  if (cdfine20.value == '1') {
    let name = "自定义20";
    name = findZiDingYiName(name);
    acccolumns.value.filter(v => v.dataIndex === 'cdfine20')[0].title = name;
  }else{
    const index = acccolumns.value.findIndex(v=>  v.dataIndex === 'cdfine20');
    if(index > 0) {acccolumns.value.splice(index, 1);}
  }

  if (cdfine21.value == '1') {
    let name = "自定义21";
    name = findZiDingYiName(name);
    acccolumns.value.filter(v => v.dataIndex === 'cdfine21')[0].title = name;
  }else{
    const index = acccolumns.value.findIndex(v=>  v.dataIndex === 'cdfine21');
    if(index > 0) {acccolumns.value.splice(index, 1);}
  }
  if (cdfine22.value == '1') {
    let name = "自定义22";
    name = findZiDingYiName(name);
    acccolumns.value.filter(v => v.dataIndex === 'cdfine22')[0].title = name;
  }else{
    const index = acccolumns.value.findIndex(v=>  v.dataIndex === 'cdfine22');
    if(index > 0) {acccolumns.value.splice(index, 1);}
  }
  if (cdfine23.value == '1') {
    let name = "自定义23";
    name = findZiDingYiName(name);
    acccolumns.value.filter(v => v.dataIndex === 'cdfine23')[0].title = name;
  }else{
    const index = acccolumns.value.findIndex(v=>  v.dataIndex === 'cdfine23');
    if(index > 0) {acccolumns.value.splice(index, 1);}
  }
  if (cdfine24.value == '1') {
    let name = "自定义24";
    name = findZiDingYiName(name);
    acccolumns.value.filter(v => v.dataIndex === 'cdfine24')[0].title = name;
  }else{
    const index = acccolumns.value.findIndex(v=>  v.dataIndex === 'cdfine24');
    if(index > 0) {acccolumns.value.splice(index, 1);}
  }
  if (cdfine25.value == '1') {
    let name = "自定义25";
    name = findZiDingYiName(name);
    acccolumns.value.filter(v => v.dataIndex === 'cdfine25')[0].title = name;
  }else{
    const index = acccolumns.value.findIndex(v=>  v.dataIndex === 'cdfine25');
    if(index > 0) {acccolumns.value.splice(index, 1);}
  }
  if (cdfine26.value == '1') {
    let name = "自定义26";
    name = findZiDingYiName(name);
    acccolumns.value.filter(v => v.dataIndex === 'cdfine26')[0].title = name;
  }else{
    const index = acccolumns.value.findIndex(v=>  v.dataIndex === 'cdfine26');
    if(index > 0) {acccolumns.value.splice(index, 1);}
  }
  if (cdfine27.value == '1') {
    let name = "自定义27";
    name = findZiDingYiName(name);
    acccolumns.value.filter(v => v.dataIndex === 'cdfine27')[0].title = name;
  }else{
    const index = acccolumns.value.findIndex(v=>  v.dataIndex === 'cdfine27');
    if(index > 0) {acccolumns.value.splice(index, 1);}
  }
  if (cdfine28.value == '1') {
    let name = "自定义28";
    name = findZiDingYiName(name);
    acccolumns.value.filter(v => v.dataIndex === 'cdfine28')[0].title = name;
  }else{
    const index = acccolumns.value.findIndex(v=>  v.dataIndex === 'cdfine28');
    if(index > 0) {acccolumns.value.splice(index, 1);}
  }
  if (cdfine29.value == '1') {
    let name = "自定义29";
    name = findZiDingYiName(name);
    acccolumns.value.filter(v => v.dataIndex === 'cdfine29')[0].title = name;
  }else{
    const index = acccolumns.value.findIndex(v=>  v.dataIndex === 'cdfine29');
    if(index >= 0) {acccolumns.value.splice(index, 1);}
  }
  if (cdfine30.value == '1') {
    let name = "自定义30";
    name = findZiDingYiName(name);
    acccolumns.value.filter(v => v.dataIndex === 'cdfine30')[0].title = name;
  }else{
    const index = acccolumns.value.findIndex(v=>  v.dataIndex === 'cdfine30');
    if(index >= 0) {acccolumns.value.splice(index, 1);}
  }

  if (!(bnum.value == '1')) {
    const index1 = acccolumns.value.findIndex(v=>  v.dataIndex === 'bnum');
    if(index1 >= 0) {acccolumns.value.splice(index1, 1);}
    const index2 = acccolumns.value.findIndex(v=>  v.dataIndex === 'bnum2');
    if(index2 >= 0) {acccolumns.value.splice(index2, 1);}
  }
  if (!(currency.value == '1')) {
    const index1 = acccolumns.value.findIndex(v=>  v.dataIndex === 'currency');
    if(index1 >= 0) {acccolumns.value.splice(index1, 1);}
    const index2 = acccolumns.value.findIndex(v=>  v.dataIndex === 'currency2');
    if(index2 >= 0) {acccolumns.value.splice(index2, 1);}
  }
  if (!containsThree.value){
    const index1 = acccolumns.value.findIndex(v=>  v.dataIndex === 'dbilldate');
    if(index1 >= 0) {acccolumns.value.splice(index1, 1);}
    const index2 = acccolumns.value.findIndex(v=>  v.dataIndex === 'inoid');
    if(index2 >= 0) {acccolumns.value.splice(index2, 1);}
    const index3 = acccolumns.value.findIndex(v=>  v.dataIndex === 'cdigest');
    if(index3 >= 0) {acccolumns.value.splice(index2, 1);}
  }
    let index1 = acccolumns.value.findIndex(v=>  v.dataIndex === 'ljMd');
    if(index1 >= 0 &&　!isLj.value) {acccolumns.value.splice(index1, 1);}
    index1 = acccolumns.value.findIndex(v=>  v.dataIndex === 'ljMc');
    if(index1 >= 0 &&　!isLj.value) {acccolumns.value.splice(index1, 1);}
    if (!isLj.value || !(bnum.value == '1') || !(currency.value == '1')){
      index1 = acccolumns.value.findIndex(v=>  v.dataIndex === 'ljSlMc');
      if(index1 >= 0  &&　 !(bnum.value == '1')) {acccolumns.value.splice(index1, 1);}
      index1 = acccolumns.value.findIndex(v=>  v.dataIndex === 'ljSlMd');
      if(index1 >= 0  &&　 !(bnum.value == '1')) {acccolumns.value.splice(index1, 1);}
      index1 = acccolumns.value.findIndex(v=>  v.dataIndex === 'ljWbMc');
      if(index1 >= 0 &&　 !(currency.value == '1')) {acccolumns.value.splice(index1, 1);}
      index1 = acccolumns.value.findIndex(v=>  v.dataIndex === 'ljWbMd');
      if(index1 >= 0 &&　 !(currency.value == '1')) {acccolumns.value.splice(index1, 1);}
    }

  let accList = data.map.accList;
  for (let i = 0; i < accList.length; i++) {
    let cc = {};
    cc['key'] = accList[i].id == null ? "" : accList[i].id;
    cc["dbilldate"] = accList[i].dbillDate == null ? "" : accList[i].dbillDate;
    cc["inoid"] = accList[i].inoId == null ? "" : accList[i].inoId;
    cc["cdigest"] = accList[i].cdigest == null ? "" : accList[i].cdigest;

    cc["uniqueCode"] = accList[i].uniqueCode == null ? "" : accList[i].uniqueCode;
    cc["vouchUnCode"] = accList[i].vouchUnCode == null ? "" : accList[i].vouchUnCode;

    if (bperson.value == '1') {
      cc["bperson"] = accList[i].cpersonId == null ? "" : accList[i].cpersonId;
    }
    if (bdept.value == '1') {
      cc["bdept"] = accList[i].cdeptId == null ? "" : accList[i].cdeptId;
    }
    if (bcus.value == '1') {
      cc["bcus"] = accList[i].ccusId == null ? "" : accList[i].ccusId;
    }
    if (bsup.value == '1') {
      cc["bsup"] = accList[i].csupId == null ? "" : accList[i].csupId;
    }
    if (bitem.value == '1') {
      cc["bitemClass"] = accList[i].projectClassId == null ? "" : accList[i].projectClassId;
    }
    if (bitem.value == '1') {
      cc["bitem"] = accList[i].projectId == null ? "" : accList[i].projectId;
    }

    cc["caozuo"] = accList[i].id;
    cc["md"] = accList[i].md == 0 ? "" : accList[i].md;
    cc["mc"] = accList[i].mc == 0 ? "" : accList[i].mc;

    cc["ljMd"] = accList[i].ljMd == 0 ? "" : accList[i].ljMd;
    cc["ljMc"] = accList[i].ljMc == 0 ? "" : accList[i].ljMc;
    if (bnum.value == '1') {
      cc["bnum"] = accList[i].md == 0 ? accList[i].ncS : accList[i].ndS;
      cc["ljSlMd"] = accList[i].ljMd == 0 ? accList[i].ljSlMd : accList[i].ljSlMd;
      cc["ljSlMc"] = accList[i].ljMc == 0 ? accList[i].ljSlMc : accList[i].ljSlMc;
    }

    if (bnum.value == '1') {
      cc["bnum2"] = accList[i].cunitPrice;
    }

    if (currency.value == '1') {
      cc["currency"] = accList[i].md == 0 ? accList[i].nfratMc : accList[i].nfratMd;
      cc["ljWbMd"] = accList[i].ljMd == 0 ? accList[i].ljWbMd : accList[i].ljWbMd;
      cc["ljWbMc"] = accList[i].ljMc == 0 ? accList[i].ljWbMc : accList[i].ljWbMc;
    }

    if (currency.value == '1') {
      cc["currency2"] = accList[i].mdF;
    }

    if (cdfine1.value == '1') {
      cc["cdfine1"] = accList[i].cdfine1;
    }
    if (cdfine2.value == '1') {
      cc["cdfine2"] = accList[i].cdfine2;
    }
    if (cdfine3.value == '1') {
      cc["cdfine3"] = accList[i].cdfine3;
    }
    if (cdfine4.value == '1') {
      cc["cdfine4"] = accList[i].cdfine4;
    }
    if (cdfine5.value == '1') {
      cc["cdfine5"] = accList[i].cdfine5;
    }
    if (cdfine6.value == '1') {
      cc["cdfine6"] = accList[i].cdfine6;
    }
    if (cdfine7.value == '1') {
      cc["cdfine7"] = accList[i].cdfine7;
    }
    if (cdfine8.value == '1') {
      cc["cdfine8"] = accList[i].cdfine8;
    }
    if (cdfine9.value == '1') {
      cc["cdfine9"] = accList[i].cdfine9;
    }
    if (cdfine10.value == '1') {
      cc["cdfine10"] = accList[i].cdfine10;
    }

    if (cdfine11.value == '1') {
      cc["cdfine11"] = accList[i].cdfine11;
    }
    if (cdfine12.value == '1') {
      cc["cdfine12"] = accList[i].cdfine12;
    }
    if (cdfine13.value == '1') {
      cc["cdfine13"] = accList[i].cdfine13;
    }
    if (cdfine14.value == '1') {
      cc["cdfine14"] = accList[i].cdfine14;
    }
    if (cdfine15.value == '1') {
      cc["cdfine15"] = accList[i].cdfine15;
    }
    if (cdfine16.value == '1') {
      cc["cdfine16"] = accList[i].cdfine16;
    }
    if (cdfine17.value == '1') {
      cc["cdfine17"] = accList[i].cdfine17;
    }
    if (cdfine18.value == '1') {
      cc["cdfine18"] = accList[i].cdfine18;
    }
    if (cdfine19.value == '1') {
      cc["cdfine19"] = accList[i].cdfine19;
    }
    if (cdfine20.value == '1') {
      cc["cdfine20"] = accList[i].cdfine20;
    }

    if (cdfine21.value == '1') {
      cc["cdfine21"] = accList[i].cdfine21;
    }
    if (cdfine22.value == '1') {
      cc["cdfine22"] = accList[i].cdfine22;
    }
    if (cdfine23.value == '1') {
      cc["cdfine23"] = accList[i].cdfine23;
    }
    if (cdfine24.value == '1') {
      cc["cdfine24"] = accList[i].cdfine24;
    }
    if (cdfine25.value == '1') {
      cc["cdfine25"] = accList[i].cdfine25;
    }
    if (cdfine26.value == '1') {
      cc["cdfine26"] = accList[i].cdfine26;
    }
    if (cdfine27.value == '1') {
      cc["cdfine27"] = accList[i].cdfine27;
    }
    if (cdfine28.value == '1') {
      cc["cdfine28"] = accList[i].cdfine28;
    }
    if (cdfine29.value == '1') {
      cc["cdfine29"] = accList[i].cdfine29;
    }
    if (cdfine30.value == '1') {
      cc["cdfine30"] = accList[i].cdfine30;
    }
    delete editableData2[accList[i].id];
    accli.value.push(cc);
  }
  accflag.value = true;
});

const shanchu = async (data: any,text) => {
  alert("shanchu"+"123" + "md:"+data.md+"mc:"+data.mc+"id:"+text);
};

const editableData2 = reactive({});
import {hasBlank} from "/@/api/task-api/tast-bus-api";
import {countHxRecord} from "/@/api/record/system/write-off";
async function handleOk() {
  lock0K.value = false
  // 添加逻辑
  for (let i = 0;i< accli.value.length;i++){
    let obj = accli.value[i]
    await editTable(obj.caozuo)
    await baocun(obj.caozuo)
  }
  lock0K.value = true
  closeModal();
  jiazai();
  if (delList.value.length > 0){
    for (let i = 0;i< delList.value.length;i++){
      let obj:any = delList.value[i]
      if (!hasBlank(obj.uniqueCode)) await deleteTable(obj.caozuo)
    }
  }
}

async function handleClose() {
  closeModal();
  jiazai();
  return true;
}

async function jiazai() {
  if (baocunflag.value) {//
    emit('save');
  }
}

const editTable = async (key: string) => {
  delete editableData2[key];
  editableData2[key] = cloneDeep(accli.value.filter(item => key === item.caozuo)[0]);
  if(editableData2[key].dbilldate.length != 10){
    editableData2[key].dbilldate = maxDate.value;
  }
};

const deleteTable = async (key: string) => {
    await useRouteApi(delInitalBalanceFuZhu, {schemaName: props.database})({
      str: key,
      iyear: iyear.value,
      databasenum: props.database,
      ccode: ccode.value
    })
}

const filterOption = (input: string, option: any) => {
  return option.label.indexOf(input) >= 0;
};


const handleChangeBitem = (caozuoId: string) => {
  let codeList:any = bitemList.value.filter(v=> v.value === editableData2[caozuoId].bitem);
  if(codeList.length > 0) {
    let citemClass = codeList[0].itemClass;
    let classList:any = bitemClassList.value.filter(v=> v.value === citemClass);
    if(classList.length > 0){
      let classnum = classList[0].value;
      editableData2[caozuoId].bitemClass = classnum;
    }
  }
}

const handleChangeBitemClass = (caozuoId: string) => {
  //alert(caozuoId + "==" + editableData2[caozuoId].bitemClass);
}

const changeMd = (caozuoId: string) => {
  editableData2[caozuoId].mc = "";
  editableData2[caozuoId].md = editableData2[caozuoId].md.replace("-","");
  if(bnum.value == '1') {
    jisuanNumAndNum2(caozuoId);
  }
}

const changeMc = (caozuoId: string) => {
  editableData2[caozuoId].md = "";
  editableData2[caozuoId].mc = editableData2[caozuoId].mc.replace("-","");
  if(bnum.value == '1') {
    jisuanNumAndNum2(caozuoId);
  }
}

// 金额格式化
function money(val: any) {
  if (val == null) val = '';
  val = val.toString().replace(/,/g, '');
  if (isNaN(val)) {
    val = '0';
  }
  let fs = val.indexOf('-') != -1
  const sign = val === (val = Math.abs(val));
  val = Math.floor(val * 100 + 0.50000000001);
  let cents:any = val % 100;
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
  editableData2[caozuoId].bnum2 = '';
  jisuanNumAndNum2(caozuoId);
}

const changeBNum2 = (caozuoId: string) => {
  jisuanNumAndNum2(caozuoId);
}

const jisuanNumAndNum2 = (caozuoId: string) => {
  var md = editableData2[caozuoId].md;
  var mc = editableData2[caozuoId].mc;
  var num = editableData2[caozuoId].bnum;
  var num2 = editableData2[caozuoId].bnum2;
  if(isNaN(md)){md = 0;}
  if(isNaN(mc)){mc = 0;}
  if(isNaN(num)){num = 0;}
  if(isNaN(num2)){num2 = 0;}
  num = parseFloat(num);
  num2 = parseFloat(num2);
  var money = md + mc;
  if(num != 0 && money != 0){
    editableData2[caozuoId].bnum2 = (money/num).toFixed(2);
    if(isNaN(editableData2[caozuoId].bnum2)){
      editableData2[caozuoId].bnum2 = '';
    }
  }else{
    editableData2[caozuoId].bnum2 = '';
  }
}

const changecurrency = (caozuoId: string) => {

}

const changecurrency2 = (caozuoId: string) => {

}

const changeMinInoId = (caozuoId: string) => {

}

const datainfo:any = ref([]);

const baocun = async (key: string) => {

  var bpersonValue = editableData2[key].bperson;
  var bdeptValue = editableData2[key].bdept;
  var bcusValue = editableData2[key].bcus;
  var bsupValue = editableData2[key].bsup;
  handleChangeBitem(key);
  var bitemClassValue = editableData2[key].bitemClass;
  var bitemValue = editableData2[key].bitem;

  var cdfine1Value = editableData2[key].cdfine1;
  var cdfine2Value = editableData2[key].cdfine2;
  var cdfine3Value = editableData2[key].cdfine3;
  var cdfine4Value = editableData2[key].cdfine4;
  var cdfine5Value = editableData2[key].cdfine5;
  var cdfine6Value = editableData2[key].cdfine6;
  var cdfine7Value = editableData2[key].cdfine7;
  var cdfine8Value = editableData2[key].cdfine8;
  var cdfine9Value = editableData2[key].cdfine9;

  var cdfine10Value = editableData2[key].cdfine10;
  var cdfine11Value = editableData2[key].cdfine11;
  var cdfine12Value = editableData2[key].cdfine12;
  var cdfine13Value = editableData2[key].cdfine13;
  var cdfine14Value = editableData2[key].cdfine14;
  var cdfine15Value = editableData2[key].cdfine15;
  var cdfine16Value = editableData2[key].cdfine16;
  var cdfine17Value = editableData2[key].cdfine17;
  var cdfine18Value = editableData2[key].cdfine18;
  var cdfine19Value = editableData2[key].cdfine19;

  var cdfine20Value = editableData2[key].cdfine20;
  var cdfine21Value = editableData2[key].cdfine21;
  var cdfine22Value = editableData2[key].cdfine22;
  var cdfine23Value = editableData2[key].cdfine23;
  var cdfine24Value = editableData2[key].cdfine24;
  var cdfine25Value = editableData2[key].cdfine25;
  var cdfine26Value = editableData2[key].cdfine26;
  var cdfine27Value = editableData2[key].cdfine27;
  var cdfine28Value = editableData2[key].cdfine28;
  var cdfine29Value = editableData2[key].cdfine29;
  var cdfine30Value = editableData2[key].cdfine30;

  var dbilldateValue = editableData2[key].dbilldate;

  if(dbilldateValue.length==undefined){
    dbilldateValue = new Date(dbilldateValue);
    dbilldateValue = dbilldateValue.getFullYear() + "-" + (dbilldateValue.getMonth()> 9 ? (dbilldateValue.getMonth() + 1) : "0" + (dbilldateValue.getMonth() + 1)) + "-" +(dbilldateValue.getDate()> 9 ? (dbilldateValue.getDate()) : "0" + (dbilldateValue.getDate()));
    editableData2[key].dbilldate = dbilldateValue;
  }

  var inoidValue = editableData2[key].inoid;
  var cdigestValue = editableData2[key].cdigest;
  var uniqueCodeValue = editableData2[key].uniqueCode;
  var vouchUnCodeValue = editableData2[key].vouchUnCode;
  if (dbilldateValue == undefined || dbilldateValue == '' || dbilldateValue.length != 10 || dbilldateValue > maxDate.value) {
    editableData2[key].dbilldate = maxDate.value;
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
  editableData2[key].md = editableData2[key].md == null?'':editableData2[key].md.replace(/,/g,'')
  editableData2[key].mc = editableData2[key].mc == null?'':editableData2[key].mc.replace(/,/g,'')
  editableData2[key].currency = editableData2[key].currency == null?'':editableData2[key].currency.replace(/,/g,'')
  if (isNaN(editableData2[key].md)) {editableData2[key].md = "";}
  if (isNaN(editableData2[key].mc)) {editableData2[key].mc = "";}

  if (editableData2[key].md != '') {editableData2[key].mc = '';}

  if(editableData2[key].md != ''){if(parseFloat(editableData2[key].md) == 0.0){editableData2[key].md = '';}}
  if(editableData2[key].mc != ''){if(parseFloat(editableData2[key].mc) == 0.0){editableData2[key].mc = '';}}

  /*if (editableData2[key].md != '') {editableData2[key].md = money(editableData2[key].md);}
  if (editableData2[key].mc != '') {editableData2[key].mc = money(editableData2[key].mc);}*/
  var mdValue = editableData2[key].md;
  var mcValue = editableData2[key].mc;
  var ljMdValue = editableData2[key].ljMd == null?'':editableData2[key].ljMd.replace(/,/g,'');
  var ljMcValue = editableData2[key].ljMc == null?'':editableData2[key].ljMc.replace(/,/g,'');
  var mmdd:any = 0.0;
  var mmcc:any = 0.0;

  if(mdValue != ''){mmdd = mdValue;}
  if(mcValue != ''){mmcc = mcValue;}

  if(parseFloat(mmdd) + parseFloat(mmcc) == 0.0){
    showMsg("请填写借贷方金额");
    return;
  }

  if (bnum.value == '1') {
    if (isNaN(editableData2[key].bnum)) {
      editableData2[key].bnum = "";
    }
    if (isNaN(editableData2[key].bnum2)) {
      editableData2[key].bnum2 = "";
    }
    var bnumValue = editableData2[key].bnum;
    var bnum2Value = editableData2[key].bnum2;
    var ljSlMdValue = editableData2[key].ljSlMd;
    var ljSlMcValue = editableData2[key].ljSlMc;
    if ((bnumValue == undefined || bnumValue == '')) {
      showMsg("请填写数量");
      return;
    }
   /* if ((bnum2Value == undefined || bnum2Value == '')) {
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
  if (currency.value  == '1') {
    if (isNaN(editableData2[key].currency)) {
      editableData2[key].currency = "";
    }
    if (isNaN(editableData2[key].currency2)) {
      editableData2[key].currency2 = "";
    }
    var currencyValue = editableData2[key].currency;
    var currency2Value = editableData2[key].currency2;
    if ((currencyValue == undefined || currencyValue == '')) {
      showMsg("请填写原币金额");
      return;
    }
    var ljWbMdValue = editableData2[key].ljWbMd == null?'':editableData2[key].ljWbMd.replace(/,/g,'');
    var ljWbMcValue = editableData2[key].ljWbMc == null?'':editableData2[key].ljWbMc.replace(/,/g,'');
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

  datainfo.value = [];

  let mx = {};
  mx["bperson"] = "0";mx["bdept"] = "0";mx["bcus"] = "0";
  mx["bsup"] = "0";mx["bitem"] = "0";mx["bitemClass"] = "0";

  mx["bnum"] = "0";mx["currency"] = "0";
  mx["ljSlMd"] = "0";mx["ljSlMc"] = "0";
  mx["ljWbMd"] = "0";mx["ljWbMc"] = "0";

  mx["ncS"] = "0";mx["ndS"] = "0";mx["ncnum"] = "0";

  mx["nfratMc"] = "0";mx["nfratMd"] = "0";mx["nfrat"] = "0";

  mx["cdfine1"] = "0";mx["cdfine2"] = "0";mx["cdfine3"] = "0";mx["cdfine4"] = "0";mx["cdfine5"] = "0";
  mx["cdfine6"] = "0";mx["cdfine7"] = "0";mx["cdfine8"] = "0";mx["cdfine9"] = "0";mx["cdfine10"] = "0";
  mx["cdfine11"] = "0";mx["cdfine12"] = "0";mx["cdfine13"] = "0";mx["cdfine14"] = "0";mx["cdfine15"] = "0";
  mx["cdfine16"] = "0";mx["cdfine17"] = "0";mx["cdfine18"] = "0";mx["cdfine19"] = "0";mx["cdfine20"] = "0";
  mx["cdfine21"] = "0";mx["cdfine22"] = "0";mx["cdfine23"] = "0";mx["cdfine24"] = "0";mx["cdfine25"] = "0";
  mx["cdfine26"] = "0";mx["cdfine27"] = "0";mx["cdfine28"] = "0";mx["cdfine29"] = "0";mx["cdfine30"] = "0";

  mx["dbillDate"] = dbilldateValue;
  mx["inoId"] = inoidValue;
  mx["cdigest"] = cdigestValue;
  mx["uniqueCode"] = uniqueCodeValue;
  mx["vouchUnCode"] = vouchUnCodeValue;

  if(editableData2[key].caozuo > 0) {
    mx["accvouid"] = editableData2[key].caozuo;
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
    if(mdValue == ''){
      mx["ncS"] = bnumValue;
    }else{
      mx["ndS"] = bnumValue;
    }
    mx["ncnum"] = bnum2Value;
    if (isLj.value){ mx["ljSlMd"] = ljSlMdValue;mx["ljSlMc"] = ljSlMcValue;}
  }else{
    mx["bnum"] = "0";
  }

  //外币核算
  if (currency.value == '1') {
    mx["currency"] = "1";
    if(mdValue == ''){
      mx["nfratMc"] = currencyValue;
    }else{
      mx["nfratMd"] = currencyValue;
    }
    mx["nfrat"] = currency2Value;
    if (isLj.value){mx["ljWbMd"] = ljWbMdValue;mx["ljWbMc"] = ljWbMcValue;}
  }else{
    mx["currency"] = "0";
  }

  mx["md"] = mdValue;
  mx["mc"] = mcValue;
  if (isLj.value){
    mx["ljMd"] = ljMdValue;
    mx["ljMc"] = ljMcValue;
  }

  if (cdfine1.value == '1') {mx["cdfine1"] = "1";mx["cdfine1Id"] = cdfine1Value;}
  if (cdfine2.value == '1') {mx["cdfine2"] = "1";mx["cdfine2Id"] = cdfine2Value;}
  if (cdfine3.value == '1') {mx["cdfine3"] = "1";mx["cdfine3Id"] = cdfine3Value;}
  if (cdfine4.value == '1') {mx["cdfine4"] = "1";mx["cdfine4Id"] = cdfine4Value;}
  if (cdfine5.value == '1') {mx["cdfine5"] = "1";mx["cdfine5Id"] = cdfine5Value;}
  if (cdfine6.value == '1') {mx["cdfine6"] = "1";mx["cdfine6Id"] = cdfine6Value;}
  if (cdfine7.value == '1') {mx["cdfine7"] = "1";mx["cdfine7Id"] = cdfine7Value;}
  if (cdfine8.value == '1') {mx["cdfine8"] = "1";mx["cdfine8Id"] = cdfine8Value;}
  if (cdfine9.value == '1') {mx["cdfine9"] = "1";mx["cdfine9Id"] = cdfine9Value;}

  if (cdfine10.value == '1') {mx["cdfine10"] = "1";mx["cdfine10Id"] = cdfine10Value;}
  if (cdfine11.value == '1') {mx["cdfine11"] = "1";mx["cdfine11Id"] = cdfine11Value;}
  if (cdfine12.value == '1') {mx["cdfine12"] = "1";mx["cdfine12Id"] = cdfine12Value;}
  if (cdfine13.value == '1') {mx["cdfine13"] = "1";mx["cdfine13Id"] = cdfine13Value;}
  if (cdfine14.value == '1') {mx["cdfine14"] = "1";mx["cdfine14Id"] = cdfine14Value;}
  if (cdfine15.value == '1') {mx["cdfine15"] = "1";mx["cdfine15Id"] = cdfine15Value;}
  if (cdfine16.value == '1') {mx["cdfine16"] = "1";mx["cdfine16Id"] = cdfine16Value;}
  if (cdfine17.value == '1') {mx["cdfine17"] = "1";mx["cdfine17Id"] = cdfine17Value;}
  if (cdfine18.value == '1') {mx["cdfine18"] = "1";mx["cdfine18Id"] = cdfine18Value;}
  if (cdfine19.value == '1') {mx["cdfine19"] = "1";mx["cdfine19Id"] = cdfine19Value;}

  if (cdfine20.value == '1') {mx["cdfine20"] = "1";mx["cdfine20Id"] = cdfine20Value;}
  if (cdfine21.value == '1') {mx["cdfine21"] = "1";mx["cdfine21Id"] = cdfine21Value;}
  if (cdfine22.value == '1') {mx["cdfine22"] = "1";mx["cdfine22Id"] = cdfine22Value;}
  if (cdfine23.value == '1') {mx["cdfine23"] = "1";mx["cdfine23Id"] = cdfine23Value;}
  if (cdfine24.value == '1') {mx["cdfine24"] = "1";mx["cdfine24Id"] = cdfine24Value;}
  if (cdfine25.value == '1') {mx["cdfine25"] = "1";mx["cdfine25Id"] = cdfine25Value;}
  if (cdfine26.value == '1') {mx["cdfine26"] = "1";mx["cdfine26Id"] = cdfine26Value;}
  if (cdfine27.value == '1') {mx["cdfine27"] = "1";mx["cdfine27Id"] = cdfine27Value;}
  if (cdfine28.value == '1') {mx["cdfine28"] = "1";mx["cdfine28Id"] = cdfine28Value;}
  if (cdfine29.value == '1') {mx["cdfine29"] = "1";mx["cdfine29Id"] = cdfine29Value;}
  if (cdfine30.value == '1') {mx["cdfine30"] = "1";mx["cdfine30Id"] = cdfine30Value;}

  datainfo.value = mx;
  let nn = await useRouteApi(saveData, {schemaName: props.database})(datainfo.value)

  baocunflag.value = true;

  Object.assign(accli.value.filter(item => key === item.caozuo)[0], editableData2[key]);
  delete editableData2[key];
  accli.value.filter(item => key === item.caozuo)[0].caozuo = nn;
};

const saveData = async (data: any) => {
  return await useRouteApi(saveSubjectInitialBalanceFuZhu,{schemaName:props.database})({
    params: data,
    accId: iyear.value,
    userName: userinfo.value.username,
    databasenum: props.database,
  })
};

const quxiao = (key: string) => {
  if(key < 0){deleteTable(key);}
  delete editableData2[key];
};

const findName = (value: string,list) => {
  return bitemList.value.filter(v=> v.value === value)[0].label;
};

const show2 = (value: string) => {
  return value;
};

const findPsnName = (value: string) => {
  var list = bpersonList.value.filter(v=> v.key === value);
  if(list.length > 0){value = list[0].label;}else{value = "";}
  return value;
};

const findDeptName = (value: string) => {
  var list = bdeptList.value.filter(v=> v.key === value);
  if(list.length > 0){value = list[0].label;}else{value = "";}
  return value;
};

const findCusName = (value: string) => {
  var list = bcusList.value.filter(v=> v.key === value);
  if(list.length > 0){value = list[0].label;}else{value = "";}
  return value;
};

const findSupName = (value: string) => {
  var list = bsupList.value.filter(v=> v.key === value);
  if(list.length > 0){value = list[0].label;}else{value = "";}
  return value;
};

const findItemName = (value: string) => {
  var list = bitemList.value.filter(v=> v.key === value);
  if(list.length > 0){value = list[0].code + "，" + list[0].label;}else{value = "";}
  return value;
};

const findItemClassName = (value: string) => {
  var list = bitemClassList.value.filter(v=> v.key === value);
  if(list.length > 0){value = list[0].label;}
  return value;
};

const findCdfine1Name = (value: string) => {var list = cdfine1List.value.filter(v=> v.key === value);if(list.length > 0){value = list[0].label;}return value;};
const findCdfine2Name = (value: string) => {var list = cdfine2List.value.filter(v=> v.key === value);if(list.length > 0){value = list[0].label;}return value;};
const findCdfine3Name = (value: string) => {var list = cdfine3List.value.filter(v=> v.key === value);if(list.length > 0){value = list[0].label;}return value;};
const findCdfine4Name = (value: string) => {var list = cdfine4List.value.filter(v=> v.key === value);if(list.length > 0){value = list[0].label;}return value;};
const findCdfine5Name = (value: string) => {var list = cdfine5List.value.filter(v=> v.key === value);if(list.length > 0){value = list[0].label;}return value;};
const findCdfine6Name = (value: string) => {var list = cdfine6List.value.filter(v=> v.key === value);if(list.length > 0){value = list[0].label;}return value;};
const findCdfine7Name = (value: string) => {var list = cdfine7List.value.filter(v=> v.key === value);if(list.length > 0){value = list[0].label;}return value;};
const findCdfine8Name = (value: string) => {var list = cdfine8List.value.filter(v=> v.key === value);if(list.length > 0){value = list[0].label;}return value;};
const findCdfine9Name = (value: string) => {var list = cdfine9List.value.filter(v=> v.key === value);if(list.length > 0){value = list[0].label;}return value;};

const findCdfine10Name = (value: string) => {var list = cdfine10List.value.filter(v=> v.key === value);if(list.length > 0){value = list[0].label;}return value;};
const findCdfine11Name = (value: string) => {var list = cdfine11List.value.filter(v=> v.key === value);if(list.length > 0){value = list[0].label;}return value;};
const findCdfine12Name = (value: string) => {var list = cdfine12List.value.filter(v=> v.key === value);if(list.length > 0){value = list[0].label;}return value;};
const findCdfine13Name = (value: string) => {var list = cdfine13List.value.filter(v=> v.key === value);if(list.length > 0){value = list[0].label;}return value;};
const findCdfine14Name = (value: string) => {var list = cdfine14List.value.filter(v=> v.key === value);if(list.length > 0){value = list[0].label;}return value;};
const findCdfine15Name = (value: string) => {var list = cdfine15List.value.filter(v=> v.key === value);if(list.length > 0){value = list[0].label;}return value;};
const findCdfine16Name = (value: string) => {var list = cdfine16List.value.filter(v=> v.key === value);if(list.length > 0){value = list[0].label;}return value;};
const findCdfine17Name = (value: string) => {var list = cdfine17List.value.filter(v=> v.key === value);if(list.length > 0){value = list[0].label;}return value;};
const findCdfine18Name = (value: string) => {var list = cdfine18List.value.filter(v=> v.key === value);if(list.length > 0){value = list[0].label;}return value;};
const findCdfine19Name = (value: string) => {var list = cdfine19List.value.filter(v=> v.key === value);if(list.length > 0){value = list[0].label;}return value;};

const findCdfine20Name = (value: string) => {var list = cdfine20List.value.filter(v=> v.key === value);if(list.length > 0){value = list[0].label;}return value;};
const findCdfine21Name = (value: string) => {var list = cdfine21List.value.filter(v=> v.key === value);if(list.length > 0){value = list[0].label;}return value;};
const findCdfine22Name = (value: string) => {var list = cdfine22List.value.filter(v=> v.key === value);if(list.length > 0){value = list[0].label;}return value;};
const findCdfine23Name = (value: string) => {var list = cdfine23List.value.filter(v=> v.key === value);if(list.length > 0){value = list[0].label;}return value;};
const findCdfine24Name = (value: string) => {var list = cdfine24List.value.filter(v=> v.key === value);if(list.length > 0){value = list[0].label;}return value;};
const findCdfine25Name = (value: string) => {var list = cdfine25List.value.filter(v=> v.key === value);if(list.length > 0){value = list[0].label;}return value;};
const findCdfine26Name = (value: string) => {var list = cdfine26List.value.filter(v=> v.key === value);if(list.length > 0){value = list[0].label;}return value;};
const findCdfine27Name = (value: string) => {var list = cdfine27List.value.filter(v=> v.key === value);if(list.length > 0){value = list[0].label;}return value;};
const findCdfine28Name = (value: string) => {var list = cdfine28List.value.filter(v=> v.key === value);if(list.length > 0){value = list[0].label;}return value;};
const findCdfine29Name = (value: string) => {var list = cdfine29List.value.filter(v=> v.key === value);if(list.length > 0){value = list[0].label;}return value;};

const findCdfine30Name = (value: string) => {var list = cdfine30List.value.filter(v=> v.key === value);if(list.length > 0){value = list[0].label;}return value;};

const editableData2IsEmpty = () => {
  if(JSON.stringify(editableData2).length <13)return true;

  console.log(editableData2);

  return false;
};

const showMsg = (msg: string) => {
  createErrorModal({
    iconType: 'warning',
    title: '提示',
    content: msg
  })
};

const editableData2NotEmptyMsg = () => {
  createErrorModal({
    iconType: 'warning',
    title: '提示',
    content: '还有数据存在编辑状态不能进行操作'
  })
};

const findZiDingYiName = (msg: string) => {
  var name = msg;
  let fzhsList2:any = fzhsList.value;
  for (let i = 0; i < fzhsList2.length; i++) {
    if (name == ("自定义" + fzhsList2[i].cdfine)) {name = fzhsList2[i].cname;}
  }
  return name;
}

const num = ref(1);

const handleAdd = () => {
  var numUsed = 0 - num.value;
  var numNexted = num.value + 1;
  num.value = numNexted;
  const newData = {
    key: uuid(),
    caozuo: numUsed,
    dbilldate:maxDate.value,
    inoid:''
  };
  delete editableData2[numUsed];
  accli.value.push(newData);
  //editTable(numUsed);
  // 打开弹框
  openRowEditPageM(true, {
    accList: accli.value,
    row: accli.value[accli.value.length-1],
    map: thisMap.value,
    fuzhus: fuzhus.value,
    showCol: acccolumns.value,
    type: 'add'
  })
};

const handleEdit = async () => {
  if (tableSelectedRowKeys.value.length == 0 || tableSelectedRowKeys.value.length > 1) {
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '请至少选择一条数据且只能选中一条数据修改！'
    })
  }else if (await checkHX()){
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '当前科目已经做过往来核销，不能进行修改操作，请取消往来核算后继续！'
    })
  }else {
    let a =  accli.value.filter(item=>item.key==tableSelectedRowKeys.value[0])
    if (a.length > 0){
      openRowEditPageM(true, {
        row: a[0],
        map: thisMap.value,
        fuzhus: fuzhus.value,
        showCol: acccolumns.value,
        type: 'edit'
      })
    }
  }
}

const onSelectChange = (keys) => {
  tableSelectedRowKeys.value = keys
}
const delList = ref([])
const handleDel = async () => {
  if (tableSelectedRowKeys.value.length == 0) {
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '请至少选择一条数据删除！'
    })
  }else if (await checkHX()){
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '当前科目已经做过往来核销，不能进行删除操作，请取消往来核算后继续！'
    })
  }else {
    tableSelectedRowKeys.value.forEach((k)=>{
      let index= accli.value.findIndex(item=>item.key == k)
      delList.value.push(accli.value[index])
      accli.value.splice(index, 1)
    })
  }
}
const checkHX = async () => {
  // 判断是否包含 人员、客户或供应商
  let list =  accli.value.filter(it=> tableSelectedRowKeys.value.indexOf(it.key) != -1 && (!hasBlank(it.bcus) || !hasBlank(it.bperson) ||!hasBlank(it.bsup))).map(it=>it.vouchUnCode)
  if (list.length > 0){
    // 查询数据库是否存在核销数据
    const size= await useRouteApi(countHxRecord,{schemaName: props.database})({list: JsonTool.json(list)});
    if (size!=null && size > 0)return true
  }
  return false;
}

const delRow = (data) => {
  if (data.type == 'add'){
    let index= accli.value.findIndex(item=>item.key == data.key)
    accli.value.splice(index, 1)
  }
  tableSelectedRowKeys.value = []
}
const reloadTable = () => {
  
}
</script>
<style src="../../../../../assets/styles/global-open.less" lang="less"></style>
<style lang="less" scoped>
@Global-Border-Color: #c9c9c9; // 全局下划线颜色
.customize-atable{
  :deep(.ant-table-container) {
    padding: 0 !important;

    .ant-table-thead {
      th {
        font-size: 14px !important;
        padding: 2px 8px !important;
        border-left: 1px solid @Global-Border-Color !important;
        border-bottom: 1px solid @Global-Border-Color !important;
        border-top: 1px solid @Global-Border-Color !important;
        text-align: center !important;
        background-color: #d8d8d8 !important;
        font-weight: bold !important;
      }

      .ant-table-row-cell-ellipsis {
        text-align: center !important;
      }
    }

    .ant-table-tbody {
      tr:not(:first-child) { td {
        font-size: 14px !important;
        padding: 2px 8px !important;
        border-left: 1px solid @Global-Border-Color !important;
        border-bottom: 1px solid @Global-Border-Color !important;
      }
      }
    }

    .ant-checkbox-inner, .ant-radio-inner {
      border-color: @Global-Border-Color;
    }
  }
}
</style>
<style>
th {
  text-align: center !important;
}
td.column-money {
  text-align: right !important;
}
td.column-caozuo {
  text-align: center !important;
}
</style>
