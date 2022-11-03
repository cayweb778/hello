<template>
  <div>
    <div>
      属性 <br>
      id int<br>
      <input/>varchar<br>
      <input/>varchar<br>
      <input/>varchar<br>
      <input/>varchar<br>

    </div>
    <div>已有</div>
    <div>
      类型:
      <select v-model="info.type">
        <option value="1">集团</option>
        <option>组织</option>
        <option>账套</option>
      </select>
    </div>


    <div>
      输入档案名<input v-model="info.recordName"/>
    </div>


    <button @click="makeCode">生成</button>
  </div>
</template>
<script setup>
import {ref} from 'vue'

const info=ref({
  type:'1',
  recordName:'SysLogger2'
})
import {makeCode} from './index';

console.log(import.meta.env)

</script>