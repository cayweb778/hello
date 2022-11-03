<template>
  <div class="m-4">
    <BasicForm
      :labelWidth="100"
      :schemas="schemas"
      :actionColOptions="{ span: 24 }"
      @submit="handleSubmit"
    />
  </div>
</template>
<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {BasicForm} from '/@/components/Form/index';
import {useMessage} from '/@/hooks/web/useMessage';
import {getFromEdit} from '/@/api/record/system/sys_project_category'
//查询栏目列表
async function findColumn() {
  getFromEdit("test").then((list:any) => {
    console.log(list)
    list.forEach(function(value:any){
      schemas.value.push({
        field: value.ctitle,
        component: value.shuxing,
        label: value.cname,
        colProps: {
          span: 8,
        },
        defaultValue: '1',
        componentProps: {
          // style:'width:200px ',
          placeholder: value.cname,
          onChange: (e: any) => {
            console.log(e);
          },
        },
      })
    })
    console.log(schemas)
  })
}

onMounted(async () => {
  await findColumn()
})

const schemas:any = ref([createColumn()])

function createColumn(){
  return {
    field: 'field1',
    component: 'Input',
    label: '字段1',
    colProps: {
      span: 8,
    },
    defaultValue: '111',
    componentProps: {
      style:'width:200px ',
      placeholder: '自定义placeholder',
      onChange: (e: any) => {
        console.log(e);
      },
    },
  }
}

const {createMessage} = useMessage();
const handleSubmit = (values: any) => {
  createMessage.success('click search,values:' + JSON.stringify(values));
}
</script>

<style scoped>

</style>