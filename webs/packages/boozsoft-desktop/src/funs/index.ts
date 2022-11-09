import {invoke} from "@tauri-apps/api/tauri";

export async function getAddr() {
    const addrJSON= await invoke("getCacheIpAddr", {name: name.value});
    return JSON.parse(addrJSON)
}