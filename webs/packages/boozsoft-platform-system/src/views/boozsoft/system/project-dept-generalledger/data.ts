import {reactive} from "vue";

const dynamicColumnAndDataModel = reactive({
  DEFAULT: [
    {
      key: '0',
      title: '栏目名称',
      dataIndex: 'name',
      align: 'left',
      width: 150,
    },
    {
      key: '3',
      title: '显示名称',
      dataIndex: 'nameNew',
      width: 150,
      align: 'center',
      slots: { customRender: 'nameInput', },
    },
    {
      key: '4',
      title: '宽度(范围)',
      dataIndex: 'width',
      align: 'center',
      width: 140,
      slots: { customRender: 'widthInput', },
    },
    {
      key: '5',
      title: '对齐方式',
      dataIndex: 'align',
      align: 'center',
      width: 140,
      slots: { customRender: 'alignRadio' },
    }
  ],
  J:[
    {
      key: '0',
      name: '序号',
      nameNew: '序号',
      check: true,
      width: 100,
      max: 120,
      min: 80,
      align: 'center'
    },
    {
      key: '1',
      name: '部门',
      nameNew: '部门',
      check: true,
      width: 350,
      max: 320,
      min: 220,
      align: 'center',
      children: [
        {
          key: '1-1',
          name: '编码',
          nameNew: '编码',
          check: true,
          width: 150,
          max: 180,
          min: 80,
          align: 'center'
        },
        {
          key: '1-2',
          name: '名称',
          nameNew: '名称',
          check: true,
          width: 150,
          max: 180,
          min: 80,
          align: 'center'
        },
      ],
    },
    {
      key: '2',
      name: '项目',
      nameNew: '项目',
      check: true,
      width: 350,
      max: 320,
      min: 220,
      align: 'center',
      children: [
        {
          key: '2-1',
          name: '编码',
          nameNew: '编码',
          check: true,
          width: 150,
          max: 180,
          min: 80,
          align: 'center'
        },
        {
          key: '2-2',
          name: '名称',
          nameNew: '名称',
          check: true,
          width: 150,
          max: 180,
          min: 80,
          align: 'center'
        },
      ],
    },
    {
      key: '3',
      name: '期初',
      nameNew: '期初',
      check: true,
      width: 350,
      max: 320,
      min: 220,
      align: 'center',
      children: [
        {
          key: '3-1',
          name: '借方本币',
          nameNew: '借方本币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
        {
          key: '3-2',
          name: '贷方本币',
          nameNew: '贷方本币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
      ],
    },
    {
      key: '4',
      name: '借方',
      nameNew: '借方',
      check: true,
      width: 175,
      max: 250,
      min: 160,
      align: 'center',
      children: [
        {
          key: '4-1',
          name: '借方本币',
          nameNew: '借方本币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
      ],
    },
    {
      key: '5',
      name: '贷方',
      nameNew: '贷方',
      check: true,
      width: 175,
      max: 250,
      min: 160,
      align: 'center',
      children: [
        {
          key: '5-1',
          name: '贷方本币',
          nameNew: '贷方本币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
      ],
    },
    {
      key: '6',
      name: '累计借方',
      nameNew: '累计借方',
      check: true,
      width: 175,
      max: 250,
      min: 160,
      align: 'center',
      children: [
        {
          key: '6-1',
          name: '借方本币',
          nameNew: '借方本币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center'
        }
      ],
    },
    {
      key: '7',
      name: '累计贷方',
      nameNew: '累计贷方',
      check: true,
      width: 175,
      max: 250,
      min: 160,
      align: 'center',
      children: [
        {
          key: '7-1',
          name: '贷方本币',
          nameNew: '贷方本币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center'
        }
      ],
    },
    {
      key: '8',
      name: '期末',
      nameNew: '期末',
      check: true,
      width: 350,
      max: 350,
      min: 250,
      align: 'center',
      children: [
        {
          key: '8-1',
          name: '借方本币',
          nameNew: '借方本币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center'
        },
        {
          key: '8-2',
          name: '贷方本币',
          nameNew: '贷方本币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center'
        }
      ]
    }
  ],
  SJ: [
    {
      key: '0',
      name: '序号',
      nameNew: '序号',
      check: true,
      width: 100,
      max: 120,
      min: 80,
      align: 'center'
    },
    {
      key: '1',
      name: '部门',
      nameNew: '部门',
      check: true,
      width: 350,
      max: 320,
      min: 220,
      align: 'center',
      children: [
        {
          key: '1-1',
          name: '编码',
          nameNew: '编码',
          check: true,
          width: 150,
          max: 180,
          min: 80,
          align: 'center'
        },
        {
          key: '1-2',
          name: '名称',
          nameNew: '名称',
          check: true,
          width: 150,
          max: 180,
          min: 80,
          align: 'center'
        },
      ],
    },
    {
      key: '2',
      name: '项目',
      nameNew: '项目',
      check: true,
      width: 350,
      max: 320,
      min: 220,
      align: 'center',
      children: [
        {
          key: '2-1',
          name: '编码',
          nameNew: '编码',
          check: true,
          width: 150,
          max: 180,
          min: 80,
          align: 'center'
        },
        {
          key: '2-2',
          name: '名称',
          nameNew: '名称',
          check: true,
          width: 150,
          max: 180,
          min: 80,
          align: 'center'
        },
      ],
    },
    {
      key: '3',
      name: '计量单位',
      nameNew: '计量单位',
      check: true,
      width: 100,
      max: 120,
      min: 80,
      align: 'center'
    },
    {
      key: '4',
      name: '期初',
      nameNew: '期初',
      check: true,
      width: 480,
      max: 600,
      min: 300,
      align: 'center',
      children: [
        {
          key: '4-1',
          name: '借方数量',
          nameNew: '借方数量',
          check: true,
          width: 120,
          max: 175,
          min: 100,
          align: 'center',
        },
        {
          key: '4-2',
          name: '借方本币',
          nameNew: '借方本币',
          check: true,
          width: 120,
          max: 175,
          min: 100,
          align: 'center',
        },
        {
          key: '4-3',
          name: '贷方数量',
          nameNew: '贷方数量',
          check: true,
          width: 120,
          max: 175,
          min: 100,
          align: 'center',
        },
        {
          key: '4-4',
          name: '贷方本币',
          nameNew: '贷方本币',
          check: true,
          width: 120,
          max: 175,
          min: 100,
          align: 'center',
        },
      ],
    },
    {
      key: '5',
      name: '借方',
      nameNew: '借方',
      check: true,
      width: 250,
      max: 350,
      min: 250,
      align: 'center',
      children: [
        {
          key: '5-1',
          name: '借方数量',
          nameNew: '借方数量',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
        {
          key: '5-2',
          name: '借方本币',
          nameNew: '借方本币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
      ],
    },
    {
      key: '6',
      name: '贷方',
      nameNew: '贷方',
      check: true,
      width: 250,
      max: 350,
      min: 250,
      align: 'center',
      children: [
        {
          key: '6-1',
          name: '贷方数量',
          nameNew: '贷方数量',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
        {
          key: '6-2',
          name: '贷方本币',
          nameNew: '贷方本币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
      ],
    },
    {
      key: '7',
      name: '累计借方',
      nameNew: '累计借方',
      check: true,
      width: 350,
      max: 350,
      min: 250,
      align: 'center',
      children: [
        {
          key: '7-1',
          name: '借方数量',
          nameNew: '借方数量',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center'
        },
        {
          key: '7-2',
          name: '借方本币',
          nameNew: '借方本币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center'
        }
      ],
    },
    {
      key: '8',
      name: '累计贷方',
      nameNew: '累计贷方',
      check: true,
      width: 350,
      max: 350,
      min: 250,
      align: 'center',
      children: [
        {
          key: '8-1',
          name: '贷方数量',
          nameNew: '贷方数量',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
        {
          key: '8-2',
          name: '贷方本币',
          nameNew: '贷方本币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
      ],
    },
    {
      key: '9',
      name: '期末',
      nameNew: '期末',
      check: true,
      width: 480,
      max: 600,
      min: 300,
      align: 'center',
      children: [
        {
          key: '9-1',
          name: '借方数量',
          nameNew: '借方数量',
          check: true,
          width: 120,
          max: 175,
          min: 100,
          align: 'center'
        },
        {
          key: '9-2',
          name: '借方本币',
          nameNew: '借方本币',
          check: true,
          width: 120,
          max: 175,
          min: 100,
          align: 'center'
        },
        {
          key: '9-3',
          name: '贷方数量',
          nameNew: '贷方数量',
          check: true,
          width: 120,
          max: 175,
          min: 100,
          align: 'center'
        },
        {
          key: '9-4',
          name: '贷方本币',
          nameNew: '贷方本币',
          check: true,
          width: 120,
          max: 175,
          min: 100,
          align: 'center'
        }
      ]
    }
  ],
  WJ: [
    {
      key: '0',
      name: '序号',
      nameNew: '序号',
      check: true,
      width: 120,
      max: 120,
      min: 80,
      align: 'center'
    },
    {
      key: '1',
      name: '部门',
      nameNew: '部门',
      check: true,
      width: 350,
      max: 320,
      min: 220,
      align: 'center',
      children: [
        {
          key: '1-1',
          name: '编码',
          nameNew: '编码',
          check: true,
          width: 150,
          max: 180,
          min: 80,
          align: 'center'
        },
        {
          key: '1-2',
          name: '名称',
          nameNew: '名称',
          check: true,
          width: 150,
          max: 180,
          min: 80,
          align: 'center'
        },
      ],
    },
    {
      key: '2',
      name: '项目',
      nameNew: '项目',
      check: true,
      width: 350,
      max: 320,
      min: 220,
      align: 'center',
      children: [
        {
          key: '2-1',
          name: '编码',
          nameNew: '编码',
          check: true,
          width: 150,
          max: 180,
          min: 80,
          align: 'center'
        },
        {
          key: '2-2',
          name: '名称',
          nameNew: '名称',
          check: true,
          width: 150,
          max: 180,
          min: 80,
          align: 'center'
        },
      ],
    },
    {
      key: '3',
      name: '原币名称',
      nameNew: '原币名称',
      check: true,
      width: 100,
      max: 120,
      min: 80,
      align: 'center'
    },
    {
      key: '4',
      name: '期初',
      nameNew: '期初',
      check: true,
      width: 400,
      max: 500,
      min: 350,
      align: 'center',
      children: [
        {
          key: '4-1',
          name: '借方原币',
          nameNew: '借方原币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
        {
          key: '4-2',
          name: '借方本币',
          nameNew: '借方本币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
        {
          key: '4-3',
          name: '贷方原币',
          nameNew: '贷方原币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
        {
          key: '4-4',
          name: '贷方本币',
          nameNew: '贷方本币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
      ],
    },
    {
      key: '5',
      name: '借方',
      nameNew: '借方',
      check: true,
      width: 220,
      max: 220,
      min: 160,
      align: 'center',
      children: [
        {
          key: '5-1',
          name: '借方原币',
          nameNew: '借方原币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
        {
          key: '5-2',
          name: '借方本币',
          nameNew: '借方本币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
      ],
    },
    {
      key: '6',
      name: '贷方',
      nameNew: '贷方',
      check: true,
      width: 220,
      max: 220,
      min: 160,
      align: 'center',
      children: [
        {
          key: '6-1',
          name: '贷方原币',
          nameNew: '贷方原币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
        {
          key: '6-2',
          name: '贷方本币',
          nameNew: '贷方本币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
      ],
    },
    {
      key: '7',
      name: '累计借方',
      nameNew: '累计借方',
      check: true,
      width: 220,
      max: 220,
      min: 160,
      align: 'center',
      children: [
        {
          key: '7-1',
          name: '借方原币',
          nameNew: '借方原币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center'
        },
        {
          key: '7-1',
          name: '借方本币',
          nameNew: '借方本币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center'
        }
      ],
    },
    {
      key: '8',
      name: '累计贷方',
      nameNew: '累计贷方',
      check: true,
      width: 220,
      max: 220,
      min: 160,
      align: 'center',
      children: [
        {
          key: '8-1',
          name: '贷方原币',
          nameNew: '贷方原币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center'
        },
        {
          key: '8-1',
          name: '贷方本币',
          nameNew: '贷方本币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center'
        },
      ],
    },
    {
      key: '9',
      name: '期末',
      nameNew: '期末',
      check: true,
      width: 400,
      max: 500,
      min: 350,
      align: 'center',
      children: [
        {
          key: '9-1',
          name: '借方原币',
          nameNew: '借方原币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center'
        },
        {
          key: '9-2',
          name: '借方本币',
          nameNew: '借方本币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center'
        },
        {
          key: '9-3',
          name: '贷方原币',
          nameNew: '贷方原币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center'
        },
        {
          key: '9-4',
          name: '贷方本币',
          nameNew: '贷方本币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center'
        }
      ]
    }
  ],
  SWJ: [
    {
      key: '0',
      name: '序号',
      nameNew: '序号',
      check: true,
      width: 120,
      max: 120,
      min: 80,
      align: 'center'
    },
    {
      key: '1',
      name: '部门',
      nameNew: '部门',
      check: true,
      width: 350,
      max: 320,
      min: 220,
      align: 'center',
      children: [
        {
          key: '1-1',
          name: '编码',
          nameNew: '编码',
          check: true,
          width: 150,
          max: 180,
          min: 80,
          align: 'center'
        },
        {
          key: '1-2',
          name: '名称',
          nameNew: '名称',
          check: true,
          width: 150,
          max: 180,
          min: 80,
          align: 'center'
        },
      ],
    },
    {
      key: '2',
      name: '项目',
      nameNew: '项目',
      check: true,
      width: 350,
      max: 320,
      min: 220,
      align: 'center',
      children: [
        {
          key: '2-1',
          name: '编码',
          nameNew: '编码',
          check: true,
          width: 150,
          max: 180,
          min: 80,
          align: 'center'
        },
        {
          key: '2-2',
          name: '名称',
          nameNew: '名称',
          check: true,
          width: 150,
          max: 180,
          min: 80,
          align: 'center'
        },
      ],
    },
    {
      key: '3',
      name: '计量单位',
      nameNew: '计量单位',
      check: true,
      width: 100,
      max: 120,
      min: 80,
      align: 'center'
    },
    {
      key: '4',
      name: '原币名称',
      nameNew: '原币名称',
      check: true,
      width: 120,
      max: 120,
      min: 100,
      align: 'center'
    },
    {
      key: '5',
      name: '期初',
      nameNew: '期初',
      check: true,
      width: 550,
      max: 600,
      min: 450,
      align: 'center',
      children: [
        {
          key: '5-1',
          name: '借方数量',
          nameNew: '借方数量',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
        {
          key: '5-2',
          name: '借方原币',
          nameNew: '借方原币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
        {
          key: '5-3',
          name: '借方本币',
          nameNew: '借方本币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
        {
          key: '5-4',
          name: '贷方数量',
          nameNew: '贷方原币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
        {
          key: '5-5',
          name: '贷方原币',
          nameNew: '贷方原币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
        {
          key: '5-6',
          name: '贷方本币',
          nameNew: '贷方本币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
      ],
    },
    {
      key: '6',
      name: '借方',
      nameNew: '借方',
      check: true,
      width: 220,
      max: 220,
      min: 160,
      align: 'center',
      children: [
        {
          key: '6-1',
          name: '借方数量',
          nameNew: '借方数量',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
        {
          key: '6-2',
          name: '借方原币',
          nameNew: '借方原币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
        {
          key: '6-3',
          name: '借方本币',
          nameNew: '借方本币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
      ],
    },
    {
      key: '7',
      name: '贷方',
      nameNew: '贷方',
      check: true,
      width: 220,
      max: 220,
      min: 160,
      align: 'center',
      children: [
        {
          key: '7-1',
          name: '贷方数量',
          nameNew: '贷方数量',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
        {
          key: '7-2',
          name: '贷方原币',
          nameNew: '贷方原币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
        {
          key: '7-3',
          name: '贷方本币',
          nameNew: '贷方本币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
      ],
    },
    {
      key: '8',
      name: '累计借方',
      nameNew: '累计借方',
      check: true,
      width: 220,
      max: 220,
      min: 160,
      align: 'center',
      children: [
        {
          key: '8-1',
          name: '借方数量',
          nameNew: '借方数量',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
        {
          key: '8-2',
          name: '借方原币',
          nameNew: '借方原币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
        {
          key: '8-3',
          name: '借方本币',
          nameNew: '借方本币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
      ],
    },
    {
      key: '9',
      name: '累计贷方',
      nameNew: '累计贷方',
      check: true,
      width: 220,
      max: 220,
      min: 160,
      align: 'center',
      children: [
        {
          key: '9-1',
          name: '贷方数量',
          nameNew: '贷方数量',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
        {
          key: '9-2',
          name: '贷方原币',
          nameNew: '贷方原币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
        {
          key: '9-3',
          name: '贷方本币',
          nameNew: '贷方本币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
      ],
    },
    {
      key: '10',
      name: '期末',
      nameNew: '期末',
      check: true,
      width: 550,
      max: 600,
      min: 450,
      align: 'center',
      children: [
        {
          key: '10-1',
          name: '借方数量',
          nameNew: '借方数量',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
        {
          key: '10-2',
          name: '借方原币',
          nameNew: '借方原币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
        {
          key: '10-3',
          name: '借方本币',
          nameNew: '借方本币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
        {
          key: '10-4',
          name: '贷方数量',
          nameNew: '贷方原币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
        {
          key: '10-5',
          name: '贷方原币',
          nameNew: '贷方原币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
        {
          key: '10-6',
          name: '贷方本币',
          nameNew: '贷方本币',
          check: true,
          width: 125,
          max: 175,
          min: 100,
          align: 'center',
        },
      ]
    }
  ],
})
// 动态名称
const thisName = (index,thisData)=>{
  let value = ''
  if (index.toString().indexOf('-') != -1){
    let arr = index.split('-');
    if (arr.length == 2)  {
      value = thisData[parseInt(arr[0])].children[parseInt(arr[1])-1].nameNew
      if ('' == value)value = thisData[parseInt(arr[0])].children[parseInt(arr[1])-1].name
    }else{
      value = thisData[parseInt(arr[0])].children[parseInt(arr[1])-1].children[parseInt(arr[2])-1].nameNew
      if ('' == value)value = thisData[parseInt(arr[0])].children[parseInt(arr[1])-1].children[parseInt(arr[2])-1].name
    }
  }else{
    value = thisData[index].nameNew
    if ('' == value)value = thisData[index].name
  }
  return value
}
// 当前宽度
const thisWidth = (index,thisData)=>{
  let value = 0
  if (index.toString().indexOf('-') != -1){
    let arr = index.split('-');
    if (arr.length == 2)  {
      value = thisData[parseInt(arr[0])].children[parseInt(arr[1])-1].width
    }else{
      value = thisData[parseInt(arr[0])].children[parseInt(arr[1])-1].children[parseInt(arr[2])-1].width
    }
  }else{
    value = thisData[index].width
  }
  return  parseInt(value)
}
// 是否显示
const thisIsShow = (index,thisData)=>{
  let value = false
  if (index.toString().indexOf('-') != -1){
    let arr = index.split('-');
    if (arr.length == 2)  {
      value = thisData[parseInt(arr[0])].children[parseInt(arr[1])-1].check
    }else{
      value = thisData[parseInt(arr[0])].children[parseInt(arr[1])-1].children[parseInt(arr[2])-1].check
    }
  }else{
    value = thisData[index].check
  }
  return  value
}
// 对齐方式
const thisAlign = (index,thisData)=>{
  let value = 'center';
  if (index.toString().indexOf('-') != -1){
    let arr = index.split('-');
    if (arr.length == 2)  {
      value = thisData[parseInt(arr[0])].children[parseInt(arr[1])-1].align
    }else{
      value = thisData[parseInt(arr[0])].children[parseInt(arr[1])-1].children[parseInt(arr[2])-1].align
    }
  }else{
    value = thisData[index].align
  }
  return  value
}

export function changeDefaultDynamics(list,num) {
  // 改变默认数据
  dynamicColumnAndDataModel[num] = list
}

export function initDynamics() {
  return dynamicColumnAndDataModel
}

export function assemblyDynamicColumn(lanmuList:any,columnList:any){
  columnList.forEach(cObj=>{
    lanmuList.forEach((lObj,index)=>{
      if (cObj.title === lObj.name){
        cObj.title = thisName(index+'',lanmuList)
        cObj.width = thisWidth(index+'',lanmuList)
        cObj.align = thisAlign(index+'',lanmuList)
        cObj.ifShow = thisIsShow(index+'',lanmuList)
        //便利子节点
        if(cObj.children){
          cObj.children.forEach((o)=>{
            o.title = thisName(o.key+'',lanmuList)
            o.width = thisWidth(o.key+'',lanmuList)
            o.align = thisAlign(o.key+'',lanmuList)
            o.ifShow = thisIsShow(o.key+'',lanmuList)
          })
        }
      }
    })
  })
  return columnList;
}

