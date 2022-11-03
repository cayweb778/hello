<template>
  <div style="padding: 5px 10px;">
    <List style="overflow-y: auto;overflow-x: hidden;height: 635px;">
      <Row :gutter="16">
        <template v-for="(item, index) in pageParameter.templateList" :key="index">
          <Col :span="6">
            <ListItem>
              <Card :hoverable="true" style="width: 100%;">
                <div style="background-color: #b4c8e3;color: white;padding: 4px;">
                  <span>&emsp;{{ item.orderNum }}</span>
                  <Popover placement="bottom">
                    <Button style="padding: 0px 4px; height: 20px;float: right">
                      <DashOutlined/>
                    </Button>
                    <template #content>
                      <p class="p_specifics" @click="openModifyPage(item)">修改</p>
                      <p class="p_specifics" @click="delData(item)">删除</p>
                    </template>
                  </Popover>
                </div>
                <div style="height: 100px;background-color: #e5e4e4;text-align: center;">
                  <div style="height: 50px;padding: 8px 20px;">
                    <span style="font-size: 16px;">编码：<span style="font-weight: bold;"> {{ item.templateNum }}</span></span>
                    <span style="font-size: 16px;margin-left: 2em;">名称：<span style="font-weight: bold;">{{ item.templateName }}</span></span>
                  </div>
                  <div style="height: 50px;padding: 8px 20px;">
                    <span style="float: left;">模板类型：<span style="font-weight: bold;">{{item.carryOverType == '1'?'公式结转':'对应结转'}}</span></span>
                    <Button v-if="item.carryOverIsShow=='0'" type="primary"  size="small" style="float: right;" @click="toAccVoucher(item)">生成凭证</Button><a-button style="float: right;" size="small" v-else>查看凭证</a-button>
                  </div>
                  <!--                        <span>金额： {{ item.carryOverSum }}</span><br>-->
                  <!--                   :disabled="item.carryOverSum=='?'"     -->
                </div>
              </Card>
            </ListItem>
          </Col>
        </template>
      </Row>
      <Query
        @register="registerQueryPage"
      />
    </List>
  </div>
</template>

<script setup="props, {content}" lang="ts">
import {inject,ref, unref} from 'vue';
import {DashOutlined} from '@ant-design/icons-vue';
import {
  Popover, Button, Row, Col, List, Card
} from "ant-design-vue";
import {useModal} from "/@/components/Modal";
import Query from './query.vue'

const [registerQueryPage, {openModal: openQueryPageM}] = useModal()
const ListItem= List.Item;
const emit=defineEmits(['edit','del'])
const pageParameter:any = inject('pageParameter')
const openModifyPage = (o) => {
  emit('edit',o)
}
const delData = (o) => {
  emit('del',o)
}
const toAccVoucher = (item) => {
  console.log('走了')
  openQueryPageM(true, {
    data: item,
    dynamicTenantId:pageParameter.dynamicTenantId
  })
}
</script>
<style lang="less" scoped="scoped">
:deep(.ant-card-body) {
  padding: 0px;
  border-left: 2px solid rgb(1, 143, 251);
  box-shadow: rgb(72 113 140) -3px 1px 7px -1px;
}
</style>
