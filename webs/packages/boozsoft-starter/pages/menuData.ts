export const modules = import.meta.globEager('./modules/**/*.ts')

const menus = Object.values(modules).map(it => it.menus)


export const sysMenuQueryAll = () => JSON.parse(JSON.stringify({menus:menus.flatMap(it=>it), modules}));
