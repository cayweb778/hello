- 微前端 连通所有模块
webs/packages/boozsoft-platform-home/src/settings/encryptionSetting.ts
- export const enableStorageEncryption = !isDevMode();
- 改为export const enableStorageEncryption = true;

- 生成与线上统统加密webstore


webs/packages/boozsoft-platform-home/src/utils/env.ts
由
```typescript
export function getCommonStoragePrefix() {
  const { VITE_GLOB_APP_SHORT_NAME } = getAppEnvConfig();
  return `${VITE_GLOB_APP_SHORT_NAME}__${getEnv()}`.toUpperCase();
}

```
改为__PRODUCTION，统一webstore key


```typescript
export function getCommonStoragePrefix() {
  const { VITE_GLOB_APP_SHORT_NAME } = getAppEnvConfig();
   return `${VITE_GLOB_APP_SHORT_NAME}__PRODUCTION`.toUpperCase();
}

```