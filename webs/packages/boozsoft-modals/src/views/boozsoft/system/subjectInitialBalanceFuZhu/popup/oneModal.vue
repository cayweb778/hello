<template>
  <BasicModal
    v-bind="$attrs"
    width="600px"
    title="辅助核算编辑"
    loadingTip="数据初始化中"
    :canFullscreen="false"
    @register="register"
  >
    <p>
      <label>借方金额</label>
      <a-input
        v-model:value="md"
        @click="md = String(md).replace(/,/g, '')"
        autocomplete="off"
        style="
            width: 70%;
            text-align: right;
            margin-left: 60px;
            border-top: hidden;
            border-left: hidden;
            border-right: hidden;
          "
      />
    </p>
    <p>
      <label>贷方金额</label>
      <a-input
        v-model:value="mc"
        @click="mc = String(mc).replace(/,/g, '')"
        autocomplete="off"
        style="
            width: 70%;
            text-align: right;
            margin-left: 60px;
            border-top: hidden;
            border-left: hidden;
            border-right: hidden;
          "
      />
    </p>
  </BasicModal>
</template>
<script setup lang="ts">
import { BasicModal, useModalInner } from '/@/components/Modal'
import {ref} from "vue";

function abc(){}
const emit=defineEmits(['register'])
import {
  Input as AInput,

} from 'ant-design-vue';
const tenantId = ref("");
const iyear = ref("");

const accflag = ref(false);

const bperson = ref('0');
const bdept = ref('0');
const bcus = ref('0');
const bsup = ref('0');
const bitem = ref('0');

const bpersonflag = ref(false);
const bdeptflag = ref(false);
const bcusflag = ref(false);
const bsupflag = ref(false);
const bitemflag = ref(false);

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

const cdfineflag1 = ref(false);
const cdfineflag2 = ref(false);
const cdfineflag3 = ref(false);
const cdfineflag4 = ref(false);
const cdfineflag5 = ref(false);
const cdfineflag6 = ref(false);
const cdfineflag7 = ref(false);
const cdfineflag8 = ref(false);
const cdfineflag9 = ref(false);
const cdfineflag10 = ref(false);
const cdfineflag11 = ref(false);
const cdfineflag12 = ref(false);
const cdfineflag13 = ref(false);
const cdfineflag14 = ref(false);
const cdfineflag15 = ref(false);
const cdfineflag16 = ref(false);
const cdfineflag17 = ref(false);
const cdfineflag18 = ref(false);
const cdfineflag19 = ref(false);
const cdfineflag20 = ref(false);
const cdfineflag21 = ref(false);
const cdfineflag22 = ref(false);
const cdfineflag23 = ref(false);
const cdfineflag24 = ref(false);
const cdfineflag25 = ref(false);
const cdfineflag26 = ref(false);
const cdfineflag27 = ref(false);
const cdfineflag28 = ref(false);
const cdfineflag29 = ref(false);
const cdfineflag30 = ref(false);

const ccode = ref("");
const ccodeName = ref("");

const [register, { closeModal }] = useModalInner((data,id) => {
  //alert(1);
  ccode.value = data.map.kemuCode.ccode;
  ccodeName.value = data.map.kemuCode.ccodeName;
  iyear.value = data.map.kemuCode.iyear;
  tenantId.value = data.map.kemuCode.tenantId;

  bnum.value = data.map.kemuCode.bnum == '1'?'1':'0';
  currency.value = data.map.kemuCode.currency == '1'?'1':'0';

  bperson.value = data.map.kemuCode.bperson == '1'?'1':'0';
  bdept.value = data.map.kemuCode.bdept == '1'?'1':'0';
  bcus.value = data.map.kemuCode.bcus == '1'?'1':'0';
  bsup.value = data.map.kemuCode.bsup == '1'?'1':'0';
  bitem.value = data.map.kemuCode.bitem == '1'?'1':'0';

  cdfine1.value = data.map.kemuCode.cdfine1 == '1'?'1':'0';
  cdfine2.value = data.map.kemuCode.cdfine2 == '1'?'1':'0';
  cdfine3.value = data.map.kemuCode.cdfine3 == '1'?'1':'0';
  cdfine4.value = data.map.kemuCode.cdfine4 == '1'?'1':'0';
  cdfine5.value = data.map.kemuCode.cdfine5 == '1'?'1':'0';
  cdfine6.value = data.map.kemuCode.cdfine6 == '1'?'1':'0';
  cdfine7.value = data.map.kemuCode.cdfine7 == '1'?'1':'0';
  cdfine8.value = data.map.kemuCode.cdfine8 == '1'?'1':'0';
  cdfine9.value = data.map.kemuCode.cdfine9 == '1'?'1':'0';
  cdfine10.value = data.map.kemuCode.cdfine10 == '1'?'1':'0';
  cdfine11.value = data.map.kemuCode.cdfine11 == '1'?'1':'0';
  cdfine12.value = data.map.kemuCode.cdfine12 == '1'?'1':'0';
  cdfine13.value = data.map.kemuCode.cdfine13 == '1'?'1':'0';
  cdfine14.value = data.map.kemuCode.cdfine14 == '1'?'1':'0';
  cdfine15.value = data.map.kemuCode.cdfine15 == '1'?'1':'0';
  cdfine16.value = data.map.kemuCode.cdfine16 == '1'?'1':'0';
  cdfine17.value = data.map.kemuCode.cdfine17 == '1'?'1':'0';
  cdfine18.value = data.map.kemuCode.cdfine18 == '1'?'1':'0';
  cdfine19.value = data.map.kemuCode.cdfine19 == '1'?'1':'0';
  cdfine20.value = data.map.kemuCode.cdfine20 == '1'?'1':'0';
  cdfine21.value = data.map.kemuCode.cdfine21 == '1'?'1':'0';
  cdfine22.value = data.map.kemuCode.cdfine22 == '1'?'1':'0';
  cdfine23.value = data.map.kemuCode.cdfine23 == '1'?'1':'0';
  cdfine24.value = data.map.kemuCode.cdfine24 == '1'?'1':'0';
  cdfine25.value = data.map.kemuCode.cdfine25 == '1'?'1':'0';
  cdfine26.value = data.map.kemuCode.cdfine26 == '1'?'1':'0';
  cdfine27.value = data.map.kemuCode.cdfine27 == '1'?'1':'0';
  cdfine28.value = data.map.kemuCode.cdfine28 == '1'?'1':'0';
  cdfine29.value = data.map.kemuCode.cdfine29 == '1'?'1':'0';
  cdfine30.value = data.map.kemuCode.cdfine30 == '1'?'1':'0';
})
</script>
