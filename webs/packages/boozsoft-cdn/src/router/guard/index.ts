import router from '@/router';

import {createPermissionGuard} from './permissionGurad';

export function setupRouterGuard() {
    createPermissionGuard(router);
}
