export const cardList = (() => {
  const result: any[] = []
  for (let i = 0; i < 24; i++) {
    result.push({
      title: 'BoozNC',
      icon: 'logos:ant-design',
      color: '#1890ff',
      active: '100',
      new: '1,799',
      download: 'bx:bx-download'
    })
  }
  return result
})()
