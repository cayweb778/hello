export const modules = import.meta.globEager('../../../../pages/**/*.ts')

const menus = Object.values(modules).map(it => it.menus)


export const sysMenuQueryAll = () => JSON.parse(JSON.stringify({menus:menus.flatMap(it=>it), modules}));
