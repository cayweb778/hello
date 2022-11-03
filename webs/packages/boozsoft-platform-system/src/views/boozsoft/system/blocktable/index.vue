<template>
  <div class="app-container">
    <div class="a-container-head">
      <div class="a-c-head-title">
        <h2>财务信息设置</h2>
      </div>
      <div class="a-c-head-btns">
        <div class="ant-btn-group a-c-h-btns-group">
          <button
            type="button"
            class="ant-btn"
            ant-click-animating-without-extra-node="false"
          ><span>新建</span></button>
          <button
            type="button"
            class="ant-btn"
            ant-click-animating-without-extra-node="false"
          ><span>打印</span></button>
          <button
            type="button"
            class="ant-btn"
          ><span>导入</span></button>
          <button
            type="button"
            class="ant-btn"
          ><span>导出</span></button>
        </div>
      </div>
    </div>
    <div class="a-container-content">
      <a-tabs type="card" tabBarGutter="0" tabPosition="left">
        <a-tab-pane key="1" tab="基本选项">
          Content of Tab Pane 1
        </a-tab-pane>
        <a-tab-pane key="2" tab="会计期间">
          Content of Tab Pane 2
        </a-tab-pane>
        <a-tab-pane key="3" tab="凭证类型">
          Content of Tab Pane 3
        </a-tab-pane>
        <a-tab-pane key="4" tab="结算方式">
          Content of Tab Pane 3
        </a-tab-pane>
        <a-tab-pane key="5" tab="凭证模板">
          Content of Tab Pane 3
        </a-tab-pane>
        <a-tab-pane key="6" tab="外币核算">
          Content of Tab Pane 3
        </a-tab-pane>
      </a-tabs>
    </div>
  </div>
</template>
<script setup lang="ts">
</script>
